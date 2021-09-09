import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { ProjectDto } from '../dtos/ProjectDto'
import { getAllProjects } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import { Link } from 'react-router-dom'

const ProjectListPage: FC = () => {
  const { token, authUser } = useContext(AuthContext)
  const [projects, setProjects] = useState<ProjectDto[]>()

  useEffect(() => {
    if (token) {
      getAllProjects(token).then(setProjects).catch(console.error)
    }
  }, [token])

  return (
    <PageLayout>
      <Header />
      <main>
        {authUser?.role === 'ADMIN' && (
          <Link to="/new-project">Neues Projekt erstellen</Link>
        )}
        <ul>
          {projects &&
            projects.length &&
            projects.map(project => (
              <li key={project.title}>
                <span>{project.customer}</span>
                <span>{project.title}</span>
              </li>
            ))}
        </ul>
      </main>
    </PageLayout>
  )
}
export default ProjectListPage
