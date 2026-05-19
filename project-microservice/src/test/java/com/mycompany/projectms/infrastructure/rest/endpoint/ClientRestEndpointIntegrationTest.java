package com.mycompany.projectms.infrastructure.rest.endpoint;

import com.mycompany.projectms.infrastructure.rest.resource.ClientResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientRestEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllClients() throws Exception {
        mockMvc.perform(get("/api/clients")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAllClientsContentType() throws Exception {
        mockMvc.perform(get("/api/clients"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetClientByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.clientName").exists());
    }

    @Test
    void testGetClientByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/clients/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientByIdResponseStructure() throws Exception {
        mockMvc.perform(get("/api/clients/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("id")))
            .andExpect(jsonPath("$", hasKey("clientName")))
            .andExpect(jsonPath("$.id", isA(Number.class)));
    }

    @Test
    void testCreateClientSuccess() throws Exception {
        ClientResource newClient = new ClientResource(100, "New Client Company");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.clientName").value("New Client Company"));
    }

    @Test
    void testCreateClientWithEmptyName() throws Exception {
        ClientResource newClient = new ClientResource(101, "");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateClientReturnsCorrectContentType() throws Exception {
        ClientResource newClient = new ClientResource(102, "Another Client");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateClientSuccess() throws Exception {
        ClientResource updatedClient = new ClientResource(1, "Updated Client Name");
        
        mockMvc.perform(put("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("Updated Client Name"));
    }

    @Test
    void testUpdateClientNotFound() throws Exception {
        ClientResource updatedClient = new ClientResource(9999, "Non-existent");
        
        mockMvc.perform(put("/api/clients/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateClientPathIdMatchesResource() throws Exception {
        // First create a client to update
        ClientResource newClient = new ClientResource(50, "Original");
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated());

        // Then update it
        ClientResource updatedClient = new ClientResource(0, "Test Update");
        mockMvc.perform(put("/api/clients/50")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteClientSuccess() throws Exception {
        mockMvc.perform(delete("/api/clients/2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteClientNotFound() throws Exception {
        mockMvc.perform(delete("/api/clients/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientByIdAfterCreate() throws Exception {
        ClientResource newClient = new ClientResource(200, "Test Client");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/clients/200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(200))
            .andExpect(jsonPath("$.clientName").value("Test Client"));
    }

    @Test
    void testCreateAndUpdateClient() throws Exception {
        ClientResource newClient = new ClientResource(300, "Original Name");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated());

        ClientResource updatedClient = new ClientResource(0, "Updated Name");
        mockMvc.perform(put("/api/clients/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientName").value("Updated Name"));
    }

    @Test
    void testGetAllClientsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/clients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    void testInvalidHttpMethod() throws Exception {
        mockMvc.perform(patch("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testCreateClientWithSpecialCharacters() throws Exception {
        ClientResource newClient = new ClientResource(103, "Client & Partners, Inc.");
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clientName").value("Client & Partners, Inc."));
    }

    @Test
    void testGetClientByIdVerifyId() throws Exception {
        mockMvc.perform(get("/api/clients/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
