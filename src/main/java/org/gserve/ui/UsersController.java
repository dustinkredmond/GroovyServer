package org.gserve.ui;

import org.gserve.auth.BCrypt;
import org.gserve.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

/**
 * Acts as a table view and form controller for:
 * <ul>
 *     <li>/users.zul</li>
 *     <li>/users/add.zul</li>
 * </ul>
 */
public class UsersController extends SelectorComposer<Component> {
    @Wire
    private Textbox textBoxUsername;
    @Wire
    private Textbox textBoxPassword;
    @Wire
    private Textbox textBoxConfirm;
    @Wire
    private Listbox listBoxRole;
    @Wire
    private Box resultBox;
    @Wire
    private Window addUserDialog;

    @Wire
    private Window userSettingsDialog;
    @Wire
    private Textbox textBoxCurrentPassword;
    @Wire
    private Textbox textBoxNewPassword;
    @Wire
    private Textbox textBoxConfirmNewPassword;

    @Listen("onClick = #buttonResetPassword")
    public void resetPass(){
        Listbox listBox = (Listbox) resultBox.getFirstChild();
        if (listBox.getSelectedItem() == null){
            Messagebox.show("Please select a user first.");
        } else {
            int userKey = Integer.valueOf(listBox.getSelectedItem().getLabel());
            User user = User.getById(userKey);
            if (user != null) {
                String msg = "Are you sure you want to reset user "+user.getUsername()+"'s password?";
                Messagebox.show(msg, "Reset Password?", Messagebox.YES | Messagebox.NO,
                        Messagebox.QUESTION, evt -> {
                            if (Messagebox.ON_YES.equals(evt.getName())) {
                                Messagebox.show("New Password: \n\n"+user.resetPassword());
                            }
                        }
                );
            }
        }
    }

    @Listen("onOK = #textBoxCurrentPassword, #textBoxNewPassword, #textBoxConfirmPassword")
    public void submitSaveFromField(){
        this.doSavePassword();
    }

    @Listen("onClick = #buttonSavePassword")
    public void doSavePassword() {
        User currentUser = User.getByUsername(
                Executions.getCurrent().getSession().getAttribute("username").toString());
        if (currentUser == null){
            Messagebox.show("Cannot save password for user: null.");
            return;
        }
        if (!BCrypt.checkpw(textBoxCurrentPassword.getText(),currentUser.getPassword())){
            Messagebox.show("Your current password was incorrect.");
            return;
        }
        if (!textBoxNewPassword.getText().equals(textBoxConfirmNewPassword.getText())){
            Messagebox.show("Passwords did not match!");
            return;
        }
        if (textBoxNewPassword.getText().equals(textBoxCurrentPassword.getText())){
            Messagebox.show("New password cannot be the same as the old password.");
        }
        String hashedPass = BCrypt.hashpw(textBoxNewPassword.getText(), BCrypt.gensalt());
        currentUser.setPassword(hashedPass);
        currentUser.save();
        Executions.getCurrent().getSession().invalidate();
        Executions.sendRedirect("");
    }

    @Listen("onClick = #buttonAdd")
    public void showAddForm(){
        Window window = (Window) Executions.createComponents("users/add.zul",null,null);
        window.doModal();
    }

    @Listen("onOK = #textBoxConfirm")
    public void allowFieldSubmitAdd(){
        this.doAddSubmission();
    }
    @Listen("onClick = #buttonSubmitAdd")
    public void doAddSubmission(){
        if (textBoxUsername.getText().isEmpty()
                || textBoxPassword.getText().isEmpty()
                || textBoxConfirm.getText().isEmpty()
                || !textBoxPassword.getText().equals(textBoxConfirm.getText())
                || listBoxRole.getSelectedItem() == null){
            Messagebox.show("Please fill out all fields before trying to add a user.");
            return;
        }
        String hashedPass = BCrypt.hashpw(textBoxPassword.getText(), BCrypt.gensalt());
        User.add(new User(
                textBoxUsername.getText(),hashedPass
                ,listBoxRole.getSelectedItem().getValue().toString()));
        Executions.sendRedirect("users.zul");
    }

    @Listen("onClick = #buttonDeleteUser")
    public void doDelete(){
        Listbox listBox = (Listbox) resultBox.getFirstChild();
        if (listBox.getSelectedItem() == null){
            Messagebox.show("Please select a user first.");
        } else {
            int userKey = Integer.valueOf(listBox.getSelectedItem().getLabel());
            User user = User.getById(userKey);
            if (user != null) {
                String msg = "Are you sure you want to delete user: " + user.getUsername() + "?";
                Messagebox.show(msg, "Delete?", Messagebox.YES | Messagebox.NO,
                        Messagebox.QUESTION, evt -> {
                            if (Messagebox.ON_YES.equals(evt.getName())) {
                                user.delete();
                                Executions.sendRedirect("users.zul");
                            }
                        }
                );
            }
        }
    }
}
