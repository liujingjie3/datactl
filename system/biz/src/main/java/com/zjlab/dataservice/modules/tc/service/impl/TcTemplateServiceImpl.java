package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.system.api.ISysBaseAPI;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.common.util.Func;
import com.zjlab.dataservice.common.util.storage.MinioUtil;
import com.zjlab.dataservice.modules.storage.service.impl.MinioFileServiceImpl;
import com.zjlab.dataservice.modules.system.mapper.SysUserRoleMapper;
import com.zjlab.dataservice.modules.tc.mapper.TodoTemplateMapper;
import com.zjlab.dataservice.modules.tc.model.dto.QueryListDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.TcCommand;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import com.zjlab.dataservice.modules.tc.model.vo.CommandVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateCountVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateQueryListVO;
import com.zjlab.dataservice.modules.tc.service.TcTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcTemplateServiceImpl extends ServiceImpl<TodoTemplateMapper, TodoTemplate> implements TcTemplateService {

    @Autowired
    private ISysBaseAPI userService;  // 注入 UserService

    @Autowired
    private TodoTemplateMapper todoTemplateMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private MinioFileServiceImpl minioFileService;

    private static final String BUCKET_NAME = "dspp";
    private static final String FOLDER_NAME = "/TJEOS/WORKDIR/FILE/command";
    private static final String adminRoleId = "f6817f48af4fb3af11b9e8bf182f618b";
    private static final String ztzRoleId = "2";

    @Override
    public PageResult<TemplateQueryListVO> qryTemplateList(QueryListDto queryListDto) {
        // 参数校验
        checkParam(queryListDto);
        IPage<TodoTemplate> page = new Page<>(queryListDto.getPageNo(), queryListDto.getPageSize());
        String userId = UserThreadLocal.getUserId(); // 获取当前用户ID
        List<String> roles = sysUserRoleMapper.getRoleByUserId(userId);
        boolean isAdmin = roles.contains(adminRoleId);
//        boolean isZTZ = roles.contains(ztzRoleId);

        IPage<TodoTemplate> resultPage = baseMapper.selectTodoTemplatePage(page, queryListDto, userId, isAdmin);

        List<TodoTemplate> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        List<TemplateQueryListVO> collect = listRecords.stream().map(TodoTemplate -> {
            TemplateQueryListVO templateQueryListVo = TemplateQueryListVO.builder().id(TodoTemplate.getId()).templateId(TodoTemplate.getTemplateId()).flag(TodoTemplate.getFlag()).templateName(TodoTemplate.getTemplateName()).updateTime(TodoTemplate.getUpdateTime()).remark(TodoTemplate.getRemark()).build();

            //todo 节点数量统计
            String createBy = TodoTemplate.getCreateBy();
            // 查用户名（需要考虑 null）
            if (StringUtils.isNotBlank(createBy)) {
                LoginUser user = userService.getUserById(createBy);
                if (user != null) {
                    templateQueryListVo.setUserName(user.getRealname()); // 这里假设 LoginUser 有 getUsername()
                }
            }
            String filePath = TodoTemplate.getFilePath();
            templateQueryListVo.setFileCount(StringUtils.isNotBlank(filePath) ? 1 : 0);
            templateQueryListVo.setNodeCount(0);

            return templateQueryListVo;
        }).collect(Collectors.toList());


        PageResult<TemplateQueryListVO> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    /**
     * 校验参数
     */
    private void checkParam(QueryListDto todoTemplateQueryDto) {
        todoTemplateQueryDto.setPageNo(Optional.ofNullable(todoTemplateQueryDto.getPageNo()).orElse(1));
        todoTemplateQueryDto.setPageSize(Optional.ofNullable(todoTemplateQueryDto.getPageSize()).orElse(50));
        String field = todoTemplateQueryDto.getOrderByField();
        if (StringUtils.isBlank(field)) {
            todoTemplateQueryDto.setOrderByField("update_time");
        } else {
            // 驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            todoTemplateQueryDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(todoTemplateQueryDto.getOrderByType())) {
            todoTemplateQueryDto.setOrderByType("desc");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoTemplateDto submit(MultipartFile file, TodoTemplateDto todoTemplateDto) throws Exception {
        // 将 DTO 转为实体
        TodoTemplate template = new TodoTemplate();
        BeanUtil.copyProperties(todoTemplateDto, template);
        template.setTemplateAttr(todoTemplateDto.getAttrs());
        template.setFlag(1);

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        String templateId;
        // 新增
        templateId = Func.randomUUID();
        template.setTemplateId(templateId);
        template.initBase(true, userId);
        log.info("before insert template info: {}", template);

        if (file != null && !file.isEmpty()) {
            String objectName = minioFileService.uploadReturnObjectName(file, BUCKET_NAME, FOLDER_NAME);
            template.setFilePath(objectName);
        }
        todoTemplateMapper.insert(template);

        return todoTemplateDto;
    }

    @Override
    public TodoTemplateDto update(MultipartFile file, TodoTemplateDto todoTemplateDto) throws Exception {
        TodoTemplate existing = baseMapper.selectTodoTemplateDetail(Long.valueOf(todoTemplateDto.getId()));
        BeanUtil.copyProperties(todoTemplateDto, existing); // 用新值覆盖旧值
        existing.setUpdateTime(LocalDateTime.now());

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        LoginUser user = userService.getUserById(userId);

        if (Func.notNull(user)) {
            existing.setUpdateBy(userId);
        }
        if (file != null && !file.isEmpty()) {
            if (existing.getFilePath() != null) {
                existing.setFilePathOld(existing.getFilePath());
            }
            String objectName = minioFileService.uploadReturnObjectName(file, BUCKET_NAME, FOLDER_NAME);
            existing.setFilePath(objectName);
        }
        log.info("before update template info: {}", existing);
        todoTemplateMapper.updateTodoTemplate(existing);
        return todoTemplateDto;
    }


    @Override
    public TodoTemplateDto findOne(TodoTemplateQueryDto todoTemplateQueryVO) {

        QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
        if (Func.isNotBlank(todoTemplateQueryVO.getTemplateName())) {
            queryWrapper.eq("template_name", todoTemplateQueryVO.getTemplateName());
        }
        if (Func.isNotBlank(todoTemplateQueryVO.getAgentId())) {
            queryWrapper.eq("agent_id", todoTemplateQueryVO.getAgentId());
        }
        if (Func.isNotBlank(todoTemplateQueryVO.getTemplateType())) {
            queryWrapper.eq("template_type", todoTemplateQueryVO.getTemplateType());
        }
        if (Func.isNotBlank(todoTemplateQueryVO.getTemplateCode())) {
            queryWrapper.eq("code", todoTemplateQueryVO.getTemplateCode());
        }
        queryWrapper.eq("del_flag", 0).orderByDesc("id").last("limit 1");
        TodoTemplate todoTemplate = baseMapper.selectOne(queryWrapper);
        if (Func.isNull(todoTemplate)) {
            return null;
        }
        TodoTemplateDto todoTemplateDto = new TodoTemplateDto();
        BeanUtil.copyProperties(todoTemplate, todoTemplateDto);
        return todoTemplateDto;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public TodoTemplateQueryDto deleteByCode(TodoTemplateQueryDto templateQueryVO) {

        List<String> templateCodes = templateQueryVO.getCodes();
        if (Func.notNull(templateCodes) && !templateCodes.isEmpty()) {

            List<TodoTemplate> templates = new ArrayList<>();

            String userId = UserThreadLocal.getUserId();
            if (userId == null) {
                throw new BaseException(ResultCode.USERID_IS_NULL);
            }
            LoginUser user = userService.getUserById(userId);

            for (String code : templateCodes) {
                QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("code", code).orderByDesc("id").last("limit 1");
                TodoTemplate todoTemplate = baseMapper.selectOne(queryWrapper);
                if (Func.notNull(todoTemplate)) {
                    todoTemplate.setDelFlag(true);
                    LocalDateTime now = LocalDateTime.now();
                    todoTemplate.setUpdateTime(now);
                    if (Func.notNull(user)) {
                        todoTemplate.setUpdateBy(user.getUsername());
                    }
                    templates.add(todoTemplate);
                }
            }
            updateBatchById(templates);
        }
        return templateQueryVO;
    }

    @Override
    public TodoTemplateQueryDto deleteByTemplateId(@NotNull TodoTemplateQueryDto templateQueryVO) {

        String agentId = templateQueryVO.getAgentId();
        String templateId = templateQueryVO.getTemplateId();
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(agentId)) {
            queryWrapper.eq("agent_id", agentId);
        }
        if (Func.notNull(templateId)) {
            queryWrapper.eq("template_id", templateId);
        }
        queryWrapper.eq("code", templateQueryVO.getTemplateCode()).orderByDesc("id").last("limit 1");
        TodoTemplate todoTemplate = baseMapper.selectOne(queryWrapper);
        if (Func.notNull(todoTemplate)) {

            todoTemplate.setDelFlag(true);
            todoTemplate.setUpdateTime(now);
            todoTemplate.setUpdateBy(agentId);

            log.info("before update template info: {}", todoTemplate);
            updateById(todoTemplate);
        }
        return templateQueryVO;
    }

    @Override
    public PageResult<TodoTemplateDto> findByUserIdPage(TodoTemplateQueryDto basePage) {
        // 构造分页参数
        IPage<TodoTemplate> page = new Page<>(basePage.getPageNo(), basePage.getPageSize());

        // 构造查询条件
        QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(basePage.getAgentId())) {
            queryWrapper.eq("agent_id", basePage.getAgentId());
        }
        if (Func.notNull(basePage.getTemplateCode())) {
            queryWrapper.eq("code", basePage.getTemplateCode());
        }
        if (Func.notNull(basePage.getTemplateType())) {
            queryWrapper.eq("template_type", basePage.getTemplateType());
        }
        if (Func.notNull(basePage.getUserId())) {
            queryWrapper.eq("create_by", basePage.getUserId());
        }

        // 查询分页数据
        IPage<TodoTemplate> resultPage = this.baseMapper.selectPage(page, queryWrapper);

        // 转换结果列表
        List<TodoTemplateDto> dtoList = resultPage.getRecords().stream().map(template -> {
            TodoTemplateDto dto = new TodoTemplateDto();
            BeanUtil.copyProperties(template, dto);
            return dto;
        }).collect(Collectors.toList());

        // 构造返回结果
        return buildPageResult(resultPage, dtoList);
    }

    @Override
    public TodoTemplate getDetail(Long id) {
        TodoTemplate todoTemplate = todoTemplateMapper.selectTodoTemplateDetail(id);
        return todoTemplate;
    }

    @Override
    public void deleteById(Long id) {
        TodoTemplate todoTemplate = todoTemplateMapper.selectTodoTemplateDetail(id);
        LocalDateTime now = LocalDateTime.now();
        String userId = UserThreadLocal.getUserId();

        if (Func.notNull(todoTemplate)) {
            todoTemplate.setDelFlag(true);
            todoTemplate.setUpdateTime(now);
            todoTemplate.setUpdateBy(userId);
            log.info("before update template info: {}", todoTemplate);
            todoTemplateMapper.updateTodoTemplate(todoTemplate);
        }
    }

    @Override
    public void publish(Long id) {
        TodoTemplate todoTemplate = todoTemplateMapper.selectTodoTemplateDetail(id);
        if (todoTemplate != null) {
            Integer currentFlag = todoTemplate.getFlag();
            // 如果为0则改为1，如果为1则改为0
            todoTemplate.setFlag(currentFlag != null && currentFlag == 1 ? 0 : 1);
            // 更新保存
            todoTemplateMapper.updateTodoTemplate(todoTemplate);
        }
    }

    @Override
    public TemplateCountVO getCount() {
        String userId = UserThreadLocal.getUserId();
        List<String> roles = sysUserRoleMapper.getRoleByUserId(userId);
        QueryWrapper<TodoTemplate> baseWrapper = new QueryWrapper<TodoTemplate>().eq("del_flag", false);
        // 如果不是管理员，按用户过滤
        // 如果用户角色ID中不包含指定管理员角色ID，则按用户过滤
        if (!roles.contains(adminRoleId)) {
            baseWrapper.eq("create_by", userId);
        }

        long totalCount = baseMapper.selectCount(baseWrapper);
        long publishedCount = baseMapper.selectCount(baseWrapper.eq("flag", 1));
        long unpublishedCount = baseMapper.selectCount(baseWrapper.eq("flag", 0));

        return new TemplateCountVO(totalCount, publishedCount, unpublishedCount);

    }

    @Override
    public List<CommandVO> parse(Long id) throws Exception {
        TodoTemplate todoTemplate = todoTemplateMapper.selectTodoTemplateDetail(id);
        String filePath = todoTemplate.getFilePath();
        List<CommandVO> list = new ArrayList<>();

        try (InputStream is = MinioUtil.getObject(BUCKET_NAME, filePath)) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第2行开始
                Row row = sheet.getRow(i);
                if (row == null) continue;
//                if (row.getCell(0) == null) continue;
                CommandVO cmd = new CommandVO();
                cmd.setIndex(i);
                cmd.setCmdCode(row.getCell(0).getStringCellValue());
                cmd.setCmdName(row.getCell(1).getStringCellValue());
                cmd.setExecSequence(row.getCell(2).getStringCellValue());
                cmd.setExecCriteria(row.getCell(3).getStringCellValue());
                cmd.setRemark(row.getCell(4).getStringCellValue());
                list.add(cmd);
            }
        }
        return list;

    }

    // 封装分页结果
    private PageResult<TodoTemplateDto> buildPageResult(IPage<?> page, List<TodoTemplateDto> listRecords) {
        PageResult<TodoTemplateDto> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }


    // ====== 5. 解析 Excel 并批量插入 tc_commands ======
//        if (file != null && !file.isEmpty()) {
//            List<TcCommand> commands = parseExcel(user,file, templateId);
//            if (!commands.isEmpty()) {
//                tcCommandMapper.batchInsert(commands); // 批量插入
//            }
//        }
    private List<TcCommand> parseExcel(LoginUser user, MultipartFile file, String templateId) {
        List<TcCommand> list = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第2行开始
                Row row = sheet.getRow(i);
                if (row == null) continue;

                TcCommand cmd = new TcCommand();
                cmd.setTemplateId(templateId);
                cmd.setCode(row.getCell(0).getStringCellValue());
                cmd.setName(row.getCell(1).getStringCellValue());
                cmd.setTimeOrder(row.getCell(2).getStringCellValue());
                cmd.setExecution(row.getCell(3).getStringCellValue());
                cmd.setDescription(row.getCell(4).getStringCellValue());
                cmd.setDelFlag(false);
                if (Func.notNull(user)) {
                    cmd.setCreateBy(user.getUsername());
                    cmd.setUpdateBy(user.getUsername());
                }
                cmd.setCreateTime(LocalDateTime.now());
                cmd.setUpdateTime(LocalDateTime.now());
                list.add(cmd);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


}
