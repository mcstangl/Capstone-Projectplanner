import { createGlobalStyle } from 'styled-components'

export default createGlobalStyle`
  :root {
    --size-xs: 4px;
    --size-s: 8px;
    --size-m: 12px;
    --size-l: 16px;
    --size-xl: 24px;
    --size-xxl: 32px;

    --maincolor: #006c5b;
    --gradient4: #c7e4dc;
    --accentcolor: #f39200;
    --accentcolor-gradient: rgba(243, 146, 0, 0.5);
    --secondarycolor: #d1d0d0;
    --darkgrey: #939393;
  }

  * {
    box-sizing: border-box;
  }

  html, body {
    margin: 0;
    font-family: Helvetica Arial sans-serif;
  }

`
