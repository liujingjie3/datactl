package com.zjlab.dataservice.modules.tc.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.common.api.page.PageRequest;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.util.Func;
import com.zjlab.dataservice.modules.tc.model.dto.BaseTcDto;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public final class CommonUtil {
    @SneakyThrows
    public static String getHostName(){
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * 检查分页和排序参数，设置默认值，支持泛型类型
     */
    public static <T extends PageRequest> void checkPageAndOrderParam(T dto) {
        dto.setPageNo(Optional.ofNullable(dto.getPageNo()).orElse(1));
        dto.setPageSize(Optional.ofNullable(dto.getPageSize()).orElse(10));

        String field = dto.getOrderByField();
        if (StringUtils.isBlank(field)) {
            dto.setOrderByField("update_time");
        } else {
            // 驼峰转下划线（userName -> user_name）
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            dto.setOrderByField(result);
        }

        if (StringUtils.isBlank(dto.getOrderByType())) {
            dto.setOrderByType("desc");
        }
    }

    public static void pageQueryWrapper (QueryWrapper queryWrapper, BaseTcDto baseTcDto){

        if (Func.notNull(baseTcDto.getAgentId())){
            queryWrapper.eq("agent_id", baseTcDto.getAgentId());
        }
        if (Func.notNull(baseTcDto.getFlag())){
            queryWrapper.eq("flag", baseTcDto.getFlag());
        }
        if (Func.isNotBlank(baseTcDto.getStartTime())){
            queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('"+ baseTcDto.getStartTime()+"')");
        }
        if (Func.isNotBlank(baseTcDto.getEndTime())){
            queryWrapper.apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('"+ baseTcDto.getEndTime()+"')");
        }
        queryWrapper.orderByDesc("create_on");
    }

    // 泛型方法：封装分页结果
    public static <T> PageResult<T> buildPageResult(IPage<?> page, List<T> listRecords) {
        PageResult<T> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

    public static boolean isUpdateSuccess(int rows) {
        return rows > 0;
    }

    /**
     * 格式化当前时间为字符串，格式为 yyyy-MM-dd H:mm:ss
     */
    public static String formatDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
        return now.format(formatter);
    }

}
