import axios from 'axios'
import { CredentialsDto } from '../dtos/CredentialsDto'
import { AccessTokenDto } from '../dtos/AccessTokenDto'
import { NewProjectDto } from '../dtos/NewProjectDto'
import { UpdateProjectDto } from '../dtos/UpdateProjectDto'
import { MilestoneDto } from '../dtos/MilestoneDto'
import { NewUserDto } from '../dtos/NewUserDto'
import { UserDto } from '../dtos/UserDto'

export const getAccessToken = (credentials: CredentialsDto) =>
  axios
    .post('/api/project-planner/auth/access_token', credentials)
    .then(response => response.data)
    .then((accessToken: AccessTokenDto) => accessToken.token)

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

export const findAllUser = (token: string) =>
  axios
    .get('/api/project-planner/user', getAuthHeaders(token))
    .then(response => response.data)

export const createNewUser = (token: string, newUserDto: NewUserDto) =>
  axios
    .post('/api/project-planner/user', newUserDto, getAuthHeaders(token))
    .then(response => response.data)

export const findUserByLoginName = (token: string, loginName: string) =>
  axios
    .get(`/api/project-planner/user/${loginName}`, getAuthHeaders(token))
    .then(response => response.data)

export const updateUser = (
  token: string,
  loginName: string,
  userDto: UserDto
) =>
  axios
    .put(
      `/api/project-planner/user/${loginName}`,
      userDto,
      getAuthHeaders(token)
    )
    .then(response => response.data)

export const resetUserPassword = (token: string, loginName: string) =>
  axios
    .put(
      `/api/project-planner/user/${loginName}/reset-password`,
      null,
      getAuthHeaders(token)
    )
    .then(response => response.data)

export const createNewMilestone = (token: string, milestoneDto: MilestoneDto) =>
  axios
    .post('/api/project-planner/milestone', milestoneDto, getAuthHeaders(token))
    .then(response => response.data)

export const updateMilestone = (token: string, milestoneDto: MilestoneDto) =>
  axios
    .put('/api/project-planner/milestone', milestoneDto, getAuthHeaders(token))
    .then(response => response.data)

export const deleteMilestone = (token: string, id: bigint) =>
  axios
    .delete('/api/project-planner/milestone/' + id, getAuthHeaders(token))
    .then(require => require.data)

export const moveToArchive = (token: string, projectTitle: string) =>
  axios
    .put(
      `/api/project-planner/project/${projectTitle}/archive`,
      null,
      getAuthHeaders(token)
    )
    .then(require => require.data)

export const restoreFromArchive = (token: string, projectTitle: string) =>
  axios
    .put(
      `/api/project-planner/project/${projectTitle}/restore`,
      null,
      getAuthHeaders(token)
    )
    .then(require => require.data)

const getAuthHeaders = (token: string) => {
  return {
    headers: {
      Authorization: 'Bearer ' + token,
    },
  }
}
