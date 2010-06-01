package net.desgrange.pwad.functional;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.desgrange.pwad.Main;
import net.desgrange.pwad.utils.UiTestCase;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.uispec4j.UISpecAdapter;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

public abstract class PwadTestCase extends UiTestCase {
    private UISpecAdapter adapter;
    private Server server;
    private int port;

    @Before
    public void ensureEnvironmentIsInitialized() throws Exception {
        getServer();
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
    }

    @After
    public void afterTest() {
        adapter = null;
    }

    public Window getMainWindow() {
        return getAdapter().getMainWindow();
    }

    private UISpecAdapter getAdapter() {
        if (adapter == null) {
            adapter = new MainClassAdapter(Main.class);
        }
        return adapter;
    }

    private Server getServer() throws Exception {
        if (server == null) {
            server = new Server(getPort());
            server.setHandler(new AbstractHandler() {
                @Override
                public void handle(final String target, final Request jettyRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
                    System.out.println("Target: " + target);
                    System.out.println("Request: " + request);
                    System.out.println("Response: " + response);
                }
            });
            server.start();
        }
        return server;
    }

    private int getPort() {
        if (port == 0) {
            port = 8080;// TODO find a free port to use automatically
        }
        return port;
    }
}
