import React from 'react'
import LoginPage from './pages/LoginPage'
import AuthProvider from './auth/AuthProvider'

function App() {
  return (
    <AuthProvider>
      <section>
        <LoginPage />
      </section>
    </AuthProvider>
  )
}

export default App
