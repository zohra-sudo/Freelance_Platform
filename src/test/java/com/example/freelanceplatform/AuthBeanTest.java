package com.example.freelanceplatform;

import com.example.freelanceplatform.beans.AuthBean;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthBeanTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthBean authBean;

    private User testUser;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setNom("zohra elghomariii");
        testUser.setEmail("zohra@test.com");
        testUser.setMotDePasse("password123");

        authBean.setEmail("zohra@test.com");
        authBean.setMotDePasse("password123");
    }

    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    // ========== TESTS CONNEXION ==========

    @Test
    @DisplayName("TC-CONNEXION-01 : Connexion réussie avec identifiants valides")
    void testConnecter_Success() {
        // Arrange
        when(userService.connecter("zohra@test.com", "password123")).thenReturn(testUser);

        // Act
        String result = authBean.connecter();

        // Assert
        assertEquals("index?faces-redirect=true", result);
        assertNotNull(authBean.getUserConnecte());
        assertEquals("zohra@test.com", authBean.getUserConnecte().getEmail());
        assertNull(authBean.getMessageErreur());
        verify(userService, times(1)).connecter("zohra@test.com", "password123");
    }

    @Test
    @DisplayName("TC-CONNEXION-02 : Échec connexion avec mot de passe incorrect")
    void testConnecter_WrongPassword() {
        // Arrange
        authBean.setMotDePasse("wrongpassword");
        when(userService.connecter("zohra@test.com", "wrongpassword")).thenReturn(null);

        // Act
        String result = authBean.connecter();

        // Assert
        assertNull(result);
        assertNull(authBean.getUserConnecte());
        assertEquals("Email or password incorrect!", authBean.getMessageErreur());
    }

    @Test
    @DisplayName("TC-CONNEXION-03 : Échec connexion avec email inexistant")
    void testConnecter_EmailNotFound() {
        // Arrange
        authBean.setEmail("inexistant@test.com");
        when(userService.connecter("inexistant@test.com", "password123")).thenReturn(null);

        // Act
        String result = authBean.connecter();

        // Assert
        assertNull(result);
        assertNull(authBean.getUserConnecte());
        assertEquals("Email or password incorrect!", authBean.getMessageErreur());
    }

    @Test
    @DisplayName("TC-CONNEXION-04 : Échec connexion avec champs vides")
    void testConnecter_EmptyFields() {
        // Arrange
        authBean.setEmail("");
        authBean.setMotDePasse("");
        when(userService.connecter("", "")).thenReturn(null);

        // Act
        String result = authBean.connecter();

        // Assert
        assertNull(result);
        assertNull(authBean.getUserConnecte());
        assertNotNull(authBean.getMessageErreur());
    }

    // ========== TESTS INSCRIPTION ==========

    @Test
    @DisplayName("TC-INSCRIPTION-01 : Inscription réussie avec email unique")
    void testInscrire_Success() {
        // Arrange
        User newUser = new User();
        newUser.setNom("Marie Martin");
        newUser.setEmail("marie@test.com");
        newUser.setMotDePasse("newpassword");
        authBean.setNouveauUser(newUser);

        when(userService.inscrire(any(User.class))).thenReturn(true);

        // Act
        String result = authBean.inscrire();

        // Assert
        assertEquals("login?faces-redirect=true", result);
        assertNull(authBean.getMessageErreur());
        verify(userService, times(1)).inscrire(any(User.class));
    }

    @Test
    @DisplayName("TC-INSCRIPTION-02 : Échec inscription avec email déjà existant")
    void testInscrire_EmailAlreadyExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setNom("Pierre Durand");
        existingUser.setEmail("existant@test.com");
        existingUser.setMotDePasse("password");
        authBean.setNouveauUser(existingUser);

        when(userService.inscrire(any(User.class))).thenReturn(false);

        // Act
        String result = authBean.inscrire();

        // Assert
        assertNull(result);
        assertEquals("This email is already in use!", authBean.getMessageErreur());
        verify(userService, times(1)).inscrire(any(User.class));
    }

    @Test
    @DisplayName("TC-INSCRIPTION-03 : Échec inscription avec champs obligatoires vides")
    void testInscrire_EmptyRequiredFields() {
        // Arrange
        User invalidUser = new User();
        invalidUser.setNom("");
        invalidUser.setEmail("");
        invalidUser.setMotDePasse("");
        authBean.setNouveauUser(invalidUser);

        when(userService.inscrire(any(User.class))).thenReturn(false);

        // Act
        String result = authBean.inscrire();

        // Assert
        assertNull(result);
        assertNotNull(authBean.getMessageErreur());
    }

    // ========== TESTS RÉINITIALISATION MOT DE PASSE ==========

    @Test
    @DisplayName("TC-RESET-01 : Vérification code secret réussi")
    void testVerifierCode_Success() {
        // Arrange
        authBean.setResetEmail("zohra@test.com");
        authBean.setResetRecoveryCode("CODE123");

        when(userService.verifierCodeSecret("zohra@test.com", "CODE123")).thenReturn(testUser);

        // Act
        String result = authBean.verifierCode();

        // Assert
        assertEquals("updatePassword?faces-redirect=true", result);
        assertNull(authBean.getMessageErreur());
    }

    @Test
    @DisplayName("TC-RESET-02 : Échec vérification code secret incorrect")
    void testVerifierCode_WrongCode() {
        // Arrange
        authBean.setResetEmail("zohra@test.com");
        authBean.setResetRecoveryCode("WRONGCODE");

        when(userService.verifierCodeSecret("zohra@test.com", "WRONGCODE")).thenReturn(null);

        // Act
        String result = authBean.verifierCode();

        // Assert
        assertNull(result);
        assertEquals("Email or Recovery Code incorrect!", authBean.getMessageErreur());
    }

    @Test
    @DisplayName("TC-RESET-03 : Échec vérification avec email inexistant")
    void testVerifierCode_EmailNotFound() {
        // Arrange
        authBean.setResetEmail("inexistant@test.com");
        authBean.setResetRecoveryCode("CODE123");

        when(userService.verifierCodeSecret("inexistant@test.com", "CODE123")).thenReturn(null);

        // Act
        String result = authBean.verifierCode();

        // Assert
        assertNull(result);
        assertEquals("Email or Recovery Code incorrect!", authBean.getMessageErreur());
    }

    @Test
    @DisplayName("TC-RESET-05 : Échec validation avec nouveau mot de passe vide")
    void testValiderNouveauMotDePasse_EmptyPassword() {
        // Arrange
        authBean.setNewMotDePasse("");

        // Act
        String result = authBean.validerNouveauMotDePasse();

        // Assert
        assertNull(result);
    }

    // ========== TESTS isConnecte() ==========

    @Test
    @DisplayName("TC-ETAT-01 : isConnecte retourne true quand utilisateur connecté")
    void testIsConnecte_True() {
        // Arrange
        authBean.setUserConnecte(testUser);

        // Act & Assert
        assertTrue(authBean.isConnecte());
    }

    @Test
    @DisplayName("TC-ETAT-02 : isConnecte retourne false quand aucun utilisateur connecté")
    void testIsConnecte_False() {
        // Arrange
        authBean.setUserConnecte(null);

        // Act & Assert
        assertFalse(authBean.isConnecte());
    }
}