import { UserDto } from './UserDto'
import { MilestoneDto } from './MilestoneDto'

export interface ProjectDto {
  customer: string
  title: string
  status: string
  owner: UserDto
  dateOfReceipt: string
  writer: UserDto[]
  motionDesign: UserDto[]
  milestones?: MilestoneDto[]
}
