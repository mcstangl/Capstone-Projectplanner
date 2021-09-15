import { Button } from './Button'
import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { createNewMilestone } from '../service/api-service'
import { MilestoneDto } from '../dtos/MilestoneDto'
import AuthContext from '../auth/AuthContext'

interface MilestoneFormData {
  title: string
  dueDate: string
  dateFinished: string
}

interface MilestoneEditProps {
  projectTitle: string
  switchEditMode: () => void
}

const MilestoneEdit: FC<MilestoneEditProps> = ({
  projectTitle,
  switchEditMode,
}) => {
  const { token } = useContext(AuthContext)
  const [formData, setFormData] = useState<MilestoneFormData>({
    title: '',
    dueDate: '',
    dateFinished: '',
  })

  const handleOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const handleOnSubmit = (event: FormEvent) => {
    event.preventDefault()
    if (token && formData.title.trim() && formData.dueDate.trim()) {
      const milestoneDto: MilestoneDto = {
        title: formData.title.trim(),
        dueDate: formData.dueDate,
        dateFinished: formData.dateFinished,
        projectTitle: projectTitle,
      }
      createNewMilestone(token, milestoneDto)
        .then(switchEditMode)
        .catch(console.error)
    }
  }
  const handleOnClick = () => switchEditMode()

  return (
    <form onSubmit={handleOnSubmit}>
      <input
        type="text"
        name="title"
        placeholder="Titel"
        value={formData.title}
        onChange={handleOnChange}
      />
      <input
        type="date"
        name="dueDate"
        value={formData.dueDate}
        onChange={handleOnChange}
      />
      <input
        type="date"
        name="dateFinished"
        value={formData.dateFinished}
        onChange={handleOnChange}
      />
      <Button type="button" onClick={handleOnClick}>
        Abbrechen
      </Button>
      {formData.title.trim() && formData.dueDate.trim() ? (
        <Button>Speichern</Button>
      ) : (
        <Button disabled>Speichern</Button>
      )}
    </form>
  )
}
export default MilestoneEdit
