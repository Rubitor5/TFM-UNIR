package com.mycompany.projectms;

import com.mycompany.projectms.infrastructure.rest.resource.ClientResource;
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
class EndToEndWorkflowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCompleteClientWorkflow() throws Exception {
        // Create a new client
        ClientResource newClient = new ClientResource(1000, "E2E Test Client");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1000));

        // Retrieve the created client
        mockMvc.perform(get("/api/clients/1000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("E2E Test Client"));

        // Update the client
        ClientResource updatedClient = new ClientResource(0, "E2E Test Client Updated");
        mockMvc.perform(put("/api/clients/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
            .andExpect(status().isOk());

        // Verify the update
        mockMvc.perform(get("/api/clients/1000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("E2E Test Client Updated"));

        // Delete the client
        mockMvc.perform(delete("/api/clients/1000"))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/clients/1000"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCompleteProjectWorkflow() throws Exception {
        Date now = new Date();
        
        // Create a new project
        ProjectResource newProject = new ProjectResource(1000, "E2E Test Project", now, null, 1, "Test Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1000));

        // Retrieve the created project
        mockMvc.perform(get("/api/projects/1000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectTitle").value("E2E Test Project"));

        // Update the project
        ProjectResource updatedProject = new ProjectResource(0, "E2E Test Project Updated", now, now, 1, "Test Client");
        mockMvc.perform(put("/api/projects/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
            .andExpect(status().isOk());

        // Verify the update
        mockMvc.perform(get("/api/projects/1000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectTitle").value("E2E Test Project Updated"));

        // Delete the project
        mockMvc.perform(delete("/api/projects/1000"))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/projects/1000"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testMultipleClientsCreationFlow() throws Exception {
        // Create multiple clients
        for (int i = 2000; i < 2005; i++) {
            ClientResource client = new ClientResource(i, "Client " + i);
            
            mockMvc.perform(post("/api/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated());
        }

        // Get all clients and verify at least 5 exist
        mockMvc.perform(get("/api/clients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    void testMultipleProjectsCreationFlow() throws Exception {
        Date date = new Date();
        
        // Create multiple projects
        for (int i = 2000; i < 2005; i++) {
            ProjectResource project = new ProjectResource(i, "Project " + i, date, date, 1, "Client 1");
            
            mockMvc.perform(post("/api/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isCreated());
        }

        // Get all projects and verify at least 5 exist
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    void testClientAndProjectIntegratedWorkflow() throws Exception {
        Date now = new Date();
        
        // Create a client
        ClientResource newClient = new ClientResource(3000, "Integrated Test Client");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated());

        // Verify client was created
        mockMvc.perform(get("/api/clients/3000"))
            .andExpect(status().isOk());

        // Create a project for that client
        ProjectResource newProject = new ProjectResource(3000, "Integrated Test Project", now, null, 3000, "Integrated Test Client");
        
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
            .andExpect(status().isCreated());

        // Verify project was created
        mockMvc.perform(get("/api/projects/3000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientId").value(3000));

        // Update the project
        ProjectResource updatedProject = new ProjectResource(0, "Integrated Test Project Updated", now, now, 3000, "Integrated Test Client");
        mockMvc.perform(put("/api/projects/3000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
            .andExpect(status().isOk());

        // Delete the project
        mockMvc.perform(delete("/api/projects/3000"))
            .andExpect(status().isNoContent());

        // Delete the client
        mockMvc.perform(delete("/api/clients/3000"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testErrorHandlingClientNotFound() throws Exception {
        // Try to get non-existent client
        mockMvc.perform(get("/api/clients/99999"))
            .andExpect(status().isNotFound());

        // Try to update non-existent client
        ClientResource client = new ClientResource(0, "Test");
        mockMvc.perform(put("/api/clients/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
            .andExpect(status().isNotFound());



        // Try to delete non-existent client
        mockMvc.perform(delete("/api/clients/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testErrorHandlingProjectNotFound() throws Exception {
        Date date = new Date();
        
        // Try to get non-existent project
        mockMvc.perform(get("/api/projects/99999"))
            .andExpect(status().isNotFound());

        // Try to update non-existent project
        ProjectResource project = new ProjectResource(0, "Test", date, date, 1, "Client");
        mockMvc.perform(put("/api/projects/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project)))
            .andExpect(status().isNotFound());

        // Try to delete non-existent project
        mockMvc.perform(delete("/api/projects/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testConcurrentClientOperations() throws Exception {
        // Create first client
        ClientResource client1 = new ClientResource(4000, "Concurrent Client 1");
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client1)))
            .andExpect(status().isCreated());

        // Create second client
        ClientResource client2 = new ClientResource(4001, "Concurrent Client 2");
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client2)))
            .andExpect(status().isCreated());

        // Retrieve both clients
        mockMvc.perform(get("/api/clients/4000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("Concurrent Client 1"));

        mockMvc.perform(get("/api/clients/4001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("Concurrent Client 2"));

        // Update first client while second exists
        ClientResource updatedClient1 = new ClientResource(0, "Updated Concurrent Client 1");
        mockMvc.perform(put("/api/clients/4000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient1)))
            .andExpect(status().isOk());

        // Verify second client unchanged
        mockMvc.perform(get("/api/clients/4001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("Concurrent Client 2"));
    }

    @Test
    void testListEndpointsWithMultipleResources() throws Exception {
        // Create multiple clients
        for (int i = 5000; i < 5003; i++) {
            ClientResource client = new ClientResource(i, "List Test Client " + i);
            mockMvc.perform(post("/api/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated());
        }

        // Verify list contains clients
        mockMvc.perform(get("/api/clients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)));
    }
}
