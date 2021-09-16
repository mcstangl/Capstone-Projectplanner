import { Button } from './Button'
import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { createNewMilestone, upDateMilestone } from '../service/api-service'
import { MilestoneDto } from '../dtos/MilestoneDto'
import AuthContext from '../auth/AuthContext'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import styled from 'styled-components/macro'
import Loader from './Loader'

interface MilestoneFormData {
  title: string
  dueDate: string
  dateFinished: string
  id?: bigint
}

interface MilestoneEditProps {
  projectTitle: string
  switchEditMode: () => void
  fetchProject: () => void
  milestone?: MilestoneDto
}

const MilestoneEdit: FC<MilestoneEditProps> = ({
  projectTitle,
  switchEditMode,
  fetchProject,
  milestone,
}) => {
  const { token } = useContext(AuthContext)
  const [error, setError] = useState<RestExceptionDto>()
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState<MilestoneFormData>({
    title: milestone ? milestone.title : '',
    dueDate: milestone ? milestone.dueDate : '',
    dateFinished: milestone ? milestone.dateFinished : '',
  })

  const handleOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value.trim() })
  }

  const handleOnSubmit = (event: FormEvent) => {
    event.preventDefault()
    setLoading(true)
    if (!(token && formData.title && formData.dueDate)) {
      return
    }
    const milestoneDto: MilestoneDto = {
      title: formData.title,
      dueDate: formData.dueDate,
      dateFinished: formData.dateFinished,
      projectTitle: projectTitle,
      id: milestone ? milestone.id : undefined,
    }
    if (milestone) {
      upDateMilestone(token, milestoneDto)
        .then(() => {
          setLoading(false)
          fetchProject()
          switchEditMode()
        })
        .catch(error => setError(error.response.data))
    } else {
      createNewMilestone(token, milestoneDto)
        .then(() => {
          setLoading(false)
          fetchProject()
          switchEditMode()
        })
        .catch(error => setError(error.response.data))
    }
  }
  const handleOnClick = () => switchEditMode()

  return (
    <section>
      {loading && <Loader />}
      {!loading && (
        <MilestoneEditStyle onSubmit={handleOnSubmit}>
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
          <div />
          <Button type="button" onClick={handleOnClick}>
            Abbrechen
          </Button>

          <Button disabled={!(formData.title && formData.dueDate)}>
            Speichern
          </Button>

          {error && <p>{error.message}</p>}
        </MilestoneEditStyle>
      )}
    </section>
  )
}
export default MilestoneEdit

const MilestoneEditStyle = styled.form`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: 1fr 1fr;
`
