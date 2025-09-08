package com.zjlab.dataservice.modules.tc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.tc.model.dto.PassInfoQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.SatellitePasses;
import com.zjlab.dataservice.modules.tc.model.entity.SatellitePassesinfo;
import com.zjlab.dataservice.modules.tc.model.vo.SatellitePassesVO;
import com.zjlab.dataservice.modules.tc.service.SatellitePassesService;
import com.zjlab.dataservice.modules.tc.mapper.SatellitePassesMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SatellitePassesServiceImpl extends ServiceImpl<SatellitePassesMapper, SatellitePasses>
        implements SatellitePassesService {

    @Override
    public List<SatellitePassesVO> passinfo(PassInfoQueryDto dto) {

        List<String> satIds = extractSatIds(dto);
        if (satIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SatellitePasses> satellitePasses = baseMapper.queryPassInfo(satIds);

        List<SatellitePassesVO> collect = satellitePasses.stream()
                .map(e -> SatellitePassesVO.builder()
                        .id(e.getId())
                        .aos(e.getAos())
                        .los(e.getLos())
                        .sat(e.getSat())
                        .station(e.getStation())
                        .duration(Duration.between(e.getAos(), e.getLos()).getSeconds()) // 秒数
                        .build()
                )
                .collect(Collectors.toList());
        return collect;
    }

    public List<String> extractSatIds(PassInfoQueryDto dto) {
        if (dto == null || dto.getSatellites() == null) {
            return Collections.emptyList();
        }
        return dto.getSatellites()
                .stream()
                .filter(Objects::nonNull) // 过滤掉 null 分组
                .flatMap(group -> group.getSatIds().stream()) // 展开成一个流
                .filter(Objects::nonNull) // 过滤掉 null 的 satId
                .map(String::trim) // 去掉首尾空格
                .filter(s -> !s.isEmpty()) // 过滤空字符串
                .distinct() // 去重
                .collect(Collectors.toList());
    }
}




