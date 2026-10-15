package com.smartservice.controller;

import com.smartservice.service.ReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() {
        this.reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("summary", reportService.getDashboardSummary());
        req.setAttribute("workloads", reportService.getAgentWorkloads());
        req.setAttribute("priorityMetrics", reportService.getPriorityMetrics());
        req.setAttribute("categoryMetrics", reportService.getCategoryMetrics());

        req.getRequestDispatcher("/WEB-INF/views/reports/reports.jsp").forward(req, resp);
    }
}
