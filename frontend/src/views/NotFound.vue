<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h2 class="title">页面不存在</h2>
      <p class="description">抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" size="large" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped lang="scss">
.not-found-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-page;
}

.not-found-content {
  text-align: center;

  .error-code {
    font-size: 120px;
    font-weight: 700;
    color: $primary-color;
    line-height: 1;
    margin-bottom: 16px;
    background: linear-gradient(135deg, #06B2B5, #63D5D6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .title {
    font-size: 24px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .description {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 32px;
  }
}
</style>
