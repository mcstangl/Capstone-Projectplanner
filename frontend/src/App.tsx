import React from 'react'
import LoginPage from './pages/LoginPage'
import AuthProvider from './auth/AuthProvider'
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import NewProjectPage from './pages/NewProjectPage'
import ProjectListPage from './pages/ProjectListPage'
import Homepage from './pages/Homepage'
import ProtectedRoute from './auth/ProtectedRoute'
import ProjectDetailsPage from './pages/ProjectDetailsPage'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Switch>
          <Route path="/login" component={LoginPage} />
          <ProtectedRoute
            adminOnly={true}
            path="/new-project"
            component={NewProjectPage}
          />
          <ProtectedRoute
            path="/projects/:projectTitle"
            component={ProjectDetailsPage}
          />
          <ProtectedRoute path="/projects" component={ProjectListPage} />
          <ProtectedRoute path="/" component={Homepage} />
        </Switch>
      </Router>
    </AuthProvider>
  )
}

export default App
