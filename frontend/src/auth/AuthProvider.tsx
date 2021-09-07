import AuthContext from './AuthContext'
import { FC, useState } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { getAccessToken } from '../service/api-service'
import jwt from 'jsonwebtoken'
import { AuthUser } from '../types/AuthUser'

const AuthProvider: FC = ({ children }) => {
  const [authUser, setAuthUser] = useState<AuthUser>()

  const login = (credentialsDto: CredentialsDto) => {
    return getAccessToken(credentialsDto).then(decodeJwtClaims)
  }

  const logout = () => setAuthUser(undefined)

  const decodeJwtClaims = (token: string) => {
    const claims = jwt.decode(token)
    if (claims !== null) {
      const loginName = claims.sub
      const authUser: AuthUser = {
        loginName: loginName,
        role: 'ADMIN',
      }
      setAuthUser(authUser)
    }
  }

  return (
    <AuthContext.Provider value={{ authUser, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider
