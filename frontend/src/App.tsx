import React from 'react'
import LoginPage from './pages/LoginPage'
import AuthProvider from './auth/AuthProvider'
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import NewProjectPage from './pages/NewProjectPage'
import ProjectListPage from './pages/ProjectListPage'
import ProtectedRoute from './auth/ProtectedRoute'
import ProjectDetailsPage from './pages/ProjectDetailsPage'
import UserListPage from './pages/UserListPage'
import NewUserPage from './pages/NewUserPage'
import UserDetailPage from './pages/UserDetailPage'
import MyAccountPage from './pages/MyAccountPage'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Switch>
          <Route path="/login" component={LoginPage} />
          <ProtectedRoute
            adminOnly
            path="/new-project"
            component={NewProjectPage}
          />
          <ProtectedRoute
            path="/projects/:projectTitle"
            component={ProjectDetailsPage}
          />
          <ProtectedRoute path="/projects" component={ProjectListPage} />
          <ProtectedRoute
            adminOnly
            path="/users/:loginName"
            component={UserDetailPage}
          />
          <ProtectedRoute path="/my-account" component={MyAccountPage} />
          <ProtectedRoute adminOnly path="/users" component={UserListPage} />
          <ProtectedRoute adminOnly path="/new-user" component={NewUserPage} />
          <ProtectedRoute path="/" component={ProjectListPage} />
        </Switch>
      </Router>
    </AuthProvider>
  )
}

export default App
