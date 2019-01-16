package cn.exrick.xboot.modules.activiti.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.base.entity.Role;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.activiti.entity.ActNode;

import java.util.List;

/**
 * 流程节点用户接口
 * @author Exrick
 */
public interface ActNodeService extends XbootBaseService<ActNode,String> {

    /**
     * 通过nodeId获取关联用户
     * @param nodeId
     * @return
     */
    List<User> findUserByNodeId(String nodeId);

    /**
     * 通过nodeId获取关联角色
     * @param nodeId
     * @return
     */
    List<Role> findRoleByNodeId(String nodeId);

    /**
     * 通过nodeId删除
     * @param nodeId
     */
    void deleteByNodeId(String nodeId);
}