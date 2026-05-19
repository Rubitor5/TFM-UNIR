package com.mycompany.projectms.infrastructure.rest.mapper;

import com.mycompany.projectms.domain.model.Client;
import com.mycompany.projectms.infrastructure.rest.resource.ClientResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientResourceMapperTest {

    @Test
    void testMapToClientResourceSuccess() {
        Client client = new Client(1, "Acme Corp");
        ClientResource resource = ClientResourceMapper.mapToClientResource(client);
        
        assertNotNull(resource);
        assertEquals(1, resource.getId());
        assertEquals("Acme Corp", resource.getClientName());
    }

    @Test
    void testMapToClientResourceNull() {
        ClientResource resource = ClientResourceMapper.mapToClientResource(null);
        assertNull(resource);
    }

    @Test
    void testMapToClientResourceWithEmptyName() {
        Client client = new Client(5, "");
        ClientResource resource = ClientResourceMapper.mapToClientResource(client);
        
        assertNotNull(resource);
        assertEquals(5, resource.getId());
        assertEquals("", resource.getClientName());
    }

    @Test
    void testMapToClientResourceWithNullName() {
        Client client = new Client(10, null);
        ClientResource resource = ClientResourceMapper.mapToClientResource(client);
        
        assertNotNull(resource);
        assertEquals(10, resource.getId());
        assertNull(resource.getClientName());
    }

    @Test
    void testMapToClientSuccess() {
        ClientResource resource = new ClientResource(2, "Global Tech");
        Client client = ClientResourceMapper.mapToClient(resource);
        
        assertNotNull(client);
        assertEquals(2, client.getId());
        assertEquals("Global Tech", client.getClientName());
    }

    @Test
    void testMapToClientNull() {
        Client client = ClientResourceMapper.mapToClient(null);
        assertNull(client);
    }

    @Test
    void testMapToClientWithEmptyName() {
        ClientResource resource = new ClientResource(7, "");
        Client client = ClientResourceMapper.mapToClient(resource);
        
        assertNotNull(client);
        assertEquals(7, client.getId());
        assertEquals("", client.getClientName());
    }

    @Test
    void testMapToClientWithNullName() {
        ClientResource resource = new ClientResource(8, null);
        Client client = ClientResourceMapper.mapToClient(resource);
        
        assertNotNull(client);
        assertEquals(8, client.getId());
        assertNull(client.getClientName());
    }

    @Test
    void testBidirectionalMapping() {
        Client originalClient = new Client(3, "Tech Solutions Inc");
        ClientResource resource = ClientResourceMapper.mapToClientResource(originalClient);
        Client mappedBackClient = ClientResourceMapper.mapToClient(resource);
        
        assertEquals(originalClient.getId(), mappedBackClient.getId());
        assertEquals(originalClient.getClientName(), mappedBackClient.getClientName());
    }

    @Test
    void testMapMultipleClients() {
        Client client1 = new Client(1, "Client A");
        Client client2 = new Client(2, "Client B");
        
        ClientResource resource1 = ClientResourceMapper.mapToClientResource(client1);
        ClientResource resource2 = ClientResourceMapper.mapToClientResource(client2);
        
        assertNotNull(resource1);
        assertNotNull(resource2);
        assertNotEquals(resource1.getId(), resource2.getId());
        assertNotEquals(resource1.getClientName(), resource2.getClientName());
    }

    @Test
    void testMapToClientWithSpecialCharacters() {
        Client client = new Client(11, "Client & Partners, LLC");
        ClientResource resource = ClientResourceMapper.mapToClientResource(client);
        
        assertEquals("Client & Partners, LLC", resource.getClientName());
    }
}
