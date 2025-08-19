package com.zjlab.dataservice.modules.tc.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 指令表
 * @TableName tc_commands
 */
@TableName("tc_command")
@Data
public class TcCommand extends TcBaseEntity {

    /**
     * 模版ID
     */
    private String templateId;
    /**
     * 指令代号
     */
    private String code;
    /**
     * 指令名称
     */
    private String name;
    /**
     * 时序
     */
    private String timeOrder;
    /**
     * 执行判据
     */
    private String execution;
    /**
     * 备注
     */
    private String description;
    /**
     * 删除标记（0=正常，1=删除）
     */

}
