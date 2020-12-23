package org.gserve.auth;

import org.gserve.BackgroundJobRunner;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.Initiator;

import java.util.Map;
import org.zkoss.zul.Window;

/**
 * Called as an init from ZUL files in order to only load the page if
 * the user has been authenticated. Any secure web page must declare
 * this {@code org.zkoss.zk.ui.util.Initiator}
 */
public class AuthenticationInit implements Initiator {

    @Override
    public void doInit(Page page, Map<String, Object> map) {
        if (!BackgroundJobRunner.hasRun()) {
            BackgroundJobRunner.init();
        }

        Session session = Executions.getCurrent().getSession();
        if (session.hasAttribute("authenticated")){
            boolean authenticated = (boolean) session.getAttribute("authenticated");
            if (!authenticated){
                Executions.sendRedirect("login.zul");
            }
        } else {
            Executions.sendRedirect("login.zul");
        }
    }

}

