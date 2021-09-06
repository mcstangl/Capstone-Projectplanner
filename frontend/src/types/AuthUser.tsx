export interface AuthUser {
  loginName: string | (() => string) | undefined
  role: string
}
