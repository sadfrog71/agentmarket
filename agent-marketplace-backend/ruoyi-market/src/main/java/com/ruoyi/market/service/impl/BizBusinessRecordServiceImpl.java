package com.ruoyi.market.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.market.domain.BizBusinessRecord;
import com.ruoyi.market.mapper.BizBusinessRecordMapper;
import com.ruoyi.market.service.IBizBusinessRecordService;

@Service
public class BizBusinessRecordServiceImpl implements IBizBusinessRecordService
{
    @Autowired
    private BizBusinessRecordMapper businessRecordMapper;

    @Override
    public BizBusinessRecord selectBusinessRecordById(Long recordId)
    {
        return businessRecordMapper.selectBusinessRecordById(recordId);
    }

    @Override
    public List<BizBusinessRecord> selectBusinessRecordList(BizBusinessRecord record)
    {
        return businessRecordMapper.selectBusinessRecordList(record);
    }

    @Override
    public int insertBusinessRecord(BizBusinessRecord record)
    {
        if (StringUtils.isEmpty(record.getRecordNo()))
        {
            record.setRecordNo("BR" + DateUtils.dateTimeNow() + IdUtils.fastSimpleUUID().substring(0, 4).toUpperCase());
        }
        if (StringUtils.isEmpty(record.getSourceType()))
        {
            record.setSourceType("OFFLINE");
        }
        if (StringUtils.isEmpty(record.getRecordStatus()))
        {
            record.setRecordStatus("PENDING");
        }
        return businessRecordMapper.insertBusinessRecord(record);
    }

    @Override
    public int updateBusinessRecord(BizBusinessRecord record)
    {
        return businessRecordMapper.updateBusinessRecord(record);
    }

    @Override
    public int deleteBusinessRecordByIds(Long[] recordIds)
    {
        return businessRecordMapper.softDeleteBusinessRecordByIds(recordIds);
    }
}
