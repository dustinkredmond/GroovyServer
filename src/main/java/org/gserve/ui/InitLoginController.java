package org.gserve.ui;

/*
 *  Copyright 2020  Dustin K. Redmond
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.gserve.api.persistence.Database;
import org.gserve.auth.AuthenticationInit;
import org.gserve.auth.BCrypt;
import org.gserve.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

public class InitLoginController extends SelectorComposer<Component> {

    @Wire
    private Textbox textBoxUser;
    @Wire
    private Textbox textBoxPass;
    @Wire
    private Textbox textBoxUrl;
    @Wire
    private Window initLogin;
    @Wire
    private Checkbox createAdmin;
    @Wire
    private Textbox textBoxPassphrase;

    @Listen("onClick = #buttonSubmit")
    public void handleSubmit() {
        if (Database.isConfigured()) {
            Messagebox.show("Database settings are already set.");
            return;
        }

        if (textBoxUser.getText().trim().isEmpty()
            || textBoxPass.getText().trim().isEmpty()
            || textBoxUrl.getText().trim().isEmpty()
            || textBoxPassphrase.getText().trim().isEmpty()) {
            Messagebox.show("All fields are required.");
            return;
        }

        try {
            InitialContext initialContext = new InitialContext();
            Context env = (Context) initialContext.lookup("java:/comp/env");
            String passphrase = (String) env.lookup("securityPassphrase");
            if (!textBoxPassphrase.getText().trim().equals(passphrase.trim())) {
                Messagebox.show("You must enter the security passphrase as it's found"
                    + " in WEB-INF/context.xml\n\nThis should be user-defined and be "
                    + "hard to guess.\nIf this is set to \"default\", you must change it, "
                    + "otherwise attackers could run arbitrary code on your machine.");
            return;
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        Database.setUsername(textBoxUser.getText().trim());
        Database.setPassword(textBoxPass.getText().trim());
        Database.setUrl(textBoxUrl.getText().trim());

        if (Database.canConnect()) {
            AuthenticationInit.setIsInitialLogin(false);
            Database.createTablesAndSetup();
            initLogin.detach();
            if (createAdmin.isChecked()) {
                String adminUser = User.generateRandomUsername();
                String adminPass = User.generateRandomPassword();
                User admin = new User(adminUser, BCrypt.hashpw(adminPass, BCrypt.gensalt()), "admin");
                User.add(admin);
                Messagebox.show("Please login with the following information:\n\n"
                    + "Username: "+adminUser+"\n"
                    + "Password: "+adminPass+"\n\n"
                    + "You can create/remove users once logged in.\n"
                    + "You will not be able to view above information again.");
            }
        } else {
            Messagebox.show("Cannot connect with provided details.");
        }
    }

}
