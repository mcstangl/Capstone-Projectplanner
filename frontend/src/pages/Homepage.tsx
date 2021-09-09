import { FC } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Link } from 'react-router-dom'
import { LinkGroup } from '../components/LinkGroup'

const Homepage: FC = () => {
  return (
    <PageLayout>
      <Header />
      <LinkGroup>
        <Link to="/projects">Projekte</Link>
      </LinkGroup>
    </PageLayout>
  )
}
export default Homepage
