export interface Result<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface LoginDTO {
  username: string
  password: string
}

export interface RegisterDTO {
  username: string
  password: string
  confirmPassword: string
  phone?: string
  role: string
  nickname?: string
  realName?: string
}

export interface LoginVO {
  token: string
  tokenPrefix: string
  expiresIn: number
  userInfo: UserInfoVO
}

export interface UserInfoVO {
  userId: number
  username: string
  nickname: string
  avatar: string
  phone: string
  role: string
  roleName: string
}

export interface Teacher {
  id: number
  userId: number
  realName: string
  subject: string
  school: string
  title: string
  description: string
  teachingYears: number
  rating: number
  reviewCount: number
  appointmentCount: number
  auditStatus: string
  auditRemark: string
  createdTime: string
}

export interface TeacherCalendar {
  id: number
  teacherId: number
  date: string
  startTime: string
  endTime: string
  status: string
  capacity: number
  bookedCount: number
  duration: number
  remark: string
}

export interface Appointment {
  id: number
  appointmentNo: string
  teacherId: number
  parentId: number
  studentId: number
  calendarId: number
  startTime: string
  endTime: string
  status: string
  reason: string
  duration: number
  rejectReason: string
  cancelReason: string
  cancelBy: string
  actualStartTime: string
  actualEndTime: string
  summary: string
  createdTime: string
}

export interface Message {
  id: number
  appointmentId: number
  senderId: number
  receiverId: number
  content: string
  type: string
  isRead: number
  readTime: string
  createdTime: string
}

export interface Evaluation {
  id: number
  appointmentId: number
  parentId: number
  teacherId: number
  rating: number
  content: string
  tags: string
  createdTime: string
}

export interface Notification {
  id: number
  userId: number
  title: string
  content: string
  type: string
  bizId: number
  isRead: number
  readTime: string
  createdTime: string
}

export interface User {
  id: number
  username: string
  phone: string
  role: string
  status: string
  avatar: string
  nickname: string
  email: string
  createdTime: string
}
