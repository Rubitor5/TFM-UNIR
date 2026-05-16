package com.mycompany.projectms.domain.service.impl;

import com.mycompany.projectms.domain.model.Project;
import com.mycompany.projectms.domain.repository.ProjectRepository;
import com.mycompany.projectms.domain.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project getProject(int projectId) {
        return projectRepository.findProject(projectId);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAllProjects();
    }

    @Override
    public void createProject(Project project) {
        projectRepository.saveProject(project);
    }

    @Override
    public void updateProject(Project project) {
        Project existing = getProject(project.getId());
        if (existing == null) {
            throw new RuntimeException("Project not found: " + project.getId());
        }
        projectRepository.updateProject(project);
    }

    @Override
    public void deleteProject(int projectId) {
        Project existing = getProject(projectId);
        if (existing == null) {
            throw new RuntimeException("Project not found: " + projectId);
        }
        projectRepository.deleteProject(projectId);
    }
}
