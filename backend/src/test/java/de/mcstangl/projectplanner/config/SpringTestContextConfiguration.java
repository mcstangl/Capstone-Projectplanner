package de.mcstangl.projectplanner.config;

import de.mcstangl.projectplanner.repository.ProjectRepository;
import de.mcstangl.projectplanner.repository.UserRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.mockito.Mockito.mock;

@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.mcstangl.projectplanner"})
@Import({JpaConfig.class})
@TestConfiguration
public class SpringTestContextConfiguration {

    public static final String MOCKED_PROJECT_REPOSITORY = "mockedProjectRepository";

    @Primary
    @Bean(name = "dataSource", destroyMethod = "shutdown")
    public EmbeddedDatabase dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Primary
    @Bean
    @Profile(MOCKED_PROJECT_REPOSITORY)
    public ProjectRepository projectRepository() {
        return mock(ProjectRepository.class);
    }

}