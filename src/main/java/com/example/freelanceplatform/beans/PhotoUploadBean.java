package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Named
@SessionScoped
public class PhotoUploadBean implements Serializable {

    @Inject
    private UserService userService;

    private Part photoPart;
    private Long targetUserId; // ID de l'utilisateur dont on change la photo

    private static final String UPLOAD_DIR = "uploads/photos/";

    public String uploadPhoto() {
        if (photoPart == null || photoPart.getSize() == 0) {
            return null;
        }
        if (targetUserId == null) {
            return null;
        }

        try {
            // Trouve le vrai chemin sur le disque
            jakarta.faces.context.FacesContext fc = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.ServletContext sc = (jakarta.servlet.ServletContext) fc.getExternalContext().getContext();

            String uploadPath = sc.getRealPath("") + File.separator + "uploads" + File.separator + "photos";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Nom unique pour le fichier
            String extension = getExtension(photoPart.getSubmittedFileName());
            String fileName = "user_" + targetUserId + "_" + UUID.randomUUID() + "." + extension;

            // Sauvegarde sur le disque
            try (InputStream input = photoPart.getInputStream()) {
                Files.copy(input, Paths.get(uploadPath, fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Met à jour la BD
            User user = userService.findById(targetUserId);
            if (user != null) {
                user.setPhoto(UPLOAD_DIR + fileName);
                userService.modifierProfil(user);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Recharge la page profil
        return "profil?faces-redirect=true&id=" + targetUserId;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "jpg";
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    public Part getPhotoPart() { return photoPart; }
    public void setPhotoPart(Part photoPart) { this.photoPart = photoPart; }

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
}