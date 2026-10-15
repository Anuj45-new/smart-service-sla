<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Service Requests - Smart Service SLA" />
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main>
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <div>
                <h1 style="font-size: 1.6rem; font-weight: 700;">Service Requests</h1>
                <p style="color: var(--text-muted); font-size: 0.95rem;">
                    <c:choose>
                        <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">Managing all organization tickets</c:when>
                        <c:when test="${sessionScope.currentUser.role == 'AGENT'}">Viewing tickets assigned to you</c:when>
                        <c:otherwise>Viewing your submitted service requests</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <c:if test="${sessionScope.currentUser.role == 'USER' || sessionScope.currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/requests?action=new" class="btn btn-primary">
                    + Raise New Request
                </a>
            </c:if>
        </div>

        <jsp:include page="../common/alerts.jsp"/>

        <div class="card">
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Created By</th>
                            <th>Assigned To</th>
                            <th>SLA Deadline</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty requests}">
                                <tr>
                                    <td colspan="9" style="text-align: center; color: var(--text-muted); padding: 2.5rem;">
                                        No service requests found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="req" items="${requests}">
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
                                        <td><c:out value="${req.createdBy.fullName}"/></td>
                                        <td>
                                            <c:out value="${req.assignedTo != null ? req.assignedTo.fullName : 'Unassigned'}"/>
                                        </td>
                                        <td>
                                            <div style="font-size: 0.85rem;"><c:out value="${req.slaDeadline}"/></div>
                                            <small style="color: var(--text-muted);"><c:out value="${req.slaHours}"/>h SLA</small>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/requests?action=view&id=${req.id}" class="btn btn-secondary btn-sm">
                                                View
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

<jsp:include page="../common/footer.jsp"/>
