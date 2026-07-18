package com.ruoyi.market.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.domain.BizAgentDetailItem;

public interface BizAgentMapper
{
    BizAgent selectAgentById(Long agentId);

    BizAgent selectPublishedAgentById(Long agentId);

    List<BizAgent> selectAgentList(BizAgent agent);

    List<BizAgent> selectPublishedAgentList(BizAgent agent);

    List<BizAgentDetailItem> selectDetailItemsByAgentId(Long agentId);

    int insertAgent(BizAgent agent);

    int updateAgent(BizAgent agent);

    int softDeleteAgentByIds(Long[] agentIds);

    int deleteDetailItemsByAgentId(Long agentId);

    int batchInsertDetailItems(@Param("items") List<BizAgentDetailItem> items);
}
