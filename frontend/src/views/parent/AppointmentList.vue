<template>
  <div class="appointment-list-container">
    <el-card class="tabs-card" shadow="never">
      <el-tabs v-model="activeTab" class="appointment-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="ALL" />
        <el-tab-pane label="待确认" name="PENDING" />
        <el-tab-pane label="已接受" name="ACCEPTED" />
        <el-tab-pane label="已完成" name="COMPLETED" />
        <el-tab-pane label="已取消" name="CANCELLED" />
      </el-tabs>
    </el-card>

    <div class="appointment-cards">
      <el-card
        v-for="appointment in filteredAppointments"
        :key="appointment.id"
        class="appointment-card"
        shadow="never"
      >
        <div class="card-header">
          <div class="teacher-info">
            <el-avatar :size="48" :src="appointment.teacherAvatar">
              {{ appointment.teacherName?.charAt(0) }}
            </el-avatar>
            <div class="teacher-detail">
              <div class="teacher-name">
                {{ appointment.teacherName }}
                <el-tag size="small" type="primary">{{ appointment.subject }}</el-tag>
              </div>
              <div class="school-info">
                <el-icon><School /></el-icon>
                <span>{{ appointment.school }}</span>
              </div>
            </div>
          </div>
          <el-tag :type="getStatusType(appointment.status)" class="status-tag">
            {{ getStatusText(appointment.status) }}
          </el-tag>
        </div>

        <div class="appointment-info">
          <div class="info-row">
            <el-icon color="#06B2B5"><Calendar /></el-icon>
            <span class="info-label">预约时间：</span>
            <span class="info-value">{{ appointment.startTime }} - {{ appointment.endTime }}</span>
          </div>
          <div class="info-row">
            <el-icon color="#06B2B5"><Document /></el-icon>
            <span class="info-label">预约原因：</span>
            <span class="info-value">{{ appointment.reason || '无' }}</span>
          </div>
          <div class="info-row" v-if="appointment.rejectReason">
            <el-icon color="#EF4444"><Warning /></el-icon>
            <span class="info-label">拒绝原因：</span>
            <span class="info-value reject-text">{{ appointment.rejectReason }}</span>
          </div>
          <div class="info-row" v-if="appointment.cancelReason">
            <el-icon color="#F5A623"><InfoFilled /></el-icon>
            <span class="info-label">取消原因：</span>
            <span class="info-value">{{ appointment.cancelReason }}</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="appointment-no">预约编号：{{ appointment.appointmentNo }}</div>
          <div class="action-buttons">
            <el-button
              v-if="appointment.status === 'PENDING'"
              type="danger"
              text
              size="small"
              @click="handleCancel(appointment)"
            >
              取消预约
            </el-button>
            <el-button
              v-if="appointment.status === 'ACCEPTED'"
              type="primary"
              text
              size="small"
              @click="handleChat(appointment)"
            >
              <el-icon><ChatDotRound /></el-icon>
              在线沟通
            </el-button>
            <el-button
              v-if="appointment.status === 'COMPLETED' && !appointment.hasEvaluated"
              type="primary"
              text
              size="small"
              @click="handleEvaluate(appointment)"
            >
              去评价
            </el-button>
            <el-button
              v-if="appointment.status === 'COMPLETED' && appointment.hasEvaluated"
              type="success"
              text
              size="small"
              disabled
            >
              已评价
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div v-if="filteredAppointments.length === 0 && !loading" class="empty-state">
      <el-empty :description="getEmptyText()" :image-size="100" />
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 15, 20]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="cancelDialogVisible" title="取消预约" width="420px">
      <el-form ref="cancelFormRef" :model="cancelForm" :rules="cancelRules">
        <el-form-item label="取消原因" prop="cancelReason">
          <el-input
            v-model="cancelForm.cancelReason"
            type="textarea"
            :rows="3"
            placeholder="请填写取消原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">再想想</el-button>
        <el-button type="danger" :loading="canceling" @click="confirmCancel">确认取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="evaluateDialogVisible" title="评价教师" width="480px">
      <div class="evaluate-content">
        <div class="evaluate-teacher">
          <el-avatar :size="56" :src="currentAppointment?.teacherAvatar">
            {{ currentAppointment?.teacherName?.charAt(0) }}
          </el-avatar>
          <span class="teacher-name">{{ currentAppointment?.teacherName }}</span>
        </div>
        <el-form ref="evaluateFormRef" :model="evaluateForm" :rules="evaluateRules">
          <el-form-item label="评分" prop="rating">
            <el-rate v-model="evaluateForm.rating" :max="5" show-text />
          </el-form-item>
          <el-form-item label="评价内容" prop="content">
            <el-input
              v-model="evaluateForm.content"
              type="textarea"
              :rows="4"
              placeholder="请分享您的预约体验"
            />
          </el-form-item>
          <el-form-item label="标签">
            <el-checkbox-group v-model="evaluateForm.tags">
              <el-checkbox label="讲解清晰" />
              <el-checkbox label="耐心负责" />
              <el-checkbox label="专业度高" />
              <el-checkbox label="沟通顺畅" />
              <el-checkbox label="效果显著" />
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="evaluateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="evaluating" @click="confirmEvaluate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  School,
  Calendar,
  Document,
  Warning,
  InfoFilled,
  ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()

const activeTab = ref('ALL')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

interface AppointmentItem {
  id: number
  appointmentNo: string
  teacherId: number
  teacherName: string
  teacherAvatar: string
  subject: string
  school: string
  startTime: string
  endTime: string
  status: string
  reason: string
  rejectReason: string
  cancelReason: string
  hasEvaluated: boolean
}

const appointmentList = ref<AppointmentItem[]>([])

const mockAppointments: AppointmentItem[] = [
  {
    id: 1,
    appointmentNo: 'YY202407150001',
    teacherId: 1,
    teacherName: '张老师',
    teacherAvatar: '',
    subject: '数学',
    school: '第一中学',
    startTime: '2024-07-20 09:00',
    endTime: '10:00',
    status: 'PENDING',
    reason: '孩子数学成绩不理想，希望老师能帮助分析薄弱环节并给出提升建议。',
    rejectReason: '',
    cancelReason: '',
    hasEvaluated: false
  },
  {
    id: 2,
    appointmentNo: 'YY202407140002',
    teacherId: 2,
    teacherName: '李老师',
    teacherAvatar: '',
    subject: '英语',
    school: '第二中学',
    startTime: '2024-07-18 14:00',
    endTime: '15:00',
    status: 'ACCEPTED',
    reason: '英语口语薄弱，想提升听说能力。',
    rejectReason: '',
    cancelReason: '',
    hasEvaluated: false
  },
  {
    id: 3,
    appointmentNo: 'YY202407100003',
    teacherId: 3,
    teacherName: '王老师',
    teacherAvatar: '',
    subject: '语文',
    school: '实验中学',
    startTime: '2024-07-12 10:00',
    endTime: '11:00',
    status: 'COMPLETED',
    reason: '作文写作技巧需要提升。',
    rejectReason: '',
    cancelReason: '',
    hasEvaluated: true
  },
  {
    id: 4,
    appointmentNo: 'YY202407080004',
    teacherId: 4,
    teacherName: '赵老师',
    teacherAvatar: '',
    subject: '物理',
    school: '第一中学',
    startTime: '2024-07-10 15:00',
    endTime: '16:00',
    status: 'COMPLETED',
    reason: '物理力学部分掌握不好。',
    rejectReason: '',
    cancelReason: '',
    hasEvaluated: false
  },
  {
    id: 5,
    appointmentNo: 'YY202407050005',
    teacherId: 5,
    teacherName: '陈老师',
    teacherAvatar: '',
    subject: '化学',
    school: '第三中学',
    startTime: '2024-07-08 09:00',
    endTime: '10:00',
    status: 'CANCELLED',
    reason: '化学方程式配平有困难。',
    rejectReason: '',
    cancelReason: '孩子临时有事，时间冲突了。',
    hasEvaluated: false
  },
  {
    id: 6,
    appointmentNo: 'YY202407030006',
    teacherId: 6,
    teacherName: '刘老师',
    teacherAvatar: '',
    subject: '历史',
    school: '实验中学',
    startTime: '2024-07-06 14:00',
    endTime: '15:00',
    status: 'CANCELLED',
    reason: '历史大题答题技巧需要指导。',
    rejectReason: '老师当天临时有教研会议，无法接待。',
    cancelReason: '',
    hasEvaluated: false
  }
]

const filteredAppointments = computed(() => {
  if (activeTab.value === 'ALL') {
    return appointmentList.value
  }
  return appointmentList.value.filter(a => a.status === activeTab.value)
})

const cancelDialogVisible = ref(false)
const canceling = ref(false)
const cancelFormRef = ref<FormInstance>()
const currentAppointment = ref<AppointmentItem | null>(null)

const cancelForm = reactive({
  cancelReason: ''
})

const cancelRules: FormRules = {
  cancelReason: [{ required: true, message: '请填写取消原因', trigger: 'blur' }]
}

const evaluateDialogVisible = ref(false)
const evaluating = ref(false)
const evaluateFormRef = ref<FormInstance>()

const evaluateForm = reactive({
  rating: 5,
  content: '',
  tags: [] as string[]
})

const evaluateRules: FormRules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [{ required: true, message: '请填写评价内容', trigger: 'blur' }]
}

function getStatusType(status: string) {
  const map: Record<string, any> = {
    PENDING: 'warning',
    ACCEPTED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info',
    REJECTED: 'danger'
  }
  return map[status] || 'info'
}

function getStatusText(status: string) {
  const map: Record<string, string> = {
    PENDING: '待确认',
    ACCEPTED: '已接受',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REJECTED: '已拒绝'
  }
  return map[status] || status
}

function getEmptyText() {
  const map: Record<string, string> = {
    ALL: '暂无预约记录',
    PENDING: '暂无待确认预约',
    ACCEPTED: '暂无已接受预约',
    COMPLETED: '暂无已完成预约',
    CANCELLED: '暂无已取消预约'
  }
  return map[activeTab.value] || '暂无数据'
}

function handleTabChange() {
  currentPage.value = 1
  loadAppointments()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
}

function handleCurrentChange(page: number) {
  currentPage.value = page
}

function handleCancel(appointment: AppointmentItem) {
  currentAppointment.value = appointment
  cancelForm.cancelReason = ''
  cancelDialogVisible.value = true
}

async function confirmCancel() {
  if (!cancelFormRef.value) return

  try {
    await cancelFormRef.value.validate()
    canceling.value = true

    setTimeout(() => {
      if (currentAppointment.value) {
        const item = appointmentList.value.find(a => a.id === currentAppointment.value!.id)
        if (item) {
          item.status = 'CANCELLED'
          item.cancelReason = cancelForm.cancelReason
        }
      }
      ElMessage.success('预约已取消')
      cancelDialogVisible.value = false
      canceling.value = false
    }, 800)
  } catch (e) {
    console.error(e)
  }
}

function handleChat(appointment: AppointmentItem) {
  router.push(`/chat/${appointment.id}`)
}

function handleEvaluate(appointment: AppointmentItem) {
  currentAppointment.value = appointment
  evaluateForm.rating = 5
  evaluateForm.content = ''
  evaluateForm.tags = []
  evaluateDialogVisible.value = true
}

async function confirmEvaluate() {
  if (!evaluateFormRef.value) return

  try {
    await evaluateFormRef.value.validate()
    evaluating.value = true

    setTimeout(() => {
      if (currentAppointment.value) {
        const item = appointmentList.value.find(a => a.id === currentAppointment.value!.id)
        if (item) {
          item.hasEvaluated = true
        }
      }
      ElMessage.success('评价提交成功')
      evaluateDialogVisible.value = false
      evaluating.value = false
    }, 800)
  } catch (e) {
    console.error(e)
  }
}

function loadAppointments() {
  loading.value = true
  setTimeout(() => {
    appointmentList.value = [...mockAppointments]
    total.value = mockAppointments.length
    loading.value = false
  }, 300)
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped lang="scss">
.appointment-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tabs-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 0 24px;
  }
}

.appointment-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }

  :deep(.el-tabs__nav-wrap::after) {
    height: 0;
  }

  :deep(.el-tabs__item) {
    height: 56px;
    line-height: 56px;
    font-size: 15px;
  }

  :deep(.el-tabs__active-bar) {
    background-color: $primary-color;
  }

  :deep(.el-tabs__item.is-active) {
    color: $primary-color;
  }
}

.appointment-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appointment-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 20px 24px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px solid $border-color;
}

.teacher-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .teacher-detail {
    .teacher-name {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .school-info {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.status-tag {
  font-size: 13px;
}

.appointment-info {
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;

  .info-row {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    font-size: 14px;

    .info-label {
      color: $text-secondary;
      flex-shrink: 0;
    }

    .info-value {
      color: $text-primary;
      flex: 1;
      line-height: 1.5;
    }

    .reject-text {
      color: $danger-color;
    }
  }
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid $border-color;

  .appointment-no {
    font-size: 12px;
    color: $text-placeholder;
  }

  .action-buttons {
    display: flex;
    gap: 12px;
  }
}

.empty-state {
  padding: 40px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 8px;
}

.evaluate-content {
  .evaluate-teacher {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: $bg-page;
    border-radius: 8px;
    margin-bottom: 20px;

    .teacher-name {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
  }
}
</style>
