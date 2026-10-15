package com.smartservice.filter;

import com.smartservice.entity.User;
import com.smartservice.enums.Role;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Unified filter managing authentication session validation and role authorization.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/logout",
            "/assets"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Force UTF-8 character encoding across all requests/responses
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        // Allow static assets, login, and logout endpoints
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Root landing redirection
        if (path.equals("/") || path.equals("/index.jsp")) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("currentUser") != null) {
                res.sendRedirect(contextPath + "/dashboard");
            } else {
                res.sendRedirect(contextPath + "/login");
            }
            return;
        }

        // Authentication verification
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        // Role authorization checks
        Role role = currentUser.getRole();

        // /reports endpoint is restricted strictly to ADMIN
        if (path.startsWith("/reports") && role != Role.ADMIN) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Reports are restricted to Administrators.");
            return;
        }

        // Assignment action inside requests is restricted strictly to ADMIN
        String action = req.getParameter("action");
        if (path.startsWith("/requests") && "assign".equalsIgnoreCase(action) && role != Role.ADMIN) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Ticket assignment requires Admin privileges.");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path.startsWith("/assets/")) {
            return true;
        }
        for (String pub : PUBLIC_PATHS) {
            if (path.equals(pub) || path.startsWith(pub + "/")) {
                return true;
            }
        }
        return false;
    }
}
