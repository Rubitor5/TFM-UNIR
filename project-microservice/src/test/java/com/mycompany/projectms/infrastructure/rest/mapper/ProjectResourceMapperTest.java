package com.mycompany.projectms.infrastructure.rest.mapper;

import com.mycompany.projectms.domain.model.Project;
import com.mycompany.projectms.infrastructure.rest.resource.ProjectResource;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectResourceMapperTest {

    @Test
    void testMapToProjectResourceSuccess() {
        Date startDate = new Date();
        Date endDate = new Date();
        Project project = new Project(1, "Website Redesign", startDate, endDate, 5, "Acme Corp");
        
        ProjectResource resource = ProjectResourceMapper.mapToProjectResource(project);
        
        assertNotNull(resource);
        assertEquals(1, resource.getId());
        assertEquals("Website Redesign", resource.getProjectTitle());
        assertEquals(startDate, resource.getDateStarted());
        assertEquals(endDate, resource.getDateEnded());
        assertEquals(5, resource.getClientId());
        assertEquals("Acme Corp", resource.getClientName());
    }

    @Test
    void testMapToProjectResourceNull() {
        ProjectResource resource = ProjectResourceMapper.mapToProjectResource(null);
        assertNull(resource);
    }

    @Test
    void testMapToProjectResourceWithNullDates() {
        Project project = new Project(2, "Mobile App", null, null, 3, "TechCorp");
        ProjectResource resource = ProjectResourceMapper.mapToProjectResource(project);
        
        assertNotNull(resource);
        assertEquals(2, resource.getId());
        assertNull(resource.getDateStarted());
        assertNull(resource.getDateEnded());
    }

    @Test
    void testMapToProjectSuccess() {
        Date startDate = new Date();
        Date endDate = new Date();
        ProjectResource resource = new ProjectResource(3, "API Development", startDate, endDate, 7, "GlobalTech");
        
        Project project = ProjectResourceMapper.mapToProject(resource);
        
        assertNotNull(project);
        assertEquals(3, project.getId());
        assertEquals("API Development", project.getProjectTitle());
        assertEquals(startDate, project.getDateStarted());
        assertEquals(endDate, project.getDateEnded());
        assertEquals(7, project.getClientId());
        assertEquals("GlobalTech", project.getClientName());
    }

    @Test
    void testMapToProjectNull() {
        Project project = ProjectResourceMapper.mapToProject(null);
        assertNull(project);
    }

    @Test
    void testMapToProjectWithNullValues() {
        ProjectResource resource = new ProjectResource(4, null, null, null, 0, null);
        Project project = ProjectResourceMapper.mapToProject(resource);
        
        assertNotNull(project);
        assertEquals(4, project.getId());
        assertNull(project.getProjectTitle());
        assertNull(project.getClientName());
    }

    @Test
    void testBidirectionalMapping() {
        Date startDate = new Date();
        Date endDate = new Date();
        Project originalProject = new Project(5, "Infrastructure Setup", startDate, endDate, 9, "DevOps Inc");
        
        ProjectResource resource = ProjectResourceMapper.mapToProjectResource(originalProject);
        Project mappedBackProject = ProjectResourceMapper.mapToProject(resource);
        
        assertEquals(originalProject.getId(), mappedBackProject.getId());
        assertEquals(originalProject.getProjectTitle(), mappedBackProject.getProjectTitle());
        assertEquals(originalProject.getDateStarted(), mappedBackProject.getDateStarted());
        assertEquals(originalProject.getDateEnded(), mappedBackProject.getDateEnded());
        assertEquals(originalProject.getClientId(), mappedBackProject.getClientId());
        assertEquals(originalProject.getClientName(), mappedBackProject.getClientName());
    }

    @Test
    void testMapMultipleProjects() {
        Date date = new Date();
        Project project1 = new Project(1, "Project A", date, date, 1, "Client A");
        Project project2 = new Project(2, "Project B", date, date, 2, "Client B");
        
        ProjectResource resource1 = ProjectResourceMapper.mapToProjectResource(project1);
        ProjectResource resource2 = ProjectResourceMapper.mapToProjectResource(project2);
        
        assertNotNull(resource1);
        assertNotNull(resource2);
        assertNotEquals(resource1.getId(), resource2.getId());
        assertNotEquals(resource1.getProjectTitle(), resource2.getProjectTitle());
    }

    @Test
    void testMapToProjectResourceWithEmptyStrings() {
        Project project = new Project(6, "", new Date(), new Date(), 0, "");
        ProjectResource resource = ProjectResourceMapper.mapToProjectResource(project);
        
        assertEquals("", resource.getProjectTitle());
        assertEquals("", resource.getClientName());
    }

    @Test
    void testMapToProjectResourceWithSpecialCharacters() {
        Project project = new Project(7, "Project & Infrastructure, Phase 2", new Date(), new Date(), 1, "Client & Partners LLC");
        ProjectResource resource = ProjectResourceMapper.mapToProjectResource(project);
        
        assertEquals("Project & Infrastructure, Phase 2", resource.getProjectTitle());
        assertEquals("Client & Partners LLC", resource.getClientName());
    }
}
