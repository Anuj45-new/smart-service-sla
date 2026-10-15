<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">
            <span>🛡️</span> Smart Service SLA
        </a>
        <c:if test="${not empty sessionScope.currentUser}">
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/requests">Requests</a></li>
                <c:if test="${sessionScope.currentUser.role == 'USER' || sessionScope.currentUser.role == 'ADMIN'}">
                    <li><a href="${pageContext.request.contextPath}/requests?action=new">+ New Request</a></li>
                </c:if>
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <li><a href="${pageContext.request.contextPath}/reports">Reports</a></li>
                </c:if>
                <li>
                    <span class="user-badge">
                        <c:out value="${sessionScope.currentUser.fullName}"/>
                        (<c:out value="${sessionScope.currentUser.role}"/>)
                    </span>
                </li>
                <li><a href="${pageContext.request.contextPath}/logout" style="color: #fca5a5;">Logout</a></li>
            </ul>
        </c:if>
    </div>
</header>
