package com.example.freelanceplatform.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet(urlPatterns = "/display/*", loadOnStartup = 1)
public class DisplayServlet extends HttpServlet {

    // Le dossier où sont stockés tes CV sur ton PC
    private static final String UPLOAD_PATH = "C:/uploads/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // On récupère le nom du fichier dans l'URL (ex: cv_abc.pdf)
        String filename = request.getPathInfo().substring(1);
        File file = new File(UPLOAD_PATH, filename);

        if (file.exists()) {
            // On définit le type de contenu (PDF)
            String contentType = getServletContext().getMimeType(filename);
            response.setContentType(contentType != null ? contentType : "application/octet-stream");
            response.setContentLength((int) file.length());

            // --- OPTIONNEL : Si tu veux forcer le TÉLÉCHARGEMENT ---
            // response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            // On envoie le fichier au navigateur
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            // Si le fichier n'est pas dans C:/uploads/, on renvoie une erreur
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}