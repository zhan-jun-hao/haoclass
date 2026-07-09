export type UserRole = 0 | 1

export type UserStatus = 0 | 1

export interface User {
  id: string
  phone: string
  nickname: string
  avatarUrl: string
  role: UserRole
  status: UserStatus
  vipExpireTime?: string
  lastLoginTime?: string
}

export interface UserPageQuery {
  current: number
  size: number
  phone?: string
  nickname?: string
  role?: UserRole
  status?: UserStatus
}

export interface UserCreatePayload {
  phone: string
  nickname: string
  avatarUrl?: string
  password: string
  role: UserRole
  status?: UserStatus
  vipExpireTime?: string
}

export interface UserUpdatePayload {
  nickname: string
  avatarUrl?: string
  role: UserRole
  status?: UserStatus
  vipExpireTime?: string
}

export interface UserPasswordPayload {
  password: string
}
