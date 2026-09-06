package it.paskinomercato.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Resolves the Liberty server name once at class load and exposes it
 * as the request attribute "serverName" so JSPs can reference it via EL
 * without scriptlets.
 *
 * The server name is read from the standard Liberty JVM property
 * {@code wlp.server.name}, which is always set by the Liberty runtime.
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
