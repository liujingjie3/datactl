package com.zjlab.dataservice.modules.taskplan.model.po;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* 
* @TableName metadata_gf
*/
@Data
@TableName(value = "metadata_gf")
public class MetadataGf implements Serializable {

    /**
    * 
    */
    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    private Integer id;
    /**
    * 
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("")
    @Length(max= 255,message="编码长度不能超过255")
    private String fileName;
    /**
    * 
    */
    @ApiModelProperty("")
    private Float fileSize;
    /**
    * 
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("")
    @Length(max= 255,message="编码长度不能超过255")
    private String filePath;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String satelliteId;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String sensorId;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String sceneId;
    /**
    * 
    */
    @ApiModelProperty("")
    private LocalDateTime receiveTime;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer orbitId;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String produceType;
    /**
    * 
    */
    @ApiModelProperty("")
    private Long productId;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String productLevel;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String productQuality;
    /**
    * 
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("")
    @Length(max= 255,message="编码长度不能超过255")
    private String productQualityReport;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String productFormat;
    /**
    * 
    */
    @ApiModelProperty("")
    private Date produceTime;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String bands;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer scenePath;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer sceneRow;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer satPath;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer satRow;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer sceneCount;
    /**
    * 
    */
    @ApiModelProperty("")
    private Float sceneShift;
    /**
    * 
    */
    @ApiModelProperty("")
    private Date startTime;
    /**
    * 
    */
    @ApiModelProperty("")
    private Date endTime;
    /**
    * 
    */
    @ApiModelProperty("")
    private Date centerTime;
    /**
    * 
    */
    @ApiModelProperty("")
    private Float imageGsd;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer widthInPixels;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer heightInPixels;
    /**
    * 
    */
    @ApiModelProperty("")
    private Long widthInMeters;
    /**
    * 
    */
    @ApiModelProperty("")
    private Long heightInMeters;
    /**
    * 
    */
    @ApiModelProperty("")
    private Float cloudPercent;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String qualityInfo;
    /**
    * 
    */
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("")
    @Length(max= 20,message="编码长度不能超过20")
    private String pixelBits;
    /**
    * 
    */
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("")
    @Length(max= 20,message="编码长度不能超过20")
    private String validPixelBits;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double rollViewingAngle;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double pitchViewingAngle;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double rollSatelliteAngle;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double pitchSatelliteAngle;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double yawSatelliteAngle;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double solarAzimuth;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double solarZenith;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double satelliteAzimuth;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double satelliteZenith;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String gainMode;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String integrationTime;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String integrationLevel;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String mapProjection;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String earthEllipsoid;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer zoneNo;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String resamplingKernel;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String heightMode;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String mtfCorrection;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String relativeCorrectionData;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topLeftLatitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topLeftLongitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topRightLatitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topRightLongitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomRightLatitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomRightLongitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomLeftLatitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomLeftLongitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topLeftMapX;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topLeftMapY;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topRightMapX;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double topRightMapY;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomRightMapX;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomRightMapY;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomLeftMapX;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double bottomLeftMapY;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String receiveStationId;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String orbitType;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String attType;
    /**
    * 
    */
    @ApiModelProperty("")
    private Long stripId;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String ddsFlag;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String startLine;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String regionName;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String endLine;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double centerLatitude;
    /**
    * 
    */
    @ApiModelProperty("")
    private Double centerLongitude;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String aGain;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String aOffset;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String formula;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String attitudeData;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String radiometricMethod;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String denoise;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String rayleighCorrection;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer usedGcpNo;
    /**
    * 
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("")
    @Length(max= 255,message="编码长度不能超过255")
    private String dataArchiveFile;
    /**
    * 
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("")
    @Length(max= 255,message="编码长度不能超过255")
    private String browseFileLocation;
    /**
    * 
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("")
    @Length(max= 255,message="编码长度不能超过255")
    private String thumbFileLocation;
    /**
    * 
    */
    @ApiModelProperty("")
    private Object geom;
    /**
    * 
    */
    @ApiModelProperty("")
    private Boolean filterPassed;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer status;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String createBy;
    /**
    * 
    */
    @ApiModelProperty("")
    private Date createTime;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String updateBy;
    /**
    * 
    */
    @ApiModelProperty("")
    private Date updateTime;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String marker;
    /**
    * 
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("")
    @Length(max= 100,message="编码长度不能超过100")
    private String checker;

}
