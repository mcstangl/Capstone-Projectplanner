import styled from 'styled-components/macro'

export default function LoginPage() {
  return (
    <PageLayout>
      <header></header>
      <LoginForm>
        <input type="text" name="loginName" placeholder="Login Name" />
        <input type="text" name="password" placeholder="Password" />
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
