import { UserDto } from './UserDto'

export interface UpdateProjectDto {
  customer: string
  title: string
  newTitle: string
  owner: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}
