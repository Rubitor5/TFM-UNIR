package com.mycompany.projectms.domain.repository;

import com.mycompany.projectms.domain.model.Client;
import java.util.List;

public interface ClientRepository {
    Client findClient(int clientId);
    List<Client> findAllClients();
    void saveClient(Client client);
    void updateClient(Client client);
    void deleteClient(int clientId);
}
