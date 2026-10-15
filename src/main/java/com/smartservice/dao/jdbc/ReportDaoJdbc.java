package com.smartservice.dao.jdbc;

import com.smartservice.dto.AgentWorkloadDto;
import com.smartservice.dto.CategoryMetricDto;
import com.smartservice.dto.DashboardSummaryDto;
import com.smartservice.dto.PriorityMetricDto;
import com.smartservice.enums.Priority;
import com.smartservice.exception.AppException;
import com.smartservice.util.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoJdbc {

    private static final Logger logger = LoggerFactory.getLogger(ReportDaoJdbc.class);

    public DashboardSummaryDto getDashboardSummary() {
        String sql = """
            SELECT
                COUNT(*) AS total_count,
                COUNT(CASE WHEN status = 'OPEN' THEN 1 END) AS open_count,
                COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) AS in_progress_count,
                COUNT(CASE WHEN status = 'RESOLVED' THEN 1 END) AS resolved_count,
                COUNT(CASE WHEN status = 'CLOSED' THEN 1 END) AS closed_count,
                COUNT(CASE WHEN status = 'ESCALATED' THEN 1 END) AS escalated_count,
                COUNT(CASE WHEN is_sla_breached = TRUE THEN 1 END) AS breached_count,
                COUNT(CASE WHEN priority = 'LOW' THEN 1 END) AS low_count,
                COUNT(CASE WHEN priority = 'MEDIUM' THEN 1 END) AS medium_count,
                COUNT(CASE WHEN priority = 'HIGH' THEN 1 END) AS high_count,
                COUNT(CASE WHEN priority = 'CRITICAL' THEN 1 END) AS critical_count
            FROM service_requests
        """;

        DashboardSummaryDto dto = new DashboardSummaryDto();

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                dto.setTotalRequests(rs.getLong("total_count"));
                dto.setOpenRequests(rs.getLong("open_count"));
                dto.setInProgressRequests(rs.getLong("in_progress_count"));
                dto.setResolvedRequests(rs.getLong("resolved_count"));
                dto.setClosedRequests(rs.getLong("closed_count"));
                dto.setEscalatedRequests(rs.getLong("escalated_count"));
                dto.setSlaBreachedRequests(rs.getLong("breached_count"));
                dto.setLowPriorityCount(rs.getLong("low_count"));
                dto.setMediumPriorityCount(rs.getLong("medium_count"));
                dto.setHighPriorityCount(rs.getLong("high_count"));
                dto.setCriticalPriorityCount(rs.getLong("critical_count"));
            }
            return dto;
        } catch (SQLException e) {
            logger.error("Error executing dashboard summary query", e);
            throw new AppException("Failed to load dashboard summary statistics", e);
        }
    }

    public List<AgentWorkloadDto> getAgentWorkloads() {
        String sql = """
            SELECT
                u.id AS agent_id,
                u.full_name AS agent_name,
                u.email AS agent_email,
                COUNT(r.id) AS total_assigned,
                COUNT(CASE WHEN r.status IN ('OPEN', 'ASSIGNED') THEN 1 END) AS open_count,
                COUNT(CASE WHEN r.status = 'IN_PROGRESS' THEN 1 END) AS in_progress_count,
                COUNT(CASE WHEN r.status = 'RESOLVED' THEN 1 END) AS resolved_count,
                COUNT(CASE WHEN r.is_sla_breached = TRUE THEN 1 END) AS breached_count
            FROM users u
            LEFT JOIN service_requests r ON u.id = r.assigned_to
            WHERE u.role = 'AGENT'
            GROUP BY u.id, u.full_name, u.email
            ORDER BY total_assigned DESC, u.full_name ASC
        """;

        List<AgentWorkloadDto> list = new ArrayList<>();

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AgentWorkloadDto dto = new AgentWorkloadDto(
                        rs.getLong("agent_id"),
                        rs.getString("agent_name"),
                        rs.getString("agent_email"),
                        rs.getLong("total_assigned"),
                        rs.getLong("open_count"),
                        rs.getLong("in_progress_count"),
                        rs.getLong("resolved_count"),
                        rs.getLong("breached_count")
                );
                list.add(dto);
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error executing agent workloads query", e);
            throw new AppException("Failed to load agent workloads", e);
        }
    }

    public List<PriorityMetricDto> getPriorityMetrics() {
        String sql = """
            SELECT
                priority,
                COUNT(*) AS total_count,
                COUNT(CASE WHEN is_sla_breached = TRUE THEN 1 END) AS breached_count,
                COALESCE(AVG(TIMESTAMPDIFF(MINUTE, created_at, resolved_at) / 60.0), 0.0) AS avg_resolution_hours
            FROM service_requests
            GROUP BY priority
            ORDER BY FIELD(priority, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW')
        """;

        List<PriorityMetricDto> list = new ArrayList<>();

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Priority priority = Priority.fromString(rs.getString("priority"));
                if (priority != null) {
                    PriorityMetricDto dto = new PriorityMetricDto(
                            priority,
                            rs.getLong("total_count"),
                            rs.getLong("breached_count"),
                            rs.getDouble("avg_resolution_hours")
                    );
                    list.add(dto);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error executing priority metrics query", e);
            throw new AppException("Failed to load priority metrics", e);
        }
    }

    public List<CategoryMetricDto> getCategoryMetrics() {
        String sql = """
            SELECT
                c.name AS category_name,
                COUNT(r.id) AS total_requests,
                COUNT(CASE WHEN r.status = 'RESOLVED' THEN 1 END) AS resolved_requests,
                COALESCE(AVG(TIMESTAMPDIFF(MINUTE, r.created_at, r.resolved_at) / 60.0), 0.0) AS avg_resolution_hours
            FROM categories c
            LEFT JOIN service_requests r ON c.id = r.category_id
            GROUP BY c.id, c.name
            ORDER BY total_requests DESC, c.name ASC
        """;

        List<CategoryMetricDto> list = new ArrayList<>();

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CategoryMetricDto dto = new CategoryMetricDto(
                        rs.getString("category_name"),
                        rs.getLong("total_requests"),
                        rs.getLong("resolved_requests"),
                        rs.getDouble("avg_resolution_hours")
                );
                list.add(dto);
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error executing category metrics query", e);
            throw new AppException("Failed to load category metrics", e);
        }
    }
}
