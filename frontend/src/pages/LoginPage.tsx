import styled from 'styled-components/macro'
import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { CredentialsDto } from '../dtos/CredentialsDto'
import AuthContext from '../auth/AuthContext'
import Header from '../components/Header'
import { PageLayout } from '../components/PageLayout'
import { Redirect } from 'react-router-dom'
import { Button } from '../components/Button'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import Loader from '../components/Loader'
import MainStyle from '../components/MainStyle'
import { InputField } from '../components/Inputfield'

const LoginPage: FC = () => {
  const { login, authUser } = useContext(AuthContext)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<RestExceptionDto>()
  const [formData, setFormData] = useState<CredentialsDto>({
    loginName: '',
    password: '',
  })

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    setLoading(true)
    setError(undefined)
    if (login && formData.loginName.trim() && formData.password.trim()) {
      const credentialsDto: CredentialsDto = {
        loginName: formData.loginName.trim(),
        password: formData.password.trim(),
      }
      login(credentialsDto).catch(error => {
        setLoading(false)
        setError(error.response.data)
      })
    }
  }

  if (authUser) {
    return <Redirect to={'/projects'} />
  }

  return (
    <PageLayout>
      <Header />
      <MainStyle>
        {loading && <Loader />}
        {!loading && (
          <LoginForm onSubmit={submitHandler}>
            <section>
              <label htmlFor="loginName"> Benutzername</label>
              <InputField
                id="loginName"
                type="text"
                name="loginName"
                value={formData.loginName}
                onChange={handleInputChange}
              />
            </section>
            <section>
              <label htmlFor="password">Passwort</label>
              <InputField
                id="password"
                type="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
              />
            </section>

            <Button
              disabled={
                !(formData.loginName.trim() && formData.password.trim())
              }
            >
              Anmelden
            </Button>
            {error && <p>{error.message}</p>}
          </LoginForm>
        )}
      </MainStyle>
    </PageLayout>
  )
}
export default LoginPage

const LoginForm = styled.form`
  display: grid;
  grid-template-columns: max-content;
  justify-content: center;
  grid-gap: var(--size-l);
  justify-items: center;

  button {
    width: 100%;
  }

  section {
    display: grid;
    justify-items: center;
  }
`
