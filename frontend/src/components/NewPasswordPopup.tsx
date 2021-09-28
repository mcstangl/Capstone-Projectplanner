import { Button } from './Button'
import { FC } from 'react'
import styled from 'styled-components/macro'
import { UserWithPasswordDto } from '../dtos/UserWithPasswordDto'

interface NewPasswordPopupProps {
  newUser: UserWithPasswordDto
  handleNewPasswordPopupOnClick: () => void
}

const NewPasswordPopup: FC<NewPasswordPopupProps> = ({
  newUser,
  handleNewPasswordPopupOnClick,
}) => {
  return (
    <NewPasswordPopupStyle>
      <p>Temporäres Passwort für Benutzer {newUser.loginName}</p>
      <p>{newUser.password}</p>
      <Button theme="secondary" onClick={handleNewPasswordPopupOnClick}>
        OK
      </Button>
    </NewPasswordPopupStyle>
  )
}
export default NewPasswordPopup

const NewPasswordPopupStyle = styled.section`
  position: absolute;
  background-color: white;
  right: 0;
  left: 0;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
  width: 250px;
  display: grid;
  grid-template-columns: 100%;
  justify-items: center;
  grid-gap: var(--size-l);
  border: 1px solid var(--secondarycolor);
  box-shadow: 3px 8px 12px grey;
  padding: var(--size-l);

  button {
    width: 100%;
  }
`
