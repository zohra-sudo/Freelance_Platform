package com.example.freelanceplatform.servlet;

import com.example.freelanceplatform.beans.AuthBean;
import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/saveProject")
public class SaveProjectServlet extends HttpServlet {

    @Inject
    private ProjectService projectService;

    @Inject
    private AuthBean authBean;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!authBean.isConnecte()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String category = req.getParameter("category");
        String budgetParam = req.getParameter("budget");
        String skills = req.getParameter("skills");

        Project project;
        if (idParam != null && !idParam.isEmpty()) {
            project = projectService.findById(Long.parseLong(idParam));
            if (project == null || !project.getAuthor().getId().equals(authBean.getUserConnecte().getId())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else {
            project = new Project();
            project.setAuthor(authBean.getUserConnecte());
        }

        project.setTitle(title);
        project.setDescription(description);
        project.setCategory(category);
        if (budgetParam != null && !budgetParam.isEmpty()) {
            project.setBudget(new BigDecimal(budgetParam));
        }
        project.setSkills(skills);

        if (idParam != null && !idParam.isEmpty()) {
            projectService.updateProject(project);
        } else {
            projectService.addProject(project);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}