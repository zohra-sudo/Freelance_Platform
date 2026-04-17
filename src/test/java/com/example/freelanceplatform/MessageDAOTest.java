package com.example.freelanceplatform;
import com.example.freelanceplatform.dao.*;

import com.example.freelanceplatform.entities.Discussion;
import com.example.freelanceplatform.entities.Message;
import com.example.freelanceplatform.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

class MessageDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Message> messageQuery;

    @Mock
    private TypedQuery<Discussion> discussionQuery;

    @InjectMocks
    private MessageDAO messageDAO;

    private AutoCloseable closeable;
    private Discussion testDiscussion;
    private Message testMessage;
    private User testUser;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Utilisateur test
        testUser = new User();
        testUser.setId(1L);
        testUser.setNom("Jean Dupont");

        // Discussion test
        testDiscussion = new Discussion();
        testDiscussion.setId(10L);
        testDiscussion.setSujet("Conversation test");

        // Message test
        testMessage = new Message();
        testMessage.setId(100L);
        testMessage.setContenu("Bonjour");
        testMessage.setDiscussion(testDiscussion);
        testMessage.setDateEnvoi(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) closeable.close();
    }

    // ========== TESTS saveDiscussion ==========

    @Test
    @DisplayName("TC-MDAO-01 : saveDiscussion persiste une discussion")
    void testSaveDiscussion() {
        // Arrange
        doNothing().when(em).persist(testDiscussion);
        when(em.contains(testDiscussion)).thenReturn(false);

        // Act
        messageDAO.saveDiscussion(testDiscussion);

        // Assert
        verify(em, times(1)).persist(testDiscussion);
        verify(em, times(1)).flush();
    }

    @Test
    @DisplayName("TC-MDAO-02 : saveDiscussion avec discussion null")
    void testSaveDiscussion_Null() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            messageDAO.saveDiscussion(null);
        });
    }

    // ========== TESTS saveMessage ==========

    @Test
    @DisplayName("TC-MDAO-03 : saveMessage persiste un message")
    void testSaveMessage() {
        // Arrange
        doNothing().when(em).persist(testMessage);
        when(em.contains(testMessage)).thenReturn(false);

        // Act
        messageDAO.saveMessage(testMessage);

        // Assert
        verify(em, times(1)).persist(testMessage);
        verify(em, times(1)).flush();
    }

    @Test
    @DisplayName("TC-MDAO-04 : saveMessage avec message null")
    void testSaveMessage_Null() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            messageDAO.saveMessage(null);
        });
    }

    // ========== TESTS findByDiscussion ==========

    @Test
    @DisplayName("TC-MDAO-05 : findByDiscussion retourne les messages d'une discussion")
    void testFindByDiscussion() {
        // Arrange
        List<Message> expectedMessages = Arrays.asList(testMessage);
        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(messageQuery);
        when(messageQuery.setParameter("id", 10L)).thenReturn(messageQuery);
        when(messageQuery.getResultList()).thenReturn(expectedMessages);

        // Act
        List<Message> result = messageDAO.findByDiscussion(10L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMessage.getId(), result.get(0).getId());
        verify(em, times(1)).createQuery(anyString(), eq(Message.class));
        verify(messageQuery, times(1)).setParameter("id", 10L);
        verify(messageQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("TC-MDAO-06 : findByDiscussion avec discussionId null")
    void testFindByDiscussion_NullId() {
        // Arrange
        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(messageQuery);
        when(messageQuery.setParameter(eq("id"), isNull())).thenReturn(messageQuery);
        when(messageQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        List<Message> result = messageDAO.findByDiscussion(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC-MDAO-07 : findByDiscussion avec discussion inexistante")
    void testFindByDiscussion_NoMessages() {
        // Arrange
        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(messageQuery);
        when(messageQuery.setParameter("id", 999L)).thenReturn(messageQuery);
        when(messageQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        List<Message> result = messageDAO.findByDiscussion(999L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC-MDAO-08 : findByDiscussion retourne messages triés par date")
    void testFindByDiscussion_OrderedByDate() {
        // Arrange
        Message message1 = new Message();
        message1.setId(1L);
        message1.setDateEnvoi(LocalDateTime.of(2024, 1, 1, 10, 0));

        Message message2 = new Message();
        message2.setId(2L);
        message2.setDateEnvoi(LocalDateTime.of(2024, 1, 1, 11, 0));

        List<Message> expectedMessages = Arrays.asList(message1, message2);

        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(messageQuery);
        when(messageQuery.setParameter("id", 10L)).thenReturn(messageQuery);
        when(messageQuery.getResultList()).thenReturn(expectedMessages);

        // Act
        List<Message> result = messageDAO.findByDiscussion(10L);

        // Assert
        assertEquals(2, result.size());
        // Vérifie que la requête contient ORDER BY
        verify(em).createQuery(contains("ORDER BY m.dateEnvoi ASC"), eq(Message.class));
    }

    // ========== TESTS findDiscussionsOf ==========

    @Test
    @DisplayName("TC-MDAO-09 : findDiscussionsOf retourne les discussions d'un utilisateur")
    void testFindDiscussionsOf() {
        // Arrange
        List<Discussion> expectedDiscussions = Arrays.asList(testDiscussion);
        when(em.createQuery(anyString(), eq(Discussion.class))).thenReturn(discussionQuery);
        when(discussionQuery.setParameter("uid", 1L)).thenReturn(discussionQuery);
        when(discussionQuery.getResultList()).thenReturn(expectedDiscussions);

        // Act
        List<Discussion> result = messageDAO.findDiscussionsOf(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDiscussion.getId(), result.get(0).getId());
        verify(em, times(1)).createQuery(anyString(), eq(Discussion.class));
        verify(discussionQuery, times(1)).setParameter("uid", 1L);
        verify(discussionQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("TC-MDAO-10 : findDiscussionsOf avec userId null")
    void testFindDiscussionsOf_NullUserId() {
        // Arrange
        when(em.createQuery(anyString(), eq(Discussion.class))).thenReturn(discussionQuery);
        when(discussionQuery.setParameter(eq("uid"), isNull())).thenReturn(discussionQuery);
        when(discussionQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        List<Discussion> result = messageDAO.findDiscussionsOf(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC-MDAO-11 : findDiscussionsOf avec utilisateur sans discussion")
    void testFindDiscussionsOf_NoDiscussions() {
        // Arrange
        when(em.createQuery(anyString(), eq(Discussion.class))).thenReturn(discussionQuery);
        when(discussionQuery.setParameter("uid", 999L)).thenReturn(discussionQuery);
        when(discussionQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        List<Discussion> result = messageDAO.findDiscussionsOf(999L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC-MDAO-12 : findDiscussionsOf retourne discussions triées par date")
    void testFindDiscussionsOf_OrderedByDate() {
        // Arrange
        Discussion disc1 = new Discussion();
        disc1.setId(1L);

        Discussion disc2 = new Discussion();
        disc2.setId(2L);

        List<Discussion> expectedDiscussions = Arrays.asList(disc1, disc2);

        when(em.createQuery(anyString(), eq(Discussion.class))).thenReturn(discussionQuery);
        when(discussionQuery.setParameter("uid", 1L)).thenReturn(discussionQuery);
        when(discussionQuery.getResultList()).thenReturn(expectedDiscussions);

        // Act
        List<Discussion> result = messageDAO.findDiscussionsOf(1L);

        // Assert
        assertEquals(2, result.size());
        // Vérifie que la requête contient ORDER BY
        verify(em).createQuery(contains("ORDER BY d.dateCreation DESC"), eq(Discussion.class));
    }

    @Test
    @DisplayName("TC-MDAO-13 : findDiscussionsOf avec plusieurs discussions")
    void testFindDiscussionsOf_MultipleDiscussions() {
        // Arrange
        Discussion disc1 = new Discussion();
        disc1.setId(1L);
        Discussion disc2 = new Discussion();
        disc2.setId(2L);
        Discussion disc3 = new Discussion();
        disc3.setId(3L);

        List<Discussion> expectedDiscussions = Arrays.asList(disc1, disc2, disc3);

        when(em.createQuery(anyString(), eq(Discussion.class))).thenReturn(discussionQuery);
        when(discussionQuery.setParameter("uid", 1L)).thenReturn(discussionQuery);
        when(discussionQuery.getResultList()).thenReturn(expectedDiscussions);

        // Act
        List<Discussion> result = messageDAO.findDiscussionsOf(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    // ========== TESTS TRANSACTIONS ==========

    @Test
    @DisplayName("TC-MDAO-14 : saveDiscussion appel flush après persist")
    void testSaveDiscussion_CallsFlush() {
        // Arrange
        doNothing().when(em).persist(testDiscussion);
        when(em.contains(testDiscussion)).thenReturn(false);

        // Act
        messageDAO.saveDiscussion(testDiscussion);

        // Assert
        verify(em, times(1)).flush();
        // Vérifie que flush est appelé après persist
        inOrder(em).verify(em).persist(testDiscussion);
        inOrder(em).verify(em).flush();
    }

    @Test
    @DisplayName("TC-MDAO-15 : saveMessage appel flush après persist")
    void testSaveMessage_CallsFlush() {
        // Arrange
        doNothing().when(em).persist(testMessage);
        when(em.contains(testMessage)).thenReturn(false);

        // Act
        messageDAO.saveMessage(testMessage);

        // Assert
        verify(em, times(1)).flush();
        // Vérifie que flush est appelé après persist
        inOrder(em).verify(em).persist(testMessage);
        inOrder(em).verify(em).flush();
    }
}