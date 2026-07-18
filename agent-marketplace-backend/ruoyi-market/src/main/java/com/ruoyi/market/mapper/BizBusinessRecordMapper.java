package com.ruoyi.market.mapper;

import java.util.List;
import com.ruoyi.market.domain.BizBusinessRecord;

public interface BizBusinessRecordMapper
{
    BizBusinessRecord selectBusinessRecordById(Long recordId);

    List<BizBusinessRecord> selectBusinessRecordList(BizBusinessRecord record);

    int insertBusinessRecord(BizBusinessRecord record);

    int updateBusinessRecord(BizBusinessRecord record);

    int softDeleteBusinessRecordByIds(Long[] recordIds);
}
