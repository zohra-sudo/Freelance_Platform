package com.example.freelanceplatform;

import com.example.freelanceplatform.beans.ApplicationBean;
import com.example.freelanceplatform.beans.AuthBean;
import com.example.freelanceplatform.dao.CandidatureDAO;
import com.example.freelanceplatform.entities.Candidature;
import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationBeanTest {

    @Mock private ProjectService projectService;
    @Mock private CandidatureDAO candidatureDAO;
    @Mock private AuthBean authBean;
    @Mock private Part cvFile;

    @InjectMocks
    private ApplicationBean applicationBean;

    private AutoCloseable closeable;
    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setNom("Test Freelancer");
        testUser.setEmail("freelancer@test.com");
        testUser.setMotDePasse("password123");

        testProject = new Project();
        testProject.setId(100L);
        testProject.setTitle("Développement Application Mobile");
        testProject.setDescription("Création d'une application mobile React Native");
        testProject.setCategory("Mobile Development");
        testProject.setBudget(new BigDecimal("5000"));
        testProject.setSkills("React Native, Node.js");

        User projectAuthor = new User();
        projectAuthor.setId(2L);
        projectAuthor.setNom("Client Test");
        testProject.setAuthor(projectAuthor);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) closeable.close();
    }

    // ========== TESTS loadProject() ==========

    @Test
    @DisplayName("TC-01 : loadProject avec ID valide charge le projet")
    void testLoadProject_WithValidId() {
        applicationBean.setProjectId(100L);
        when(projectService.findById(100L)).thenReturn(testProject);

        applicationBean.loadProject();

        assertNotNull(applicationBean.getProject());
        assertEquals("Développement Application Mobile", applicationBean.getProject().getTitle());
        assertFalse(applicationBean.isCandidatureEnvoyee());
        assertNotNull(applicationBean.getCandidature().getProject());
        assertEquals(testProject, applicationBean.getCandidature().getProject());
        verify(projectService, times(1)).findById(100L);
    }

    @Test
    @DisplayName("TC-02 : loadProject avec ID null ne charge rien")
    void testLoadProject_WithNullId() {
        applicationBean.setProjectId(null);

        applicationBean.loadProject();

        assertNull(applicationBean.getProject());
        assertFalse(applicationBean.isCandidatureEnvoyee());
        verify(projectService, never()).findById(any());
    }

    @Test
    @DisplayName("TC-03 : loadProject avec ID inexistant retourne null")
    void testLoadProject_WithInvalidId() {
        applicationBean.setProjectId(999L);
        when(projectService.findById(999L)).thenReturn(null);

        applicationBean.loadProject();

        assertNull(applicationBean.getProject());
        assertFalse(applicationBean.isCandidatureEnvoyee());
        verify(projectService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("TC-04 : loadProject réinitialise candidatureEnvoyee")
    void testLoadProject_ResetsCandidatureEnvoyee() {
        applicationBean.setCandidatureEnvoyee(true);
        applicationBean.setProjectId(100L);
        when(projectService.findById(100L)).thenReturn(testProject);

        applicationBean.loadProject();

        assertFalse(applicationBean.isCandidatureEnvoyee());
    }

    // ========== TESTS submitApplication() ==========

    @Test
    @DisplayName("TC-05 : submitApplication sans connexion redirige vers login")
    void testSubmitApplication_UserNotLoggedIn() {
        when(authBean.isConnecte()).thenReturn(false);

        String result = applicationBean.submitApplication();

        assertEquals("login?faces-redirect=true", result);
        assertFalse(applicationBean.isCandidatureEnvoyee());
        verify(candidatureDAO, never()).save(any(Candidature.class));
    }

    @Test
    @DisplayName("TC-06 : submitApplication avec project null retourne null")
    void testSubmitApplication_ProjectNull() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);
        applicationBean.setProjectId(null);
        applicationBean.setProject(null);

        String result = applicationBean.submitApplication();

        assertNull(result);
        assertFalse(applicationBean.isCandidatureEnvoyee());
        verify(candidatureDAO, never()).save(any(Candidature.class));
    }

    @Test
    @DisplayName("TC-07 : submitApplication recharge le projet si null via projectId")
    void testSubmitApplication_ReloadsProjectIfNull() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);
        applicationBean.setProjectId(100L);
        applicationBean.setProject(null);
        when(projectService.findById(100L)).thenReturn(testProject);
        doNothing().when(candidatureDAO).save(any(Candidature.class));

        String result = applicationBean.submitApplication();

        assertNull(result);
        verify(projectService, times(1)).findById(100L);
        assertNotNull(applicationBean.getProject());
        assertTrue(applicationBean.isCandidatureEnvoyee());
    }

    @Test
    @DisplayName("TC-08 : submitApplication réussie — candidature bien sauvegardée")
    void testSubmitApplication_Success() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);
        applicationBean.setProjectId(100L);
        applicationBean.setProject(testProject);
        when(projectService.findById(100L)).thenReturn(testProject);
        doNothing().when(candidatureDAO).save(any(Candidature.class));

        String result = applicationBean.submitApplication();

        assertNull(result);
        assertTrue(applicationBean.isCandidatureEnvoyee());
    }

    @Test
    @DisplayName("TC-09 : submitApplication — données de candidature correctement renseignées")
    void testSubmitApplication_CorrectCandidatureData() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);
        applicationBean.setProjectId(100L);
        applicationBean.setProject(testProject);
        when(projectService.findById(100L)).thenReturn(testProject);
        doNothing().when(candidatureDAO).save(any(Candidature.class));

        applicationBean.submitApplication();

        ArgumentCaptor<Candidature> captor = ArgumentCaptor.forClass(Candidature.class);
        verify(candidatureDAO, times(1)).save(captor.capture());

        Candidature saved = captor.getValue();
        assertEquals(testProject, saved.getProject());
        assertEquals(testUser, saved.getFreelance());
        assertEquals("PENDING", saved.getStatut());
        assertNotNull(saved.getDatePostulation());
    }

    @Test
    @DisplayName("TC-10 : submitApplication — exception ne met pas candidatureEnvoyee à true")
    void testSubmitApplication_ExceptionKeepsFlagFalse() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);
        applicationBean.setProjectId(100L);
        applicationBean.setProject(testProject);
        when(projectService.findById(100L)).thenReturn(testProject);
        doThrow(new RuntimeException("Erreur base de données"))
                .when(candidatureDAO).save(any(Candidature.class));

        String result = applicationBean.submitApplication();

        assertNull(result);
        assertFalse(applicationBean.isCandidatureEnvoyee());
    }

    @Test
    @DisplayName("TC-11 : submitApplication avec projet inexistant")
    void testSubmitApplication_ProjectNotFound() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);
        applicationBean.setProjectId(999L);
        applicationBean.setProject(null);
        when(projectService.findById(999L)).thenReturn(null);

        String result = applicationBean.submitApplication();

        assertNull(result);
        assertFalse(applicationBean.isCandidatureEnvoyee());
        verify(candidatureDAO, never()).save(any(Candidature.class));
    }

    @Test
    @DisplayName("TC-12 : submitApplication sur son propre projet (interdit)")
    void testSubmitApplication_OwnProject() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(testUser);

        Project ownProject = new Project();
        ownProject.setId(200L);
        ownProject.setAuthor(testUser);

        applicationBean.setProjectId(200L);
        applicationBean.setProject(ownProject);
        when(projectService.findById(200L)).thenReturn(ownProject);

        String result = applicationBean.submitApplication();

        assertNull(result);
        assertFalse(applicationBean.isCandidatureEnvoyee());
        verify(candidatureDAO, never()).save(any(Candidature.class));
    }

    // ========== TESTS GETTERS/SETTERS ==========

    @Test
    @DisplayName("TC-13 : getters et setters de base")
    void testGettersAndSetters() {
        Candidature c = new Candidature();
        c.setMessageMotivation("Message test");

        applicationBean.setProjectId(500L);
        applicationBean.setProject(testProject);
        applicationBean.setCandidature(c);
        applicationBean.setCvFile(cvFile);

        assertEquals(500L, applicationBean.getProjectId());
        assertEquals(testProject, applicationBean.getProject());
        assertEquals(c, applicationBean.getCandidature());
        assertEquals(cvFile, applicationBean.getCvFile());
    }

    @Test
    @DisplayName("TC-14 : candidature initialisée non nulle par défaut")
    void testCandidatureInitialized() {
        assertNotNull(applicationBean.getCandidature());
    }

    @Test
    @DisplayName("TC-15 : candidatureEnvoyee false par défaut")
    void testCandidatureEnvoyeeFalseByDefault() {
        assertFalse(applicationBean.isCandidatureEnvoyee());
    }

    @Test
    @DisplayName("TC-16 : project null par défaut")
    void testProjectNullByDefault() {
        assertNull(applicationBean.getProject());
    }

    @Test
    @DisplayName("TC-17 : projectId null par défaut")
    void testProjectIdNullByDefault() {
        assertNull(applicationBean.getProjectId());
    }

    @Test
    @DisplayName("TC-18 : cvFile null par défaut")
    void testCvFileNullByDefault() {
        assertNull(applicationBean.getCvFile());
    }
}