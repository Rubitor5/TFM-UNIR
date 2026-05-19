package com.mycompany.projectms.domain.service;

import com.mycompany.projectms.domain.model.Project;
import com.mycompany.projectms.domain.repository.ProjectRepository;
import com.mycompany.projectms.domain.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProjectSuccess() {
        Date startDate = new Date();
        Date endDate = new Date();
        Project expectedProject = new Project(1, "Website Redesign", startDate, endDate, 5, "Acme Corp");
        when(projectRepository.findProject(1)).thenReturn(expectedProject);

        Project result = projectService.getProject(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Website Redesign", result.getProjectTitle());
        assertEquals(5, result.getClientId());
        verify(projectRepository, times(1)).findProject(1);
    }

    @Test
    void testGetProjectNotFound() {
        when(projectRepository.findProject(999)).thenReturn(null);

        Project result = projectService.getProject(999);

        assertNull(result);
        verify(projectRepository, times(1)).findProject(999);
    }

    @Test
    void testGetAllProjectsSuccess() {
        Date date = new Date();
        List<Project> expectedProjects = Arrays.asList(
            new Project(1, "Project A", date, date, 1, "Client A"),
            new Project(2, "Project B", date, date, 2, "Client B"),
            new Project(3, "Project C", date, date, 3, "Client C")
        );
        when(projectRepository.findAllProjects()).thenReturn(expectedProjects);

        List<Project> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Project A", result.get(0).getProjectTitle());
        assertEquals("Project B", result.get(1).getProjectTitle());
        assertEquals("Project C", result.get(2).getProjectTitle());
        verify(projectRepository, times(1)).findAllProjects();
    }

    @Test
    void testGetAllProjectsEmpty() {
        when(projectRepository.findAllProjects()).thenReturn(Arrays.asList());

        List<Project> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(projectRepository, times(1)).findAllProjects();
    }

    @Test
    void testCreateProjectSuccess() {
        Date date = new Date();
        Project project = new Project(1, "New Project", date, date, 1, "Client");

        projectService.createProject(project);

        verify(projectRepository, times(1)).saveProject(project);
    }

    @Test
    void testUpdateProjectSuccess() {
        Date date = new Date();
        Project existingProject = new Project(1, "Old Title", date, date, 1, "Client");
        Project updatedProject = new Project(1, "New Title", date, date, 1, "Client");
        
        when(projectRepository.findProject(1)).thenReturn(existingProject);

        projectService.updateProject(updatedProject);

        verify(projectRepository, times(1)).findProject(1);
        verify(projectRepository, times(1)).updateProject(updatedProject);
    }

    @Test
    void testUpdateProjectNotFound() {
        Date date = new Date();
        Project project = new Project(999, "Non-existent Project", date, date, 1, "Client");
        when(projectRepository.findProject(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> projectService.updateProject(project));
        verify(projectRepository, times(1)).findProject(999);
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void testDeleteProjectSuccess() {
        Date date = new Date();
        Project existingProject = new Project(1, "Project to Delete", date, date, 1, "Client");
        when(projectRepository.findProject(1)).thenReturn(existingProject);

        projectService.deleteProject(1);

        verify(projectRepository, times(1)).findProject(1);
        verify(projectRepository, times(1)).deleteProject(1);
    }

    @Test
    void testDeleteProjectNotFound() {
        when(projectRepository.findProject(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> projectService.deleteProject(999));
        verify(projectRepository, times(1)).findProject(999);
        verify(projectRepository, never()).deleteProject(anyInt());
    }

    @Test
    void testUpdateProjectThrowsCorrectException() {
        Date date = new Date();
        Project project = new Project(50, "Test", date, date, 1, "Client");
        when(projectRepository.findProject(50)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.updateProject(project);
        });

        assertTrue(exception.getMessage().contains("Project not found"));
    }

    @Test
    void testDeleteProjectThrowsCorrectException() {
        when(projectRepository.findProject(75)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.deleteProject(75);
        });

        assertTrue(exception.getMessage().contains("Project not found"));
    }

    @Test
    void testCreateMultipleProjects() {
        Date date = new Date();
        Project project1 = new Project(1, "Project 1", date, date, 1, "Client 1");
        Project project2 = new Project(2, "Project 2", date, date, 2, "Client 2");

        projectService.createProject(project1);
        projectService.createProject(project2);

        verify(projectRepository, times(2)).saveProject(any());
    }

    @Test
    void testGetProjectWithNullDates() {
        Project expectedProject = new Project(5, "Project Without Dates", null, null, 1, "Client");
        when(projectRepository.findProject(5)).thenReturn(expectedProject);

        Project result = projectService.getProject(5);

        assertNotNull(result);
        assertNull(result.getDateStarted());
        assertNull(result.getDateEnded());
    }
}
