import styled from 'styled-components/macro'

export const Column2Style = styled.span`
  @media (max-width: 500px) {
    display: none;
  }
`

export const Column5Style = styled.span`
  @media (max-width: 800px) {
    display: none;
  }
`

export const Column6Style = styled.span`
  @media (max-width: 1000px) {
    display: none;
  }
`

export const Column7Style = styled.span`
  @media (max-width: 1200px) {
    display: none;
  }
`

export const Column8Style = styled.span`
  @media (max-width: 1400px) {
    display: none;
  }
`
export const ListItemStyle = styled.section`
  display: grid;
  grid-column-gap: var(--size-m);
  padding: 1rem;
  grid-template-columns: var(--size-xxl) 64px repeat(2, 1fr);

  @media (min-width: 500px) {
    grid-template-columns: var(--size-xxl) 64px 8rem repeat(2, 1fr);
  }

  @media (min-width: 800px) {
    grid-template-columns: var(--size-xxl) 64px 8rem repeat(3, 1fr);
  }

  @media (min-width: 1000px) {
    grid-template-columns: var(--size-xxl) 64px 8rem repeat(4, 1fr);
  }

  @media (min-width: 1200px) {
    grid-template-columns: var(--size-xxl) 64px 8rem repeat(5, 1fr);
  }

  @media (min-width: 1400px) {
    grid-template-columns: var(--size-xxl) 64px 8rem repeat(6, 1fr);
  }
`
