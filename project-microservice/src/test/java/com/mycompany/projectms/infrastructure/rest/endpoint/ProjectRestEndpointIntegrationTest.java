package com.mycompany.projectms.infrastructure.rest.endpoint;

import com.mycompany.projectms.infrastructure.rest.resource.ProjectResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectRestEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllProjects() throws Exception {
        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAllProjectsContentType() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetProjectByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.projectTitle").exists());
    }

    @Test
    void testGetProjectByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/projects/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetProjectByIdResponseStructure() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("id")))
            .andExpect(jsonPath("$", hasKey("projectTitle")))
            .andExpect(jsonPath("$", hasKey("clientId")))
            .andExpect(jsonPath("$", hasKey("clientName")))
            .andExpect(jsonPath("$.id", isA(Number.class)));
    }

    @Test
    void testGetProjectByIdVerifyId() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateProjectSuccess() throws Exception {
        Date now = new Date();
        ProjectResource newProject = new ProjectResource(100, "New Project Title", now, now, 1, "Client Name");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.projectTitle").value("New Project Title"))
            .andExpect(jsonPath("$.clientId").value(1));
    }

    @Test
    void testCreateProjectWithNullDates() throws Exception {
        ProjectResource newProject = new ProjectResource(101, "Project Without Dates", null, null, 1, "Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateProjectReturnsCorrectContentType() throws Exception {
        Date now = new Date();
        ProjectResource newProject = new ProjectResource(102, "Another Project", now, now, 2, "Another Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateProjectSuccess() throws Exception {
        Date now = new Date();
        ProjectResource updatedProject = new ProjectResource(1, "Updated Project Title", now, now, 2, "Updated Client");
        
        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateProjectNotFound() throws Exception {
        Date now = new Date();
        ProjectResource updatedProject = new ProjectResource(9999, "Non-existent", now, now, 1, "Client");
        
        mockMvc.perform(put("/api/projects/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProjectPathIdMatchesResource() throws Exception {
        Date now = new Date();
        // First create a project to update
        ProjectResource newProject = new ProjectResource(50, "Original", now, now, 1, "Client");
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated());

        // Then update it
        ProjectResource updatedProject = new ProjectResource(0, "Test Update", now, now, 1, "Client");
        mockMvc.perform(put("/api/projects/50")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteProjectSuccess() throws Exception {
        mockMvc.perform(delete("/api/projects/3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProjectNotFound() throws Exception {
        mockMvc.perform(delete("/api/projects/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllProjectsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    void testCreateAndRetrieveProject() throws Exception {
        Date now = new Date();
        ProjectResource newProject = new ProjectResource(200, "Test Project", now, now, 5, "Test Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects/200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(200))
            .andExpect(jsonPath("$.projectTitle").value("Test Project"));
    }

    @Test
    void testCreateAndUpdateProject() throws Exception {
        Date now = new Date();
        ProjectResource newProject = new ProjectResource(300, "Original Title", now, now, 1, "Original Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated());

        ProjectResource updatedProject = new ProjectResource(0, "Updated Title", now, now, 2, "Updated Client");
        mockMvc.perform(put("/api/projects/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
            .andExpect(status().isOk());
    }

    @Test
    void testInvalidHttpMethod() throws Exception {
        mockMvc.perform(patch("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testCreateProjectWithSpecialCharacters() throws Exception {
        Date now = new Date();
        ProjectResource newProject = new ProjectResource(103, "Project & Infrastructure Phase 2", now, now, 1, "Client & Partners Inc.");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.projectTitle").value("Project & Infrastructure Phase 2"));
    }

    @Test
    void testGetProjectFieldValidation() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dateStarted").exists())
            .andExpect(jsonPath("$.dateEnded").exists());
    }

    @Test
    void testCreateProjectWithEmptyTitle() throws Exception {
        Date now = new Date();
        ProjectResource newProject = new ProjectResource(104, "", now, now, 1, "Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated());
    }
}
