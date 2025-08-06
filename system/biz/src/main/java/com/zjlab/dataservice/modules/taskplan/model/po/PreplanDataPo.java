package com.zjlab.dataservice.modules.taskplan.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjlab.dataservice.modules.dataset.handler.PointTypeHandler;
import com.zjlab.dataservice.modules.taskplan.model.entity.IWeatherInfoDay;
import com.zjlab.dataservice.modules.taskplan.model.entity.WeatherInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
* 
* @TableName preplan_data
*/
@Data
@TableName(value = "preplan_data_2")
public class PreplanDataPo implements Serializable {

    /**
    * 
    */
    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    private Integer id;
    /**
    * 
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    /**
    * 
    */
    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    private Double latitude;
    /**
    * 
    */
    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    private Double longitude;
    /**
    * 
    */
    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    @JsonIgnore
    private Double altitude;
    /**
    * 
    */
    @ApiModelProperty("")
    @JsonIgnore
    private BigDecimal latitudeRate;
    /**
    * 
    */
    @ApiModelProperty("")
    @JsonIgnore
    private BigDecimal longitudeRate;
    /**
    * 
    */
    @ApiModelProperty("")
    @JsonIgnore
    private Double altitudeRate;
    /**
    * 
    */
    @ApiModelProperty("")
    private String satelliteId;


    @ApiModelProperty("")
    private Integer elevator;

    @ApiModelProperty("")
    private String direction;

    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    private Double offNadirDeg;

    private Integer day;

    private LocalDateTime localTime;

    @ApiModelProperty("")
    @TableField(typeHandler = PointTypeHandler.class)
    @JsonIgnore // 忽略这个字段，避免在序列化时直接输出
    private String geomInfo;

    @TableField(exist = false)
    @JsonIgnore
    private String geomJson;

    @TableField(exist = false)
    private IWeatherInfoDay weatherJson;

    @TableField(exist = false)
    private Integer quality;

    @TableField(exist = false)
    private boolean isDomestic;


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
