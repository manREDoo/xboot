package cn.exrick.xboot.modules.activiti.dao;

import cn.exrick.xboot.base.XbootBaseDao;
import cn.exrick.xboot.modules.activiti.entity.ActNode;

import java.util.List;

/**
 * 流程节点用户数据处理层
 * @author Exrick
 */
public interface ActNodeDao extends XbootBaseDao<ActNode,String> {

    /**
     * 通过nodeId获取
     * @param nodeId
     * @param type
     * @return
     */
    List<ActNode> findByNodeIdAndType(String nodeId, Integer type);

    /**
     * 通过nodeId删除
     * @param nodeId
     */
    void deleteByNodeId(String nodeId);
}