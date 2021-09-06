import styled from 'styled-components/macro'
import { ChangeEvent, FormEvent, useState } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { getAccessToken } from '../service/api-service'

interface Credentials {
  loginName: string
  password: string
}

export default function LoginPage() {
  const [formData, setFormData] = useState<Credentials>({
    loginName: '',
    password: '',
  })

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    const credentialsDto = new CredentialsDto(
      formData.loginName,
      formData.password
    )
    getAccessToken(credentialsDto).catch(console.error)
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
          type="text"
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
