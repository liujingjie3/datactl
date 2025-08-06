package com.zjlab.dataservice.modules.dataset.aspect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.dataset.enums.TaskTypeEnum;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetMarkFilePo;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetOperationLogPo;
import com.zjlab.dataservice.modules.dataset.service.MarkService;
import com.zjlab.dataservice.modules.dataset.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private OperationLogService operationLogService;

    @Resource
    private MarkService markService;

    @Pointcut(value = "@annotation(com.zjlab.dataservice.modules.dataset.aspect.OperationLog)")
    public void logPointCut(){

    }

    @Around("logPointCut()")
    public Object recordOperationLog(ProceedingJoinPoint point) throws Throwable {
        //执行方法
        Object result = point.proceed();
        //保存日志
        try {
            saveOperationLog(point);
        }catch (Exception e){
            log.error("save operation log error!", e);
        }
        return result;
    }

    private void saveOperationLog(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        //获取接口入参
        Object[] args = point.getArgs();
        Object arg = args[0];
        JSONObject json = (JSONObject) JSONObject.toJSON(arg);
        Integer id = json.getInteger("id");
        JSONArray ids = json.getJSONArray("ids");
        String markType = json.getString("markType");
        String markUrl = json.getString("markUrl");
        String taskType = json.getString("taskType");

        OperationEnum action = annotation.action();
        if (taskType == null) {
            switch (action) {
                case SEGMENT:
                    taskType = TaskTypeEnum.SEGMENT.getType();
                    break;
                case CLASSIFY:
                    taskType = TaskTypeEnum.CLASSIFY.getType();
                    break;
                case FILTER_METADATA:
                    taskType = TaskTypeEnum.FILTER.getType();
                    break;
                case CHECK:
                default:
                    taskType = TaskTypeEnum.TEXT.getType();
                    break;
            }
        }

        //编辑只提供了单条编辑
        String lastMarkType = null;
        if (action.equals(OperationEnum.EDIT)){
            DatasetMarkFilePo markFilePo = markService.getById(id);
            lastMarkType = markFilePo.getMarkType();
        }
        List<DatasetOperationLogPo> entityList = new ArrayList<>();
        if (id != null){
            DatasetOperationLogPo operationLogPo = new DatasetOperationLogPo();
            operationLogPo.setOperation(annotation.action().getName());
            operationLogPo.setMarkType(markType);
            operationLogPo.setLastMarkType(lastMarkType);
            operationLogPo.setMarkUrl(markUrl);
            operationLogPo.setTaskType(taskType);
            //获取基础信息--人员 + 时间
            String userId = UserThreadLocal.getUserId();
            operationLogPo.setFileId(id);
            operationLogPo.setCreateBy(userId);
            operationLogPo.setCreateTime(LocalDateTime.now());
            entityList.add(operationLogPo);
        }
        if (ids != null){
            for (int i = 0; i < ids.size(); i++) {
                int item = ids.getInteger(i);
                DatasetOperationLogPo operationLogPo = new DatasetOperationLogPo();
                operationLogPo.setOperation(annotation.action().getName());
                operationLogPo.setMarkType(markType);
                operationLogPo.setLastMarkType(lastMarkType);
                operationLogPo.setMarkUrl(markUrl);
                operationLogPo.setTaskType(taskType);
                //获取基础信息--人员 + 时间
                String userId = UserThreadLocal.getUserId();
                operationLogPo.setFileId(item);
                operationLogPo.setCreateBy(userId);
                operationLogPo.setCreateTime(LocalDateTime.now());
                entityList.add(operationLogPo);
            }
        }
        //批量保存入库
        operationLogService.saveBatch(entityList);
    }
}
