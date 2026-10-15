<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login - Smart Service Request System" />
<jsp:include page="common/header.jsp"/>
<jsp:include page="common/navbar.jsp"/>

<main>
    <div class="container" style="max-width: 460px; margin-top: 2rem;">
        <div class="card">
            <div class="card-header" style="justify-content: center; text-align: center;">
                <h2 class="card-title" style="font-size: 1.4rem;">Sign In</h2>
            </div>

            <jsp:include page="common/alerts.jsp"/>

            <form action="${pageContext.request.contextPath}/login" method="POST">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" value="<c:out value='${username}'/>" required autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
                    Login to Dashboard
                </button>
            </form>

            <div style="margin-top: 1.5rem; padding: 1rem; background: #f8fafc; border: 1px dashed var(--border); border-radius: 6px; font-size: 0.825rem;">
                <strong>Demo Accounts (password: password123):</strong>
                <ul style="margin-left: 1.25rem; margin-top: 0.35rem;">
                    <li><strong>Admin:</strong> <code>admin</code></li>
                    <li><strong>Agent:</strong> <code>agent_john</code> or <code>agent_sarah</code></li>
                    <li><strong>User:</strong> <code>user_alice</code> or <code>user_bob</code></li>
                </ul>
            </div>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp"/>
