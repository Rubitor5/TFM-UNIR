package com.mycompany.projectms.infrastructure.rest.endpoint;

import com.mycompany.projectms.infrastructure.rest.resource.ProjectResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllProjects() throws Exception {
        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetProjectById() throws Exception {
        mockMvc.perform(get("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.projectTitle").exists());
    }

    @Test
    public void testGetNonexistentProject() throws Exception {
        mockMvc.perform(get("/api/projects/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
