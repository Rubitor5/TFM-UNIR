package com.mycompany.projectms.infrastructure.rest.mapper;

import com.mycompany.projectms.domain.model.Client;
import com.mycompany.projectms.infrastructure.rest.resource.ClientResource;

public class ClientResourceMapper {

    public static ClientResource mapToClientResource(Client client) {
        if (client == null) {
            return null;
        }
        return new ClientResource(client.getId(), client.getClientName());
    }

    public static Client mapToClient(ClientResource resource) {
        if (resource == null) {
            return null;
        }
        return new Client(resource.getId(), resource.getClientName());
    }
}
