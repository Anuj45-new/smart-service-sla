<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Raise New Service Request - Smart Service SLA" />
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main>
    <div class="container" style="max-width: 760px;">
        <div style="margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/requests" style="color: var(--primary); text-decoration: none; font-size: 0.9rem;">
                &larr; Back to Requests
            </a>
            <h1 style="font-size: 1.6rem; font-weight: 700; margin-top: 0.5rem;">Raise New Service Request</h1>
            <p style="color: var(--text-muted); font-size: 0.95rem;">
                Fill out the form below. The system automatically calculates and enforces the SLA resolution deadline.
            </p>
        </div>

        <jsp:include page="../common/alerts.jsp"/>

        <div class="card">
            <form action="${pageContext.request.contextPath}/requests" method="POST">
                <input type="hidden" name="action" value="create">

                <div class="form-group">
                    <label for="title">Request Title *</label>
                    <input type="text" id="title" name="title" value="<c:out value='${title}'/>" placeholder="e.g. Broken monitor in Meeting Room B" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="categoryId">Category *</label>
                        <select id="categoryId" name="categoryId" required>
                            <option value="">-- Select Category --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" ${selectedCategoryId == cat.id ? 'selected' : ''}>
                                    <c:out value="${cat.name}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="priority">Priority &amp; SLA *</label>
                        <select id="priority" name="priority" required>
                            <option value="">-- Select Priority --</option>
                            <c:forEach var="p" items="${priorities}">
                                <option value="${p.name()}" ${selectedPriority == p.name() ? 'selected' : ''}>
                                    <c:out value="${p.name()}"/> (<c:out value="${p.defaultSlaHours}"/> hours SLA)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="description">Detailed Description *</label>
                    <textarea id="description" name="description" rows="5" placeholder="Provide full details of the issue..." required><c:out value='${description}'/></textarea>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                    <a href="${pageContext.request.contextPath}/requests" class="btn btn-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary">Submit Request</button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
