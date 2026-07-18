package com.ruoyi.web.controller.open;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.service.IBizAgentService;

@Anonymous
@RestController
@RequestMapping("/open/agents")
public class OpenAgentController extends BaseController
{
    @Autowired
    private IBizAgentService agentService;

    @GetMapping
    public TableDataInfo list(BizAgent agent)
    {
        startPage();
        List<BizAgent> list = agentService.selectPublishedAgentList(agent);
        return getDataTable(list);
    }

    @GetMapping("/{agentId}")
    public AjaxResult detail(@PathVariable Long agentId)
    {
        BizAgent agent = agentService.selectPublishedAgentById(agentId);
        return agent == null ? error("智能体不存在或未发布") : success(agent);
    }
}
