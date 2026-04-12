package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Application;
import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.service.ApplicationService;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

@Named
@RequestScoped
public class ApplicationBean implements Serializable {

    private Long projectId;
    private Project project;
    private Application application = new Application();

    private Part cvFile;

    @Inject
    private ProjectService projectService;

    @Inject
    private ApplicationService applicationService;

    public void loadProject() {
        if (projectId != null) {
            project = projectService.findById(projectId);
        }
    }

    public String submitApplication() {

        if (projectId == null) {
            return null;
        }

        Project selectedProject = projectService.findById(projectId);
        if (selectedProject == null) {
            return null;
        }

        application.setProject(selectedProject);

        // 📄 Upload du CV
        if (cvFile != null && cvFile.getSize() > 0) {
            try {
                String fileName = Paths.get(cvFile.getSubmittedFileName()).getFileName().toString();

                String uploadDir = "C:/uploads/"; // ⚠️ change ce chemin si besoin
                Files.createDirectories(Paths.get(uploadDir));

                InputStream input = cvFile.getInputStream();
                Files.copy(input, Paths.get(uploadDir + fileName));

                application.setCvFileName(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        applicationService.save(application);

        return "jobs?faces-redirect=true";
    }

    // ===== GETTERS & SETTERS =====

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Part getCvFile() {
        return cvFile;
    }

    public void setCvFile(Part cvFile) {
        this.cvFile = cvFile;
    }
}