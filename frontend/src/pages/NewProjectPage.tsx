import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import styled from 'styled-components/macro'
import { NewProjectDto } from '../dtos/NewProjectDto'
import { createNewProject } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import { Link, useHistory } from 'react-router-dom'
import { Button } from '../components/Button'
import { LinkGroup } from '../components/LinkGroup'

const NewProjectPage: FC = () => {
  const { token } = useContext(AuthContext)
  const [formData, setFormData] = useState<NewProjectDto>({
    customer: '',
    title: '',
  })

  const history = useHistory()

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    if (token) {
      createNewProject(formData, token)
        .then(() => history.push('/projects'))
        .catch(console.error)
    }
  }

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  return (
    <PageLayout>
      <Header />
      <main>
        <LinkGroup>
          <Link to="/projects">Zurück zur Liste</Link>
        </LinkGroup>

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
          <Button>Speichern</Button>
        </ProjectForm>
      </main>
    </PageLayout>
  )
}
export default NewProjectPage

const ProjectForm = styled.form`
  display: grid;
  grid-gap: var(--size-l);
  justify-items: center;
`
