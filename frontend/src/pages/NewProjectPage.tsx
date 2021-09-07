import { ChangeEvent, FC, FormEvent, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import styled from 'styled-components/macro'
import { NewProjectDto } from '../dtos/NewProjectDto'

const NewProjectPage: FC = () => {
  const [formData, setFormData] = useState<NewProjectDto>({
    customer: '',
    title: '',
  })
  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    console.log(formData)
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
