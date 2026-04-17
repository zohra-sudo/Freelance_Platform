package com.example.freelanceplatform;

import com.example.freelanceplatform.beans.AuthBean;
import com.example.freelanceplatform.beans.MessagerieBean;
import com.example.freelanceplatform.dao.MessageDAO;
import com.example.freelanceplatform.entities.Discussion;
import com.example.freelanceplatform.entities.Message;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessagerieBeanTest {

    @Mock private MessageDAO messageDAO;
    @Mock private UserService userService;
    @Mock private AuthBean authBean;

    @InjectMocks
    private MessagerieBean messagerieBean;

    private AutoCloseable closeable;
    private User currentUser;
    private User otherUser;
    private Discussion discussion;
    private Message message;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Utilisateur courant
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setNom("Jean Dupont");

        // Autre utilisateur
        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setNom("Marie Martin");

        // Discussion
        discussion = new Discussion();
        discussion.setId(10L);
        List<User> participants = new ArrayList<>();
        participants.add(currentUser);
        participants.add(otherUser);
        discussion.setParticipants(participants);

        // Message
        message = new Message();
        message.setId(100L);
        message.setContenu("Bonjour");
        message.setDateEnvoi(LocalDateTime.now());
        message.setExpediteur(currentUser);
        message.setDiscussion(discussion);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) closeable.close();
    }

    // ========== TESTS getInterlocutor ==========

    @Test
    @DisplayName("getInterlocutor retourne l'autre participant")
    void testGetInterlocutor() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        User interlocutor = messagerieBean.getInterlocutor(discussion);

        assertNotNull(interlocutor);
        assertEquals(2L, interlocutor.getId());
        assertEquals("Marie Martin", interlocutor.getNom());
    }

    @Test
    @DisplayName("getInterlocutor retourne null si discussion null")
    void testGetInterlocutor_NullDiscussion() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        User interlocutor = messagerieBean.getInterlocutor(null);

        assertNull(interlocutor);
    }

    @Test
    @DisplayName("getInterlocutor retourne null si non connecté")
    void testGetInterlocutor_NotConnected() {
        when(authBean.isConnecte()).thenReturn(false);

        User interlocutor = messagerieBean.getInterlocutor(discussion);

        assertNull(interlocutor);
    }

    // ========== TESTS getInterlocutorName ==========

    @Test
    @DisplayName("getInterlocutorName retourne le nom")
    void testGetInterlocutorName() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        String name = messagerieBean.getInterlocutorName(discussion);

        assertEquals("Marie Martin", name);
    }

    @Test
    @DisplayName("getInterlocutorName retourne Unknown si discussion null")
    void testGetInterlocutorName_Null() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        String name = messagerieBean.getInterlocutorName(null);

        assertEquals("Unknown", name);
    }

    // ========== TESTS getInterlocutorInitials ==========

    @Test
    @DisplayName("getInterlocutorInitials retourne les initiales")
    void testGetInterlocutorInitials() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        String initials = messagerieBean.getInterlocutorInitials(discussion);

        assertEquals("MM", initials);
    }

    @Test
    @DisplayName("getInterlocutorInitials retourne ? si nom null")
    void testGetInterlocutorInitials_NullName() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        Discussion d = new Discussion();
        List<User> participants = new ArrayList<>();
        User userWithoutName = new User();
        userWithoutName.setId(3L);
        userWithoutName.setNom(null);
        participants.add(currentUser);
        participants.add(userWithoutName);
        d.setParticipants(participants);

        String initials = messagerieBean.getInterlocutorInitials(d);

        assertEquals("?", initials);
    }

    // ========== TESTS getInterlocutorColor ==========

    @Test
    @DisplayName("getInterlocutorColor retourne une couleur")
    void testGetInterlocutorColor() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        String color = messagerieBean.getInterlocutorColor(discussion);

        assertNotNull(color);
        assertTrue(color.startsWith("#"));
    }

    @Test
    @DisplayName("getInterlocutorColor retourne couleur par défaut si nom null")
    void testGetInterlocutorColor_Default() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        Discussion d = new Discussion();
        List<User> participants = new ArrayList<>();
        User userWithoutName = new User();
        userWithoutName.setId(3L);
        userWithoutName.setNom(null);
        participants.add(currentUser);
        participants.add(userWithoutName);
        d.setParticipants(participants);

        String color = messagerieBean.getInterlocutorColor(d);

        assertEquals("#C47D2B", color);
    }

    // ========== TESTS getInterlocutorPhoto ==========

    @Test
    @DisplayName("getInterlocutorPhoto retourne chaîne vide si pas de photo")
    void testGetInterlocutorPhoto_Empty() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        String photo = messagerieBean.getInterlocutorPhoto(discussion);

        assertEquals("", photo);
    }

    @Test
    @DisplayName("getInterlocutorPhoto retourne la photo si existante")
    void testGetInterlocutorPhoto_WithPhoto() {
        when(authBean.isConnecte()).thenReturn(true);
        when(authBean.getUserConnecte()).thenReturn(currentUser);

        otherUser.setPhoto("profile.jpg");
        discussion.setParticipants(Arrays.asList(currentUser, otherUser));

        String photo = messagerieBean.getInterlocutorPhoto(discussion);

        assertEquals("profile.jpg", photo);
    }

    // ========== TESTS formatTime ==========

    @Test
    @DisplayName("formatTime retourne la date formatée")
    void testFormatTime() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 14, 30);
        String result = messagerieBean.formatTime(date);
        assertEquals("14:30", result);
    }

    @Test
    @DisplayName("formatTime retourne chaîne vide si null")
    void testFormatTime_Null() {
        String result = messagerieBean.formatTime(null);
        assertEquals("", result);
    }

    // ========== TESTS getLastMessage ==========

    @Test
    @DisplayName("getLastMessage retourne le dernier message")
    void testGetLastMessage() {
        when(messageDAO.findByDiscussion(10L)).thenReturn(Arrays.asList(message));

        String lastMessage = messagerieBean.getLastMessage(discussion);

        assertEquals("Bonjour", lastMessage);
    }

    @Test
    @DisplayName("getLastMessage retourne message par défaut si aucun message")
    void testGetLastMessage_NoMessages() {
        when(messageDAO.findByDiscussion(10L)).thenReturn(new ArrayList<>());

        String lastMessage = messagerieBean.getLastMessage(discussion);

        assertEquals("Start chatting...", lastMessage);
    }

    @Test
    @DisplayName("getLastMessage tronqué si trop long")
    void testGetLastMessage_Truncated() {
        Message longMessage = new Message();
        longMessage.setContenu("Ceci est un message tres long qui depasse largement la limite de trente huit caracteres");
        when(messageDAO.findByDiscussion(10L)).thenReturn(Arrays.asList(longMessage));

        String lastMessage = messagerieBean.getLastMessage(discussion);

        assertEquals("Ceci est un message tres long qui depasse ...", lastMessage);
    }

    // ========== TESTS getLastMessageTime ==========

    @Test
    @DisplayName("getLastMessageTime retourne l'heure du dernier message")
    void testGetLastMessageTime() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 14, 30);
        message.setDateEnvoi(date);
        when(messageDAO.findByDiscussion(10L)).thenReturn(Arrays.asList(message));

        String time = messagerieBean.getLastMessageTime(discussion);

        assertEquals("14:30", time);
    }

    @Test
    @DisplayName("getLastMessageTime retourne chaîne vide si aucun message")
    void testGetLastMessageTime_NoMessages() {
        when(messageDAO.findByDiscussion(10L)).thenReturn(new ArrayList<>());

        String time = messagerieBean.getLastMessageTime(discussion);

        assertEquals("", time);
    }

    // ========== TESTS getters/setters ==========

    @Test
    @DisplayName("getters et setters de base")
    void testGettersAndSetters() {
        messagerieBean.setDestinataireId(5L);
        messagerieBean.setDiscussionId(15L);
        messagerieBean.setNouveauMessage("Test");

        assertEquals(5L, messagerieBean.getDestinataireId());
        assertEquals(15L, messagerieBean.getDiscussionId());
        assertEquals("Test", messagerieBean.getNouveauMessage());
    }

    @Test
    @DisplayName("getConversations retourne la liste")
    void testGetConversations() {
        List<Discussion> list = new ArrayList<>();
        // Pas besoin de mock, on teste juste le getter
        assertNull(messagerieBean.getConversations());
    }

    @Test
    @DisplayName("getMessages retourne la liste")
    void testGetMessages() {
        assertNull(messagerieBean.getMessages());
    }

    @Test
    @DisplayName("getDiscussionActive retourne la discussion active")
    void testGetDiscussionActive() {
        assertNull(messagerieBean.getDiscussionActive());
    }
}