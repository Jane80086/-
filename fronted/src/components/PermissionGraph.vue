<template>
  <div ref="chart" style="width:100%;height:400px;"></div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { roles } from '@/config/roles'
const chart = ref(null)
onMounted(() => {
  const myChart = echarts.init(chart.value)
  const data = Object.entries(roles).map(([key, value]) => ({
    name: value.name,
    children: value.permissions.map(p => ({ name: p }))
  }))
  myChart.setOption({
    tooltip: {},
    series: [{
      type: 'tree',
      data: data,
      top: '5%',
      left: '20%',
      bottom: '2%',
      right: '20%',
      symbolSize: 10,
      label: { position: 'left', verticalAlign: 'middle', align: 'right', fontSize: 14 },
      leaves: { label: { position: 'right', verticalAlign: 'middle', align: 'left' } },
      expandAndCollapse: true,
      animationDuration: 550,
      animationDurationUpdate: 750
    }]
  })
})
</script> 