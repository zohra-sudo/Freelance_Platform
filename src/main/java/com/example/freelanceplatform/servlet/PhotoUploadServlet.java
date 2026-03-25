package com.example.freelanceplatform.servlet;

import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            // Récupère l'ID de l'utilisateur
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId manquant");
                return;
            }
            Long userId = Long.parseLong(userIdStr);

            // Récupère le fichier uploadé
            Part filePart = request.getPart("photo");
            if (filePart == null || filePart.getSize() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fichier manquant");
                return;
            }

            // Crée le dossier uploads/photos si nécessaire
            String uploadPath = getServletContext().getRealPath("")
                    + File.separator + "uploads" + File.separator + "photos";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Nom de fichier unique
            String originalName = filePart.getSubmittedFileName();
            String extension = "jpg";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
            }
            String fileName = "user_" + userId + "_" + UUID.randomUUID() + "." + extension;

            // Sauvegarde le fichier
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(uploadPath, fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Met à jour la BD
            User user = userService.findById(userId);
            if (user != null) {
                user.setPhoto("uploads/photos/" + fileName);
                userService.modifierProfil(user);
            }

            // Réponse OK
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("OK");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}