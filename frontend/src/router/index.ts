import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', public: true }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'ParentDashboard',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: { role: 'PARENT' },
    children: [
      {
        path: '',
        name: 'ParentHome',
        component: () => import('@/views/parent/Dashboard.vue'),
        meta: { title: '首页', role: 'PARENT' }
      }
    ]
  },
  {
    path: '/teachers',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: { role: 'PARENT' },
    children: [
      {
        path: '',
        name: 'TeacherList',
        component: () => import('@/views/parent/TeacherList.vue'),
        meta: { title: '教师列表', role: 'PARENT' }
      },
      {
        path: ':id',
        name: 'TeacherDetail',
        component: () => import('@/views/parent/TeacherDetail.vue'),
        meta: { title: '教师详情', role: 'PARENT' }
      }
    ]
  },
  {
    path: '/appointments',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: { role: 'PARENT' },
    children: [
      {
        path: '',
        name: 'ParentAppointments',
        component: () => import('@/views/parent/AppointmentList.vue'),
        meta: { title: '我的预约', role: 'PARENT' }
      }
    ]
  },
  {
    path: '/chat/:appointmentId',
    name: 'Chat',
    component: () => import('@/views/Chat.vue'),
    meta: { title: '在线沟通' }
  },
  {
    path: '/teacher/dashboard',
    component: () => import('@/layouts/TeacherLayout.vue'),
    meta: { role: 'TEACHER' },
    children: [
      {
        path: '',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/Dashboard.vue'),
        meta: { title: '工作台', role: 'TEACHER' }
      }
    ]
  },
  {
    path: '/teacher/calendar',
    component: () => import('@/layouts/TeacherLayout.vue'),
    meta: { role: 'TEACHER' },
    children: [
      {
        path: '',
        name: 'TeacherCalendar',
        component: () => import('@/views/teacher/Calendar.vue'),
        meta: { title: '时间管理', role: 'TEACHER' }
      }
    ]
  },
  {
    path: '/teacher/appointments',
    component: () => import('@/layouts/TeacherLayout.vue'),
    meta: { role: 'TEACHER' },
    children: [
      {
        path: '',
        name: 'TeacherAppointments',
        component: () => import('@/views/teacher/AppointmentManage.vue'),
        meta: { title: '预约管理', role: 'TEACHER' }
      }
    ]
  },
  {
    path: '/teacher/profile',
    component: () => import('@/layouts/TeacherLayout.vue'),
    meta: { role: 'TEACHER' },
    children: [
      {
        path: '',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/Profile.vue'),
        meta: { title: '个人资料', role: 'TEACHER' }
      }
    ]
  },
  {
    path: '/admin/dashboard',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据统计', role: 'ADMIN' }
      }
    ]
  },
  {
    path: '/admin/users',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      {
        path: '',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', role: 'ADMIN' }
      }
    ]
  },
  {
    path: '/admin/teachers',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      {
        path: 'audit',
        name: 'TeacherAudit',
        component: () => import('@/views/admin/TeacherAudit.vue'),
        meta: { title: '教师审核', role: 'ADMIN' }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在', public: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  userStore.initFromStorage()

  const title = to.meta.title as string
  if (title) {
    document.title = `${title} - 教师交流预约管理系统`
  } else {
    document.title = '教师交流预约管理系统'
  }

  if (to.meta.public) {
    next()
    return
  }

  if (!userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  const requiredRole = to.meta.role as string
  if (requiredRole && userStore.role !== requiredRole && userStore.role !== 'ADMIN') {
    if (userStore.role === 'TEACHER') {
      next('/teacher/dashboard')
    } else if (userStore.role === 'ADMIN') {
      next('/admin/dashboard')
    } else {
      next('/dashboard')
    }
    return
  }

  next()
})

export default router
