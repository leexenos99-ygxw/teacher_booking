import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfoVO } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const tokenPrefix = ref<string>('Bearer')
  const userInfo = ref<UserInfoVO | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const userId = computed(() => userInfo.value?.userId || 0)

  function setToken(t: string, prefix: string) {
    token.value = t
    tokenPrefix.value = prefix
    localStorage.setItem('token', t)
    localStorage.setItem('tokenPrefix', prefix)
  }

  function setUserInfo(info: UserInfoVO) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    tokenPrefix.value = 'Bearer'
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('tokenPrefix')
    localStorage.removeItem('userInfo')
  }

  function initFromStorage() {
    const savedToken = localStorage.getItem('token')
    const savedPrefix = localStorage.getItem('tokenPrefix')
    const savedUserInfo = localStorage.getItem('userInfo')

    if (savedToken) {
      token.value = savedToken
    }
    if (savedPrefix) {
      tokenPrefix.value = savedPrefix
    }
    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (e) {
        console.error('Failed to parse userInfo from storage', e)
      }
    }
  }

  return {
    token,
    tokenPrefix,
    userInfo,
    isLoggedIn,
    role,
    userId,
    setToken,
    setUserInfo,
    logout,
    initFromStorage
  }
})
