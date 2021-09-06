import AuthContext from './AuthContext'
import { FC, useState } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { getAccessToken } from '../service/api-service'
import jwt from 'jsonwebtoken'
import { AuthUser } from '../types/AuthUser'

const AuthProvider: FC = ({ children }) => {
  const [authUser, setAuthUser] = useState<AuthUser>()

  const onLogin = (credentialsDto: CredentialsDto) => {
    getAccessToken(credentialsDto).then(decodeJwtClaims).catch(console.error)
  }

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
    <AuthContext.Provider value={{ authUser, onLogin }}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider
