import styled from 'styled-components/macro'

export const EditSelect = styled.div`
  select {
    width: 100%;
    padding: 0.5rem;
    background: white;
    border: 0;
    border-bottom: 1px solid var(--secondarycolor);
  }

  option {
    background: white;
  }

  &-selected:after {
    background: var(--gradient4);
  }
`
