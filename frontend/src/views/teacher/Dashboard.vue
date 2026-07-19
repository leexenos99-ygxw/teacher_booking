<template>
  <div class="dashboard-container">
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>您好，{{ teacherInfo.realName || userStore.userInfo?.nickname || '老师' }}！</h2>
          <p>欢迎来到教师交流预约管理系统，高效管理您的预约日程</p>
        </div>
        <div class="welcome-icon">
          <el-icon :size="64" color="#fff"><Sunny /></el-icon>
        </div>
      </div>
    </el-card>

    <div class="stats-section">
      <h3 class="section-title">数据统计</h3>
      <div class="stats-grid">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-today">
              <el-icon :size="28"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.todayCount }}</div>
              <div class="stat-label">今日预约</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-pending">
              <el-icon :size="28"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.pendingCount }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-completed">
              <el-icon :size="28"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.completedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-rating">
              <el-icon :size="28"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.reviewCount }}</div>
              <div class="stat-label">总评价</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="recent-section">
      <div class="section-header">
        <h3 class="section-title">最近预约</h3>
        <el-button type="primary" text @click="goToAppointments">查看全部</el-button>
      </div>
      <el-card class="appointment-list-card" shadow="never">
        <el-empty v-if="recentAppointments.length === 0" description="暂无预约记录" />
        <div v-else class="appointment-list">
          <div
            v-for="appointment in recentAppointments"
            :key="appointment.id"
            class="appointment-item"
          >
            <div class="appointment-info">
              <div class="appointment-time">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDateTime(appointment.startTime) }}</span>
              </div>
              <div class="appointment-duration">
                时长：{{ appointment.duration }}分钟
              </div>
            </div>
            <div class="appointment-status">
              <el-tag :type="getStatusType(appointment.status)" size="small">
                {{ getStatusText(appointment.status) }}
              </el-tag>
            </div>
            <div class="appointment-actions">
              <el-button
                v-if="appointment.status === 'PENDING'"
                type="primary"
                size="small"
                @click="handleAccept(appointment.id)"
              >
                接受
              </el-button>
              <el-button
                v-if="appointment.status === 'PENDING'"
                type="danger"
                size="small"
                text
                @click="handleReject(appointment.id)"
              >
                拒绝
              </el-button>
              <el-button
                v-if="appointment.status === 'ACCEPTED'"
                type="primary"
                size="small"
                @click="goToChat(appointment.id)"
              >
                开始沟通
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Sunny, Calendar, Clock, CircleCheck, Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getTeacherProfile } from '@/api/teacher'
import { getTeacherAppointments, acceptAppointment, rejectAppointment } from '@/api/appointment'
import type { Teacher, Appointment } from '@/types'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const teacherInfo = ref<Partial<Teacher>>({})
const recentAppointments = ref<Appointment[]>([])

const stats = reactive({
  todayCount: 0,
  pendingCount: 0,
  completedCount: 0,
  reviewCount: 0
})

function formatDateTime(time: string) {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

function getStatusType(status: string) {
  const typeMap: Record<string, string> = {
    PENDING: 'warning',
    ACCEPTED: 'primary',
    IN_PROGRESS: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info',
    REJECTED: 'danger'
  }
  return typeMap[status] || 'info'
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    PENDING: '待确认',
    ACCEPTED: '已接受',
    IN_PROGRESS: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REJECTED: '已拒绝'
  }
  return textMap[status] || status
}

async function handleAccept(id: number) {
  try {
    await acceptAppointment(id)
    ElMessage.success('已接受预约')
    loadRecentAppointments()
  } catch (error) {
    console.error('Accept appointment failed:', error)
  }
}

async function handleReject(id: number) {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请输入拒绝原因...'
    })
    await rejectAppointment(id, reason)
    ElMessage.success('已拒绝预约')
    loadRecentAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Reject appointment failed:', error)
    }
  }
}

function goToAppointments() {
  router.push('/teacher/appointments')
}

function goToChat(appointmentId: number) {
  router.push(`/chat/${appointmentId}`)
}

async function loadTeacherInfo() {
  try {
    const data = await getTeacherProfile()
    teacherInfo.value = data
    stats.reviewCount = data.reviewCount || 0
  } catch (error) {
    console.error('Load teacher profile failed:', error)
  }
}

async function loadRecentAppointments() {
  try {
    const data = await getTeacherAppointments({ page: 1, size: 5 })
    recentAppointments.value = data.records

    const today = dayjs().format('YYYY-MM-DD')
    stats.todayCount = data.records.filter((a: Appointment) =>
      dayjs(a.startTime).format('YYYY-MM-DD') === today
    ).length
    stats.pendingCount = data.records.filter((a: Appointment) => a.status === 'PENDING').length
    stats.completedCount = data.records.filter((a: Appointment) => a.status === 'COMPLETED').length
  } catch (error) {
    console.error('Load appointments failed:', error)
  }
}

onMounted(() => {
  loadTeacherInfo()
  loadRecentAppointments()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.welcome-card {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, #06B2B5 0%, #63D5D6 100%);

  :deep(.el-card__body) {
    padding: 32px;
  }
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .welcome-text {
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #fff;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.9);
      margin: 0;
    }
  }

  .welcome-icon {
    opacity: 0.9;
  }
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.stats-section {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-top: 16px;

    @media (max-width: 1024px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 640px) {
      grid-template-columns: 1fr;
    }
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

  .stat-today {
    background: linear-gradient(135deg, #06B2B5, #63D5D6);
  }

  .stat-pending {
    background: linear-gradient(135deg, #F5A623, #FBBF24);
  }

  .stat-completed {
    background: linear-gradient(135deg, #22C55E, #4ADE80);
  }

  .stat-rating {
    background: linear-gradient(135deg, #8B5CF6, #A78BFA);
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

.appointment-list-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 0;
  }
}

.appointment-list {
  .appointment-item {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid $border-color;
    gap: 16px;

    &:last-child {
      border-bottom: none;
    }

    @media (max-width: 768px) {
      flex-wrap: wrap;
    }
  }
}

.appointment-info {
  flex: 1;
  min-width: 0;

  .appointment-time {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 15px;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: 4px;
  }

  .appointment-duration {
    font-size: 13px;
    color: $text-secondary;
    padding-left: 22px;
  }
}

.appointment-status {
  flex-shrink: 0;
}

.appointment-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
</style>
