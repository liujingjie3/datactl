package com.zjlab.dataservice.modules.dataset.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zjlab.dataservice.modules.dataset.handler.PointTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotNull;

@Data
@TableName(value = "thematic_shanghai")
public class ThematicJZHPo {
    @NotNull(message = "[]不能为空")
    @ApiModelProperty("")
    private Integer id;
    /**
     *
     */
    @TableField(typeHandler = PointTypeHandler.class)
    @JsonIgnore // 忽略这个字段，避免在序列化时直接输出
    private String geom;
    //
    @TableField(exist = false)
    private String geomJson;

    /**
     *
     */
    @ApiModelProperty("")
    private Integer value;
    /**
     *
     */
    @ApiModelProperty("")
    private Long pac1;


    @TableField(exist = false)
    private String fillColor;
    /**
     * 边框颜色
     */
    @TableField(exist = false)
    private String borderColor;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getGeomJsonObject() {
        try {
            return objectMapper.readTree(this.geomJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
