<template>
  <div class="profile-container">
    <div class="page-header">
      <h2 class="page-title">个人资料</h2>
      <p class="page-subtitle">完善您的教师信息，让家长更好地了解您</p>
    </div>

    <el-card class="profile-card" shadow="never">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="profile-form"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="学科" prop="subject">
          <el-select v-model="form.subject" placeholder="请选择学科" style="width: 100%">
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="生物" value="生物" />
            <el-option label="政治" value="政治" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="学校" prop="school">
          <el-input v-model="form.school" placeholder="请输入所在学校" />
        </el-form-item>

        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
            <el-option label="初级教师" value="初级教师" />
            <el-option label="中级教师" value="中级教师" />
            <el-option label="高级教师" value="高级教师" />
            <el-option label="特级教师" value="特级教师" />
          </el-select>
        </el-form-item>

        <el-form-item label="教龄" prop="teachingYears">
          <el-input-number
            v-model="form.teachingYears"
            :min="0"
            :max="60"
            style="width: 100%"
          />
          <span class="unit-text">年</span>
        </el-form-item>

        <el-form-item label="个人简介" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请输入您的个人简介，包括教学经验、教学理念等"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">
            保存
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getTeacherProfile, updateTeacherProfile } from '@/api/teacher'
import type { Teacher } from '@/types'

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<Partial<Teacher>>({
  realName: '',
  subject: '',
  school: '',
  title: '',
  teachingYears: 0,
  description: ''
})

const originalForm = reactive<Partial<Teacher>>({})

const rules = reactive<FormRules>({
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
  school: [{ required: true, message: '请输入学校', trigger: 'blur' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  teachingYears: [{ required: true, message: '请输入教龄', trigger: 'blur' }],
  description: [
    { required: true, message: '请输入个人简介', trigger: 'blur' },
    { min: 20, message: '个人简介至少20个字', trigger: 'blur' }
  ]
})

async function handleSave() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    await updateTeacherProfile(form)
    ElMessage.success('保存成功')

    Object.assign(originalForm, { ...form })
  } catch (error) {
    console.error('Save profile failed:', error)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  Object.assign(form, { ...originalForm })
  formRef.value?.resetFields()
}

async function loadProfile() {
  try {
    const data = await getTeacherProfile()
    Object.assign(form, data)
    Object.assign(originalForm, data)
  } catch (error) {
    console.error('Load profile failed:', error)
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped lang="scss">
.profile-container {
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

.profile-card {
  border-radius: 12px;
  border: 1px solid $border-color;
  max-width: 700px;

  :deep(.el-card__body) {
    padding: 32px 40px;

    @media (max-width: 640px) {
      padding: 20px 16px;
    }
  }
}

.profile-form {
  :deep(.el-form-item) {
    margin-bottom: 24px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: $text-primary;
  }

  .unit-text {
    margin-left: 8px;
    font-size: 14px;
    color: $text-secondary;
  }
}
</style>
