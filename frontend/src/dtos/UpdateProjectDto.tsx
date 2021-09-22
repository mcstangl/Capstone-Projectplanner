import { UserDto } from './UserDto'

export interface UpdateProjectDto {
  customer: string
  title: string
  newTitle: string
  status: string
  dateOfReceipt: string
  owner: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}
