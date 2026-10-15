package com.smartservice.controller;

import com.smartservice.dto.RequestCreateDto;
import com.smartservice.entity.ServiceRequest;
import com.smartservice.entity.User;
import com.smartservice.enums.Priority;
import com.smartservice.enums.Role;
import com.smartservice.enums.Status;
import com.smartservice.exception.AppException;
import com.smartservice.service.RequestService;
import com.smartservice.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "RequestServlet", urlPatterns = {"/requests"})
public class RequestServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RequestServlet.class);
    private RequestService requestService;
    private UserService userService;

    @Override
    public void init() {
        this.requestService = new RequestService();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new" -> showCreateForm(req, resp);
            case "view" -> showDetailView(req, resp);
            case "list" -> showListView(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + "/requests");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            resp.sendRedirect(req.getContextPath() + "/requests");
            return;
        }

        switch (action) {
            case "create" -> handleCreate(req, resp);
            case "assign" -> handleAssign(req, resp);
            case "status" -> handleStatusUpdate(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + "/requests");
        }
    }

    private void showListView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        List<ServiceRequest> requests;

        if (currentUser.getRole() == Role.ADMIN) {
            requests = requestService.getAllRequests();
        } else if (currentUser.getRole() == Role.AGENT) {
            requests = requestService.getRequestsForAgent(currentUser.getId());
        } else {
            requests = requestService.getRequestsByUser(currentUser.getId());
        }

        req.setAttribute("requests", requests);
        req.getRequestDispatcher("/WEB-INF/views/requests/list.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", requestService.getAllCategories());
        req.setAttribute("priorities", Priority.values());
        req.getRequestDispatcher("/WEB-INF/views/requests/create.jsp").forward(req, resp);
    }

    private void showDetailView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/requests");
            return;
        }

        try {
            Long requestId = Long.parseLong(idParam.trim());
            ServiceRequest serviceRequest = requestService.getRequestById(requestId);
            User currentUser = getCurrentUser(req);

            // Access check: USER can only view their own requests, AGENT can view assigned, ADMIN can view all
            if (currentUser.getRole() == Role.USER && !serviceRequest.getCreatedBy().getId().equals(currentUser.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this request.");
                return;
            }

            req.setAttribute("requestItem", serviceRequest);
            req.setAttribute("historyList", requestService.getRequestHistory(requestId));
            req.setAttribute("allStatuses", Status.values());

            if (currentUser.getRole() == Role.ADMIN) {
                req.setAttribute("agents", userService.getAllAgents());
            }

            req.getRequestDispatcher("/WEB-INF/views/requests/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/requests");
        } catch (AppException e) {
            req.setAttribute("errorMessage", e.getMessage());
            showListView(req, resp);
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String categoryIdStr = req.getParameter("categoryId");
        String priorityStr = req.getParameter("priority");

        // Validation
        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            categoryIdStr == null || categoryIdStr.trim().isEmpty() ||
            priorityStr == null || priorityStr.trim().isEmpty()) {

            req.setAttribute("errorMessage", "All fields are required.");
            req.setAttribute("title", title);
            req.setAttribute("description", description);
            req.setAttribute("selectedCategoryId", categoryIdStr);
            req.setAttribute("selectedPriority", priorityStr);
            showCreateForm(req, resp);
            return;
        }

        try {
            Long categoryId = Long.parseLong(categoryIdStr.trim());
            Priority priority = Priority.fromString(priorityStr.trim());

            if (priority == null) {
                req.setAttribute("errorMessage", "Invalid priority selected.");
                showCreateForm(req, resp);
                return;
            }

            RequestCreateDto dto = new RequestCreateDto();
            dto.setTitle(title.trim());
            dto.setDescription(description.trim());
            dto.setCategoryId(categoryId);
            dto.setPriority(priority);

            ServiceRequest created = requestService.createRequest(dto, currentUser.getId());

            // Post-Redirect-Get pattern
            resp.sendRedirect(req.getContextPath() + "/requests?action=view&id=" + created.getId() + "&success=created");
        } catch (Exception e) {
            logger.error("Failed to process request creation", e);
            req.setAttribute("errorMessage", e.getMessage());
            showCreateForm(req, resp);
        }
    }

    private void handleAssign(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin privileges required.");
            return;
        }

        String requestIdStr = req.getParameter("requestId");
        String agentIdStr = req.getParameter("agentId");

        try {
            Long requestId = Long.parseLong(requestIdStr);
            Long agentId = Long.parseLong(agentIdStr);

            requestService.assignRequest(requestId, agentId, currentUser.getId());
            resp.sendRedirect(req.getContextPath() + "/requests?action=view&id=" + requestId + "&success=assigned");
        } catch (Exception e) {
            logger.error("Error during assignment", e);
            resp.sendRedirect(req.getContextPath() + "/requests?action=view&id=" + requestIdStr + "&error=" + e.getMessage());
        }
    }

    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getCurrentUser(req);
        String requestIdStr = req.getParameter("requestId");
        String statusStr = req.getParameter("status");
        String remarks = req.getParameter("remarks");

        try {
            Long requestId = Long.parseLong(requestIdStr);
            Status status = Status.fromString(statusStr);

            if (status == null) {
                resp.sendRedirect(req.getContextPath() + "/requests?action=view&id=" + requestId + "&error=Invalid+status");
                return;
            }

            requestService.updateStatus(requestId, status, remarks, currentUser.getId());
            resp.sendRedirect(req.getContextPath() + "/requests?action=view&id=" + requestId + "&success=updated");
        } catch (Exception e) {
            logger.error("Error updating status", e);
            resp.sendRedirect(req.getContextPath() + "/requests?action=view&id=" + requestIdStr + "&error=" + e.getMessage());
        }
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (User) session.getAttribute("currentUser");
    }
}
