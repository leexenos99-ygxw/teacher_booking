<template>
  <div class="appointment-manage-container">
    <div class="page-header">
      <h2 class="page-title">预约管理</h2>
      <p class="page-subtitle">查看和管理您的所有预约记录</p>
    </div>

    <el-card class="appointment-card" shadow="never">
      <el-tabs v-model="activeTab" class="appointment-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="ALL" />
        <el-tab-pane label="待确认" name="PENDING" />
        <el-tab-pane label="已接受" name="ACCEPTED" />
        <el-tab-pane label="已完成" name="COMPLETED" />
        <el-tab-pane label="已取消" name="CANCELLED" />
      </el-tabs>

      <el-empty v-if="appointmentList.length === 0" description="暂无预约记录" />

      <div v-else class="appointment-list">
        <div
          v-for="appointment in appointmentList"
          :key="appointment.id"
          class="appointment-item"
        >
          <div class="appointment-header">
            <div class="appointment-no">
              <span class="label">预约编号：</span>
              <span class="value">{{ appointment.appointmentNo }}</span>
            </div>
            <el-tag :type="getStatusType(appointment.status)" size="small">
              {{ getStatusText(appointment.status) }}
            </el-tag>
          </div>

          <div class="appointment-body">
            <div class="appointment-info-row">
              <div class="info-item">
                <el-icon color="#06B2B5"><Calendar /></el-icon>
                <span>{{ formatDateTime(appointment.startTime) }}</span>
              </div>
              <div class="info-item">
                <el-icon color="#06B2B5"><Clock /></el-icon>
                <span>{{ appointment.duration }}分钟</span>
              </div>
            </div>

            <div v-if="appointment.reason" class="appointment-reason">
              <span class="reason-label">预约原因：</span>
              <span class="reason-text">{{ appointment.reason }}</span>
            </div>

            <div v-if="appointment.rejectReason" class="appointment-reason reject">
              <span class="reason-label">拒绝原因：</span>
              <span class="reason-text">{{ appointment.rejectReason }}</span>
            </div>
          </div>

          <div class="appointment-footer">
            <div class="appointment-time">
              创建时间：{{ formatDateTime(appointment.createdTime) }}
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
              <el-button
                v-if="appointment.status === 'ACCEPTED'"
                type="success"
                size="small"
                @click="handleFinish(appointment.id)"
              >
                完成
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Clock } from '@element-plus/icons-vue'
import {
  getTeacherAppointments,
  acceptAppointment,
  rejectAppointment,
  finishAppointment
} from '@/api/appointment'
import type { Appointment } from '@/types'
import dayjs from 'dayjs'

const router = useRouter()

const activeTab = ref('ALL')
const appointmentList = ref<Appointment[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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

function formatDateTime(time: string) {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

function handleTabChange() {
  currentPage.value = 1
  loadAppointments()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadAppointments()
}

function handleCurrentChange(page: number) {
  currentPage.value = page
  loadAppointments()
}

async function handleAccept(id: number) {
  try {
    await acceptAppointment(id)
    ElMessage.success('已接受预约')
    loadAppointments()
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
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Reject appointment failed:', error)
    }
  }
}

async function handleFinish(id: number) {
  try {
    await ElMessageBox.confirm('确定要完成这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await finishAppointment(id)
    ElMessage.success('预约已完成')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Finish appointment failed:', error)
    }
  }
}

function goToChat(appointmentId: number) {
  router.push(`/chat/${appointmentId}`)
}

async function loadAppointments() {
  try {
    const params: {
      page: number
      size: number
      status?: string
    } = {
      page: currentPage.value,
      size: pageSize.value
    }

    if (activeTab.value !== 'ALL') {
      params.status = activeTab.value
    }

    const data = await getTeacherAppointments(params)
    appointmentList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('Load appointments failed:', error)
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped lang="scss">
.appointment-manage-container {
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

.appointment-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 0;
  }
}

.appointment-tabs {
  padding: 0 20px;
  border-bottom: 1px solid $border-color;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }
}

.appointment-list {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appointment-item {
  border: 1px solid $border-color;
  border-radius: 10px;
  padding: 16px 20px;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
}

.appointment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid $border-color;

  .appointment-no {
    .label {
      font-size: 13px;
      color: $text-secondary;
    }

    .value {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
    }
  }
}

.appointment-body {
  margin-bottom: 12px;
}

.appointment-info-row {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;
  flex-wrap: wrap;

  .info-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: $text-primary;
  }
}

.appointment-reason {
  display: flex;
  font-size: 13px;
  line-height: 1.6;

  &.reject {
    .reason-label {
      color: $danger-color;
    }
  }

  .reason-label {
    color: $text-secondary;
    flex-shrink: 0;
  }

  .reason-text {
    color: $text-primary;
    flex: 1;
  }
}

.appointment-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid $border-color;
  gap: 12px;

  @media (max-width: 640px) {
    flex-direction: column;
    align-items: flex-start;
  }

  .appointment-time {
    font-size: 12px;
    color: $text-placeholder;
  }

  .appointment-actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
}

.pagination-wrapper {
  padding: 16px 20px 20px;
  display: flex;
  justify-content: center;
}
</style>
