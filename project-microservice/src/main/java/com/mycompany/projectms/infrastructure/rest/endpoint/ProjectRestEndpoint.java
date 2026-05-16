package com.mycompany.projectms.infrastructure.rest.endpoint;

import com.mycompany.projectms.domain.model.Project;
import com.mycompany.projectms.domain.service.ProjectService;
import com.mycompany.projectms.infrastructure.rest.mapper.ProjectResourceMapper;
import com.mycompany.projectms.infrastructure.rest.resource.ProjectResource;
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
@RequestMapping("/api/projects")
public class ProjectRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRestEndpoint.class);

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResource>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        List<ProjectResource> resources = projects.stream()
            .map(ProjectResourceMapper::mapToProjectResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResource> getProject(@PathVariable Integer projectId) {
        Project project = projectService.getProject(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProjectResourceMapper.mapToProjectResource(project));
    }

    @PostMapping
    public ResponseEntity<ProjectResource> createProject(@RequestBody ProjectResource resource) {
        Project project = ProjectResourceMapper.mapToProject(resource);
        projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResourceMapper.mapToProjectResource(project));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResource> updateProject(@PathVariable Integer projectId, @RequestBody ProjectResource resource) {
        resource.setId(projectId);
        try {
            Project project = ProjectResourceMapper.mapToProject(resource);
            projectService.updateProject(project);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
