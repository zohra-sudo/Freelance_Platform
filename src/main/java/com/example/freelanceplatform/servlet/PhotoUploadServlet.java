package com.example.freelanceplatform.servlet;

import com.example.freelanceplatform.beans.AuthBean;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@WebServlet("/uploadPhoto")
@MultipartConfig(
        maxFileSize    = 5242880,
        maxRequestSize = 10485760
)
public class PhotoUploadServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // ── 1. VÉRIFICATION SESSION ────────────────────────────────────
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session introuvable.");
                return;
            }

            AuthBean auth = (AuthBean) session.getAttribute("authBean");
            if (auth == null || auth.getUserConnecte() == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non connecté.");
                return;
            }

            Long connectedUserId = auth.getUserConnecte().getId();

            // ── 2. VÉRIFICATION SÉCURITÉ ───────────────────────────────────
            String userIdParam = request.getParameter("userId");
            if (userIdParam == null || userIdParam.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId manquant.");
                return;
            }

            Long targetUserId;
            try {
                targetUserId = Long.parseLong(userIdParam.trim());
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId invalide.");
                return;
            }

            if (!connectedUserId.equals(targetUserId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Action non autorisée.");
                return;
            }

            // ── 3. TRAITEMENT DU FICHIER ───────────────────────────────────
            Part filePart = request.getPart("photo");
            if (filePart == null || filePart.getSize() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fichier manquant.");
                return;
            }

            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le fichier doit être une image.");
                return;
            }

            String uploadPath = System.getProperty("user.home") + File.separator + "FreelancePhotos";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalName = filePart.getSubmittedFileName();
            String extension = "jpg";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName
                        .substring(originalName.lastIndexOf('.') + 1)
                        .toLowerCase();
            }
            String fileName = "user_" + targetUserId + "_" + UUID.randomUUID() + "." + extension;

            try (InputStream input = filePart.getInputStream()) {
                Files.copy(
                        input,
                        Paths.get(uploadPath, fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            // ── 4. CORRECTION PRINCIPALE ───────────────────────────────────
            // Charger une entité FRAÎCHE depuis la BDD (pas celle de la session)
            User userFromDb = userService.findById(targetUserId);
            if (userFromDb == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable.");
                return;
            }

            // Supprimer l'ancienne photo du disque
            if (userFromDb.getPhoto() != null && !userFromDb.getPhoto().isEmpty()) {
                File oldFile = new File(uploadPath, userFromDb.getPhoto());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // Mettre à jour et récupérer l'entité managée retournée par merge()
            userFromDb.setPhoto(fileName);
            User managedUser = userService.modifierProfil(userFromDb); // ✅ on utilise le retour

            // Synchroniser la session avec l'entité managée à jour
            auth.setUserConnecte(managedUser != null ? managedUser : userFromDb);

            // ── 5. RÉPONSE ─────────────────────────────────────────────────
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("OK");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur serveur : " + e.getMessage()
            );
        }
    }
}