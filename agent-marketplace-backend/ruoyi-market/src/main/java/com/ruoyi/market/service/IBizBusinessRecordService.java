package com.ruoyi.market.service;

import java.util.List;
import com.ruoyi.market.domain.BizBusinessRecord;

public interface IBizBusinessRecordService
{
    BizBusinessRecord selectBusinessRecordById(Long recordId);

    List<BizBusinessRecord> selectBusinessRecordList(BizBusinessRecord record);

    int insertBusinessRecord(BizBusinessRecord record);

    int updateBusinessRecord(BizBusinessRecord record);

    int deleteBusinessRecordByIds(Long[] recordIds);
}
