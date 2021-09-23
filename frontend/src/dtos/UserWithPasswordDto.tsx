import { UserDto } from './UserDto'

export interface UserWithPasswordDto extends UserDto {
  password: string
}
