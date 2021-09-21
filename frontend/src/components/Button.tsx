import styled, { css } from 'styled-components/macro'

export const Button = styled.button`
  padding: 0.5rem 2rem;
  background-color: white;
  border: 1px solid var(--maincolor);
  color: var(--maincolor);
  font-size: 1em;
  border-radius: var(--size-xs);

  ${props =>
    props.theme === 'secondary'
      ? css`
          border: 1px solid var(--accentcolor);
          color: var(--accentcolor);
        `
      : css`
          border: 1px solid var(--maincolor);
          color: var(--maincolor);
        `}
  &:hover {
    ${props =>
      props.theme === 'secondary'
        ? css`
            background-color: var(--accentcolor);
            color: white;
          `
        : css`
            background-color: var(--maincolor);
            color: white; ;
          `}
  }

  :disabled {
    border-color: var(--darkgrey);
    background: white;
    color: var(--darkgrey);
  }
`
