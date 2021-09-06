import styled from 'styled-components/macro'
import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import AuthContext from '../auth/AuthContext'

const LoginPage: FC = () => {
  const { onLogin, authUser } = useContext(AuthContext)
  const [formData, setFormData] = useState<CredentialsDto>({
    loginName: '',
    password: '',
  })

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    if (onLogin) {
      onLogin(formData)
    }
  }

  if (authUser) {
    console.log(authUser)
  }

  return (
    <PageLayout>
      <header></header>
      <LoginForm onSubmit={submitHandler}>
        <input
          type="text"
          name="loginName"
          placeholder="Login Name"
          value={formData.loginName}
          onChange={handleInputChange}
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleInputChange}
        />
        <Button>Login</Button>
      </LoginForm>
    </PageLayout>
  )
}
export default LoginPage

const PageLayout = styled.section`
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  display: grid;
  grid-template-rows: 170px min-content;
`
const LoginForm = styled.form`
  display: grid;
  grid-gap: var(--size-l);
  justify-items: center;
`

const Button = styled.button``
