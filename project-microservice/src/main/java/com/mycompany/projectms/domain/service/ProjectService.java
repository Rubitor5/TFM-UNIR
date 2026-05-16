package com.mycompany.projectms.domain.service;

import com.mycompany.projectms.domain.model.Project;
import java.util.List;

public interface ProjectService {
    Project getProject(int projectId);
    List<Project> getAllProjects();
    void createProject(Project project);
    void updateProject(Project project);
    void deleteProject(int projectId);
}
