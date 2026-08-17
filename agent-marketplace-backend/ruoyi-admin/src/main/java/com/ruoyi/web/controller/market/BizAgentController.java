package com.ruoyi.web.controller.market;

import java.util.List;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.market.domain.BizAgent;
import com.ruoyi.market.domain.BizAgentCaseImportRow;
import com.ruoyi.market.service.IBizAgentService;
import com.ruoyi.common.utils.poi.ExcelUtil;

@Tag(name = "后台管理-智能体")
@RestController
@RequestMapping("/market/agent")
public class BizAgentController extends BaseController
{
    @Autowired
    private IBizAgentService agentService;

    @Operation(summary = "查询智能体管理列表")
    @PreAuthorize("@ss.hasPermi('market:agent:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAgent agent)
    {
        startPage();
        List<BizAgent> list = agentService.selectAgentList(agent);
        return getDataTable(list);
    }

    @Operation(summary = "查询智能体完整详情")
    @PreAuthorize("@ss.hasPermi('market:agent:query')")
    @GetMapping("/{agentId}")
    public AjaxResult getInfo(@PathVariable Long agentId)
    {
        return success(agentService.selectAgentById(agentId));
    }

    @Operation(summary = "新增智能体")
    @PreAuthorize("@ss.hasPermi('market:agent:add')")
    @Log(title = "智能体管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BizAgent agent)
    {
        agent.setCreateBy(getUsername());
        return toAjax(agentService.insertAgent(agent));
    }

    @Operation(summary = "更新并发布智能体")
    @PreAuthorize("@ss.hasPermi('market:agent:edit')")
    @Log(title = "智能体管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BizAgent agent)
    {
        agent.setUpdateBy(getUsername());
        return toAjax(agentService.updateAgent(agent));
    }

    @Operation(summary = "导入智能体部署案例")
    @PreAuthorize("@ss.hasPermi('market:agent:edit')")
    @Log(title = "智能体案例", businessType = BusinessType.IMPORT)
    @PostMapping("/{agentId}/case-import")
    public AjaxResult importCases(@PathVariable Long agentId, MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<BizAgentCaseImportRow> util = new ExcelUtil<>(BizAgentCaseImportRow.class);
        List<BizAgentCaseImportRow> rows = util.importExcel(file.getInputStream());
        return success(agentService.importCases(agentId, rows, updateSupport));
    }

    @Operation(summary = "下载智能体案例导入模板")
    @PreAuthorize("@ss.hasPermi('market:agent:edit')")
    @PostMapping("/case-import-template")
    public void importCaseTemplate(HttpServletResponse response)
    {
        ExcelUtil<BizAgentCaseImportRow> util = new ExcelUtil<>(BizAgentCaseImportRow.class);
        util.importTemplateExcel(response, "智能体部署案例");
    }

    @Operation(summary = "删除智能体")
    @PreAuthorize("@ss.hasPermi('market:agent:remove')")
    @Log(title = "智能体管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{agentIds}")
    public AjaxResult remove(@PathVariable Long[] agentIds)
    {
        return toAjax(agentService.deleteAgentByIds(agentIds));
    }
}
