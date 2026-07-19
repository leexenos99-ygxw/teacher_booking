<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="sidebar">
      <div class="logo-area">
        <el-icon :size="28" color="#06B2B5"><ChatLineSquare /></el-icon>
        <span class="logo-text">家校预约</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#23455A"
        active-text-color="#06B2B5"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/teachers">
          <el-icon><User /></el-icon>
          <span>教师列表</span>
        </el-menu-item>
        <el-menu-item index="/appointments">
          <el-icon><Calendar /></el-icon>
          <span>我的预约</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-button type="primary" text @click="goNotifications">
              <el-icon :size="20"><Bell /></el-icon>
            </el-button>
          </el-badge>

          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { ChatLineSquare, HomeFilled, User, Calendar, Bell, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const unreadCount = ref(0)

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => (route.meta.title as string) || '')

function handleCommand(command: string) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

function goNotifications() {
  // TODO: 跳转到通知页面
}

onMounted(() => {
  userStore.initFromStorage()
})
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background: #fff;
  border-right: 1px solid $border-color;
  display: flex;
  flex-direction: column;
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid $border-color;

  .logo-text {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
  }
}

.sidebar-menu {
  border-right: none;
  padding: 12px 0;

  :deep(.el-menu-item) {
    height: 48px;
    line-height: 48px;
    margin: 4px 12px;
    border-radius: 10px;

    &.is-active {
      background-color: rgba(6, 178, 181, 0.1);
    }

    &:hover {
      background-color: rgba(6, 178, 181, 0.05);
    }
  }
}

.header {
  background: #fff;
  border-bottom: 1px solid $border-color;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.header-left {
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;

  .notification-badge {
    cursor: pointer;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: $bg-page;
  }

  .username {
    font-size: 14px;
    color: $text-primary;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.main-content {
  padding: 20px;
  background: $bg-page;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
