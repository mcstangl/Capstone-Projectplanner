import { FC } from 'react'
import styled from 'styled-components/macro'

const Logo: FC = () => {
  return <Wrapper> </Wrapper>
}
export default Logo

const Wrapper = styled.div`
  // border: solid 2px var(--maincolor);
  border-radius: 50%;
  color: var(--maincolor);
  width: 100px;
  height: 100px;
  margin: var(--size-m);
`
