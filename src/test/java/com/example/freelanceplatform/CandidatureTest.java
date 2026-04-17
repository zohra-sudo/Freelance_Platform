package com.example.freelanceplatform;
import com.example.freelanceplatform.entities.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CandidatureTest {

    private Candidature candidature;
    private User testFreelance;
    private Project testProject;

    @BeforeEach
    void setUp() {
        candidature = new Candidature();

        testFreelance = new User();
        testFreelance.setId(1L);
        testFreelance.setNom("Jean Dupont");
        testFreelance.setEmail("jean@test.com");

        testProject = new Project();
        testProject.setId(100L);
        testProject.setTitle("Développement Web");
    }

    // ========== TESTS GETTERS/SETTERS ==========

    @Test
    @DisplayName("TC-CAND-01 : test id getter/setter")
    void testId() {
        candidature.setId(1L);
        assertEquals(1L, candidature.getId());
    }

    @Test
    @DisplayName("TC-CAND-02 : test messageMotivation getter/setter")
    void testMessageMotivation() {
        candidature.setMessageMotivation("Je suis très motivé pour ce poste");
        assertEquals("Je suis très motivé pour ce poste", candidature.getMessageMotivation());
    }

    @Test
    @DisplayName("TC-CAND-03 : test propositionDelai getter/setter")
    void testPropositionDelai() {
        candidature.setPropositionDelai(15);
        assertEquals(15, candidature.getPropositionDelai());
    }

    @Test
    @DisplayName("TC-CAND-04 : test statut getter/setter")
    void testStatut() {
        candidature.setStatut("PENDING");
        assertEquals("PENDING", candidature.getStatut());
    }

    @Test
    @DisplayName("TC-CAND-05 : test phone getter/setter")
    void testPhone() {
        candidature.setPhone("0612345678");
        assertEquals("0612345678", candidature.getPhone());
    }

    @Test
    @DisplayName("TC-CAND-06 : test portfolioLink getter/setter")
    void testPortfolioLink() {
        candidature.setPortfolioLink("https://portfolio.com/jean");
        assertEquals("https://portfolio.com/jean", candidature.getPortfolioLink());
    }

    @Test
    @DisplayName("TC-CAND-07 : test datePostulation getter/setter")
    void testDatePostulation() {
        Date now = new Date();
        candidature.setDatePostulation(now);
        assertEquals(now, candidature.getDatePostulation());
    }

    @Test
    @DisplayName("TC-CAND-08 : test freelance getter/setter")
    void testFreelance() {
        candidature.setFreelance(testFreelance);
        assertNotNull(candidature.getFreelance());
        assertEquals(1L, candidature.getFreelance().getId());
        assertEquals("Jean Dupont", candidature.getFreelance().getNom());
    }

    @Test
    @DisplayName("TC-CAND-09 : test project getter/setter")
    void testProject() {
        candidature.setProject(testProject);
        assertNotNull(candidature.getProject());
        assertEquals(100L, candidature.getProject().getId());
        assertEquals("Développement Web", candidature.getProject().getTitle());
    }

    // ========== TESTS VALEURS NULL ==========

    @Test
    @DisplayName("TC-CAND-10 : test valeurs par défaut")
    void testDefaultValues() {
        assertNull(candidature.getId());
        assertNull(candidature.getMessageMotivation());
        assertNull(candidature.getPropositionDelai());
        assertNull(candidature.getStatut());
        assertNull(candidature.getPhone());
        assertNull(candidature.getPortfolioLink());
        assertNull(candidature.getDatePostulation());
        assertNull(candidature.getFreelance());
        assertNull(candidature.getProject());
    }

    // ========== TESTS CHAMPS VIDES ==========

    @Test
    @DisplayName("TC-CAND-11 : test messageMotivation vide")
    void testEmptyMessageMotivation() {
        candidature.setMessageMotivation("");
        assertEquals("", candidature.getMessageMotivation());
    }

    @Test
    @DisplayName("TC-CAND-12 : test phone vide")
    void testEmptyPhone() {
        candidature.setPhone("");
        assertEquals("", candidature.getPhone());
    }

    @Test
    @DisplayName("TC-CAND-13 : test portfolioLink vide")
    void testEmptyPortfolioLink() {
        candidature.setPortfolioLink("");
        assertEquals("", candidature.getPortfolioLink());
    }

    // ========== TESTS VALEURS LIMITES ==========

    @Test
    @DisplayName("TC-CAND-14 : test propositionDelai avec valeur négative")
    void testNegativePropositionDelai() {
        candidature.setPropositionDelai(-5);
        assertEquals(-5, candidature.getPropositionDelai());
    }

    @Test
    @DisplayName("TC-CAND-15 : test propositionDelai avec zéro")
    void testZeroPropositionDelai() {
        candidature.setPropositionDelai(0);
        assertEquals(0, candidature.getPropositionDelai());
    }

    @Test
    @DisplayName("TC-CAND-16 : test propositionDelai avec grande valeur")
    void testLargePropositionDelai() {
        candidature.setPropositionDelai(999);
        assertEquals(999, candidature.getPropositionDelai());
    }

    // ========== TESTS STATUT ==========

    @Test
    @DisplayName("TC-CAND-17 : test statut PENDING")
    void testStatutPending() {
        candidature.setStatut("PENDING");
        assertEquals("PENDING", candidature.getStatut());
    }

    @Test
    @DisplayName("TC-CAND-18 : test statut ACCEPTED")
    void testStatutAccepted() {
        candidature.setStatut("ACCEPTED");
        assertEquals("ACCEPTED", candidature.getStatut());
    }

    @Test
    @DisplayName("TC-CAND-19 : test statut REJECTED")
    void testStatutRejected() {
        candidature.setStatut("REJECTED");
        assertEquals("REJECTED", candidature.getStatut());
    }

    // ========== TESTS RELATIONS ==========

    @Test
    @DisplayName("TC-CAND-20 : test relation avec User (freelance)")
    void testFreelanceRelation() {
        User freelance = new User();
        freelance.setId(10L);
        freelance.setNom("Marie Martin");

        candidature.setFreelance(freelance);

        assertEquals(10L, candidature.getFreelance().getId());
        assertEquals("Marie Martin", candidature.getFreelance().getNom());
    }

    @Test
    @DisplayName("TC-CAND-21 : test relation avec Project")
    void testProjectRelation() {
        Project project = new Project();
        project.setId(200L);
        project.setTitle("Application Mobile");

        candidature.setProject(project);

        assertEquals(200L, candidature.getProject().getId());
        assertEquals("Application Mobile", candidature.getProject().getTitle());
    }

    // ========== TESTS CHAÎNES LONGUES ==========

    @Test
    @DisplayName("TC-CAND-22 : test messageMotivation très long")
    void testLongMessageMotivation() {
        String longMessage = "a".repeat(1000);
        candidature.setMessageMotivation(longMessage);
        assertEquals(longMessage, candidature.getMessageMotivation());
    }

    @Test
    @DisplayName("TC-CAND-23 : test portfolioLink très long")
    void testLongPortfolioLink() {
        String longLink = "https://" + "a".repeat(200) + ".com";
        candidature.setPortfolioLink(longLink);
        assertEquals(longLink, candidature.getPortfolioLink());
    }

    // ========== TESTS DATE ==========

    @Test
    @DisplayName("TC-CAND-24 : test datePostulation avec date spécifique")
    void testSpecificDate() {
        @SuppressWarnings("deprecation")
        Date specificDate = new Date(2024 - 1900, 0, 15); // 15 janvier 2024
        candidature.setDatePostulation(specificDate);
        assertEquals(specificDate, candidature.getDatePostulation());
    }

    @Test
    @DisplayName("TC-CAND-25 : test datePostulation mise à jour")
    void testDateUpdate() {
        Date oldDate = new Date();
        candidature.setDatePostulation(oldDate);

        Date newDate = new Date(oldDate.getTime() + 86400000); // +1 jour
        candidature.setDatePostulation(newDate);

        assertEquals(newDate, candidature.getDatePostulation());
        assertNotEquals(oldDate, candidature.getDatePostulation());
    }

    // ========== TESTS ÉGALITÉ ==========

    @Test
    @DisplayName("TC-CAND-26 : test deux candidatures différentes")
    void testDifferentCandidatures() {
        Candidature c1 = new Candidature();
        c1.setId(1L);
        c1.setMessageMotivation("Message 1");

        Candidature c2 = new Candidature();
        c2.setId(2L);
        c2.setMessageMotivation("Message 2");

        assertNotEquals(c1.getId(), c2.getId());
        assertNotEquals(c1.getMessageMotivation(), c2.getMessageMotivation());
    }

    @Test
    @DisplayName("TC-CAND-27 : test constructeur par défaut")
    void testDefaultConstructor() {
        Candidature newCandidature = new Candidature();
        assertNotNull(newCandidature);
        assertNull(newCandidature.getId());
        assertNull(newCandidature.getMessageMotivation());
    }
}