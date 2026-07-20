package com.ruoyi.web.controller.open;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "前台公开接口-智能体")
@RestController
@RequestMapping("/open/agents")
public class OpenAgentController extends BaseController
{
    @Autowired
    private IBizAgentService agentService;

    @Operation(summary = "查询已发布智能体列表")
    @GetMapping
    public TableDataInfo list(BizAgent agent)
    {
        startPage();
        List<BizAgent> list = agentService.selectPublishedAgentList(agent);
        return getDataTable(list);
    }

    @Operation(summary = "查询已发布智能体详情")
    @GetMapping("/{agentId}")
    public AjaxResult detail(@PathVariable Long agentId)
    {
        BizAgent agent = agentService.selectPublishedAgentById(agentId);
        return agent == null ? error("智能体不存在或未发布") : success(agent);
    }
}
