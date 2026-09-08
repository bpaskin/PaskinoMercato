package it.paskinomercato.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Resolves the WebSphere server name once at class load and exposes it
 * as the request attribute "serverName" so JSPs can reference it via EL
 * without scriptlets.
 *
 * com.ibm.websphere.runtime.ServerName is loaded via reflection so that
 * the WAR compiles without a proprietary WebSphere JAR on the local
 * classpath. The real class is always present on the WebSphere JRE at
 * runtime.
 */
public class ServerNameFilter implements Filter {

    private static final String SERVER_NAME;

    static {
        SERVER_NAME = System.getProperty("wlp.server.name");
    }

    public void init(FilterConfig config) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setAttribute("serverName", SERVER_NAME);
        chain.doFilter(request, response);
    }

    public void destroy() {}
}
