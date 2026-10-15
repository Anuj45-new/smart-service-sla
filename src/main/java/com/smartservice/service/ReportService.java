package com.smartservice.service;

import com.smartservice.dao.jdbc.ReportDaoJdbc;
import com.smartservice.dto.AgentWorkloadDto;
import com.smartservice.dto.CategoryMetricDto;
import com.smartservice.dto.DashboardSummaryDto;
import com.smartservice.dto.PriorityMetricDto;

import java.util.List;

public class ReportService {

    private final ReportDaoJdbc reportDaoJdbc;

    public ReportService() {
        this.reportDaoJdbc = new ReportDaoJdbc();
    }

    public ReportService(ReportDaoJdbc reportDaoJdbc) {
        this.reportDaoJdbc = reportDaoJdbc;
    }

    public DashboardSummaryDto getDashboardSummary() {
        return reportDaoJdbc.getDashboardSummary();
    }

    public List<AgentWorkloadDto> getAgentWorkloads() {
        return reportDaoJdbc.getAgentWorkloads();
    }

    public List<PriorityMetricDto> getPriorityMetrics() {
        return reportDaoJdbc.getPriorityMetrics();
    }

    public List<CategoryMetricDto> getCategoryMetrics() {
        return reportDaoJdbc.getCategoryMetrics();
    }
}
