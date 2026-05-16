package com.mycompany.projectms.domain.repository;

import com.mycompany.projectms.domain.model.Project;
import java.util.List;

public interface ProjectRepository {
    Project findProject(int projectId);
    List<Project> findAllProjects();
    void saveProject(Project project);
    void updateProject(Project project);
    void deleteProject(int projectId);
}
