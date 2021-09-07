import React from 'react'
import LoginPage from './pages/LoginPage'
import AuthProvider from './auth/AuthProvider'
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import NewProjectPage from './pages/NewProjectPage'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Switch>
          <Route path={'/new-project'}>
            <NewProjectPage />
          </Route>
          <Route path={'/'}>
            <LoginPage />
          </Route>
        </Switch>
      </Router>
    </AuthProvider>
  )
}

export default App
