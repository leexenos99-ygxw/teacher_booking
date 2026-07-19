<template>
  <div class="calendar-container">
    <div class="calendar-header">
      <div class="header-left">
        <h2 class="page-title">时间管理</h2>
        <p class="page-subtitle">设置您的可预约时间段，方便家长预约</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">
        添加时间段
      </el-button>
    </div>

    <div class="calendar-content">
      <el-card class="date-picker-card" shadow="never">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          size="large"
          style="width: 100%"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
        />
      </el-card>

      <el-card class="time-slot-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">{{ selectedDate || '请选择日期' }} 的时间段</span>
            <span class="slot-count">共 {{ calendarList.length }} 个时间段</span>
          </div>
        </template>

        <el-empty v-if="calendarList.length === 0" description="暂无时间段，请添加" />

        <div v-else class="time-slot-list">
          <div
            v-for="slot in calendarList"
            :key="slot.id"
            class="time-slot-item"
          >
            <div class="slot-info">
              <div class="slot-time">
                <el-icon color="#06B2B5"><Clock /></el-icon>
                <span class="time-text">{{ slot.startTime }} - {{ slot.endTime }}</span>
              </div>
              <div class="slot-details">
                <el-tag size="small" type="info">
                  容量：{{ slot.capacity }}人
                </el-tag>
                <el-tag size="small" type="success">
                  已预约：{{ slot.bookedCount }}人
                </el-tag>
                <el-tag
                  v-if="slot.status"
                  :type="slot.status === 'AVAILABLE' ? 'success' : 'info'"
                  size="small"
                >
                  {{ slot.status === 'AVAILABLE' ? '可预约' : '已关闭' }}
                </el-tag>
              </div>
            </div>
            <div class="slot-actions">
              <el-button
                type="danger"
                text
                size="small"
                @click="handleDelete(slot.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="添加时间段"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="form.startTime"
            placeholder="选择开始时间"
            style="width: 100%"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="form.endTime"
            placeholder="选择结束时间"
            style="width: 100%"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>

        <el-form-item label="容量" prop="capacity">
          <el-input-number
            v-model="form.capacity"
            :min="1"
            :max="100"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Clock } from '@element-plus/icons-vue'
import { getMyCalendar, setCalendar } from '@/api/teacher'
import type { TeacherCalendar } from '@/types'
import dayjs from 'dayjs'

const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const calendarList = ref<TeacherCalendar[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<Partial<TeacherCalendar>>({
  date: '',
  startTime: '',
  endTime: '',
  capacity: 1,
  status: 'AVAILABLE',
  remark: ''
})

const rules = reactive<FormRules>({
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
})

function handleDateChange() {
  loadCalendar()
}

function openAddDialog() {
  form.date = selectedDate.value
  form.startTime = ''
  form.endTime = ''
  form.capacity = 1
  form.remark = ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    if (form.startTime && form.endTime && form.startTime >= form.endTime) {
      ElMessage.error('结束时间必须晚于开始时间')
      return
    }

    submitting.value = true

    const newSlot: TeacherCalendar = {
      id: Date.now(),
      teacherId: 0,
      date: form.date || '',
      startTime: form.startTime || '',
      endTime: form.endTime || '',
      capacity: form.capacity || 1,
      bookedCount: 0,
      status: 'AVAILABLE',
      duration: 0,
      remark: form.remark || ''
    }

    const updatedList = [...calendarList.value, newSlot]
    await setCalendar(updatedList)

    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadCalendar()
  } catch (error) {
    console.error('Add calendar failed:', error)
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这个时间段吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const updatedList = calendarList.value.filter((item) => item.id !== id)
    await setCalendar(updatedList)

    ElMessage.success('删除成功')
    loadCalendar()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete calendar failed:', error)
    }
  }
}

async function loadCalendar() {
  try {
    const data = await getMyCalendar({
      startDate: selectedDate.value,
      endDate: selectedDate.value
    })
    calendarList.value = data.sort((a, b) => a.startTime.localeCompare(b.startTime))
  } catch (error) {
    console.error('Load calendar failed:', error)
  }
}

onMounted(() => {
  loadCalendar()
})
</script>

<style scoped lang="scss">
.calendar-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.calendar-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;

  @media (max-width: 640px) {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-left {
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
}

.calendar-content {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.date-picker-card,
.time-slot-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.time-slot-card {
  :deep(.el-card__header) {
    padding: 16px 20px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
  }

  .slot-count {
    font-size: 13px;
    color: $text-secondary;
  }
}

.time-slot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.time-slot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: $bg-page;
  border-radius: 10px;
  gap: 16px;

  @media (max-width: 640px) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.slot-info {
  flex: 1;
  min-width: 0;

  .slot-time {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;

    .time-text {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
  }

  .slot-details {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
}

.slot-actions {
  flex-shrink: 0;
}
</style>
