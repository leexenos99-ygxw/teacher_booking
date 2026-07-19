import axios, { type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Result } from '@/types'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `${userStore.tokenPrefix} ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data as Result
    if (res.code !== 200) {
      if (res.code === 401) {
        ElMessageBox.confirm('登录状态已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
        })
        return Promise.reject(new Error(res.message))
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const res = error.response.data as Result
      if (status === 401) {
        ElMessageBox.confirm('登录状态已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
        })
        return Promise.reject(new Error(res?.message || '未授权'))
      }
      if (status === 403) {
        ElMessage.error(res?.message || '没有访问权限')
        return Promise.reject(new Error(res?.message || '没有访问权限'))
      }
      if (status === 400) {
        ElMessage.error(res?.message || '参数错误')
        return Promise.reject(new Error(res?.message || '参数错误'))
      }
      ElMessage.error(res?.message || error.message || '网络错误')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export function request<T = any>(config: AxiosRequestConfig): Promise<T> {
  return service.request<Result<T>, T>(config)
}

export default service
