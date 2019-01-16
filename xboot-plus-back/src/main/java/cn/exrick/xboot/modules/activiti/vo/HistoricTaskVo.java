package cn.exrick.xboot.modules.activiti.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import java.util.Date;

/**
 * @author Exrickx
 */
@Data
public class HistoricTaskVo {

    private String id;

    private String name;

    private String key;

    private String description;

    private String executionId;

    private String assignee;

    private String owner;

    private String procDefId;

    private String procInstId;

    private String applyer;

    private String category;

    private Integer priority;

    private String deleteReason;

    private String comment;

    private Long duration;

    private Long workTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    private String processName;

    private String routeName;

    private String businessKey;

    private String tableId;

    public HistoricTaskVo(HistoricTaskInstance task){
        this.id = task.getId();
        this.name = task.getName();
        this.key = task.getTaskDefinitionKey();
        this.description = task.getDescription();
        this.executionId = task.getExecutionId();
        this.assignee = task.getAssignee();
        this.owner = task.getOwner();
        this.procDefId = task.getProcessDefinitionId();
        this.procInstId = task.getProcessInstanceId();
        this.priority = task.getPriority();
        this.category = task.getCategory();
        this.deleteReason = getMyDeleteReason(task.getDeleteReason());
        this.duration = task.getDurationInMillis();
        this.workTime = task.getWorkTimeInMillis();
        this.createTime = task.getCreateTime();
        this.startTime = task.getStartTime();
        this.endTime = task.getEndTime();
        this.dueTime = task.getDueDate();
    }

    public String getMyDeleteReason(String deleteReason){

        if("completed".equals(deleteReason)){
            deleteReason="审批通过";
        }else if("backed".equals(deleteReason)){
            deleteReason="审批驳回";
        }else if("deleted".equals(deleteReason)){
            deleteReason="已删除";
        }else{
            if(StrUtil.isNotBlank(deleteReason)){
                deleteReason="【删除原因】"+deleteReason;
            }
        }
        return deleteReason;
    }
}
