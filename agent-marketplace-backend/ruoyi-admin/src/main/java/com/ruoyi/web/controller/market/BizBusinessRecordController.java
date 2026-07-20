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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.market.domain.BizBusinessRecord;
import com.ruoyi.market.service.IBizBusinessRecordService;

@Tag(name = "后台管理-商务登记")
@RestController
@RequestMapping("/market/business")
public class BizBusinessRecordController extends BaseController
{
    @Autowired
    private IBizBusinessRecordService businessRecordService;

    @Operation(summary = "查询商务登记列表")
    @PreAuthorize("@ss.hasPermi('market:business:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizBusinessRecord record)
    {
        startPage();
        List<BizBusinessRecord> list = businessRecordService.selectBusinessRecordList(record);
        return getDataTable(list);
    }

    @Operation(summary = "查询商务登记详情")
    @PreAuthorize("@ss.hasPermi('market:business:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId)
    {
        return success(businessRecordService.selectBusinessRecordById(recordId));
    }

    @Operation(summary = "新增商务登记")
    @PreAuthorize("@ss.hasPermi('market:business:add')")
    @Log(title = "商务登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BizBusinessRecord record)
    {
        record.setCreateBy(getUsername());
        if (record.getOwnerId() == null)
        {
            record.setOwnerId(getUserId());
        }
        return toAjax(businessRecordService.insertBusinessRecord(record));
    }

    @Operation(summary = "更新商务登记")
    @PreAuthorize("@ss.hasPermi('market:business:edit')")
    @Log(title = "商务登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BizBusinessRecord record)
    {
        record.setUpdateBy(getUsername());
        return toAjax(businessRecordService.updateBusinessRecord(record));
    }

    @Operation(summary = "删除商务登记")
    @PreAuthorize("@ss.hasPermi('market:business:remove')")
    @Log(title = "商务登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(businessRecordService.deleteBusinessRecordByIds(recordIds));
    }
}
