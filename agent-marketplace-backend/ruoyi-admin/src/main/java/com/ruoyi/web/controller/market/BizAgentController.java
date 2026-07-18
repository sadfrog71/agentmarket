package com.ruoyi.web.controller.market;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.service.IBizAgentService;

@RestController
@RequestMapping("/market/agent")
public class BizAgentController extends BaseController
{
    @Autowired
    private IBizAgentService agentService;

    @PreAuthorize("@ss.hasPermi('market:agent:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAgent agent)
    {
        startPage();
        List<BizAgent> list = agentService.selectAgentList(agent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('market:agent:query')")
    @GetMapping("/{agentId}")
    public AjaxResult getInfo(@PathVariable Long agentId)
    {
        return success(agentService.selectAgentById(agentId));
    }

    @PreAuthorize("@ss.hasPermi('market:agent:add')")
    @Log(title = "智能体管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BizAgent agent)
    {
        agent.setCreateBy(getUsername());
        return toAjax(agentService.insertAgent(agent));
    }

    @PreAuthorize("@ss.hasPermi('market:agent:edit')")
    @Log(title = "智能体管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BizAgent agent)
    {
        agent.setUpdateBy(getUsername());
        return toAjax(agentService.updateAgent(agent));
    }

    @PreAuthorize("@ss.hasPermi('market:agent:remove')")
    @Log(title = "智能体管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{agentIds}")
    public AjaxResult remove(@PathVariable Long[] agentIds)
    {
        return toAjax(agentService.deleteAgentByIds(agentIds));
    }
}
