package com.ruoyi.market.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.domain.BizAgentDetailItem;
import com.ruoyi.market.mapper.BizAgentMapper;
import com.ruoyi.market.service.IBizAgentService;

@Service
public class BizAgentServiceImpl implements IBizAgentService
{
    @Autowired
    private BizAgentMapper agentMapper;

    @Override
    public BizAgent selectAgentById(Long agentId)
    {
        BizAgent agent = agentMapper.selectAgentById(agentId);
        return fillDetailItems(agent);
    }

    @Override
    public BizAgent selectPublishedAgentById(Long agentId)
    {
        BizAgent agent = agentMapper.selectPublishedAgentById(agentId);
        return fillDetailItems(agent);
    }

    @Override
    public List<BizAgent> selectAgentList(BizAgent agent)
    {
        return agentMapper.selectAgentList(agent);
    }

    @Override
    public List<BizAgent> selectPublishedAgentList(BizAgent agent)
    {
        return agentMapper.selectPublishedAgentList(agent);
    }

    @Override
    @Transactional
    public int insertAgent(BizAgent agent)
    {
        normalizeDefaults(agent, true);
        int rows = agentMapper.insertAgent(agent);
        saveDetailItems(agent);
        return rows;
    }

    @Override
    @Transactional
    public int updateAgent(BizAgent agent)
    {
        normalizeDefaults(agent, false);
        int rows = agentMapper.updateAgent(agent);
        agentMapper.deleteDetailItemsByAgentId(agent.getAgentId());
        saveDetailItems(agent);
        return rows;
    }

    @Override
    @Transactional
    public int deleteAgentByIds(Long[] agentIds)
    {
        return agentMapper.softDeleteAgentByIds(agentIds);
    }

    private BizAgent fillDetailItems(BizAgent agent)
    {
        if (agent != null)
        {
            agent.setDetailItems(agentMapper.selectDetailItemsByAgentId(agent.getAgentId()));
        }
        return agent;
    }

    private void normalizeDefaults(BizAgent agent, boolean creating)
    {
        if (agent.getRecommendFlag() == null) agent.setRecommendFlag("N");
        if (agent.getPublishStatus() == null) agent.setPublishStatus("0");
        if (agent.getSortNo() == null) agent.setSortNo(0);
        if (agent.getHotScore() == null) agent.setHotScore(0);
        if (agent.getDeployCount() == null) agent.setDeployCount(0);
        if (creating && agent.getVersionNo() == null) agent.setVersionNo(1);
        if ("1".equals(agent.getPublishStatus()) && agent.getPublishedAt() == null)
        {
            agent.setPublishedAt(new Date());
        }
    }

    private void saveDetailItems(BizAgent agent)
    {
        List<BizAgentDetailItem> items = agent.getDetailItems();
        if (items == null) items = Collections.emptyList();
        int sort = 0;
        for (BizAgentDetailItem item : items)
        {
            item.setAgentId(agent.getAgentId());
            if (item.getSortNo() == null) item.setSortNo(sort);
            if (item.getStatus() == null) item.setStatus("0");
            sort++;
        }
        if (!items.isEmpty())
        {
            agentMapper.batchInsertDetailItems(items);
        }
    }
}
