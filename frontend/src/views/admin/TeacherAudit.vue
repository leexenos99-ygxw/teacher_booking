<template>
  <div class="teacher-audit-container">
    <div class="page-header">
      <h2 class="page-title">教师审核</h2>
      <p class="page-subtitle">审核教师注册申请</p>
    </div>

    <el-card class="audit-card" shadow="never">
      <el-tabs v-model="activeTab" class="audit-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="待审核" name="PENDING" />
        <el-tab-pane label="已通过" name="APPROVED" />
        <el-tab-pane label="已拒绝" name="REJECTED" />
      </el-tabs>

      <el-empty v-if="teacherList.length === 0" description="暂无数据" />

      <div v-else class="teacher-list">
        <div
          v-for="teacher in teacherList"
          :key="teacher.id"
          class="teacher-item"
        >
          <div class="teacher-info">
            <el-avatar :size="56">
              {{ teacher.realName?.charAt(0) }}
            </el-avatar>
            <div class="teacher-detail">
              <div class="teacher-name-row">
                <span class="teacher-name">{{ teacher.realName }}</span>
                <el-tag :type="getStatusType(teacher.auditStatus)" size="small">
                  {{ getStatusText(teacher.auditStatus) }}
                </el-tag>
              </div>
              <div class="teacher-meta">
                <span class="meta-item">
                  <el-icon color="#06B2B5"><Reading /></el-icon>
                  {{ teacher.subject }}
                </span>
                <span class="meta-item">
                  <el-icon color="#06B2B5"><School /></el-icon>
                  {{ teacher.school }}
                </span>
                <span class="meta-item">
                  <el-icon color="#06B2B5"><Medal /></el-icon>
                  {{ teacher.title }}
                </span>
                <span class="meta-item">
                  <el-icon color="#06B2B5"><Clock /></el-icon>
                  {{ teacher.teachingYears }}年教龄
                </span>
              </div>
              <div class="teacher-desc">
                {{ teacher.description }}
              </div>
            </div>
          </div>

          <div class="teacher-footer">
            <div class="apply-time">
              申请时间：{{ formatDateTime(teacher.createdTime) }}
            </div>
            <div class="audit-actions" v-if="teacher.auditStatus === 'PENDING'">
              <el-button type="primary" size="small" @click="handleApprove(teacher.id)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="handleReject(teacher.id)">
                拒绝
              </el-button>
            </div>
            <div v-else-if="teacher.auditStatus === 'REJECTED'" class="reject-reason">
              拒绝原因：{{ teacher.auditRemark }}
            </div>
          </div>
        </div>
      </div>

      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝审核"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="拒绝原因" prop="remark">
          <el-input
            v-model="rejectForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="confirmReject">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Reading, School, Medal, Clock } from '@element-plus/icons-vue'
import type { Teacher } from '@/types'
import dayjs from 'dayjs'

const activeTab = ref('PENDING')
const teacherList = ref<Teacher[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rejectDialogVisible = ref(false)
const submitting = ref(false)
const rejectFormRef = ref<FormInstance>()
const currentRejectId = ref<number | null>(null)

const rejectForm = reactive({
  remark: ''
})

const rejectRules = reactive<FormRules>({
  remark: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
})

function getStatusType(status: string) {
  const typeMap: Record<string, string> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return typeMap[status] || 'info'
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝'
  }
  return textMap[status] || status
}

function formatDateTime(time: string) {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

function handleTabChange() {
  currentPage.value = 1
  loadTeacherList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadTeacherList()
}

function handleCurrentChange(page: number) {
  currentPage.value = page
  loadTeacherList()
}

async function handleApprove(id: number) {
  try {
    await ElMessageBox.confirm('确定要通过该教师的审核吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })

    const teacher = teacherList.value.find((t) => t.id === id)
    if (teacher) {
      teacher.auditStatus = 'APPROVED'
    }
    ElMessage.success('审核通过')
    loadTeacherList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Approve teacher failed:', error)
    }
  }
}

function handleReject(id: number) {
  currentRejectId.value = id
  rejectForm.remark = ''
  rejectDialogVisible.value = true
}

async function confirmReject() {
  if (!rejectFormRef.value || !currentRejectId.value) return

  try {
    await rejectFormRef.value.validate()
    submitting.value = true

    const teacher = teacherList.value.find((t) => t.id === currentRejectId.value)
    if (teacher) {
      teacher.auditStatus = 'REJECTED'
      teacher.auditRemark = rejectForm.remark
    }

    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    loadTeacherList()
  } catch (error) {
    console.error('Reject teacher failed:', error)
  } finally {
    submitting.value = false
  }
}

function generateMockTeachers(): Teacher[] {
  const subjects = ['语文', '数学', '英语', '物理', '化学', '生物']
  const schools = ['第一中学', '实验中学', '外国语学校', '育才中学', '华师大附中']
  const titles = ['初级教师', '中级教师', '高级教师', '特级教师']
  const statuses = ['PENDING', 'APPROVED', 'REJECTED']
  const teachers: Teacher[] = []

  const names = ['张明', '李华', '王芳', '刘伟', '陈静', '杨帆', '赵磊', '周婷', '吴涛', '郑敏',
    '孙浩', '马超', '朱琳', '胡军', '林峰']

  for (let i = 0; i < 15; i++) {
    const status = statuses[i % 3]
    teachers.push({
      id: i + 1,
      userId: 100 + i,
      realName: names[i],
      subject: subjects[i % subjects.length],
      school: schools[i % schools.length],
      title: titles[i % titles.length],
      description: `本人从事教育工作多年，具有丰富的教学经验。教学风格生动活泼，注重培养学生的学习兴趣和思维能力。曾多次获得优秀教师称号。`,
      teachingYears: (i % 20) + 1,
      rating: 4.5 + (i % 5) * 0.1,
      reviewCount: Math.floor(Math.random() * 50) + 10,
      appointmentCount: Math.floor(Math.random() * 100) + 20,
      auditStatus: status,
      auditRemark: status === 'REJECTED' ? '提交的教师资格证书不清晰，请重新上传。' : '',
      createdTime: dayjs().subtract(i + 1, 'day').format('YYYY-MM-DD HH:mm:ss')
    })
  }

  return teachers
}

const allTeachers = ref<Teacher[]>([])

function loadTeacherList() {
  if (allTeachers.value.length === 0) {
    allTeachers.value = generateMockTeachers()
  }

  const filtered = allTeachers.value.filter((t) => t.auditStatus === activeTab.value)
  total.value = filtered.length

  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  teacherList.value = filtered.slice(start, end)
}

onMounted(() => {
  loadTeacherList()
})
</script>

<style scoped lang="scss">
.teacher-audit-container {
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

.audit-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 0;
  }
}

.audit-tabs {
  padding: 0 20px;
  border-bottom: 1px solid $border-color;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }
}

.teacher-list {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.teacher-item {
  border: 1px solid $border-color;
  border-radius: 10px;
  padding: 20px;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
}

.teacher-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;

  @media (max-width: 640px) {
    flex-direction: column;
  }
}

.teacher-detail {
  flex: 1;
  min-width: 0;
}

.teacher-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;

  .teacher-name {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
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
    font-size: 13px;
    color: $text-secondary;
  }
}

.teacher-desc {
  font-size: 13px;
  color: $text-secondary;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.teacher-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid $border-color;
  gap: 12px;

  @media (max-width: 640px) {
    flex-direction: column;
    align-items: flex-start;
  }

  .apply-time {
    font-size: 12px;
    color: $text-placeholder;
  }

  .audit-actions {
    display: flex;
    gap: 8px;
  }

  .reject-reason {
    font-size: 13px;
    color: $danger-color;
  }
}

.pagination-wrapper {
  padding: 16px 20px 20px;
  display: flex;
  justify-content: center;
}
</style>
