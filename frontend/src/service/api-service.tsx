import axios from 'axios'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { AccessToken } from '../dtos/AccessToken'
import { NewProjectDto } from '../dtos/NewProjectDto'

export const getAccessToken = (credentials: CredentialsDto) =>
  axios
    .post('api/project-planner/auth/access_token', credentials)
    .then(response => response.data)
    .then((accessToken: AccessToken) => accessToken.token)

export const createNewProject = (newProject: NewProjectDto, token: string) =>
  axios
    .post('api/project-planner/project', newProject, {
      headers: {
        Authorization: 'Bearer ' + token,
      },
    })
    .then(response => response.data)
