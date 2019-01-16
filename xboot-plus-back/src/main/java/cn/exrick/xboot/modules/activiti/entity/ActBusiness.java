package cn.exrick.xboot.modules.activiti.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import cn.exrick.xboot.common.constant.ActivitiConstant;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Exrickx
 */
@Data
@Entity
@Table(name = "t_act_business")
@TableName("t_act_business")
@ApiModel(value = "业务申请")
public class ActBusiness extends XbootBaseEntity {

    @ApiModelProperty(value = "申请标题")
    private String title;

    @ApiModelProperty(value = "创建用户id")
    private String userId;

    @ApiModelProperty(value = "关联表id")
    private String tableId;

    @ApiModelProperty(value = "流程定义id")
    private String procDefId;

    @ApiModelProperty(value = "流程实例id")
    private String procInstId;

    @ApiModelProperty(value = "状态 0草稿默认 1处理中 2结束")
    private Integer status = ActivitiConstant.STATUS_TO_APPLY;

    @ApiModelProperty(value = "结果状态 0未提交默认 1处理中 2通过 3驳回")
    private Integer result = ActivitiConstant.RESULT_TO_SUBMIT;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交申请时间")
    private Date applyTime;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "分配用户id")
    private String assignee;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "所属流程名")
    private String processName;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "前端路由名")
    private String routeName;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "任务优先级 默认0")
    private Integer priority = 0;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "当前任务")
    private String currTaskName;
}
