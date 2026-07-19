import { request } from '@/utils/request'
import type { LoginDTO, RegisterDTO, LoginVO, UserInfoVO } from '@/types'

export function login(data: LoginDTO) {
  return request<LoginVO>({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data: RegisterDTO) {
  return request<void>({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request<UserInfoVO>({
    url: '/auth/userInfo',
    method: 'get'
  })
}

export function logout() {
  return request<void>({
    url: '/auth/logout',
    method: 'post'
  })
}
