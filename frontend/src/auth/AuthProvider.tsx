import AuthContext from './AuthContext'
import { FC, useState } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { getAccessToken } from '../service/api-service'
import jwt, { JwtPayload } from 'jsonwebtoken'
import { AuthUser } from '../types/AuthUser'

const AuthProvider: FC = ({ children }) => {
  const [authUser, setAuthUser] = useState<AuthUser>()
  const [token, setToken] = useState<string>()

  const login = (credentialsDto: CredentialsDto) => {
    return getAccessToken(credentialsDto)
      .then(token => {
        setToken(token)
        return token
      })
      .then(decodeJwtClaims)
  }

  const logout = () => setAuthUser(undefined)

  const decodeJwtClaims = (token: string) => {
    const claims = jwt.decode(token) as JwtPayload
    if (claims !== null) {
      const loginName = claims.sub
      const authUser: AuthUser = {
        loginName: loginName,
        role: claims.role,
      }
      setAuthUser(authUser)
    }
  }

  return (
    <AuthContext.Provider value={{ authUser, login, logout, token }}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider
