package com.mycompany.projectms.infrastructure.rest.endpoint;

import com.mycompany.projectms.domain.model.Client;
import com.mycompany.projectms.domain.service.ClientService;
import com.mycompany.projectms.infrastructure.rest.mapper.ClientResourceMapper;
import com.mycompany.projectms.infrastructure.rest.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
public class ClientRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRestEndpoint.class);

    @Autowired
    private ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientResource>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        List<ClientResource> resources = clients.stream()
            .map(ClientResourceMapper::mapToClientResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResource> getClient(@PathVariable Integer clientId) {
        Client client = clientService.getClient(clientId);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClientResourceMapper.mapToClientResource(client));
    }

    @PostMapping
    public ResponseEntity<ClientResource> createClient(@RequestBody ClientResource resource) {
        Client client = ClientResourceMapper.mapToClient(resource);
        clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResourceMapper.mapToClientResource(client));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResource> updateClient(@PathVariable Integer clientId, @RequestBody ClientResource resource) {
        resource.setId(clientId);
        try {
            Client client = ClientResourceMapper.mapToClient(resource);
            clientService.updateClient(client);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Integer clientId) {
        try {
            clientService.deleteClient(clientId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
