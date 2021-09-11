import { UserDto } from './UserDto'

export interface NewProjectDto {
  customer: string
  title: string
  owner: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}
