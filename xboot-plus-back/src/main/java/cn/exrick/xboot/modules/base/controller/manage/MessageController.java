package cn.exrick.xboot.modules.base.controller.manage;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.base.entity.Message;
import cn.exrick.xboot.modules.base.entity.MessageSend;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.service.MessageSendService;
import cn.exrick.xboot.modules.base.service.MessageService;
import cn.exrick.xboot.modules.base.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "消息内容管理接口")
@RequestMapping("/xboot/message")
@Transactional
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageSendService sendService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/getByCondition",method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<Message>> getByCondition(@ModelAttribute Message message,
                                                @ModelAttribute SearchVo searchVo,
                                                @ModelAttribute PageVo pageVo){

        Page<Message> page = messageService.findByCondition(message, searchVo, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<Message>>().setData(page);
    }

    @RequestMapping(value = "/get/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<Message> get(@PathVariable String id){

        Message message = messageService.get(id);
        return new ResultUtil<Message>().setData(message);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加消息")
    public Result<Object> addMessage(@ModelAttribute Message message){

        Message m = messageService.save(message);
        // 保存消息发送表
        List<MessageSend> messageSends = new ArrayList<>();
        if(CommonConstant.MESSAGE_RANGE_ALL.equals(message.getRange())){
            // 全体
            List<User> allUser = userService.getAll();
            allUser.forEach(u->{
                MessageSend ms = new MessageSend();
                ms.setMessageId(m.getId());
                ms.setUserId(u.getId());
                messageSends.add(ms);
            });
            // 推送
            messagingTemplate.convertAndSend("/topic/subscribe", "您收到了新的系统消息");
        }else{
            // 指定用户
            for(String id:message.getUserIds()){
                MessageSend ms = new MessageSend();
                ms.setMessageId(m.getId());
                ms.setUserId(id);
                messageSends.add(ms);
                // 指定用户
                messagingTemplate.convertAndSendToUser(id,"/queue/subscribe", "您收到了新的消息");
            }
        }
        sendService.saveOrUpdateAll(messageSends);
        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "编辑消息")
    public Result<Object> editMessage(@ModelAttribute Message message){

        Message m = messageService.update(message);
        return new ResultUtil<Object>().setSuccessMsg("编辑成功");
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除消息")
    public Result<Object> delMessage(@PathVariable String[] ids){

        for(String id:ids){
            messageService.delete(id);
            // 删除发送表
            sendService.deleteByMessageId(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("编辑成功");
    }
}
