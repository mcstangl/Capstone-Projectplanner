import axios from 'axios'
import { CredentialsDto } from '../dtos/CredentialsDto'

export const getAccessToken = (credentials: CredentialsDto) =>
  axios
    .post('api/project-planner/auth/access_token', credentials)
    .then(response => response.data)
