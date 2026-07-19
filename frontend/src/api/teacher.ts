import { request } from '@/utils/request'
import type { Teacher, TeacherCalendar, PageResult } from '@/types'

export function getTeacherList(params: {
  page: number
  size: number
  subject?: string
  keyword?: string
}) {
  return request<PageResult<Teacher>>({
    url: '/public/teachers/list',
    method: 'get',
    params
  })
}

export function getTeacherDetail(teacherId: number) {
  return request<Teacher>({
    url: `/public/teachers/${teacherId}`,
    method: 'get'
  })
}

export function getTeacherCalendar(teacherId: number, params?: {
  startDate?: string
  endDate?: string
}) {
  return request<TeacherCalendar[]>({
    url: `/public/teachers/${teacherId}/calendar`,
    method: 'get',
    params
  })
}

export function getTeacherProfile() {
  return request<Teacher>({
    url: '/teacher/profile',
    method: 'get'
  })
}

export function updateTeacherProfile(data: Partial<Teacher>) {
  return request<void>({
    url: '/teacher/profile',
    method: 'put',
    data
  })
}

export function getMyCalendar(params?: {
  startDate?: string
  endDate?: string
}) {
  return request<TeacherCalendar[]>({
    url: '/teacher/calendar',
    method: 'get',
    params
  })
}

export function setCalendar(data: TeacherCalendar[]) {
  return request<void>({
    url: '/teacher/calendar',
    method: 'post',
    data
  })
}
