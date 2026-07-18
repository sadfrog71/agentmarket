package com.ruoyi.market.service;

import java.util.List;
import com.ruoyi.market.domain.BizAgent;

public interface IBizAgentService
{
    BizAgent selectAgentById(Long agentId);

    BizAgent selectPublishedAgentById(Long agentId);

    List<BizAgent> selectAgentList(BizAgent agent);

    List<BizAgent> selectPublishedAgentList(BizAgent agent);

    int insertAgent(BizAgent agent);

    int updateAgent(BizAgent agent);

    int deleteAgentByIds(Long[] agentIds);
}
