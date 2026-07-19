<template>
  <div class="chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <el-button type="primary" text :icon="ArrowLeft" @click="goBack">
          返回
        </el-button>
      </div>

      <div class="appointment-info-card">
        <h3 class="card-title">预约信息</h3>
        <div class="info-item">
          <el-icon color="#06B2B5"><Calendar /></el-icon>
          <div class="info-content">
            <div class="info-label">预约时间</div>
            <div class="info-value">{{ formatDateTime(appointment?.startTime) }}</div>
          </div>
        </div>
        <div class="info-item">
          <el-icon color="#06B2B5"><Clock /></el-icon>
          <div class="info-content">
            <div class="info-label">时长</div>
            <div class="info-value">{{ appointment?.duration }}分钟</div>
          </div>
        </div>
        <div class="info-item">
          <el-icon color="#06B2B5"><Tickets /></el-icon>
          <div class="info-content">
            <div class="info-label">预约编号</div>
            <div class="info-value">{{ appointment?.appointmentNo }}</div>
          </div>
        </div>
      </div>

      <div class="user-info-card">
        <h3 class="card-title">对方信息</h3>
        <div class="user-avatar-wrapper">
          <el-avatar :size="64">
            {{ otherUserName?.charAt(0) }}
          </el-avatar>
        </div>
        <div class="user-name">{{ otherUserName }}</div>
      </div>
    </div>

    <div class="chat-main">
      <div class="chat-header">
        <div class="header-left">
          <span class="chat-title">在线沟通</span>
          <el-tag type="primary" size="small" class="status-tag">
            沟通中
          </el-tag>
        </div>
        <div class="header-right">
          <div class="countdown-wrapper">
            <el-icon color="#F5A623"><Timer /></el-icon>
            <span class="countdown-text">剩余时间：{{ countdownText }}</span>
          </div>
        </div>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <el-empty v-if="messageList.length === 0" description="暂无消息，开始聊天吧" />

        <div
          v-for="message in messageList"
          :key="message.id"
          :class="['message-item', isMyMessage(message) ? 'message-mine' : 'message-other']"
        >
          <div v-if="!isMyMessage(message)" class="message-avatar">
            <el-avatar :size="36">
              {{ otherUserName?.charAt(0) }}
            </el-avatar>
          </div>
          <div class="message-content-wrapper">
            <div v-if="!isMyMessage(message)" class="message-sender">
              {{ otherUserName }}
            </div>
            <div class="message-bubble">
              {{ message.content }}
            </div>
            <div class="message-time">
              {{ formatMessageTime(message.createdTime) }}
            </div>
          </div>
          <div v-if="isMyMessage(message)" class="message-avatar">
            <el-avatar :size="36">
              {{ userStore.userInfo?.nickname?.charAt(0) }}
            </el-avatar>
          </div>
        </div>
      </div>

      <div class="chat-input-wrapper">
        <div class="chat-input-container">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="2"
            placeholder="请输入消息..."
            resize="none"
            @keydown.enter.exact.prevent="handleSend"
          />
          <el-button type="primary" :loading="sending" @click="handleSend">
            <el-icon><Promotion /></el-icon>
            <span>发送</span>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Calendar,
  Clock,
  Tickets,
  Timer,
  Promotion
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getTeacherAppointmentDetail,
  getParentAppointmentDetail,
  getAppointmentMessages,
  sendAppointmentMessage
} from '@/api/appointment'
import type { Appointment, Message } from '@/types'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const messagesContainer = ref<HTMLElement>()
const appointment = ref<Appointment>()
const messageList = ref<Message[]>([])
const inputMessage = ref('')
const sending = ref(false)
const countdownText = ref('00:00')

const appointmentId = computed(() => Number(route.params.appointmentId))
const isTeacher = computed(() => userStore.role === 'TEACHER')
const otherUserName = ref('家长')

let countdownTimer: number | null = null

function formatDateTime(time?: string) {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

function formatMessageTime(time: string) {
  return dayjs(time).format('HH:mm')
}

function isMyMessage(message: Message) {
  return message.senderId === userStore.userInfo?.userId
}

function goBack() {
  router.back()
}

async function handleSend() {
  if (!inputMessage.value.trim() || !appointmentId.value) return

  try {
    sending.value = true
    const message = await sendAppointmentMessage(
      appointmentId.value,
      inputMessage.value.trim(),
      'TEXT',
      isTeacher.value
    )
    messageList.value.push(message)
    inputMessage.value = ''
    scrollToBottom()
  } catch (error) {
    console.error('Send message failed:', error)
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

function startCountdown() {
  if (!appointment.value?.endTime) return

  const endTime = dayjs(appointment.value.endTime)

  function updateCountdown() {
    const now = dayjs()
    const diff = endTime.diff(now, 'second')

    if (diff <= 0) {
      countdownText.value = '00:00'
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
      return
    }

    const minutes = Math.floor(diff / 60)
    const seconds = diff % 60
    countdownText.value = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  }

  updateCountdown()
  countdownTimer = window.setInterval(updateCountdown, 1000)
}

async function loadAppointment() {
  try {
    if (isTeacher.value) {
      appointment.value = await getTeacherAppointmentDetail(appointmentId.value)
      otherUserName.value = '家长'
    } else {
      appointment.value = await getParentAppointmentDetail(appointmentId.value)
      otherUserName.value = '教师'
    }
    startCountdown()
  } catch (error) {
    console.error('Load appointment failed:', error)
  }
}

async function loadMessages() {
  try {
    const messages = await getAppointmentMessages(appointmentId.value, isTeacher.value)
    messageList.value = messages
    scrollToBottom()
  } catch (error) {
    console.error('Load messages failed:', error)
  }
}

onMounted(() => {
  userStore.initFromStorage()
  loadAppointment()
  loadMessages()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  height: 100vh;
  background: $bg-page;
}

.chat-sidebar {
  width: 300px;
  background: #fff;
  border-right: 1px solid $border-color;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;

  @media (max-width: 768px) {
    display: none;
  }
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid $border-color;
}

.appointment-info-card,
.user-info-card {
  padding: 20px;
  border-bottom: 1px solid $border-color;

  .card-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 16px 0;
  }
}

.info-item {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;

  &:last-child {
    margin-bottom: 0;
  }

  .info-content {
    flex: 1;
    min-width: 0;

    .info-label {
      font-size: 12px;
      color: $text-secondary;
      margin-bottom: 2px;
    }

    .info-value {
      font-size: 14px;
      color: $text-primary;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.user-info-card {
  text-align: center;

  .user-avatar-wrapper {
    margin-bottom: 12px;
  }

  .user-name {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
  }
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid $border-color;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .chat-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
  }

  .header-right {
    .countdown-wrapper {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: rgba(245, 166, 35, 0.1);
      border-radius: 20px;

      .countdown-text {
        font-size: 14px;
        font-weight: 600;
        color: #F5A623;
      }
    }
  }

  @media (max-width: 640px) {
    padding: 0 16px;

    .header-right .countdown-wrapper {
      padding: 4px 10px;

      .countdown-text {
        font-size: 12px;
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;

  @media (max-width: 640px) {
    padding: 16px;
  }
}

.message-item {
  display: flex;
  gap: 10px;
  max-width: 70%;

  &.message-mine {
    align-self: flex-end;
    flex-direction: row-reverse;

    .message-content-wrapper {
      align-items: flex-end;
    }

    .message-bubble {
      background: linear-gradient(135deg, #06B2B5, #63D5D6);
      color: #fff;
      border-radius: 12px 4px 12px 12px;
    }

    .message-time {
      text-align: right;
    }
  }

  &.message-other {
    align-self: flex-start;

    .message-bubble {
      background: #fff;
      color: $text-primary;
      border-radius: 4px 12px 12px 12px;
      border: 1px solid $border-color;
    }
  }

  @media (max-width: 640px) {
    max-width: 85%;
  }
}

.message-avatar {
  flex-shrink: 0;
}

.message-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 100%;
}

.message-sender {
  font-size: 12px;
  color: $text-secondary;
  padding: 0 4px;
}

.message-bubble {
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
  word-break: break-word;
}

.message-time {
  font-size: 11px;
  color: $text-placeholder;
  padding: 0 4px;
}

.chat-input-wrapper {
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid $border-color;
  flex-shrink: 0;

  @media (max-width: 640px) {
    padding: 12px 16px;
  }
}

.chat-input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;

  :deep(.el-textarea) {
    flex: 1;
  }

  .el-button {
    height: 40px;
    padding: 0 20px;
  }
}
</style>
