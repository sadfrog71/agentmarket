package com.ruoyi.market.service;

import java.util.List;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.domain.BizAgentCaseImportRow;

public interface IBizAgentService
{
    BizAgent selectAgentById(Long agentId);

    BizAgent selectPublishedAgentById(Long agentId);

    List<BizAgent> selectAgentList(BizAgent agent);

    List<BizAgent> selectPublishedAgentList(BizAgent agent);

    int insertAgent(BizAgent agent);

    int updateAgent(BizAgent agent);

    String importCases(Long agentId, List<BizAgentCaseImportRow> rows, boolean updateSupport);

    int deleteAgentByIds(Long[] agentIds);
}
