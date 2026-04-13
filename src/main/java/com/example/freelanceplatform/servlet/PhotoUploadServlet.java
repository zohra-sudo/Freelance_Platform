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
        maxFileSize    = 5242880,   // 5 MB
        maxRequestSize = 10485760   // 10 MB
)
public class PhotoUploadServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // --- SÉCURITÉ : VÉRIFICATION DE LA SESSION ---
            HttpSession session = request.getSession();

            // On récupère le bean géré par JSF (généralement stocké sous son nom de classe avec minuscule ou tel que défini par @Named)
            AuthBean auth = (AuthBean) session.getAttribute("authBean");

            if (auth == null || auth.getUserConnecte() == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Accès refusé : vous n'êtes pas connecté.");
                return;
            }

            // On récupère l'utilisateur depuis la session serveur (impossible à truquer par le client)
            User userConnecte = auth.getUserConnecte();
            Long userId = userConnecte.getId();

            // --- TRAITEMENT DU FICHIER ---
            Part filePart = request.getPart("photo");
            if (filePart == null || filePart.getSize() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fichier manquant ou vide.");
                return;
            }

            // Préparation du dossier de stockage
            String uploadPath = getServletContext().getRealPath("")
                    + File.separator + "uploads" + File.separator + "photos";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Détermination du nom de fichier unique
            String originalName = filePart.getSubmittedFileName();
            String extension = "jpg"; // extension par défaut
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
            }

            // On inclut l'ID utilisateur et un UUID pour éviter les conflits de noms
            String fileName = "user_" + userId + "_" + UUID.randomUUID() + "." + extension;

            // Sauvegarde physique du fichier sur le serveur
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(uploadPath, fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // --- MISE À JOUR DE LA BASE DE DONNÉES ---
            // On met à jour l'objet utilisateur de la session
            userConnecte.setPhoto("uploads/photos/" + fileName);

            // Appel au service pour persister en base
            userService.modifierProfil(userConnecte);

            // Réponse de succès
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("OK");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur : " + e.getMessage());
        }
    }
}