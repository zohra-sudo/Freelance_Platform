package com.example.freelanceplatform.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/display/*")
public class ImageDisplayServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupérer le nom du fichier depuis l'URL (ex: /display/user_1_abc.jpg)
        String filename = request.getPathInfo();
        if (filename == null || filename.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // On enlève le "/" au début
        filename = filename.substring(1);

        // 2. Définir le même chemin que dans ton PhotoUploadServlet
        String uploadPath = System.getProperty("user.home") + File.separator + "FreelancePhotos";
        File file = new File(uploadPath, filename);

        // 3. Vérifier si le fichier existe physiquement
        if (file.exists()) {
            // Déterminer le type de contenu (image/jpeg, image/png, etc.)
            String contentType = getServletContext().getMimeType(file.getName());
            response.setContentType(contentType != null ? contentType : "image/jpeg");

            // Envoyer l'image au navigateur
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            // Si l'image n'existe pas sur le disque
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}