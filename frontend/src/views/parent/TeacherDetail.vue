<template>
  <div class="teacher-detail-container">
    <el-button type="primary" text :icon="ArrowLeft" class="back-btn" @click="goBack">
      返回列表
    </el-button>

    <el-card class="teacher-info-card" shadow="never">
      <div class="teacher-header">
        <el-avatar :size="88" :src="teacher.avatar">
          {{ teacher.realName?.charAt(0) }}
        </el-avatar>
        <div class="teacher-main-info">
          <div class="teacher-name-row">
            <h2 class="teacher-name">{{ teacher.realName }}</h2>
            <el-tag type="primary" size="large">{{ teacher.subject }}</el-tag>
          </div>
          <div class="teacher-meta">
            <span class="meta-item">
              <el-icon><School /></el-icon>
              {{ teacher.school }}
            </span>
            <span class="meta-item">
              <el-icon><Medal /></el-icon>
              {{ teacher.title }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              教龄 {{ teacher.teachingYears || 0 }}年
            </span>
          </div>
          <div class="teacher-rating-row">
            <div class="rating-display">
              <el-icon v-for="i in 5" :key="i" :color="i <= Math.floor(teacher.rating || 5) ? '#F5A623' : '#E7ECEF'">
                <Star />
              </el-icon>
              <span class="rating-score">{{ teacher.rating?.toFixed(1) || '5.0' }}</span>
              <span class="rating-count">{{ teacher.reviewCount || 0 }}条评价</span>
            </div>
            <div class="appointment-count">
              累计预约 {{ teacher.appointmentCount || 0 }}次
            </div>
          </div>
        </div>
      </div>

      <div class="teacher-description">
        <h3 class="section-subtitle">教师简介</h3>
        <p>{{ teacher.description || '暂无简介' }}</p>
      </div>
    </el-card>

    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h3 class="section-title">可预约时间段</h3>
        </div>
      </template>

      <div class="calendar-list" v-if="availableSlots.length > 0">
        <div
          v-for="slot in availableSlots"
          :key="slot.id"
          class="calendar-item"
          :class="{ selected: selectedSlot?.id === slot.id }"
          @click="selectSlot(slot)"
        >
          <div class="slot-date">
            <el-icon><Calendar /></el-icon>
            <span>{{ slot.date }}</span>
          </div>
          <div class="slot-time">
            <el-icon><Timer /></el-icon>
            <span>{{ slot.startTime }} - {{ slot.endTime }}</span>
          </div>
          <div class="slot-status">
            <el-tag :type="slot.bookedCount < slot.capacity ? 'success' : 'info'" size="small">
              {{ slot.bookedCount }}/{{ slot.capacity }}人
            </el-tag>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无可用时间段" :image-size="80" />

      <div class="book-action">
        <el-button
          type="primary"
          size="large"
          :disabled="!selectedSlot"
          @click="handleBook"
        >
          立即预约
        </el-button>
      </div>
    </el-card>

    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h3 class="section-title">评价列表</h3>
          <span class="review-total">共 {{ reviewList.length }} 条</span>
        </div>
      </template>

      <div class="review-list">
        <div v-for="review in reviewList" :key="review.id" class="review-item">
          <div class="review-header">
            <el-avatar :size="40" :src="review.parentAvatar">
              {{ review.parentName?.charAt(0) }}
            </el-avatar>
            <div class="reviewer-info">
              <div class="reviewer-name">{{ review.parentName }}</div>
              <div class="review-time">{{ review.createdTime }}</div>
            </div>
            <div class="review-rating">
              <el-icon v-for="i in 5" :key="i" :color="i <= review.rating ? '#F5A623' : '#E7ECEF'" :size="14">
                <Star />
              </el-icon>
            </div>
          </div>
          <div class="review-content">{{ review.content }}</div>
          <div v-if="review.tags" class="review-tags">
            <el-tag v-for="tag in review.tags.split(',')" :key="tag" size="small" type="info">
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <div v-if="reviewList.length === 0" class="empty-reviews">
        <el-empty description="暂无评价" :image-size="80" />
      </div>
    </el-card>

    <el-dialog v-model="bookDialogVisible" title="预约确认" width="480px">
      <div class="book-dialog-content">
        <div class="book-info-row">
          <span class="label">教师：</span>
          <span class="value">{{ teacher.realName }}</span>
        </div>
        <div class="book-info-row">
          <span class="label">学科：</span>
          <span class="value">{{ teacher.subject }}</span>
        </div>
        <div class="book-info-row" v-if="selectedSlot">
          <span class="label">时间：</span>
          <span class="value">{{ selectedSlot.date }} {{ selectedSlot.startTime }} - {{ selectedSlot.endTime }}</span>
        </div>
        <el-form ref="formRef" :model="bookForm" :rules="bookRules" class="book-form">
          <el-form-item label="预约原因" prop="reason">
            <el-input v-model="bookForm.reason" type="textarea" :rows="3" placeholder="请描述您的预约原因" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="booking" @click="confirmBook">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  ArrowLeft,
  School,
  Medal,
  Clock,
  Star,
  Calendar,
  Timer
} from '@element-plus/icons-vue'
import type { Teacher, TeacherCalendar, Evaluation } from '@/types'

const route = useRoute()
const router = useRouter()

const teacherId = Number(route.params.id)

const teacher = ref<Teacher>({
  id: 0,
  userId: 0,
  realName: '',
  subject: '',
  school: '',
  title: '',
  description: '',
  teachingYears: 0,
  rating: 5,
  reviewCount: 0,
  appointmentCount: 0,
  auditStatus: '',
  auditRemark: '',
  createdTime: ''
})

const availableSlots = ref<TeacherCalendar[]>([])
const selectedSlot = ref<TeacherCalendar | null>(null)

const reviewList = ref<any[]>([])

const bookDialogVisible = ref(false)
const booking = ref(false)
const formRef = ref<FormInstance>()

const bookForm = reactive({
  reason: ''
})

const bookRules: FormRules = {
  reason: [{ required: true, message: '请填写预约原因', trigger: 'blur' }]
}

function goBack() {
  router.back()
}

function selectSlot(slot: TeacherCalendar) {
  if (slot.bookedCount >= slot.capacity) {
    ElMessage.warning('该时间段已满')
    return
  }
  selectedSlot.value = slot
}

function handleBook() {
  if (!selectedSlot.value) {
    ElMessage.warning('请选择预约时间段')
    return
  }
  bookDialogVisible.value = true
}

async function confirmBook() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    booking.value = true

    setTimeout(() => {
      ElMessage.success('预约提交成功，请等待教师确认')
      bookDialogVisible.value = false
      booking.value = false
      bookForm.reason = ''
      router.push('/appointments')
    }, 1000)
  } catch (e) {
    console.error(e)
  } finally {
    // booking.value = false
  }
}

function loadTeacherDetail() {
  const mockTeacher: Teacher = {
    id: teacherId,
    userId: teacherId,
    realName: '张老师',
    subject: '数学',
    school: '第一中学',
    title: '高级教师',
    description: '从事数学教学15年，擅长初高中数学辅导，教学方法独特，深受学生喜爱。多次获得市级优秀教师称号，所教学生在各类数学竞赛中屡获佳绩。注重培养学生的数学思维能力，善于根据学生特点制定个性化教学方案。',
    teachingYears: 15,
    rating: 4.9,
    reviewCount: 128,
    appointmentCount: 356,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-01-15'
  }
  teacher.value = mockTeacher

  availableSlots.value = [
    {
      id: 1,
      teacherId,
      date: '2024-07-20（周六）',
      startTime: '09:00',
      endTime: '10:00',
      status: 'AVAILABLE',
      capacity: 5,
      bookedCount: 2,
      duration: 60,
      remark: ''
    },
    {
      id: 2,
      teacherId,
      date: '2024-07-20（周六）',
      startTime: '14:00',
      endTime: '15:00',
      status: 'AVAILABLE',
      capacity: 5,
      bookedCount: 4,
      duration: 60,
      remark: ''
    },
    {
      id: 3,
      teacherId,
      date: '2024-07-21（周日）',
      startTime: '10:00',
      endTime: '11:00',
      status: 'AVAILABLE',
      capacity: 5,
      bookedCount: 1,
      duration: 60,
      remark: ''
    },
    {
      id: 4,
      teacherId,
      date: '2024-07-21（周日）',
      startTime: '15:00',
      endTime: '16:00',
      status: 'AVAILABLE',
      capacity: 5,
      bookedCount: 5,
      duration: 60,
      remark: ''
    }
  ]

  reviewList.value = [
    {
      id: 1,
      parentName: '王家长',
      parentAvatar: '',
      rating: 5,
      content: '张老师非常专业，讲解清晰，孩子收获很大。强烈推荐！',
      tags: '讲解清晰,耐心负责,专业度高',
      createdTime: '2024-07-10 15:30'
    },
    {
      id: 2,
      parentName: '李家长',
      parentAvatar: '',
      rating: 5,
      content: '老师很有耐心，针对孩子的薄弱环节进行了重点讲解，孩子数学成绩有明显提升。',
      tags: '耐心负责,效果显著',
      createdTime: '2024-07-05 10:20'
    },
    {
      id: 3,
      parentName: '陈家长',
      parentAvatar: '',
      rating: 4,
      content: '整体不错，老师很负责，沟通顺畅。',
      tags: '沟通顺畅',
      createdTime: '2024-06-28 14:15'
    }
  ]
}

onMounted(() => {
  loadTeacherDetail()
})
</script>

<style scoped lang="scss">
.teacher-detail-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.back-btn {
  align-self: flex-start;
  font-size: 14px;
}

.teacher-info-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.teacher-header {
  display: flex;
  gap: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid $border-color;
}

.teacher-main-info {
  flex: 1;
}

.teacher-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;

  .teacher-name {
    font-size: 24px;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }
}

.teacher-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    color: $text-secondary;
  }
}

.teacher-rating-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .rating-display {
    display: flex;
    align-items: center;
    gap: 6px;

    .rating-score {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }

    .rating-count {
      font-size: 13px;
      color: $text-secondary;
    }
  }

  .appointment-count {
    font-size: 13px;
    color: $text-secondary;
  }
}

.teacher-description {
  padding-top: 20px;

  .section-subtitle {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 12px 0;
  }

  p {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.8;
    margin: 0;
  }
}

.section-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__header) {
    padding: 16px 24px;
    border-bottom: 1px solid $border-color;
  }

  :deep(.el-card__body) {
    padding: 20px 24px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }

  .review-total {
    font-size: 13px;
    color: $text-secondary;
  }
}

.calendar-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.calendar-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border: 1px solid $border-color;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: $primary-color;
    background: rgba(6, 178, 181, 0.03);
  }

  &.selected {
    border-color: $primary-color;
    background: rgba(6, 178, 181, 0.08);
  }
}

.slot-date,
.slot-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: $text-primary;
}

.book-action {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid $border-color;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding-bottom: 20px;
  border-bottom: 1px solid $border-color;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;

  .reviewer-info {
    flex: 1;

    .reviewer-name {
      font-size: 14px;
      font-weight: 500;
      color: $text-primary;
    }

    .review-time {
      font-size: 12px;
      color: $text-placeholder;
      margin-top: 2px;
    }
  }
}

.review-content {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.empty-reviews {
  padding: 20px 0;
}

.book-dialog-content {
  .book-info-row {
    display: flex;
    margin-bottom: 12px;
    font-size: 14px;

    .label {
      color: $text-secondary;
      width: 70px;
      flex-shrink: 0;
    }

    .value {
      color: $text-primary;
      font-weight: 500;
    }
  }

  .book-form {
    margin-top: 16px;
  }
}
</style>
