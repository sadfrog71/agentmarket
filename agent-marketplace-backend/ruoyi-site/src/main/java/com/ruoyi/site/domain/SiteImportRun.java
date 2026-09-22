package com.ruoyi.site.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 一次冻结来源快照对应一条导入批次；同一清单哈希不能被当作新批次重复写入。
 */
public class SiteImportRun extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String importRunId;
    private String manifestId;
    private String sourceSnapshotHash;
    private String manifestSha256;
    private String runStatus;
    private Date startedAt;
    private Date completedAt;
    private Integer totalItems;
    private Integer importedItems;
    private Integer skippedItems;
    private Integer excludedItems;
    private Integer conflictItems;
    private Integer failedItems;

    public String getImportRunId() { return importRunId; }
    public void setImportRunId(String importRunId) { this.importRunId = importRunId; }
    public String getManifestId() { return manifestId; }
    public void setManifestId(String manifestId) { this.manifestId = manifestId; }
    public String getSourceSnapshotHash() { return sourceSnapshotHash; }
    public void setSourceSnapshotHash(String sourceSnapshotHash) { this.sourceSnapshotHash = sourceSnapshotHash; }
    public String getManifestSha256() { return manifestSha256; }
    public void setManifestSha256(String manifestSha256) { this.manifestSha256 = manifestSha256; }
    public String getRunStatus() { return runStatus; }
    public void setRunStatus(String runStatus) { this.runStatus = runStatus; }
    public Date getStartedAt() { return startedAt; }
    public void setStartedAt(Date startedAt) { this.startedAt = startedAt; }
    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public Integer getImportedItems() { return importedItems; }
    public void setImportedItems(Integer importedItems) { this.importedItems = importedItems; }
    public Integer getSkippedItems() { return skippedItems; }
    public void setSkippedItems(Integer skippedItems) { this.skippedItems = skippedItems; }
    public Integer getExcludedItems() { return excludedItems; }
    public void setExcludedItems(Integer excludedItems) { this.excludedItems = excludedItems; }
    public Integer getConflictItems() { return conflictItems; }
    public void setConflictItems(Integer conflictItems) { this.conflictItems = conflictItems; }
    public Integer getFailedItems() { return failedItems; }
    public void setFailedItems(Integer failedItems) { this.failedItems = failedItems; }
}
