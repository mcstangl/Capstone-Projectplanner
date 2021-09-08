import React from 'react'
import LoginPage from './pages/LoginPage'
import AuthProvider from './auth/AuthProvider'
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import NewProjectPage from './pages/NewProjectPage'
import ProjectListPage from './pages/ProjectListPage'
import Homepage from './pages/Homepage'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Switch>
          <Route path={'/new-project'}>
            <NewProjectPage />
          </Route>
          <Route path={'/projects'}>
            <ProjectListPage />
          </Route>
          <Route path={'/login'}>
            <LoginPage />
          </Route>
          <Route path={'/'}>
            <Homepage />
          </Route>
        </Switch>
      </Router>
    </AuthProvider>
  )
}

export default App
