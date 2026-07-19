<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2 class="page-title">数据统计</h2>
      <p class="page-subtitle">平台整体数据概览</p>
    </div>

    <div class="stats-section">
      <div class="stats-grid">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-users">
              <el-icon :size="28"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-teachers">
              <el-icon :size="28"><Avatar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.teacherCount }}</div>
              <div class="stat-label">教师总数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-parents">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.parentCount }}</div>
              <div class="stat-label">家长总数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-appointments">
              <el-icon :size="28"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.appointmentCount }}</div>
              <div class="stat-label">预约总数</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="charts-section">
      <div class="charts-grid">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">预约趋势</span>
            </div>
          </template>
          <div class="chart-wrapper">
            <v-chart class="chart" :option="trendChartOption" autoresize />
          </div>
        </el-card>

        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">学科分布</span>
            </div>
          </template>
          <div class="chart-wrapper">
            <v-chart class="chart" :option="subjectChartOption" autoresize />
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { UserFilled, Avatar, User, Tickets } from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import dayjs from 'dayjs'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const stats = reactive({
  userCount: 0,
  teacherCount: 0,
  parentCount: 0,
  appointmentCount: 0
})

const trendChartOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['预约数'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '10%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [] as string[]
  },
  yAxis: {
    type: 'value',
    minInterval: 1
  },
  series: [
    {
      name: '预约数',
      type: 'line',
      smooth: true,
      data: [] as number[],
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(6, 178, 181, 0.3)' },
            { offset: 1, color: 'rgba(6, 178, 181, 0.05)' }
          ]
        }
      },
      lineStyle: {
        color: '#06B2B5',
        width: 2
      },
      itemStyle: {
        color: '#06B2B5'
      }
    }
  ]
})

const subjectChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c}人 ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: '5%',
    top: 'center'
  },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 0, name: '语文', itemStyle: { color: '#06B2B5' } },
        { value: 0, name: '数学', itemStyle: { color: '#63D5D6' } },
        { value: 0, name: '英语', itemStyle: { color: '#3B82F6' } },
        { value: 0, name: '物理', itemStyle: { color: '#8B5CF6' } },
        { value: 0, name: '化学', itemStyle: { color: '#F5A623' } },
        { value: 0, name: '其他', itemStyle: { color: '#9CA3AF' } }
      ]
    }
  ]
})

function generateMockTrendData() {
  const days = []
  const counts = []
  for (let i = 6; i >= 0; i--) {
    days.push(dayjs().subtract(i, 'day').format('MM-DD'))
    counts.push(Math.floor(Math.random() * 20) + 5)
  }
  return { days, counts }
}

function generateMockSubjectData() {
  return [
    { value: 12, name: '语文' },
    { value: 18, name: '数学' },
    { value: 15, name: '英语' },
    { value: 8, name: '物理' },
    { value: 6, name: '化学' },
    { value: 4, name: '其他' }
  ]
}

function loadStats() {
  stats.userCount = 156
  stats.teacherCount = 63
  stats.parentCount = 93
  stats.appointmentCount = 248

  const trendData = generateMockTrendData()
  trendChartOption.value.xAxis.data = trendData.days
  trendChartOption.value.series[0].data = trendData.counts

  const subjectData = generateMockSubjectData()
  subjectChartOption.value.series[0].data = subjectData
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  .page-title {
    font-size: 22px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 4px 0;
  }

  .page-subtitle {
    font-size: 14px;
    color: $text-secondary;
    margin: 0;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 640px) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
  }

  .stat-users {
    background: linear-gradient(135deg, #06B2B5, #63D5D6);
  }

  .stat-teachers {
    background: linear-gradient(135deg, #3B82F6, #60A5FA);
  }

  .stat-parents {
    background: linear-gradient(135deg, #8B5CF6, #A78BFA);
  }

  .stat-appointments {
    background: linear-gradient(135deg, #F5A623, #FBBF24);
  }

  .stat-info {
    .stat-number {
      font-size: 28px;
      font-weight: 700;
      color: $text-primary;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: $text-secondary;
      margin-top: 4px;
    }
  }
}

.charts-section {
  .charts-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 16px;

    @media (max-width: 1024px) {
      grid-template-columns: 1fr;
    }
  }
}

.chart-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__header) {
    padding: 16px 20px;
  }

  :deep(.el-card__body) {
    padding: 16px 20px 20px;
  }
}

.chart-header {
  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
  }
}

.chart-wrapper {
  width: 100%;
  height: 320px;

  .chart {
    width: 100%;
    height: 100%;
  }
}
</style>
