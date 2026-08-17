package com.ruoyi.market.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.domain.BizAgentCaseImportRow;
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
        validateAgent(agent);
        int rows = agentMapper.insertAgent(agent);
        saveDetailItems(agent);
        return rows;
    }

    @Override
    @Transactional
    public int updateAgent(BizAgent agent)
    {
        normalizeDefaults(agent, false);
        validateAgent(agent);
        int rows = agentMapper.updateAgent(agent);
        agentMapper.deleteDetailItemsByAgentId(agent.getAgentId());
        saveDetailItems(agent);
        return rows;
    }

    @Override
    @Transactional
    public String importCases(Long agentId, List<BizAgentCaseImportRow> rows, boolean updateSupport)
    {
        BizAgent agent = agentMapper.selectAgentById(agentId);
        if (agent == null)
        {
            throw new IllegalArgumentException("智能体不存在或已删除");
        }
        if (rows == null || rows.isEmpty())
        {
            throw new IllegalArgumentException("Excel 中没有可导入的案例数据");
        }

        List<String> errors = new ArrayList<>();
        Set<String> importedTitles = new HashSet<>();
        List<BizAgentCaseImportRow> normalizedRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++)
        {
            BizAgentCaseImportRow row = rows.get(i);
            int rowNumber = i + 2;
            String title = trim(row.getTitle());
            String content = trim(row.getContent());
            if (StringUtils.isEmpty(title))
            {
                errors.add("第" + rowNumber + "行：案例标题不能为空");
            }
            if (StringUtils.isEmpty(content))
            {
                errors.add("第" + rowNumber + "行：案例说明不能为空");
            }
            if (!StringUtils.isEmpty(title) && !importedTitles.add(title))
            {
                errors.add("第" + rowNumber + "行：案例标题在 Excel 中重复");
            }
            row.setTitle(title);
            row.setValueText(trim(row.getValueText()));
            row.setContent(content);
            normalizedRows.add(row);
        }
        if (!errors.isEmpty())
        {
            throw new IllegalArgumentException("案例导入失败：" + String.join("；", errors));
        }

        List<BizAgentDetailItem> currentItems = agentMapper.selectDetailItemsByAgentId(agentId);
        Map<String, BizAgentDetailItem> existingCases = new HashMap<>();
        int maxSort = 0;
        for (BizAgentDetailItem item : currentItems)
        {
            if ("CASE".equals(item.getItemType()))
            {
                existingCases.put(trim(item.getTitle()), item);
                if (item.getSortNo() != null && item.getSortNo() > maxSort)
                {
                    maxSort = item.getSortNo();
                }
            }
        }

        List<BizAgentDetailItem> inserts = new ArrayList<>();
        int updated = 0;
        int skipped = 0;
        for (BizAgentCaseImportRow row : normalizedRows)
        {
            BizAgentDetailItem existing = existingCases.get(row.getTitle());
            if (existing != null)
            {
                if (!updateSupport)
                {
                    skipped++;
                    continue;
                }
                existing.setValueText(row.getValueText());
                existing.setContent(row.getContent());
                existing.setSortNo(row.getSortNo() == null ? existing.getSortNo() : row.getSortNo());
                existing.setStatus("0");
                agentMapper.updateDetailItem(existing);
                updated++;
                continue;
            }

            BizAgentDetailItem item = new BizAgentDetailItem();
            item.setAgentId(agentId);
            item.setItemType("CASE");
            item.setTitle(row.getTitle());
            item.setValueText(row.getValueText());
            item.setContent(row.getContent());
            item.setSortNo(row.getSortNo() == null ? ++maxSort : row.getSortNo());
            item.setStatus("0");
            inserts.add(item);
            existingCases.put(row.getTitle(), item);
        }
        if (!inserts.isEmpty())
        {
            agentMapper.batchInsertDetailItems(inserts);
        }
        return "导入完成：新增 " + inserts.size() + " 条，更新 " + updated + " 条，跳过重复 " + skipped + " 条";
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

    private void validateAgent(BizAgent agent)
    {
        require(agent.getAgentCode(), "智能体编码不能为空");
        require(agent.getAgentName(), "智能体名称不能为空");
        require(agent.getCategoryCode(), "场景域不能为空");
        require(agent.getProviderName(), "服务商不能为空");
        require(agent.getSummary(), "卡片摘要不能为空");
        require(agent.getDescription(), "详细说明不能为空");
        require(agent.getCertLevel(), "认证等级不能为空");
        require(agent.getPublishStatus(), "发布状态不能为空");
        if (agent.getDetailItems() == null)
        {
            return;
        }
        for (BizAgentDetailItem item : agent.getDetailItems())
        {
            require(item.getItemType(), "详情项类型不能为空");
            require(item.getTitle(), "详情项标题不能为空");
            if ("FEATURE".equals(item.getItemType()) || "CASE".equals(item.getItemType()) || "PRICE_FEATURE".equals(item.getItemType()))
            {
                require(item.getContent(), item.getItemType().equals("CASE") ? "案例说明不能为空" : "详情项内容不能为空");
            }
            if ("METRIC".equals(item.getItemType()) || "COMPATIBILITY".equals(item.getItemType()))
            {
                require(item.getValueText(), "指标值或字段值不能为空");
            }
        }
    }

    private void require(String value, String message)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException(message);
        }
    }

    private String trim(String value)
    {
        return value == null ? null : value.trim();
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
