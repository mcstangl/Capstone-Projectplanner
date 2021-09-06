import { createContext } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { AuthUser } from '../types/AuthUser'

interface AuthContext {
  authUser?: AuthUser
  onLogin?: (credentialsDto: CredentialsDto) => void
}

export default createContext<AuthContext>({})
