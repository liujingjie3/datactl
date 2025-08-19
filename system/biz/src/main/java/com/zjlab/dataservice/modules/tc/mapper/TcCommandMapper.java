package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.entity.TcCommand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TcCommandMapper extends BaseMapper<TcCommand> {
    int batchInsert(@Param("list") List<TcCommand> list);

}




