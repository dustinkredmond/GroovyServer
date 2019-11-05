package org.gserve.menu;

import org.gserve.api.persistence.SystemVariables;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Window;

import java.util.HashMap;

public class MainMenuBarController extends SelectorComposer<Component> {

    @Listen("onClick = #menuSettings")
    public void showSettingsForm() {
        HashMap<String,Object> arg = new HashMap<>();

        arg.put("infoSelected",SystemVariables.getValue("alertLevel").equalsIgnoreCase("INFO"));
        arg.put("warningSelected",SystemVariables.getValue("alertLevel").equalsIgnoreCase("WARNING"));
        arg.put("errorSelected",SystemVariables.getValue("alertLevel").equalsIgnoreCase("ERROR"));
        arg.put("offSelected",SystemVariables.getValue("alertLevel").equalsIgnoreCase("OFF"));


        arg.put("smtpUsername", SystemVariables.getValue("smtpUsername"));
        arg.put("smtpPassword", SystemVariables.getValue("smtpPassword"));
        arg.put("smtpServer",SystemVariables.getValue("smtpServer"));
        arg.put("smtpPort",SystemVariables.getValue("smtpPort"));
        arg.put("alertLevel",SystemVariables.getValue("alertLevel"));
        arg.put("alertEmail",SystemVariables.getValue("alertEmail"));

        Window window = (Window) Executions.createComponents("systemSettings.zul",null,arg);
        window.doModal();
    }

    @Listen("onClick = #menuUserSettings")
    public void showUserSettingsForm(){
        Window window = (Window) Executions.createComponents("userSettings.zul",null,null);
        window.doModal();
    }
}
