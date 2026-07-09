import { defineStore } from 'pinia'

export interface LoginUserState {
  token: string
  userId: string
  phone: string
  nickname: string
  role: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('haoclass_token') || '',
    userId: localStorage.getItem('haoclass_user_id') || '',
    phone: localStorage.getItem('haoclass_phone') || '',
    nickname: localStorage.getItem('haoclass_nickname') || '',
    role: localStorage.getItem('haoclass_role') || ''
  }),
  actions: {
    login(user: LoginUserState) {
      this.token = user.token
      this.userId = user.userId
      this.phone = user.phone
      this.nickname = user.nickname
      this.role = user.role
      localStorage.setItem('haoclass_token', user.token)
      localStorage.setItem('haoclass_user_id', user.userId)
      localStorage.setItem('haoclass_phone', user.phone)
      localStorage.setItem('haoclass_nickname', user.nickname)
      localStorage.setItem('haoclass_role', user.role)
    },
    logout() {
      this.token = ''
      this.userId = ''
      this.phone = ''
      this.nickname = ''
      this.role = ''
      localStorage.removeItem('haoclass_token')
      localStorage.removeItem('haoclass_user_id')
      localStorage.removeItem('haoclass_phone')
      localStorage.removeItem('haoclass_nickname')
      localStorage.removeItem('haoclass_role')
    }
  }
})
