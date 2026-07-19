import { request } from '@/utils/request'
import type { Appointment, Message, PageResult, Evaluation } from '@/types'

export function createAppointment(data: Partial<Appointment>) {
  return request<Appointment>({
    url: '/parent/appointments',
    method: 'post',
    data
  })
}

export function getParentAppointments(params: {
  page: number
  size: number
  status?: string
}) {
  return request<PageResult<Appointment>>({
    url: '/parent/appointments/list',
    method: 'get',
    params
  })
}

export function getParentAppointmentDetail(appointmentId: number) {
  return request<Appointment>({
    url: `/parent/appointments/${appointmentId}`,
    method: 'get'
  })
}

export function cancelParentAppointment(appointmentId: number, reason?: string) {
  return request<void>({
    url: `/parent/appointments/${appointmentId}/cancel`,
    method: 'post',
    data: { reason }
  })
}

export function getTeacherAppointments(params: {
  page: number
  size: number
  status?: string
}) {
  return request<PageResult<Appointment>>({
    url: '/teacher/appointments/list',
    method: 'get',
    params
  })
}

export function getTeacherAppointmentDetail(appointmentId: number) {
  return request<Appointment>({
    url: `/teacher/appointments/${appointmentId}`,
    method: 'get'
  })
}

export function acceptAppointment(appointmentId: number) {
  return request<void>({
    url: `/teacher/appointments/${appointmentId}/accept`,
    method: 'post'
  })
}

export function rejectAppointment(appointmentId: number, reason?: string) {
  return request<void>({
    url: `/teacher/appointments/${appointmentId}/reject`,
    method: 'post',
    data: { reason }
  })
}

export function cancelTeacherAppointment(appointmentId: number, reason?: string) {
  return request<void>({
    url: `/teacher/appointments/${appointmentId}/cancel`,
    method: 'post',
    data: { reason }
  })
}

export function finishAppointment(appointmentId: number) {
  return request<void>({
    url: `/teacher/appointments/${appointmentId}/finish`,
    method: 'post'
  })
}

export function getAppointmentMessages(appointmentId: number, isTeacher = false) {
  const prefix = isTeacher ? '/teacher' : '/parent'
  return request<Message[]>({
    url: `${prefix}/appointments/${appointmentId}/messages`,
    method: 'get'
  })
}

export function sendAppointmentMessage(
  appointmentId: number,
  content: string,
  type: string = 'TEXT',
  isTeacher = false
) {
  const prefix = isTeacher ? '/teacher' : '/parent'
  return request<Message>({
    url: `${prefix}/appointments/${appointmentId}/messages`,
    method: 'post',
    data: { content, type }
  })
}

export function createEvaluation(data: Partial<Evaluation>) {
  return request<Evaluation>({
    url: '/parent/evaluations',
    method: 'post',
    data
  })
}

export function getTeacherEvaluations(teacherId: number, params: {
  page: number
  size: number
}) {
  return request<PageResult<Evaluation>>({
    url: `/public/teachers/${teacherId}/evaluations`,
    method: 'get',
    params
  })
}
