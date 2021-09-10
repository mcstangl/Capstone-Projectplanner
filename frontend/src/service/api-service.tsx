import axios from 'axios'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { AccessToken } from '../dtos/AccessToken'
import { NewProjectDto } from '../dtos/NewProjectDto'
import { UpdateProjectDto } from '../dtos/UpdateProjectDto'

export const getAccessToken = (credentials: CredentialsDto) =>
  axios
    .post('/api/project-planner/auth/access_token', credentials)
    .then(response => response.data)
    .then((accessToken: AccessToken) => accessToken.token)

export const createNewProject = (newProject: NewProjectDto, token: string) =>
  axios
    .post('/api/project-planner/project', newProject, getAuthHeaders(token))
    .then(response => response.data)

export const updateProject = (updateProject: UpdateProjectDto, token: string) =>
  axios
    .put(
      '/api/project-planner/project/' + updateProject.title,
      updateProject,
      getAuthHeaders(token)
    )
    .then(response => response.data)

export const findAllProjects = (token: string) =>
  axios
    .get('/api/project-planner/project', getAuthHeaders(token))
    .then(response => response.data)

export const findProjectByTitle = (title: string, token: string) =>
  axios
    .get('/api/project-planner/project/' + title, getAuthHeaders(token))
    .then(response => response.data)

const getAuthHeaders = (token: string) => {
  return {
    headers: {
      Authorization: 'Bearer ' + token,
    },
  }
}
