package com.mycompany.projectms.domain.service;

import com.mycompany.projectms.domain.model.Client;
import com.mycompany.projectms.domain.repository.ClientRepository;
import com.mycompany.projectms.domain.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetClientSuccess() {
        Client expectedClient = new Client(1, "Acme Corp");
        when(clientRepository.findClient(1)).thenReturn(expectedClient);

        Client result = clientService.getClient(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Acme Corp", result.getClientName());
        verify(clientRepository, times(1)).findClient(1);
    }

    @Test
    void testGetClientNotFound() {
        when(clientRepository.findClient(999)).thenReturn(null);

        Client result = clientService.getClient(999);

        assertNull(result);
        verify(clientRepository, times(1)).findClient(999);
    }

    @Test
    void testGetAllClientsSuccess() {
        List<Client> expectedClients = Arrays.asList(
            new Client(1, "Client A"),
            new Client(2, "Client B"),
            new Client(3, "Client C")
        );
        when(clientRepository.findAllClients()).thenReturn(expectedClients);

        List<Client> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Client A", result.get(0).getClientName());
        assertEquals("Client B", result.get(1).getClientName());
        assertEquals("Client C", result.get(2).getClientName());
        verify(clientRepository, times(1)).findAllClients();
    }

    @Test
    void testGetAllClientsEmpty() {
        when(clientRepository.findAllClients()).thenReturn(Arrays.asList());

        List<Client> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(clientRepository, times(1)).findAllClients();
    }

    @Test
    void testCreateClientSuccess() {
        Client client = new Client(1, "New Client");

        clientService.createClient(client);

        verify(clientRepository, times(1)).saveClient(client);
    }

    @Test
    void testUpdateClientSuccess() {
        Client existingClient = new Client(1, "Old Name");
        Client updatedClient = new Client(1, "New Name");
        
        when(clientRepository.findClient(1)).thenReturn(existingClient);

        clientService.updateClient(updatedClient);

        verify(clientRepository, times(1)).findClient(1);
        verify(clientRepository, times(1)).updateClient(updatedClient);
    }

    @Test
    void testUpdateClientNotFound() {
        Client client = new Client(999, "Non-existent Client");
        when(clientRepository.findClient(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> clientService.updateClient(client));
        verify(clientRepository, times(1)).findClient(999);
        verify(clientRepository, never()).updateClient(any());
    }

    @Test
    void testDeleteClientSuccess() {
        Client existingClient = new Client(1, "Client to Delete");
        when(clientRepository.findClient(1)).thenReturn(existingClient);

        clientService.deleteClient(1);

        verify(clientRepository, times(1)).findClient(1);
        verify(clientRepository, times(1)).deleteClient(1);
    }

    @Test
    void testDeleteClientNotFound() {
        when(clientRepository.findClient(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> clientService.deleteClient(999));
        verify(clientRepository, times(1)).findClient(999);
        verify(clientRepository, never()).deleteClient(anyInt());
    }

    @Test
    void testUpdateClientThrowsCorrectException() {
        Client client = new Client(50, "Test");
        when(clientRepository.findClient(50)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.updateClient(client);
        });

        assertTrue(exception.getMessage().contains("Client not found"));
    }

    @Test
    void testDeleteClientThrowsCorrectException() {
        when(clientRepository.findClient(75)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.deleteClient(75);
        });

        assertTrue(exception.getMessage().contains("Client not found"));
    }

    @Test
    void testCreateMultipleClients() {
        Client client1 = new Client(1, "Client 1");
        Client client2 = new Client(2, "Client 2");

        clientService.createClient(client1);
        clientService.createClient(client2);

        verify(clientRepository, times(2)).saveClient(any());
    }
}
