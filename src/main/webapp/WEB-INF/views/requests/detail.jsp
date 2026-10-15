<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Request #${requestItem.id} Details - Smart Service SLA" />
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main>
    <div class="container">
        <div style="margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/requests" style="color: var(--primary); text-decoration: none; font-size: 0.9rem;">
                &larr; Back to Requests
            </a>
            <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 0.5rem;">
                <h1 style="font-size: 1.6rem; font-weight: 700;">
                    Request #<c:out value="${requestItem.id}"/>: <c:out value="${requestItem.title}"/>
                </h1>
                <div style="display: flex; gap: 0.5rem; align-items: center;">
                    <span class="badge badge-${requestItem.priority.name().toLowerCase()}">
                        <c:out value="${requestItem.priority}"/>
                    </span>
                    <span class="badge badge-${requestItem.status.name().toLowerCase()}">
                        <c:out value="${requestItem.status}"/>
                    </span>
                    <c:if test="${requestItem.slaBreached}">
                        <span class="badge badge-sla-breached">SLA BREACHED</span>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="../common/alerts.jsp"/>

        <!-- SLA Breach Warning Alert Box -->
        <c:if test="${requestItem.slaBreached}">
            <div class="alert alert-danger" style="display: flex; align-items: center; justify-content: space-between;">
                <div>
                    <strong>⚠️ SLA Breached &amp; Auto-Escalated!</strong>
                    <div style="font-size: 0.85rem; margin-top: 0.25rem;">
                        This ticket exceeded its allotted <c:out value="${requestItem.slaHours}"/>-hour SLA window.
                        <c:if test="${requestItem.escalation != null}">
                            Reason: <em><c:out value="${requestItem.escalation.reason}"/></em> (at <c:out value="${requestItem.escalation.breachedAt}"/>).
                        </c:if>
                    </div>
                </div>
            </div>
        </c:if>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem;">
            <!-- Left Column: Details & Audit Trail -->
            <div>
                <!-- Ticket Information Card -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Issue Description</h2>
                    </div>
                    <div style="white-space: pre-wrap; line-height: 1.6; color: var(--text-main); margin-bottom: 1.5rem;">
                        <c:out value="${requestItem.description}"/>
                    </div>

                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem; padding-top: 1rem; border-top: 1px solid var(--border); font-size: 0.875rem;">
                        <div>
                            <span style="color: var(--text-muted); display: block;">Category</span>
                            <strong><c:out value="${requestItem.category.name}"/></strong>
                        </div>
                        <div>
                            <span style="color: var(--text-muted); display: block;">Raised By</span>
                            <strong><c:out value="${requestItem.createdBy.fullName}"/></strong>
                        </div>
                        <div>
                            <span style="color: var(--text-muted); display: block;">Assigned Agent</span>
                            <strong>
                                <c:out value="${requestItem.assignedTo != null ? requestItem.assignedTo.fullName : 'Unassigned'}"/>
                            </strong>
                        </div>
                        <div>
                            <span style="color: var(--text-muted); display: block;">Created At</span>
                            <strong><c:out value="${requestItem.createdAt}"/></strong>
                        </div>
                        <c:if test="${requestItem.resolvedAt != null}">
                            <div>
                                <span style="color: var(--text-muted); display: block;">Resolved At</span>
                                <strong style="color: var(--success);"><c:out value="${requestItem.resolvedAt}"/></strong>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Audit History Card -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Audit Trail &amp; History</h2>
                    </div>

                    <div class="timeline">
                        <c:choose>
                            <c:when test="${empty historyList}">
                                <p style="color: var(--text-muted);">No history records recorded.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="h" items="${historyList}">
                                    <div class="timeline-item">
                                        <div style="display: flex; justify-content: space-between; align-items: center;">
                                            <span style="font-weight: 600; font-size: 0.9rem;">
                                                <c:out value="${h.action}"/>
                                                <c:if test="${not empty h.oldValue && not empty h.newValue}">
                                                    (<c:out value="${h.oldValue}"/> &rarr; <c:out value="${h.newValue}"/>)
                                                </c:if>
                                            </span>
                                            <span class="timeline-time"><c:out value="${h.performedAt}"/></span>
                                        </div>
                                        <div style="font-size: 0.85rem; color: var(--text-main); margin-top: 0.25rem;">
                                            <c:out value="${h.remarks}"/>
                                        </div>
                                        <div style="font-size: 0.775rem; color: var(--text-muted); margin-top: 0.15rem;">
                                            Performed by: <strong><c:out value="${h.performedBy.fullName}"/></strong>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Right Column: SLA Info & Action Controls -->
            <div>
                <!-- SLA Deadline Card -->
                <div class="card" style="border-left: 4px solid ${requestItem.slaBreached ? 'var(--critical)' : 'var(--primary)'};">
                    <div class="card-header">
                        <h2 class="card-title">SLA Compliance</h2>
                    </div>
                    <div style="margin-bottom: 0.75rem;">
                        <span style="color: var(--text-muted); font-size: 0.8rem; text-transform: uppercase;">Allocated SLA Window</span>
                        <div style="font-size: 1.15rem; font-weight: 700;"><c:out value="${requestItem.slaHours}"/> Hours</div>
                    </div>
                    <div style="margin-bottom: 0.75rem;">
                        <span style="color: var(--text-muted); font-size: 0.8rem; text-transform: uppercase;">Deadline</span>
                        <div style="font-size: 1rem; font-weight: 600; color: ${requestItem.slaBreached ? 'var(--critical)' : 'var(--text-main)'};">
                            <c:out value="${requestItem.slaDeadline}"/>
                        </div>
                    </div>
                    <div>
                        <span style="color: var(--text-muted); font-size: 0.8rem; text-transform: uppercase;">SLA Status</span>
                        <div>
                            <c:choose>
                                <c:when test="${requestItem.slaBreached}">
                                    <strong style="color: var(--critical);">BREACHED &amp; ESCALATED</strong>
                                </c:when>
                                <c:when test="${requestItem.status == 'RESOLVED' || requestItem.status == 'CLOSED'}">
                                    <strong style="color: var(--success);">RESOLVED WITHIN SLA</strong>
                                </c:when>
                                <c:otherwise>
                                    <strong style="color: var(--info);">WITHIN SLA WINDOW</strong>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Admin Assignment Card -->
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <div class="card">
                        <div class="card-header">
                            <h2 class="card-title">Assign to Agent</h2>
                        </div>
                        <form action="${pageContext.request.contextPath}/requests" method="POST">
                            <input type="hidden" name="action" value="assign">
                            <input type="hidden" name="requestId" value="${requestItem.id}">

                            <div class="form-group">
                                <label for="agentId">Select Support Agent</label>
                                <select id="agentId" name="agentId" required>
                                    <option value="">-- Choose Agent --</option>
                                    <c:forEach var="agent" items="${agents}">
                                        <option value="${agent.id}" ${requestItem.assignedTo != null && requestItem.assignedTo.id == agent.id ? 'selected' : ''}>
                                            <c:out value="${agent.fullName}"/> (<c:out value="${agent.email}"/>)
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <button type="submit" class="btn btn-primary btn-sm" style="width: 100%;">
                                Assign Request
                            </button>
                        </form>
                    </div>
                </c:if>

                <!-- Status Transition Card (AGENT or ADMIN) -->
                <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'AGENT'}">
                    <div class="card">
                        <div class="card-header">
                            <h2 class="card-title">Update Status</h2>
                        </div>
                        <form action="${pageContext.request.contextPath}/requests" method="POST">
                            <input type="hidden" name="action" value="status">
                            <input type="hidden" name="requestId" value="${requestItem.id}">

                            <div class="form-group">
                                <label for="status">New Status</label>
                                <select id="status" name="status" required>
                                    <c:forEach var="st" items="${allStatuses}">
                                        <option value="${st.name()}" ${requestItem.status == st ? 'selected' : ''}>
                                            <c:out value="${st.name()}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="remarks">Audit Remarks</label>
                                <textarea id="remarks" name="remarks" rows="3" placeholder="Explain the change or resolution details..."></textarea>
                            </div>

                            <button type="submit" class="btn btn-secondary btn-sm" style="width: 100%;">
                                Save Status Update
                            </button>
                        </form>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
