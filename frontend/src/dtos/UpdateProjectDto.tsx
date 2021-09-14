import { UserDto } from './UserDto'

export interface UpdateProjectDto {
  customer: string
  title: string
  newTitle: string
  dateOfReceipt: string
  owner: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}
