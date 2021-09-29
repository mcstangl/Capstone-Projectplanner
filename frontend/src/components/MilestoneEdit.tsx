import { Button } from './Button'
import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import {
  createNewMilestone,
  deleteMilestone,
  updateMilestone,
} from '../service/api-service'
import { MilestoneDto } from '../dtos/MilestoneDto'
import AuthContext from '../auth/AuthContext'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import styled from 'styled-components/macro'
import Loader from './Loader'
import { EditInputField } from './EditInputField'

interface MilestoneFormData {
  title: string
  dueDate: string
  dateFinished: string
  id?: bigint
}

interface MilestoneEditProps {
  projectTitle: string
  switchEditMode: () => void
  fetchProject: () => Promise<void> | undefined
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
    dateFinished:
      milestone && milestone.dateFinished ? milestone.dateFinished : '',
  })

  const handleOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const handleOnSubmit = (event: FormEvent) => {
    event.preventDefault()

    if (!(token && formData.title.trim() && formData.dueDate)) {
      return
    }
    const milestoneDto: MilestoneDto = {
      title: formData.title.trim(),
      dueDate: formData.dueDate,
      dateFinished: formData.dateFinished,
      projectTitle: projectTitle,
      id: milestone ? milestone.id : undefined,
    }
    if (milestone) {
      setLoading(true)
      updateMilestone(token, milestoneDto)
        .then(() => {
          switchEditMode()
          setLoading(false)
        })
        .then(() => fetchProject())

        .catch(error => {
          setLoading(false)
          setError(error.response.data)
        })
    } else {
      setLoading(true)
      createNewMilestone(token, milestoneDto)
        .then(() => {
          switchEditMode()
          setLoading(false)
        })
        .then(() => fetchProject())
        .catch(error => {
          setLoading(false)
          setError(error.response.data)
        })
    }
  }

  const handleDeleteOnClick = () => {
    if (milestone && milestone.id && token) {
      setLoading(true)
      deleteMilestone(token, milestone.id)
        .then(() => fetchProject())
        .catch(error => {
          setLoading(false)
          setError(error.response.data)
        })
    }
  }

  const handleOnClick = () => switchEditMode()

  return (
    <section>
      {loading && <Loader />}
      {!loading && (
        <MilestoneEditStyle onSubmit={handleOnSubmit}>
          <section>
            <label htmlFor="title">Milestone</label>
            <EditInputField
              id="title"
              type="text"
              name="title"
              placeholder="Titel"
              value={formData.title}
              onChange={handleOnChange}
            />
          </section>

          <section>
            <label htmlFor="dueDate">Fällig am</label>
            <EditInputField
              id="dueDate"
              type="date"
              name="dueDate"
              value={formData.dueDate}
              onChange={handleOnChange}
            />
          </section>

          <section>
            <label htmlFor="dateFinished"> Fertig am</label>
            <EditInputField
              id="dateFinished"
              type="date"
              name="dateFinished"
              value={formData.dateFinished}
              onChange={handleOnChange}
            />
          </section>

          <Button disabled={!(formData.title.trim() && formData.dueDate)}>
            Speichern
          </Button>
          <Button type="button" onClick={handleOnClick}>
            Abbrechen
          </Button>
          {milestone && (
            <Button
              type="button"
              theme="secondary"
              onClick={handleDeleteOnClick}
            >
              Löschen
            </Button>
          )}
          {!milestone && <div />}
          {error && <p>{error.message}</p>}
        </MilestoneEditStyle>
      )}
    </section>
  )
}
export default MilestoneEdit

const MilestoneEditStyle = styled.form`
  padding: 1rem;
  border: 1px solid var(--secondarycolor);
  background: white;
  box-shadow: 3px 8px 8px grey;
  position: absolute;
  right: 0;
  left: 0;
  width: max-content;
  margin-left: auto;
  margin-right: auto;
  display: grid;
  grid-template-columns: max-content;
  grid-gap: var(--size-l);

  label {
    font-size: 110%;
  }

  section {
    display: grid;
    width: 100%;
  }
`
