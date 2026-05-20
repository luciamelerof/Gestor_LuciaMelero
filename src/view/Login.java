package view;

import dao.impl.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JLabel lblRegistro;

    public Login() {
        initUI();
    }

    private void initUI() {
        setTitle("RentACar — Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Título
        JLabel lblTitulo = new JLabel("🚗 RentACar", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Usuario:"), gbc);
        txtUsername = new JTextField();
        gbc.gridy = 2;
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField();
        gbc.gridy = 4;
        panel.add(txtPassword, gbc);

        // Botón entrar
        btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(new Color(70, 130, 180));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 5;
        panel.add(btnEntrar, gbc);

        // Enlace registro
        lblRegistro = new JLabel("¿No tienes cuenta? Regístrate", SwingConstants.CENTER);
        lblRegistro.setForeground(new Color(70, 130, 180));
        lblRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        panel.add(lblRegistro, gbc);

        add(panel);

        // Acciones
        btnEntrar.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());

        lblRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new Registro().setVisible(true);
                dispose();
            }
        });
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor rellena todos los campos.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAOImpl dao = new UsuarioDAOImpl();
        Usuario usuario = dao.validar(username, password);

        if (usuario != null) {
            new VentanaPrincipal(usuario).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos.",
                "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
