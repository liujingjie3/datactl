package com.zjlab.dataservice.modules.subject.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@TableName("UserTeamVO")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserTeamVO", description = "UserTeamVO")
public class UserTeamVO  {


    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private List<String> userIdList;

    /**团队id*/
    @Excel(name = "团队id", width = 15)
    @ApiModelProperty(value = "团队id")
    private String teamId;








}
