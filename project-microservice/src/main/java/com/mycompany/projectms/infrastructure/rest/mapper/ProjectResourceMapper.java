package com.mycompany.projectms.infrastructure.rest.mapper;

import com.mycompany.projectms.domain.model.Project;
import com.mycompany.projectms.infrastructure.rest.resource.ProjectResource;

public class ProjectResourceMapper {

    public static ProjectResource mapToProjectResource(Project project) {
        if (project == null) {
            return null;
        }
        return new ProjectResource(project.getId(), project.getProjectTitle(), project.getDateStarted(),
                project.getDateEnded(), project.getClientId(), project.getClientName());
    }

    public static Project mapToProject(ProjectResource resource) {
        if (resource == null) {
            return null;
        }
        return new Project(resource.getId(), resource.getProjectTitle(), resource.getDateStarted(),
                resource.getDateEnded(), resource.getClientId(), resource.getClientName());
    }
}
