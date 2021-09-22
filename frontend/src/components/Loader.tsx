import { FC } from 'react'
import styled from 'styled-components/macro'

const Loader: FC = () => {
  return (
    <LoadingStyle>
      <div />
      <div />
      <div />
      <div />
    </LoadingStyle>
  )
}
export default Loader

const LoadingStyle = styled.div`
  display: inline-block;
  position: absolute;
  top: 50%;
  left: 50%;
  width: 80px;
  height: 80px;
  border: 1px solid lightgray;
  border-radius: 12px;
  box-shadow: 3px 8px 8px lightgray;
  background: white;

  div {
    box-sizing: border-box;
    display: block;
    position: absolute;
    width: 64px;
    height: 64px;
    margin: 8px;
    border: 8px solid var(--maincolor);
    border-radius: 50%;
    animation: lds-ring 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
    border-color: var(--maincolor) transparent transparent transparent;
  }

  div:nth-child(1) {
    animation-delay: -0.45s;
  }

  div:nth-child(2) {
    animation-delay: -0.3s;
  }

  div:nth-child(3) {
    animation-delay: -0.15s;
  }

  @keyframes lds-ring {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
`
