package de.mcstangl.projectplanner;

import de.mcstangl.projectplanner.config.SpringTestContextConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = SpringTestContextConfiguration.class)
class ProjectPlannerApplicationTests {

    @Test
    void contextLoads() {
    }

}
