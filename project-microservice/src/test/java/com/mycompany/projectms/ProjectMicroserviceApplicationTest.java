package com.mycompany.projectms;

import com.mycompany.projectms.infrastructure.rest.endpoint.ProjectRestEndpoint;
import com.mycompany.projectms.infrastructure.rest.endpoint.ClientRestEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProjectMicroserviceApplicationTest {

    @Autowired(required = false)
    private ProjectRestEndpoint projectEndpoint;

    @Autowired(required = false)
    private ClientRestEndpoint clientEndpoint;

    @Test
    public void contextLoads() {
        assertThat(projectEndpoint).isNotNull();
        assertThat(clientEndpoint).isNotNull();
    }
}
