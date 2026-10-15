package com.smartservice.controller;

import com.smartservice.dto.DashboardSummaryDto;
import com.smartservice.entity.ServiceRequest;
import com.smartservice.entity.User;
import com.smartservice.enums.Role;
import com.smartservice.service.ReportService;
import com.smartservice.service.RequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private ReportService reportService;
    private RequestService requestService;

    @Override
    public void init() {
        this.reportService = new ReportService();
        this.requestService = new RequestService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        DashboardSummaryDto summary = reportService.getDashboardSummary();
        req.setAttribute("summary", summary);

        // Load relevant recent tickets depending on the logged-in role
        List<ServiceRequest> recentRequests;
        if (currentUser.getRole() == Role.ADMIN) {
            recentRequests = requestService.getAllRequests();
            req.setAttribute("agents", reportService.getAgentWorkloads());
        } else if (currentUser.getRole() == Role.AGENT) {
            recentRequests = requestService.getRequestsForAgent(currentUser.getId());
        } else {
            recentRequests = requestService.getRequestsByUser(currentUser.getId());
        }

        // Limit recent list to top 10 items for dashboard overview
        if (recentRequests.size() > 10) {
            recentRequests = recentRequests.subList(0, 10);
        }
        req.setAttribute("recentRequests", recentRequests);

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
