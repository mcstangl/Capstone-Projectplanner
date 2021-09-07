import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import styled from 'styled-components/macro'
import { NewProjectDto } from '../dtos/NewProjectDto'
import { createNewProject } from '../service/api-service'
import AuthContext from '../auth/AuthContext'

const NewProjectPage: FC = () => {
  const { token } = useContext(AuthContext)
  const [formData, setFormData] = useState<NewProjectDto>({
    customer: '',
    title: '',
  })
  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    if (token) {
      createNewProject(formData, token).catch(console.error)
    }
  }

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  return (
    <PageLayout>
      <Header />
      <ProjectForm onSubmit={submitHandler}>
        <input
          name="customer"
          type="text"
          placeholder="Kundenname"
          value={formData.customer}
          onChange={handleInputChange}
        />
        <input
          name="title"
          type="text"
          placeholder="Projekt Titel"
          value={formData.title}
          onChange={handleInputChange}
        />
        <button>Speichern</button>
      </ProjectForm>
    </PageLayout>
  )
}
export default NewProjectPage

const ProjectForm = styled.form`
  display: grid;
  grid-gap: var(--size-l);
  justify-items: center;
`
