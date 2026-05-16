package com.mycompany.projectms.domain.service.impl;

import com.mycompany.projectms.domain.model.Client;
import com.mycompany.projectms.domain.repository.ClientRepository;
import com.mycompany.projectms.domain.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Client getClient(int clientId) {
        return clientRepository.findClient(clientId);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAllClients();
    }

    @Override
    public void createClient(Client client) {
        clientRepository.saveClient(client);
    }

    @Override
    public void updateClient(Client client) {
        Client existing = getClient(client.getId());
        if (existing == null) {
            throw new RuntimeException("Client not found: " + client.getId());
        }
        clientRepository.updateClient(client);
    }

    @Override
    public void deleteClient(int clientId) {
        Client existing = getClient(clientId);
        if (existing == null) {
            throw new RuntimeException("Client not found: " + clientId);
        }
        clientRepository.deleteClient(clientId);
    }
}
