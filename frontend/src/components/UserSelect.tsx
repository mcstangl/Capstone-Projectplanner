import { ChangeEvent, FC } from 'react'
import { ProjectDto } from '../dtos/ProjectDto'
import { UserDto } from '../dtos/UserDto'

interface UserSelectProps {
  handleSelectChange: (event: ChangeEvent<HTMLSelectElement>) => void
  project?: ProjectDto
  userList?: UserDto[]
  name: string
}

const UserSelect: FC<UserSelectProps> = ({
  handleSelectChange,
  project,
  userList,
  name,
}) => {
  let defaultValue: string
  if (project && name === 'owner') {
    defaultValue = project.owner.loginName
  } else if (project && project.writer[0] && name === 'writer') {
    defaultValue = project.writer[0].loginName
  } else if (project && project.motionDesign[0] && name === 'motionDesign') {
    defaultValue = project.motionDesign[0].loginName
  } else defaultValue = ''

  return (
    <select
      onChange={handleSelectChange}
      defaultValue={defaultValue}
      name={name}
    >
      {name !== 'owner' && (
        <option key="none" value={undefined}>
          kein
        </option>
      )}
      {userList?.map(user => (
        <option key={user.loginName} value={user.loginName}>
          {user.loginName}
        </option>
      ))}
    </select>
  )
}
export default UserSelect
