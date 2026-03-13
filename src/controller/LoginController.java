package controller;

import javax.swing.JOptionPane;

import dao.UsuarioDAO;
import model.Usuario;
import view.LoginFrame;
import view.MainFrame;
import view.RegistroFrame;

public class LoginController {

    private LoginFrame view;
    private UsuarioDAO dao;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.dao = new UsuarioDAO();
    }

    public void login() {

        Usuario usuario = dao.login(
                view.getAlias(),
                view.getPassword()
        );

        if (usuario != null) {
            new MainFrame(usuario).setVisible(true);
            view.dispose();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Usuario o contraseña incorrectos");
        }
    }

    public void irARegistro() {
        new RegistroFrame().setVisible(true);
        view.dispose();
    }
}
