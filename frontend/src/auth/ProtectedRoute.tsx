import { FC, useContext } from 'react'
import AuthContext from './AuthContext'
import { Redirect, Route, RouteProps } from 'react-router-dom'

interface ProtectedRouteProps extends RouteProps {
  path: string
  adminOnly?: boolean
}

const ProtectedRoute: FC<ProtectedRouteProps> = ({ adminOnly, ...props }) => {
  const { authUser } = useContext(AuthContext)
  if (!authUser) {
    return <Redirect to="/login" />
  }
  if (adminOnly && authUser.role !== 'ADMIN') {
    return <Redirect to="/" />
  }
  return <Route {...props} />
}
export default ProtectedRoute
