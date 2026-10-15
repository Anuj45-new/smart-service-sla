<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">
        <c:out value="${errorMessage}"/>
    </div>
</c:if>

<c:if test="${not empty param.error}">
    <div class="alert alert-danger">
        <c:out value="${param.error}"/>
    </div>
</c:if>

<c:if test="${not empty param.success}">
    <div class="alert alert-success">
        <c:choose>
            <c:when test="${param.success == 'created'}">Service request submitted successfully with calculated SLA deadline.</c:when>
            <c:when test="${param.success == 'assigned'}">Request assigned to agent successfully.</c:when>
            <c:when test="${param.success == 'updated'}">Request status updated successfully.</c:when>
            <c:otherwise><c:out value="${param.success}"/></c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${param.logout == 'true'}">
    <div class="alert alert-success">
        You have been logged out successfully.
    </div>
</c:if>
