package com.mycompany.projectms.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void testClientNoArgsConstructor() {
        Client client = new Client();
        assertEquals(0, client.getId());
        assertNull(client.getClientName());
    }

    @Test
    void testClientAllArgsConstructor() {
        Client client = new Client(1, "Acme Corp");
        assertEquals(1, client.getId());
        assertEquals("Acme Corp", client.getClientName());
    }

    @Test
    void testSetAndGetId() {
        Client client = new Client();
        client.setId(42);
        assertEquals(42, client.getId());
    }

    @Test
    void testSetAndGetClientName() {
        Client client = new Client();
        client.setClientName("Tech Solutions");
        assertEquals("Tech Solutions", client.getClientName());
    }

    @Test
    void testSetAndGetClientNameNull() {
        Client client = new Client(1, "Original Name");
        client.setClientName(null);
        assertNull(client.getClientName());
    }

    @Test
    void testSetAndGetClientNameEmpty() {
        Client client = new Client(1, "Original Name");
        client.setClientName("");
        assertEquals("", client.getClientName());
    }

    @Test
    void testToString() {
        Client client = new Client(5, "Global Enterprises");
        String result = client.toString();
        assertTrue(result.contains("Client{"));
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("clientName='Global Enterprises'"));
    }

    @Test
    void testToStringWithNullName() {
        Client client = new Client(1, null);
        String result = client.toString();
        assertTrue(result.contains("Client{"));
        assertTrue(result.contains("id=1"));
    }

    @Test
    void testClientEquality() {
        Client client1 = new Client(1, "Company A");
        Client client2 = new Client(1, "Company A");
        assertEquals(client1.getId(), client2.getId());
        assertEquals(client1.getClientName(), client2.getClientName());
    }

    @Test
    void testClientInequality() {
        Client client1 = new Client(1, "Company A");
        Client client2 = new Client(2, "Company B");
        assertNotEquals(client1.getId(), client2.getId());
        assertNotEquals(client1.getClientName(), client2.getClientName());
    }

    @Test
    void testClientNameUpdate() {
        Client client = new Client(1, "Old Name");
        assertEquals("Old Name", client.getClientName());
        client.setClientName("New Name");
        assertEquals("New Name", client.getClientName());
    }
}
