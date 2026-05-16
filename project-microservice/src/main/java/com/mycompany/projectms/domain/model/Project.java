package com.mycompany.projectms.domain.model;

import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String projectTitle;
    private Date dateStarted;
    private Date dateEnded;
    private int clientId;
    private String clientName;

    public Project() {
    }

    public Project(int id, String projectTitle, Date dateStarted, Date dateEnded, int clientId, String clientName) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.dateStarted = dateStarted;
        this.dateEnded = dateEnded;
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateEnded() {
        return dateEnded;
    }

    public void setDateEnded(Date dateEnded) {
        this.dateEnded = dateEnded;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "Project{" + "id=" + id + ", projectTitle='" + projectTitle + '\'' + ", dateStarted=" + dateStarted
                + ", dateEnded=" + dateEnded + ", clientId=" + clientId + ", clientName='" + clientName + '\'' + '}';
    }
}
