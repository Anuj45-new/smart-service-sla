<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Analytics &amp; SLA Compliance Reports - Smart Service SLA" />
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main>
    <div class="container">
        <div style="margin-bottom: 1.5rem;">
            <h1 style="font-size: 1.6rem; font-weight: 700;">Analytics &amp; SLA Compliance Reports</h1>
            <p style="color: var(--text-muted); font-size: 0.95rem;">
                Aggregated business intelligence queries executed directly via <strong>JDBC (PreparedStatement, COUNT, GROUP BY, AVG, JOIN)</strong>.
            </p>
        </div>

        <jsp:include page="../common/alerts.jsp"/>

        <!-- High-level Summary Metrics -->
        <div class="kpi-grid">
            <div class="kpi-card kpi-total">
                <div class="kpi-label">Total Handled</div>
                <div class="kpi-value"><c:out value="${summary.totalRequests}"/></div>
            </div>
            <div class="kpi-card kpi-resolved">
                <div class="kpi-label">Resolved Tickets</div>
                <div class="kpi-value"><c:out value="${summary.resolvedRequests}"/></div>
            </div>
            <div class="kpi-card kpi-breached">
                <div class="kpi-label" style="color: var(--critical);">Breached SLA</div>
                <div class="kpi-value" style="color: var(--critical);"><c:out value="${summary.slaBreachedRequests}"/></div>
            </div>
            <div class="kpi-card" style="border-top: 4px solid var(--warning);">
                <div class="kpi-label">Active Escalations</div>
                <div class="kpi-value"><c:out value="${summary.escalatedRequests}"/></div>
            </div>
        </div>

        <!-- 1. Agent Workload & Performance Table -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">1. Support Agent Workload &amp; Breach Metrics (SQL JOIN &amp; GROUP BY)</h2>
            </div>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Agent Name</th>
                            <th>Email Address</th>
                            <th>Total Assigned</th>
                            <th>Open / Queued</th>
                            <th>In Progress</th>
                            <th>Resolved</th>
                            <th>SLA Breaches</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty workloads}">
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 1.5rem;">
                                        No agent workload data found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="w" items="${workloads}">
                                    <tr>
                                        <td><strong><c:out value="${w.agentName}"/></strong></td>
                                        <td><c:out value="${w.agentEmail}"/></td>
                                        <td><strong><c:out value="${w.totalAssigned}"/></strong></td>
                                        <td><c:out value="${w.openCount}"/></td>
                                        <td><c:out value="${w.inProgressCount}"/></td>
                                        <td><span style="color: var(--success); font-weight: 600;"><c:out value="${w.resolvedCount}"/></span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${w.breachedCount > 0}">
                                                    <span class="badge badge-escalated"><c:out value="${w.breachedCount}"/> Breached</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--success);">0</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
            <!-- 2. Priority Resolution Speed Metrics -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">2. Priority Analysis &amp; Avg Resolution Time</h2>
                </div>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Priority</th>
                                <th>Total Raised</th>
                                <th>SLA Breached</th>
                                <th>Avg Resolution (Hours)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${priorityMetrics}">
                                <tr>
                                    <td>
                                        <span class="badge badge-${p.priority.name().toLowerCase()}">
                                            <c:out value="${p.priority}"/>
                                        </span>
                                    </td>
                                    <td><c:out value="${p.totalCount}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.breachedCount > 0}">
                                                <strong style="color: var(--critical);"><c:out value="${p.breachedCount}"/></strong>
                                            </c:when>
                                            <c:otherwise>0</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <strong><fmt:formatNumber value="${p.avgResolutionHours}" maxFractionDigits="2"/> hrs</strong>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 3. Category Distribution Metrics -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">3. Category Ticket Volume &amp; Efficiency</h2>
                </div>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Total Tickets</th>
                                <th>Resolved</th>
                                <th>Avg Resolution (Hours)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${categoryMetrics}">
                                <tr>
                                    <td><strong><c:out value="${c.categoryName}"/></strong></td>
                                    <td><c:out value="${c.totalRequests}"/></td>
                                    <td><span style="color: var(--success); font-weight: 600;"><c:out value="${c.resolvedRequests}"/></span></td>
                                    <td>
                                        <strong><fmt:formatNumber value="${c.avgResolutionHours}" maxFractionDigits="2"/> hrs</strong>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
