<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="40" color="#06B2B5"><ChatLineSquare /></el-icon>
        </div>
        <h1>教师交流预约管理系统</h1>
        <p class="subtitle">规范化家校沟通，让沟通更高效</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" size="large" :prefix-icon="User" />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          size="large"
          show-password
          :prefix-icon="Lock"
          @keyup.enter="handleLogin"
        />
      </el-form-item>

      <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
        登录
      </el-button>
    </el-form>

      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="register-link">立即注册</router-link>
      </div>

      <div v-if="isDev" class="demo-accounts">
        <p>测试账号：</p>
        <p>家长：parent1 / parent123</p>
        <p>教师：teacher1 / teacher123</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, ChatLineSquare } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import type { LoginDTO } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isDev = import.meta.env.DEV

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<LoginDTO>({
  username: isDev ? 'teacher1' : '',
  password: isDev ? 'teacher123' : ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    const res = await login(form)
    userStore.setToken(res.token, res.tokenPrefix)
    userStore.setUserInfo(res.userInfo)

    ElMessage.success('登录成功')

    const redirect = route.query.redirect as string
    if (redirect) {
      router.push(redirect)
    } else {
      if (res.userInfo.role === 'TEACHER') {
        router.push('/teacher/dashboard')
      } else if (res.userInfo.role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else {
        router.push('/dashboard')
      }
    }
  } catch (error) {
    console.error('Login failed:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #06B2B5 0%, #04999C 50%, #028081 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    top: -200px;
    left: -200px;
  }

  &::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    bottom: -100px;
    right: -100px;
  }
}

.login-card {
  width: 420px;
  padding: 48px 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  .logo {
    width: 72px;
    height: 72px;
    margin: 0 auto 16px;
    background: linear-gradient(135deg, #06B2B5, #63D5D6);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #23455A;
    margin-bottom: 8px;
  }

  .subtitle {
    font-size: 14px;
    color: #9CA3AF;
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  margin-top: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #6B7280;

  .register-link {
    color: #06B2B5;
    margin-left: 4px;
    font-weight: 500;
  }
}

.demo-accounts {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #E7ECEF;
  font-size: 12px;
  color: #9CA3AF;
  line-height: 1.8;
}
</style>
