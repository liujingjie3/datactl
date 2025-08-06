package com.zjlab.dataservice.modules.myspace.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.myspace.model.po.CollectPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("postgre")
public interface CollectMapper extends BaseMapper<CollectPo> {

    List<Integer> selectCollectedImageIds(@Param("userId") String userId);

    boolean existsByImageId(@Param("imageId")Integer id,@Param("userId") String userId);
}
