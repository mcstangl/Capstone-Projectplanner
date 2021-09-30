import styled from 'styled-components/macro'
import { FC } from 'react'
import { Button } from './Button'

interface ErrorPopupProps {
  message: string
  resetErrorState: () => void
}

const ErrorPopup: FC<ErrorPopupProps> = ({ message, resetErrorState }) => {
  return (
    <ErrorPopupStyle>
      <p>{message}</p>
      <Button theme="secondary" onClick={resetErrorState}>
        OK
      </Button>
    </ErrorPopupStyle>
  )
}
export default ErrorPopup

const ErrorPopupStyle = styled.section`
  position: fixed;
  background-color: white;
  top: 30%;
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
