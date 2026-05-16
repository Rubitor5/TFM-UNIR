package com.mycompany.projectms.infrastructure.rest.resource;

import java.io.Serializable;

public class ClientResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String clientName;

    public ClientResource() {
    }

    public ClientResource(int id, String clientName) {
        this.id = id;
        this.clientName = clientName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "ClientResource{" + "id=" + id + ", clientName='" + clientName + '\'' + '}';
    }
}
