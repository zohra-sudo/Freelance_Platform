package com.example.freelanceplatform;
import com.example.freelanceplatform.entities.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PosteTest {

    private Poste poste;
    private User testClient;

    @BeforeEach
    void setUp() {
        poste = new Poste();

        testClient = new User();
        testClient.setId(1L);
        testClient.setNom("Client Test");
        testClient.setEmail("client@test.com");
    }

    // ========== TESTS GETTERS/SETTERS ==========

    @Test
    @DisplayName("TC-POSTE-01 : test id getter/setter")
    void testId() {
        poste.setId(1L);
        assertEquals(1L, poste.getId());
    }

    @Test
    @DisplayName("TC-POSTE-02 : test titre getter/setter")
    void testTitre() {
        poste.setTitre("Développeur Full Stack");
        assertEquals("Développeur Full Stack", poste.getTitre());
    }

    @Test
    @DisplayName("TC-POSTE-03 : test description getter/setter")
    void testDescription() {
        poste.setDescription("Recherche développeur expérimenté en Java et Angular");
        assertEquals("Recherche développeur expérimenté en Java et Angular", poste.getDescription());
    }

    @Test
    @DisplayName("TC-POSTE-04 : test delaiEstimatif getter/setter")
    void testDelaiEstimatif() {
        poste.setDelaiEstimatif(30);
        assertEquals(30, poste.getDelaiEstimatif());
    }

    @Test
    @DisplayName("TC-POSTE-05 : test budget getter/setter")
    void testBudget() {
        poste.setBudget(5000.0);
        assertEquals(5000.0, poste.getBudget());
    }

    @Test
    @DisplayName("TC-POSTE-06 : test statut getter/setter")
    void testStatut() {
        poste.setStatut("OPEN");
        assertEquals("OPEN", poste.getStatut());
    }

    @Test
    @DisplayName("TC-POSTE-07 : test datePublication getter/setter")
    void testDatePublication() {
        Date now = new Date();
        poste.setDatePublication(now);
        assertEquals(now, poste.getDatePublication());
    }

    @Test
    @DisplayName("TC-POSTE-08 : test competencesRequises getter/setter")
    void testCompetencesRequises() {
        List<String> competences = Arrays.asList("Java", "Spring Boot", "Angular");
        poste.setCompetencesRequises(competences);
        assertEquals(3, poste.getCompetencesRequises().size());
        assertTrue(poste.getCompetencesRequises().contains("Java"));
        assertTrue(poste.getCompetencesRequises().contains("Spring Boot"));
        assertTrue(poste.getCompetencesRequises().contains("Angular"));
    }

    @Test
    @DisplayName("TC-POSTE-09 : test client getter/setter")
    void testClient() {
        poste.setClient(testClient);
        assertNotNull(poste.getClient());
        assertEquals(1L, poste.getClient().getId());
        assertEquals("Client Test", poste.getClient().getNom());
    }

    // ========== TESTS VALEURS NULL ==========

    @Test
    @DisplayName("TC-POSTE-10 : test valeurs par défaut")
    void testDefaultValues() {
        assertNull(poste.getId());
        assertNull(poste.getTitre());
        assertNull(poste.getDescription());
        assertNull(poste.getDelaiEstimatif());
        assertNull(poste.getBudget());
        assertNull(poste.getStatut());
        assertNull(poste.getDatePublication());
        assertNull(poste.getCompetencesRequises());
        assertNull(poste.getClient());
    }

    // ========== TESTS CHAMPS VIDES ==========

    @Test
    @DisplayName("TC-POSTE-11 : test titre vide")
    void testEmptyTitre() {
        poste.setTitre("");
        assertEquals("", poste.getTitre());
    }

    @Test
    @DisplayName("TC-POSTE-12 : test description vide")
    void testEmptyDescription() {
        poste.setDescription("");
        assertEquals("", poste.getDescription());
    }

    @Test
    @DisplayName("TC-POSTE-13 : test competencesRequises vide")
    void testEmptyCompetencesRequises() {
        poste.setCompetencesRequises(new ArrayList<>());
        assertTrue(poste.getCompetencesRequises().isEmpty());
    }

    // ========== TESTS VALEURS LIMITES ==========

    @Test
    @DisplayName("TC-POSTE-14 : test delaiEstimatif avec valeur négative")
    void testNegativeDelaiEstimatif() {
        poste.setDelaiEstimatif(-5);
        assertEquals(-5, poste.getDelaiEstimatif());
    }

    @Test
    @DisplayName("TC-POSTE-15 : test delaiEstimatif avec zéro")
    void testZeroDelaiEstimatif() {
        poste.setDelaiEstimatif(0);
        assertEquals(0, poste.getDelaiEstimatif());
    }

    @Test
    @DisplayName("TC-POSTE-16 : test delaiEstimatif avec grande valeur")
    void testLargeDelaiEstimatif() {
        poste.setDelaiEstimatif(365);
        assertEquals(365, poste.getDelaiEstimatif());
    }

    @Test
    @DisplayName("TC-POSTE-17 : test budget avec valeur négative")
    void testNegativeBudget() {
        poste.setBudget(-1000.0);
        assertEquals(-1000.0, poste.getBudget());
    }

    @Test
    @DisplayName("TC-POSTE-18 : test budget avec zéro")
    void testZeroBudget() {
        poste.setBudget(0.0);
        assertEquals(0.0, poste.getBudget());
    }

    @Test
    @DisplayName("TC-POSTE-19 : test budget avec grande valeur")
    void testLargeBudget() {
        poste.setBudget(1000000.0);
        assertEquals(1000000.0, poste.getBudget());
    }

    @Test
    @DisplayName("TC-POSTE-20 : test budget avec valeur décimale")
    void testDecimalBudget() {
        poste.setBudget(1250.75);
        assertEquals(1250.75, poste.getBudget());
    }

    // ========== TESTS STATUT ==========

    @Test
    @DisplayName("TC-POSTE-21 : test statut OPEN")
    void testStatutOpen() {
        poste.setStatut("OPEN");
        assertEquals("OPEN", poste.getStatut());
    }

    @Test
    @DisplayName("TC-POSTE-22 : test statut IN_PROGRESS")
    void testStatutInProgress() {
        poste.setStatut("IN_PROGRESS");
        assertEquals("IN_PROGRESS", poste.getStatut());
    }

    @Test
    @DisplayName("TC-POSTE-23 : test statut CLOSED")
    void testStatutClosed() {
        poste.setStatut("CLOSED");
        assertEquals("CLOSED", poste.getStatut());
    }

    @Test
    @DisplayName("TC-POSTE-24 : test statut CANCELLED")
    void testStatutCancelled() {
        poste.setStatut("CANCELLED");
        assertEquals("CANCELLED", poste.getStatut());
    }

    // ========== TESTS COMPETENCES ==========

    @Test
    @DisplayName("TC-POSTE-25 : test ajout compétence unique")
    void testSingleCompetence() {
        List<String> competences = new ArrayList<>();
        competences.add("Java");
        poste.setCompetencesRequises(competences);

        assertEquals(1, poste.getCompetencesRequises().size());
        assertEquals("Java", poste.getCompetencesRequises().get(0));
    }

    @Test
    @DisplayName("TC-POSTE-26 : test plusieurs compétences")
    void testMultipleCompetences() {
        List<String> competences = Arrays.asList("Python", "Django", "PostgreSQL", "Docker");
        poste.setCompetencesRequises(competences);

        assertEquals(4, poste.getCompetencesRequises().size());
        assertTrue(poste.getCompetencesRequises().contains("Python"));
        assertTrue(poste.getCompetencesRequises().contains("Docker"));
    }

    @Test
    @DisplayName("TC-POSTE-27 : test compétence avec espaces")
    void testCompetenceWithSpaces() {
        List<String> competences = Arrays.asList("  Java  ", " Spring Boot ");
        poste.setCompetencesRequises(competences);

        assertEquals("  Java  ", poste.getCompetencesRequises().get(0));
        assertEquals(" Spring Boot ", poste.getCompetencesRequises().get(1));
    }

    // ========== TESTS RELATIONS ==========

    @Test
    @DisplayName("TC-POSTE-28 : test relation avec User (client)")
    void testClientRelation() {
        User client = new User();
        client.setId(5L);
        client.setNom("Client Société X");

        poste.setClient(client);

        assertEquals(5L, poste.getClient().getId());
        assertEquals("Client Société X", poste.getClient().getNom());
    }

    @Test
    @DisplayName("TC-POSTE-29 : test client null")
    void testClientNull() {
        poste.setClient(null);
        assertNull(poste.getClient());
    }

    // ========== TESTS CHAÎNES LONGUES ==========

    @Test
    @DisplayName("TC-POSTE-30 : test titre très long")
    void testLongTitre() {
        String longTitre = "Développeur ".repeat(50);
        poste.setTitre(longTitre);
        assertEquals(longTitre, poste.getTitre());
    }

    @Test
    @DisplayName("TC-POSTE-31 : test description très longue")
    void testLongDescription() {
        String longDescription = "Description détaillée ".repeat(100);
        poste.setDescription(longDescription);
        assertEquals(longDescription, poste.getDescription());
    }

    // ========== TESTS DATE ==========

    @Test
    @DisplayName("TC-POSTE-32 : test datePublication avec date spécifique")
    void testSpecificDate() {
        @SuppressWarnings("deprecation")
        Date specificDate = new Date(2024 - 1900, 0, 15);
        poste.setDatePublication(specificDate);
        assertEquals(specificDate, poste.getDatePublication());
    }

    @Test
    @DisplayName("TC-POSTE-33 : test datePublication mise à jour")
    void testDateUpdate() {
        Date oldDate = new Date();
        poste.setDatePublication(oldDate);

        Date newDate = new Date(oldDate.getTime() + 86400000);
        poste.setDatePublication(newDate);

        assertEquals(newDate, poste.getDatePublication());
        assertNotEquals(oldDate, poste.getDatePublication());
    }

    // ========== TESTS MODIFICATION ==========

    @Test
    @DisplayName("TC-POSTE-34 : test modification du titre")
    void testTitreModification() {
        poste.setTitre("Titre original");
        assertEquals("Titre original", poste.getTitre());

        poste.setTitre("Nouveau titre");
        assertEquals("Nouveau titre", poste.getTitre());
    }

    @Test
    @DisplayName("TC-POSTE-35 : test modification du budget")
    void testBudgetModification() {
        poste.setBudget(1000.0);
        assertEquals(1000.0, poste.getBudget());

        poste.setBudget(2500.50);
        assertEquals(2500.50, poste.getBudget());
    }

    // ========== TESTS CONSTRUCTEUR ==========

    @Test
    @DisplayName("TC-POSTE-36 : test constructeur par défaut")
    void testDefaultConstructor() {
        Poste newPoste = new Poste();
        assertNotNull(newPoste);
        assertNull(newPoste.getId());
        assertNull(newPoste.getTitre());
        assertNull(newPoste.getCompetencesRequises());
    }

    // ========== TESTS DEUX POSTES DIFFÉRENTS ==========

    @Test
    @DisplayName("TC-POSTE-37 : test deux postes différents")
    void testDifferentPostes() {
        Poste p1 = new Poste();
        p1.setId(1L);
        p1.setTitre("Poste 1");

        Poste p2 = new Poste();
        p2.setId(2L);
        p2.setTitre("Poste 2");

        assertNotEquals(p1.getId(), p2.getId());
        assertNotEquals(p1.getTitre(), p2.getTitre());
    }
}