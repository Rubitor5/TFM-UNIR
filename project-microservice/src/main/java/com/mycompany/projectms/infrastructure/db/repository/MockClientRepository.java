package com.mycompany.projectms.infrastructure.db.repository;

import com.mycompany.projectms.domain.model.Client;
import com.mycompany.projectms.domain.repository.ClientRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MockClientRepository implements ClientRepository {

    private static final Map<Integer, Client> CLIENTS = new HashMap<>();

    static {
        CLIENTS.put(1, new Client(1, "Acme Corporation"));
        CLIENTS.put(2, new Client(2, "Tech Innovations Inc"));
        CLIENTS.put(3, new Client(3, "Global Solutions Ltd"));
    }

    @Override
    public Client findClient(int clientId) {
        return CLIENTS.getOrDefault(clientId, null);
    }

    @Override
    public List<Client> findAllClients() {
        return new ArrayList<>(CLIENTS.values());
    }

    @Override
    public void saveClient(Client client) {
        if (client.getId() == 0) {
            client.setId(CLIENTS.size() + 1);
        }
        CLIENTS.put(client.getId(), client);
    }

    @Override
    public void updateClient(Client client) {
        if (CLIENTS.containsKey(client.getId())) {
            CLIENTS.put(client.getId(), client);
        }
    }

    @Override
    public void deleteClient(int clientId) {
        CLIENTS.remove(clientId);
    }
}
