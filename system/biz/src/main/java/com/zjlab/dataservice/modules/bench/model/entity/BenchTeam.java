package com.zjlab.dataservice.modules.bench.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;

/**
 * 模型表（bench_teams）
 */
@Data
@TableName("bench_teams")
public class BenchTeam extends BasePo {



    /** 模型名称 */
    private String name;

}
