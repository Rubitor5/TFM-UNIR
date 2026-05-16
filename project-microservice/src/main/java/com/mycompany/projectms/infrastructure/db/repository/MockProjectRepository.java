package com.mycompany.projectms.infrastructure.db.repository;

import com.mycompany.projectms.domain.model.Project;
import com.mycompany.projectms.domain.repository.ProjectRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MockProjectRepository implements ProjectRepository {

    private static final Map<Integer, Project> PROJECTS = new HashMap<>();

    static {
        try {
            PROJECTS.put(1, new Project(1, "Mobile App Development",
                new Date(System.currentTimeMillis() - 86400000L * 30), null, 1, "Acme Corporation"));
            PROJECTS.put(2, new Project(2, "Cloud Migration",
                new Date(System.currentTimeMillis() - 86400000L * 60), null, 2, "Tech Innovations Inc"));
            PROJECTS.put(3, new Project(3, "Data Analytics Platform",
                new Date(System.currentTimeMillis() - 86400000L * 45), null, 3, "Global Solutions Ltd"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Project findProject(int projectId) {
        return PROJECTS.getOrDefault(projectId, null);
    }

    @Override
    public List<Project> findAllProjects() {
        return new ArrayList<>(PROJECTS.values());
    }

    @Override
    public void saveProject(Project project) {
        if (project.getId() == 0) {
            project.setId(PROJECTS.size() + 1);
        }
        PROJECTS.put(project.getId(), project);
    }

    @Override
    public void updateProject(Project project) {
        if (PROJECTS.containsKey(project.getId())) {
            PROJECTS.put(project.getId(), project);
        }
    }

    @Override
    public void deleteProject(int projectId) {
        PROJECTS.remove(projectId);
    }
}
