import { UserDto } from './UserDto'

export interface ProjectDto {
  customer: string
  title: string
  owner: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}
