import axios from 'axios'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { AccessToken } from '../dtos/AccessToken'

export const getAccessToken = (credentials: CredentialsDto) =>
  axios
    .post('api/project-planner/auth/access_token', credentials)
    .then(response => response.data)
    .then((accessToken: AccessToken) => accessToken.token)
