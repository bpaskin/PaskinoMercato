package it.paskinomercato.servlet;

import com.ibm.websphere.runtime.ServerName;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
        SERVER_NAME = com.ibm.websphere.runtime.ServerName.getDisplayName();
    }

    public void init(FilterConfig config) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setAttribute("serverName", SERVER_NAME);
        chain.doFilter(request, response);
    }

    public void destroy() {}
}
