export const seedAgents = [
  {
    agentId: 1,
    agentCode: 'DMA_LEAKAGE',
    agentName: 'DMA漏损分析智能体',
    categoryCode: 'network',
    providerName: '华衍水务研究院',
    summary: '基于 DMA 分区计量数据自动识别漏损异常，定位漏点区域，并给出修复优先级。',
    description: '结合管网拓扑、压力与流量数据，形成持续监测、异常解释、处置建议和效果报告。',
    priceText: '5万-8万',
    certLevel: 'L2',
    rating: 4.8,
    deployCount: 18,
    deliveryCycle: '2-4周',
    serviceMode: '本地化部署',
    recommendFlag: 'Y',
    detailItems: [
      { itemType: 'FEATURE', title: '实时漏损监测', content: '7×24 小时持续监测各 DMA 分区夜间最小流量，异常时即时提示。' },
      { itemType: 'FEATURE', title: '漏点区域精准定位', content: '结合管网拓扑与压力数据缩小排查范围，减少无效开挖。' },
      { itemType: 'METRIC', title: '漏损率平均降幅', valueText: '34%', content: '从 13.2% 降至 8.7%。' },
      { itemType: 'METRIC', title: '已部署水司', valueText: '18家', content: '包含 3 家 L2 实测验证单位。' },
      { itemType: 'CASE', title: '华衍（无锡）水务有限公司', valueText: '产销差率降低 34%', content: '覆盖 48 个 DMA 分区，部署后 6 个月产销差率从 14.1% 降至 9.3%。' },
      { itemType: 'PRICE_FEATURE', title: '本地化部署', content: '数据不出水司。' },
      { itemType: 'COMPATIBILITY', title: '数据接口', valueText: 'SCADA / OPC-UA' },
      { itemType: 'COMPATIBILITY', title: '操作系统', valueText: 'Windows / Linux' }
    ]
  },
  { agentId: 2, agentCode: 'WATER_FORECAST', agentName: '水量预测智能体', categoryCode: 'production', providerName: '华衍水务研究院', summary: '融合气象、节假日和历史用水数据，提前预测供水需求并辅助调度。', priceText: '6万-9万', certLevel: 'L2', rating: 4.9, deployCount: 12, deliveryCycle: '2-3周', serviceMode: '标准实施', recommendFlag: 'Y', detailItems: [] },
  { agentId: 3, agentCode: 'QUALITY_WARNING', agentName: '水质异常预警智能体', categoryCode: 'quality', providerName: '威派格科技', summary: '实时监测水质多维参数，识别异常模式并提前推送处置建议。', priceText: '8万-12万', certLevel: 'L2', rating: 4.7, deployCount: 9, deliveryCycle: '3-5周', serviceMode: '定制实施', recommendFlag: 'Y', detailItems: [] },
  { agentId: 4, agentCode: 'SERVICE_ASSISTANT', agentName: '智能客服助手', categoryCode: 'customer', providerName: '和达科技', summary: '自动处理报修、查账和投诉，复杂问题转人工跟进。', priceText: '3万-5万', certLevel: 'L2', rating: 4.6, deployCount: 15, deliveryCycle: '1-3周', serviceMode: '标准实施', recommendFlag: 'Y', detailItems: [] }
]
