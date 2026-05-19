package com.mycompany.projectms.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testProjectNoArgsConstructor() {
        Project project = new Project();
        assertEquals(0, project.getId());
        assertNull(project.getProjectTitle());
        assertNull(project.getDateStarted());
        assertNull(project.getDateEnded());
        assertEquals(0, project.getClientId());
        assertNull(project.getClientName());
    }

    @Test
    void testProjectAllArgsConstructor() {
        Date startDate = new Date();
        Date endDate = new Date();
        Project project = new Project(1, "Website Redesign", startDate, endDate, 5, "Acme Corp");
        
        assertEquals(1, project.getId());
        assertEquals("Website Redesign", project.getProjectTitle());
        assertEquals(startDate, project.getDateStarted());
        assertEquals(endDate, project.getDateEnded());
        assertEquals(5, project.getClientId());
        assertEquals("Acme Corp", project.getClientName());
    }

    @Test
    void testSetAndGetId() {
        Project project = new Project();
        project.setId(10);
        assertEquals(10, project.getId());
    }

    @Test
    void testSetAndGetProjectTitle() {
        Project project = new Project();
        project.setProjectTitle("Mobile App Development");
        assertEquals("Mobile App Development", project.getProjectTitle());
    }

    @Test
    void testSetAndGetDateStarted() {
        Project project = new Project();
        Date date = new Date();
        project.setDateStarted(date);
        assertEquals(date, project.getDateStarted());
    }

    @Test
    void testSetAndGetDateEnded() {
        Project project = new Project();
        Date date = new Date();
        project.setDateEnded(date);
        assertEquals(date, project.getDateEnded());
    }

    @Test
    void testSetAndGetClientId() {
        Project project = new Project();
        project.setClientId(15);
        assertEquals(15, project.getClientId());
    }

    @Test
    void testSetAndGetClientName() {
        Project project = new Project();
        project.setClientName("Global Tech Inc");
        assertEquals("Global Tech Inc", project.getClientName());
    }

    @Test
    void testProjectDatesNullable() {
        Project project = new Project(1, "Test Project", null, null, 1, "Client");
        assertNull(project.getDateStarted());
        assertNull(project.getDateEnded());
    }

    @Test
    void testToString() {
        Date startDate = new Date();
        Date endDate = new Date();
        Project project = new Project(1, "API Development", startDate, endDate, 5, "TechCorp");
        
        String result = project.toString();
        assertTrue(result.contains("Project{"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("projectTitle='API Development'"));
        assertTrue(result.contains("clientId=5"));
        assertTrue(result.contains("clientName='TechCorp'"));
    }

    @Test
    void testProjectEquality() {
        Date date = new Date();
        Project project1 = new Project(1, "Same Project", date, date, 5, "Client A");
        Project project2 = new Project(1, "Same Project", date, date, 5, "Client A");
        
        assertEquals(project1.getId(), project2.getId());
        assertEquals(project1.getProjectTitle(), project2.getProjectTitle());
        assertEquals(project1.getClientId(), project2.getClientId());
    }

    @Test
    void testProjectInequality() {
        Date date = new Date();
        Project project1 = new Project(1, "Project A", date, date, 5, "Client A");
        Project project2 = new Project(2, "Project B", date, date, 6, "Client B");
        
        assertNotEquals(project1.getId(), project2.getId());
        assertNotEquals(project1.getProjectTitle(), project2.getProjectTitle());
    }

    @Test
    void testProjectUpdate() {
        Project project = new Project(1, "Old Title", null, null, 1, "Old Client");
        project.setProjectTitle("New Title");
        project.setClientName("New Client");
        
        assertEquals("New Title", project.getProjectTitle());
        assertEquals("New Client", project.getClientName());
    }

    @Test
    void testProjectNullValues() {
        Project project = new Project(1, null, null, null, 0, null);
        assertNull(project.getProjectTitle());
        assertNull(project.getDateStarted());
        assertNull(project.getDateEnded());
        assertNull(project.getClientName());
    }
}
