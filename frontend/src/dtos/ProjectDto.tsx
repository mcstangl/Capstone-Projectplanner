import { UserDto } from './UserDto'

export interface ProjectDto {
  customer: string
  title: string
  owner: UserDto
  dateOfReceipt: string
  writer: UserDto[]
  motionDesign: UserDto[]
}
