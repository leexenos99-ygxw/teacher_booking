<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <div class="logo">
          <el-icon :size="40" color="#06B2B5"><ChatLineSquare /></el-icon>
        </div>
        <h1>用户注册</h1>
        <p class="subtitle">创建账号，开启高效沟通</p>
      </div>

      <el-tabs v-model="activeTab" class="role-tabs">
        <el-tab-pane label="家长注册" name="PARENT" />
        <el-tab-pane label="教师注册" name="TEACHER" />
      </el-tabs>

      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请设置用户名" size="large" :prefix-icon="User" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请设置密码（6-20位）"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" size="large" :prefix-icon="Phone" />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" size="large" :prefix-icon="UserFilled" />
        </el-form-item>

        <el-form-item v-if="activeTab === 'TEACHER'" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" size="large" :prefix-icon="Avatar" />
        </el-form-item>

        <el-button type="primary" size="large" class="register-btn" :loading="loading" @click="handleRegister">
          注册
        </el-button>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="login-link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Phone, UserFilled, Avatar, ChatLineSquare } from '@element-plus/icons-vue'
import { register } from '@/api/auth'
import type { RegisterDTO } from '@/types'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const activeTab = ref('PARENT')

const form = reactive<RegisterDTO>({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  role: 'PARENT',
  nickname: '',
  realName: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
})

watch(activeTab, (val) => {
  form.role = val
})

async function handleRegister() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    await register(form)
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (error) {
    console.error('Register failed:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.register-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #06B2B5 0%, #04999C 50%, #028081 100%);
  position: relative;
  overflow: hidden;
  padding: 20px 0;

  &::before {
    content: '';
    position: absolute;
    width: 500px;
    height: 500px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    top: -150px;
    right: -150px;
  }

  &::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    bottom: -100px;
    left: -100px;
  }
}

.register-card {
  width: 440px;
  padding: 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
  max-height: 90vh;
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 24px;

  .logo {
    width: 64px;
    height: 64px;
    margin: 0 auto 12px;
    background: linear-gradient(135deg, #06B2B5, #63D5D6);
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  h1 {
    font-size: 22px;
    font-weight: 600;
    color: #23455A;
    margin-bottom: 6px;
  }

  .subtitle {
    font-size: 13px;
    color: #9CA3AF;
  }
}

.role-tabs {
  margin-bottom: 8px;
}

.register-form {
  :deep(.el-form-item) {
    margin-bottom: 16px;
  }
}

.register-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  margin-top: 4px;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #6B7280;

  .login-link {
    color: #06B2B5;
    margin-left: 4px;
    font-weight: 500;
  }
}
</style>
