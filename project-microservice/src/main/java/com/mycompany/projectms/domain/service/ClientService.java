package com.mycompany.projectms.domain.service;

import com.mycompany.projectms.domain.model.Client;
import java.util.List;

public interface ClientService {
    Client getClient(int clientId);
    List<Client> getAllClients();
    void createClient(Client client);
    void updateClient(Client client);
    void deleteClient(int clientId);
}
