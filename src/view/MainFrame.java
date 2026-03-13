package view;

import javax.swing.*;
import java.awt.*;
import model.Usuario;


@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    @SuppressWarnings("unused")
	private Usuario usuario;

    public MainFrame(Usuario usuario) {

        this.usuario = usuario;

        setTitle("TestManager - " + usuario.getAlias());
        setSize(1920,1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Proyectos", new ProyectoPanel(usuario));

        add(tabs, BorderLayout.CENTER);
    }
}
