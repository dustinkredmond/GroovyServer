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

import org.gserve.api.persistence.Database;
import org.gserve.auth.AuthenticationInit;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
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

    @Listen("onClick = #buttonSubmit")
    public void handleSubmit() {
        if (Database.isConfigured()) {
            Messagebox.show("Database settings are already set.");
            return;
        }

        if (textBoxUser.getText().trim().isEmpty()
            || textBoxPass.getText().trim().isEmpty()
            || textBoxUrl.getText().trim().isEmpty()) {
            Messagebox.show("All fields are required.");
            return;
        }

        Database.setUsername(textBoxUser.getText().trim());
        Database.setPassword(textBoxPass.getText().trim());
        Database.setUrl(textBoxUrl.getText().trim());

        if (Database.canConnect()) {
            AuthenticationInit.setIsInitialLogin(false);
            initLogin.detach();
        } else {
            Messagebox.show("Cannot connect with provided details.");
        }
    }

}