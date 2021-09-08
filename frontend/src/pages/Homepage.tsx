import { FC } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Link } from 'react-router-dom'

const Homepage: FC = () => {
  return (
    <PageLayout>
      <Header />
      <section>
        <Link to="/projects">Projekte</Link>
      </section>
    </PageLayout>
  )
}
export default Homepage
