import { createContext } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { AuthUser } from '../types/AuthUser'

interface AuthContext {
  authUser?: AuthUser
  login?: (credentialsDto: CredentialsDto) => Promise<void>
  logout?: () => void
  token?: string | undefined
}

export default createContext<AuthContext>({})
