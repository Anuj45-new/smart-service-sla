<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard - Smart Service SLA" />
<jsp:include page="common/header.jsp"/>
<jsp:include page="common/navbar.jsp"/>

<main>
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <div>
                <h1 style="font-size: 1.6rem; font-weight: 700;">Service Request Dashboard</h1>
                <p style="color: var(--text-muted); font-size: 0.95rem;">
                    Welcome back, <strong><c:out value="${sessionScope.currentUser.fullName}"/></strong>!
                </p>
            </div>
            <c:if test="${sessionScope.currentUser.role == 'USER' || sessionScope.currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/requests?action=new" class="btn btn-primary">
                    + Raise New Request
                </a>
            </c:if>
        </div>

        <jsp:include page="common/alerts.jsp"/>

        <!-- KPI Metrics Grid -->
        <div class="kpi-grid">
            <div class="kpi-card kpi-total">
                <div class="kpi-label">Total Requests</div>
                <div class="kpi-value"><c:out value="${summary.totalRequests}"/></div>
            </div>
            <div class="kpi-card" style="border-top: 4px solid var(--info);">
                <div class="kpi-label">Open / New</div>
                <div class="kpi-value"><c:out value="${summary.openRequests}"/></div>
            </div>
            <div class="kpi-card kpi-in-progress">
                <div class="kpi-label">In Progress</div>
                <div class="kpi-value"><c:out value="${summary.inProgressRequests}"/></div>
            </div>
            <div class="kpi-card kpi-resolved">
                <div class="kpi-label">Resolved</div>
                <div class="kpi-value"><c:out value="${summary.resolvedRequests}"/></div>
            </div>
            <div class="kpi-card kpi-breached">
                <div class="kpi-label" style="color: var(--critical);">SLA Breached</div>
                <div class="kpi-value" style="color: var(--critical);"><c:out value="${summary.slaBreachedRequests}"/></div>
            </div>
        </div>

        <!-- Priority Breakdown Cards -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Requests by Priority &amp; Fixed SLAs</h2>
            </div>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem;">
                <div style="padding: 1rem; background: #fef2f2; border: 1px solid #fee2e2; border-radius: 6px;">
                    <div style="font-weight: 700; color: #b91c1c;">CRITICAL (2h SLA)</div>
                    <div style="font-size: 1.5rem; font-weight: 700; margin-top: 0.25rem;"><c:out value="${summary.criticalPriorityCount}"/></div>
                </div>
                <div style="padding: 1rem; background: #fff7ed; border: 1px solid #ffedd5; border-radius: 6px;">
                    <div style="font-weight: 700; color: #c2410c;">HIGH (6h SLA)</div>
                    <div style="font-size: 1.5rem; font-weight: 700; margin-top: 0.25rem;"><c:out value="${summary.highPriorityCount}"/></div>
                </div>
                <div style="padding: 1rem; background: #fefce8; border: 1px solid #fef9c3; border-radius: 6px;">
                    <div style="font-weight: 700; color: #a16207;">MEDIUM (24h SLA)</div>
                    <div style="font-size: 1.5rem; font-weight: 700; margin-top: 0.25rem;"><c:out value="${summary.mediumPriorityCount}"/></div>
                </div>
                <div style="padding: 1rem; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px;">
                    <div style="font-weight: 700; color: #475569;">LOW (72h SLA)</div>
                    <div style="font-size: 1.5rem; font-weight: 700; margin-top: 0.25rem;"><c:out value="${summary.lowPriorityCount}"/></div>
                </div>
            </div>
        </div>

        <!-- Agent Workload (For Admins) -->
        <c:if test="${sessionScope.currentUser.role == 'ADMIN' && not empty agents}">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Agent Workload Overview (Live SQL Aggregation)</h2>
                </div>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Agent Name</th>
                                <th>Email</th>
                                <th>Total Assigned</th>
                                <th>Open</th>
                                <th>In Progress</th>
                                <th>Resolved</th>
                                <th>Breached</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="agent" items="${agents}">
                                <tr>
                                    <td><strong><c:out value="${agent.agentName}"/></strong></td>
                                    <td><c:out value="${agent.agentEmail}"/></td>
                                    <td><c:out value="${agent.totalAssigned}"/></td>
                                    <td><c:out value="${agent.openCount}"/></td>
                                    <td><c:out value="${agent.inProgressCount}"/></td>
                                    <td><span style="color: var(--success); font-weight: 600;"><c:out value="${agent.resolvedCount}"/></span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${agent.breachedCount > 0}">
                                                <span class="badge badge-escalated"><c:out value="${agent.breachedCount}"/></span>
                                            </c:when>
                                            <c:otherwise>0</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>

        <!-- Recent Requests -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">
                    <c:choose>
                        <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">Recent Organization Requests</c:when>
                        <c:when test="${sessionScope.currentUser.role == 'AGENT'}">Assigned Requests</c:when>
                        <c:otherwise>Your Recent Service Requests</c:otherwise>
                    </c:choose>
                </h2>
                <a href="${pageContext.request.contextPath}/requests" class="btn btn-secondary btn-sm">View All &rarr;</a>
            </div>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Deadline / SLA</th>
                            <th>Assigned To</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty recentRequests}">
                                <tr>
                                    <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">
                                        No service requests found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="req" items="${recentRequests}">
                                    <tr>
                                        <td>#<c:out value="${req.id}"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/requests?action=view&id=${req.id}" style="color: var(--primary); font-weight: 600; text-decoration: none;">
                                                <c:out value="${req.title}"/>
                                            </a>
                                        </td>
                                        <td><c:out value="${req.category.name}"/></td>
                                        <td>
                                            <span class="badge badge-${req.priority.name().toLowerCase()}">
                                                <c:out value="${req.priority}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge badge-${req.status.name().toLowerCase()}">
                                                <c:out value="${req.status}"/>
                                            </span>
                                            <c:if test="${req.slaBreached}">
                                                <span class="badge badge-sla-breached">SLA BREACHED</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <div style="font-size: 0.85rem;"><c:out value="${req.slaDeadline}"/></div>
                                            <small style="color: var(--text-muted);"><c:out value="${req.slaHours}"/>h SLA</small>
                                        </td>
                                        <td>
                                            <c:out value="${req.assignedTo != null ? req.assignedTo.fullName : 'Unassigned'}"/>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/requests?action=view&id=${req.id}" class="btn btn-secondary btn-sm">
                                                Details
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp"/>
