<template>
  <div class="dashboard-container">
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>您好，{{ userStore.userInfo?.nickname || '家长' }}！</h2>
          <p>欢迎来到教师交流预约管理系统，开启高效家校沟通之旅</p>
        </div>
        <div class="welcome-icon">
          <el-icon :size="64" color="#06B2B5"><Sunny /></el-icon>
        </div>
      </div>
    </el-card>

    <div class="quick-entry-section">
      <h3 class="section-title">快捷入口</h3>
      <div class="quick-entry-grid">
        <el-card class="quick-entry-card" shadow="hover" @click="goToTeachers">
          <div class="entry-content">
            <div class="entry-icon icon-teacher">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="entry-info">
              <h4>预约教师</h4>
              <p>浏览教师列表，预约心仪教师</p>
            </div>
            <el-icon class="entry-arrow"><ArrowRight /></el-icon>
          </div>
        </el-card>

        <el-card class="quick-entry-card" shadow="hover" @click="goToAppointments">
          <div class="entry-content">
            <div class="entry-icon icon-appointment">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="entry-info">
              <h4>我的预约</h4>
              <p>查看和管理您的预约记录</p>
            </div>
            <el-icon class="entry-arrow"><ArrowRight /></el-icon>
          </div>
        </el-card>
      </div>
    </div>

    <div class="stats-section">
      <h3 class="section-title">预约统计</h3>
      <div class="stats-grid">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-pending">
              <el-icon :size="28"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ pendingCount }}</div>
              <div class="stat-label">待确认预约</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-completed">
              <el-icon :size="28"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ completedCount }}</div>
              <div class="stat-label">已完成预约</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Sunny, User, Calendar, ArrowRight, Clock, CircleCheck } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const pendingCount = ref(0)
const completedCount = ref(0)

function goToTeachers() {
  router.push('/teachers')
}

function goToAppointments() {
  router.push('/appointments')
}

onMounted(() => {
  // TODO: 调用接口获取统计数据
  pendingCount.value = 2
  completedCount.value = 5
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
  margin: 0 0 16px 0;
}

.quick-entry-section {
  .quick-entry-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }
}

.quick-entry-card {
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid $border-color;

  &:hover {
    transform: translateY(-2px);
  }

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.entry-content {
  display: flex;
  align-items: center;
  gap: 16px;

  .entry-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
  }

  .icon-teacher {
    background: linear-gradient(135deg, #06B2B5, #63D5D6);
  }

  .icon-appointment {
    background: linear-gradient(135deg, #3B82F6, #60A5FA);
  }

  .entry-info {
    flex: 1;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 4px 0;
    }

    p {
      font-size: 13px;
      color: $text-secondary;
      margin: 0;
    }
  }

  .entry-arrow {
    color: $text-placeholder;
    font-size: 20px;
  }
}

.stats-section {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    @media (max-width: 768px) {
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

  .stat-pending {
    background: linear-gradient(135deg, #F5A623, #FBBF24);
  }

  .stat-completed {
    background: linear-gradient(135deg, #22C55E, #4ADE80);
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
</style>
