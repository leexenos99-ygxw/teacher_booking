<template>
  <div class="teacher-list-container">
    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索教师姓名、学校"
          :prefix-icon="Search"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-select v-model="selectedSubject" placeholder="选择学科" class="subject-select" @change="handleSearch">
          <el-option label="全部学科" value="" />
          <el-option label="语文" value="语文" />
          <el-option label="数学" value="数学" />
          <el-option label="英语" value="英语" />
          <el-option label="物理" value="物理" />
          <el-option label="化学" value="化学" />
          <el-option label="生物" value="生物" />
          <el-option label="历史" value="历史" />
          <el-option label="地理" value="地理" />
          <el-option label="政治" value="政治" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
    </el-card>

    <div class="teacher-grid">
      <el-card
        v-for="teacher in teacherList"
        :key="teacher.id"
        class="teacher-card"
        shadow="hover"
        @click="goToDetail(teacher.id)"
      >
        <div class="teacher-info">
          <el-avatar :size="64" :src="teacher.avatar">
            {{ teacher.realName?.charAt(0) }}
          </el-avatar>
          <div class="teacher-basic">
            <div class="teacher-name-row">
              <h3 class="teacher-name">{{ teacher.realName }}</h3>
              <el-tag type="primary" size="small">{{ teacher.subject }}</el-tag>
            </div>
            <div class="teacher-school">
              <el-icon><School /></el-icon>
              <span>{{ teacher.school }}</span>
            </div>
            <div class="teacher-title">{{ teacher.title }}</div>
          </div>
        </div>
        <div class="teacher-rating">
          <div class="rating-score">
            <el-icon color="#F5A623"><Star /></el-icon>
            <span>{{ teacher.rating?.toFixed(1) || '5.0' }}</span>
          </div>
          <div class="rating-count">{{ teacher.reviewCount || 0 }}条评价</div>
        </div>
        <div class="teacher-desc">
          {{ teacher.description || '暂无简介' }}
        </div>
        <div class="teacher-footer">
          <div class="teacher-stats">
            <span>教龄 {{ teacher.teachingYears || 0 }}年</span>
            <span>·</span>
            <span>{{ teacher.appointmentCount || 0 }}次预约</span>
          </div>
          <el-button type="primary" size="small" @click.stop="goToDetail(teacher.id)">立即预约</el-button>
        </div>
      </el-card>
    </div>

    <div v-if="teacherList.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无符合条件的教师" />
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 12, 16, 20]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, School, Star } from '@element-plus/icons-vue'
import type { Teacher } from '@/types'

const router = useRouter()

const searchKeyword = ref('')
const selectedSubject = ref('')
const teacherList = ref<Teacher[]>([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const loading = ref(false)

const mockTeachers: Teacher[] = [
  {
    id: 1,
    userId: 1,
    realName: '张老师',
    subject: '数学',
    school: '第一中学',
    title: '高级教师',
    description: '从事数学教学15年，擅长初高中数学辅导，教学方法独特，深受学生喜爱。',
    teachingYears: 15,
    rating: 4.9,
    reviewCount: 128,
    appointmentCount: 356,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-01-15'
  },
  {
    id: 2,
    userId: 2,
    realName: '李老师',
    subject: '英语',
    school: '第二中学',
    title: '特级教师',
    description: '英语专业硕士，拥有丰富的教学经验，擅长听说读写全面提升。',
    teachingYears: 12,
    rating: 4.8,
    reviewCount: 96,
    appointmentCount: 280,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-02-20'
  },
  {
    id: 3,
    userId: 3,
    realName: '王老师',
    subject: '语文',
    school: '实验中学',
    title: '一级教师',
    description: '语文学科带头人，注重培养学生的阅读和写作能力，教学风格生动有趣。',
    teachingYears: 10,
    rating: 4.7,
    reviewCount: 85,
    appointmentCount: 220,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-03-10'
  },
  {
    id: 4,
    userId: 4,
    realName: '赵老师',
    subject: '物理',
    school: '第一中学',
    title: '高级教师',
    description: '物理教学专家，擅长将复杂的物理概念简单化，让学生轻松掌握。',
    teachingYears: 18,
    rating: 4.9,
    reviewCount: 156,
    appointmentCount: 420,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-01-05'
  },
  {
    id: 5,
    userId: 5,
    realName: '陈老师',
    subject: '化学',
    school: '第三中学',
    title: '一级教师',
    description: '化学实验教学能手，通过生动的实验让学生爱上化学。',
    teachingYears: 8,
    rating: 4.6,
    reviewCount: 67,
    appointmentCount: 180,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-04-15'
  },
  {
    id: 6,
    userId: 6,
    realName: '刘老师',
    subject: '历史',
    school: '实验中学',
    title: '高级教师',
    description: '历史学科资深教师，善于用故事讲述历史，让学生在趣味中学习。',
    teachingYears: 20,
    rating: 4.8,
    reviewCount: 112,
    appointmentCount: 300,
    auditStatus: 'APPROVED',
    auditRemark: '',
    createdTime: '2024-02-01'
  }
]

function handleSearch() {
  currentPage.value = 1
  fetchTeacherList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  fetchTeacherList()
}

function handleCurrentChange(page: number) {
  currentPage.value = page
  fetchTeacherList()
}

function fetchTeacherList() {
  loading.value = true

  setTimeout(() => {
    let filtered = [...mockTeachers]

    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter(
        t => t.realName.toLowerCase().includes(keyword) || t.school.toLowerCase().includes(keyword)
      )
    }

    if (selectedSubject.value) {
      filtered = filtered.filter(t => t.subject === selectedSubject.value)
    }

    total.value = filtered.length
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    teacherList.value = filtered.slice(start, end)

    loading.value = false
  }, 300)
}

function goToDetail(id: number) {
  router.push(`/teachers/${id}`)
}

onMounted(() => {
  fetchTeacherList()
})
</script>

<style scoped lang="scss">
.teacher-list-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-card {
  border-radius: 12px;
  border: 1px solid $border-color;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;

  .search-input {
    flex: 1;
    max-width: 400px;
  }

  .subject-select {
    width: 160px;
  }

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: stretch;

    .search-input,
    .subject-select {
      max-width: none;
      width: 100%;
    }
  }
}

.teacher-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.teacher-card {
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid $border-color;

  &:hover {
    transform: translateY(-2px);
  }

  :deep(.el-card__body) {
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}

.teacher-info {
  display: flex;
  gap: 16px;
  align-items: flex-start;

  .teacher-basic {
    flex: 1;
    min-width: 0;
  }
}

.teacher-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;

  .teacher-name {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }
}

.teacher-school {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: $text-secondary;
  margin-bottom: 4px;
}

.teacher-title {
  font-size: 13px;
  color: $text-placeholder;
}

.teacher-rating {
  text-align: right;
  flex-shrink: 0;

  .rating-score {
    display: flex;
    align-items: center;
    gap: 4px;
    justify-content: flex-end;
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
  }

  .rating-count {
    font-size: 12px;
    color: $text-secondary;
    margin-top: 2px;
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
  padding-top: 12px;
  border-top: 1px solid $border-color;

  .teacher-stats {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: $text-secondary;
  }
}

.empty-state {
  padding: 60px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 8px;
}
</style>
