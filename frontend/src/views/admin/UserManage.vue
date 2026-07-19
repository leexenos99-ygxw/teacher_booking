<template>
  <div class="user-manage-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <p class="page-subtitle">管理平台所有用户账号</p>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称/手机号"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="searchForm.role"
            placeholder="全部角色"
            clearable
            style="width: 160px"
          >
            <el-option label="教师" value="TEACHER" />
            <el-option label="家长" value="PARENT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="userList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="36" :src="row.avatar">
                {{ row.nickname?.charAt(0) }}
              </el-avatar>
              <div class="user-detail">
                <div class="username">{{ row.nickname }}</div>
                <div class="user-account">{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 'ACTIVE' ? 'danger' : 'primary'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import type { User } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const userList = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  role: ''
})

function getRoleText(role: string) {
  const textMap: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    PARENT: '家长'
  }
  return textMap[role] || role
}

function getRoleTagType(role: string) {
  const typeMap: Record<string, string> = {
    ADMIN: 'danger',
    TEACHER: 'primary',
    PARENT: 'success'
  }
  return typeMap[role] || 'info'
}

function formatDateTime(time: string) {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

function handleSearch() {
  currentPage.value = 1
  loadUserList()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.role = ''
  currentPage.value = 1
  loadUserList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadUserList()
}

function handleCurrentChange(page: number) {
  currentPage.value = page
  loadUserList()
}

async function handleToggleStatus(row: User) {
  const action = row.status === 'ACTIVE' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    row.status = row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Toggle user status failed:', error)
    }
  }
}

function generateMockUsers(): User[] {
  const roles = ['TEACHER', 'PARENT']
  const statuses = ['ACTIVE', 'ACTIVE', 'ACTIVE', 'DISABLED']
  const users: User[] = []

  for (let i = 1; i <= 25; i++) {
    const role = roles[i % roles.length]
    users.push({
      id: i,
      username: `user${i}`,
      nickname: role === 'TEACHER' ? `张老师${i}号` : `李家长${i}号`,
      phone: `138${String(10000000 + i).slice(-8)}`,
      role,
      status: statuses[i % statuses.length],
      avatar: '',
      email: `user${i}@example.com`,
      createdTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss')
    })
  }

  return users
}

const allUsers = ref<User[]>([])

function loadUserList() {
  loading.value = true

  setTimeout(() => {
    if (allUsers.value.length === 0) {
      allUsers.value = generateMockUsers()
    }

    let filtered = [...allUsers.value]

    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      filtered = filtered.filter(
        (u) =>
          u.username.toLowerCase().includes(keyword) ||
          u.nickname.toLowerCase().includes(keyword) ||
          u.phone.includes(keyword)
      )
    }

    if (searchForm.role) {
      filtered = filtered.filter((u) => u.role === searchForm.role)
    }

    total.value = filtered.length

    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    userList.value = filtered.slice(start, end)

    loading.value = false
  }, 300)
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped lang="scss">
.user-manage-container {
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

.search-card,
.table-card {
  border-radius: 12px;
  border: 1px solid $border-color;
}

.search-card {
  :deep(.el-card__body) {
    padding: 16px 20px 0;
  }
}

.table-card {
  :deep(.el-card__body) {
    padding: 0;
  }
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-detail {
    .username {
      font-size: 14px;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: 2px;
    }

    .user-account {
      font-size: 12px;
      color: $text-secondary;
    }
  }
}

.pagination-wrapper {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
