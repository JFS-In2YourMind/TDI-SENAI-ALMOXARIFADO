package br.com.tdi.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TelaQRCode extends JFrame {

    public TelaQRCode() {
        setTitle("Acesso Celular - TDI Almoxarifado");
        setSize(380, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Aponte a câmera do celular para conectar", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(new Color(13, 71, 161));
        add(lblTitulo, BorderLayout.NORTH);

        try {
            String urlAcesso = "http://192.168.1.130:8080";
            String apiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" + urlAcesso;
            URL url = new URL(apiUrl);
            ImageIcon icon = new ImageIcon(url);
            JLabel lblQR = new JLabel(icon, SwingConstants.CENTER);
            add(lblQR, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel lblErro = new JLabel("Erro ao carregar o QR Code", SwingConstants.CENTER);
            add(lblErro, BorderLayout.CENTER);
        }

        JLabel lblRodape = new JLabel("http://192.168.1.130:8080", SwingConstants.CENTER);
        lblRodape.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRodape.setForeground(Color.GRAY);
        add(lblRodape, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new TelaQRCode().setVisible(true);
        });
    }
}