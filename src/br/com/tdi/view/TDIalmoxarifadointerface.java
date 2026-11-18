package br.com.tdi.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

// =====================================================
// CLASSE DE CONEXÃO DIRETA COM O MYSQL (tdi_almoxarifado)
// =====================================================
class BancoTDI {
    private static final String URL = "jdbc:mysql://localhost:3306/tdi_almoxarifado?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "SENAI";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

// =====================================================
// TELA DE LOGIN E CADASTRO (CENTRALIZADA E HARMONIOSA)
// =====================================================
class TelaLogin extends JFrame {
    private JTextField campoUsuarioLogin;
    private JPasswordField campoSenhaLogin;
    private JTextField campoNomeCadastro;
    private JTextField campoEmailCadastro;
    private JPasswordField campoSenhaCadastro;
    private JPasswordField campoConfirmaSenhaCadastro;
    private JTabbedPane abas;

    private final Color AZUL_ESCURO = new Color(25, 57, 82);
    private final Color AZUL_CLARO = new Color(238, 244, 249);
    private final Color AZUL = new Color(82, 125, 157);
    private final Color VERDE = new Color(102, 155, 131);
    private final Color BRANCO = Color.WHITE;

    public TelaLogin() {
        setTitle("TDI Máquinas Agrícolas | Autenticação do Sistema");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(AZUL_CLARO);

        JPanel painelTopo = new JPanel();
        painelTopo.setBackground(AZUL_ESCURO);
        painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.Y_AXIS));
        painelTopo.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel titulo = new JLabel("TDI MÁQUINAS AGRÍCOLAS", SwingConstants.CENTER);
        titulo.setForeground(BRANCO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Sistema Profissional de Gestão de Almoxarifado (MySQL Conectado)", SwingConstants.CENTER);
        subtitulo.setForeground(new Color(205, 220, 232));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTopo.add(titulo);
        painelTopo.add(Box.createVerticalStrut(6));
        painelTopo.add(subtitulo);

        abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        abas.setBackground(AZUL_CLARO);

        abas.addTab("    Entrar no Sistema    ", criarPainelCentralizado(criarPainelLogin(), 450, 310));
        abas.addTab("    Criar Novo Cadastro    ", criarPainelCentralizado(criarPainelCadastro(), 450, 430));

        painelPrincipal.add(painelTopo, BorderLayout.NORTH);
        painelPrincipal.add(abas, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    private JPanel criarPainelCentralizado(JPanel formulario, int larguraCard, int alturaCard) {
        JPanel painelExterno = new JPanel(new GridBagLayout());
        painelExterno.setBackground(AZUL_CLARO);
        
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(BRANCO);
        cartao.setPreferredSize(new Dimension(larguraCard, alturaCard));
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229), 1),
                new EmptyBorder(25, 30, 25, 30)
        ));
        cartao.add(formulario, BorderLayout.CENTER);

        painelExterno.add(cartao);
        return painelExterno;
    }

    private JPanel criarPainelLogin() {
        JPanel painel = new JPanel();
        painel.setBackground(BRANCO);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel("Usuário ou E-mail:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(AZUL_ESCURO);
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);

        campoUsuarioLogin = new JTextField();
        estilizarCampoCompacto(campoUsuarioLogin);

        JLabel l2 = new JLabel("Senha:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(AZUL_ESCURO);
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);

        campoSenhaLogin = new JPasswordField();
        estilizarCampoCompacto(campoSenhaLogin);

        JButton btnEntrar = new JButton("ACESSAR SISTEMA");
        btnEntrar.setBackground(VERDE);
        btnEntrar.setForeground(BRANCO);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(390, 38));
        btnEntrar.setPreferredSize(new Dimension(390, 38));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEntrar.addActionListener(e -> realizarLogin());
        campoSenhaLogin.addActionListener(e -> realizarLogin());

        painel.add(l1);
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoUsuarioLogin);
        painel.add(Box.createVerticalStrut(12));
        painel.add(l2);
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoSenhaLogin);
        painel.add(Box.createVerticalStrut(22));
        painel.add(btnEntrar);

        return painel;
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel();
        painel.setBackground(BRANCO);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel lNome = new JLabel("Nome Completo:");
        lNome.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lNome.setForeground(AZUL_ESCURO);
        lNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoNomeCadastro = criarCampoEstilizadoCompacto();

        JLabel l1 = new JLabel("Usuário ou E-mail de Acesso:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(AZUL_ESCURO);
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoEmailCadastro = criarCampoEstilizadoCompacto();

        JLabel l2 = new JLabel("Escolha sua Senha:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(AZUL_ESCURO);
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoSenhaCadastro = new JPasswordField();
        estilizarCampoCompacto(campoSenhaCadastro);

        JLabel l3 = new JLabel("Confirme a Senha:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l3.setForeground(AZUL_ESCURO);
        l3.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoConfirmaSenhaCadastro = new JPasswordField();
        estilizarCampoCompacto(campoConfirmaSenhaCadastro);

        JButton btnCadastrar = new JButton("FINALIZAR CADASTRO");
        btnCadastrar.setBackground(AZUL);
        btnCadastrar.setForeground(BRANCO);
        btnCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(390, 38));
        btnCadastrar.setPreferredSize(new Dimension(390, 38));
        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCadastrar.addActionListener(e -> realizarCadastro());

        painel.add(lNome);
        painel.add(Box.createVerticalStrut(2));
        painel.add(campoNomeCadastro);
        painel.add(Box.createVerticalStrut(6));
        painel.add(l1);
        painel.add(Box.createVerticalStrut(2));
        painel.add(campoEmailCadastro);
        painel.add(Box.createVerticalStrut(6));
        painel.add(l2);
        painel.add(Box.createVerticalStrut(2));
        painel.add(campoSenhaCadastro);
        painel.add(Box.createVerticalStrut(6));
        painel.add(l3);
        painel.add(Box.createVerticalStrut(2));
        painel.add(campoConfirmaSenhaCadastro);
        painel.add(Box.createVerticalStrut(14));
        painel.add(btnCadastrar);

        return painel;
    }

    private void estilizarCampoCompacto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBackground(Color.WHITE);
        campo.setForeground(AZUL_ESCURO);
        campo.setCaretColor(AZUL_ESCURO);
        campo.setMaximumSize(new Dimension(390, 34));
        campo.setPreferredSize(new Dimension(390, 34));
        campo.setMinimumSize(new Dimension(390, 34));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 202, 218)),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private JTextField criarCampoEstilizadoCompacto() {
        JTextField campo = new JTextField();
        estilizarCampoCompacto(campo);
        return campo;
    }

    private void realizarLogin() {
        String loginDigitado = campoUsuarioLogin.getText().trim();
        String senha = new String(campoSenhaLogin.getPassword()).trim();

        if (loginDigitado.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o usuário/e-mail e a senha.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT nome, email FROM usuarios WHERE (email = ? OR nome = ?) AND senha = ?";
        try (Connection conn = BancoTDI.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginDigitado);
            stmt.setString(2, loginDigitado);
            stmt.setString(3, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nomeUsuario = rs.getString("nome");
                    dispose();
                    SwingUtilities.invokeLater(() -> new TDIalmoxarifadointerface(nomeUsuario).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar com o banco MySQL:\n" + ex.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarCadastro() {
        String nome = campoNomeCadastro.getText().trim();
        String email = campoEmailCadastro.getText().trim();
        String senha = new String(campoSenhaCadastro.getPassword()).trim();
        String confirma = new String(campoConfirmaSenhaCadastro.getPassword()).trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos para cadastrar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!senha.equals(confirma)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Erro na Senha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlVerifica = "SELECT id FROM usuarios WHERE email = ?";
        String sqlInsert = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conn = BancoTDI.conectar()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(sqlVerifica)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Este usuário/e-mail já está cadastrado no banco.", "Usuário Existente", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                insertStmt.setString(1, nome);
                insertStmt.setString(2, email);
                insertStmt.setString(3, senha);
                insertStmt.executeUpdate();
            }

            campoNomeCadastro.setText("");
            campoEmailCadastro.setText("");
            campoSenhaCadastro.setText("");
            campoConfirmaSenhaCadastro.setText("");

            JOptionPane.showMessageDialog(this,
                    "Cadastro salvo permanentemente no MySQL!\nAgora faça o login com seu usuário e senha.",
                    "Cadastro Concluído",
                    JOptionPane.INFORMATION_MESSAGE);

            abas.setSelectedIndex(0);
            campoUsuarioLogin.setText(email);
            campoSenhaLogin.setText("");
            campoSenhaLogin.requestFocus();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar usuário no MySQL:\n" + ex.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// =====================================================
// JANELA DE LEITURA DE QR CODE VIA WEBCAM
// =====================================================
class DialogLeitorWebcam extends JDialog {
    private Webcam webcam;
    private WebcamPanel painelWebcam;
    private volatile boolean executando = true;
    private final Consumer<String> callback;

    public DialogLeitorWebcam(Frame parent, Consumer<String> callback) {
        super(parent, "Leitor Óptico de QR Code - Câmera ao Vivo", true);
        this.callback = callback;

        setSize(660, 540);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharCamera();
            }
        });

        try {
            List<Webcam> webcams = Webcam.getWebcams();
            if (webcams == null || webcams.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Nenhuma webcam detectada.", "Câmera Indisponível", JOptionPane.WARNING_MESSAGE);
                executando = false;
                dispose();
                return;
            }

            webcam = Webcam.getDefault();
            webcam.setViewSize(WebcamResolution.VGA.getSize());

            painelWebcam = new WebcamPanel(webcam);
            painelWebcam.setMirrored(true);
            painelWebcam.setFPSDisplayed(false);
            painelWebcam.setFPSLimited(true);

            add(painelWebcam, BorderLayout.CENTER);

            JPanel rodape = new JPanel(new BorderLayout());
            rodape.setBackground(new Color(25, 57, 82));
            rodape.setBorder(new EmptyBorder(12, 16, 12, 16));

            JLabel lblInstrucao = new JLabel("Aponte o QR Code ou código de barras para a câmera", SwingConstants.CENTER);
            lblInstrucao.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblInstrucao.setForeground(Color.WHITE);

            JButton btnCancelar = new JButton("Fechar Câmera");
            btnCancelar.setBackground(new Color(185, 108, 108));
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancelar.setFocusPainted(false);
            btnCancelar.setBorderPainted(false);
            btnCancelar.addActionListener(e -> {
                fecharCamera();
                dispose();
            });

            rodape.add(lblInstrucao, BorderLayout.CENTER);
            rodape.add(btnCancelar, BorderLayout.EAST);
            add(rodape, BorderLayout.SOUTH);

            iniciarLeitura();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Erro ao abrir câmera: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            fecharCamera();
            dispose();
        }
    }

    private void iniciarLeitura() {
        Thread thread = new Thread(() -> {
            MultiFormatReader leitor = new MultiFormatReader();
            while (executando) {
                try {
                    if (webcam != null && webcam.isOpen()) {
                        BufferedImage imagem = webcam.getImage();
                        if (imagem != null) {
                            BufferedImageLuminanceSource fonte = new BufferedImageLuminanceSource(imagem);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(fonte));
                            try {
                                Result resultado = leitor.decode(bitmap);
                                if (resultado != null && resultado.getText() != null) {
                                    String textoLido = resultado.getText().trim();
                                    SwingUtilities.invokeLater(() -> {
                                        fecharCamera();
                                        if (callback != null) {
                                            callback.accept(textoLido);
                                        }
                                        dispose();
                                    });
                                    break;
                                }
                            } catch (Exception e) {
                            } finally {
                                leitor.reset();
                            }
                        }
                    }
                    Thread.sleep(120);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                }
            }
        }, "LeitorQRWebcam");

        thread.setDaemon(true);
        thread.start();
    }

    private void fecharCamera() {
        executando = false;
        try {
            if (painelWebcam != null) painelWebcam.stop();
            if (webcam != null && webcam.isOpen()) webcam.close();
        } catch (Exception ignored) {
        }
    }
}

// =====================================================
// CLASSE PRINCIPAL DO SISTEMA DE ALMOXARIFADO (MYSQL)
// =====================================================
public class TDIalmoxarifadointerface extends JFrame {

    private final Color AZUL = new Color(82, 125, 157);
    private final Color AZUL_ESCURO = new Color(25, 57, 82);
    private final Color AZUL_CLARO = new Color(238, 244, 249);
    private final Color VERDE = new Color(102, 155, 131);
    private final Color DOURADO = new Color(195, 148, 85);
    private final Color VERMELHO = new Color(185, 108, 108);
    private final Color BRANCO = Color.WHITE;
    private final Color TEXTO = new Color(35, 52, 69);

    private final CardLayout layout = new CardLayout();
    private final JPanel conteudo = new JPanel(layout);

    private final ArrayList<Produto> produtos = new ArrayList<>();
    private final Map<Integer, String> mapaCategorias = new HashMap<>();
    private DefaultTableModel modelo;
    private JTable tabela;

    private JTextField codigo;
    private JTextField descricao;
    private JComboBox<CategoriaItem> comboCategoria;
    private JTextField quantidade;
    private JTextField minimo;
    private JTextField localizacao;
    private JTextField custo;
    private JTextField pesquisa;

    private JLabel totalProdutos;
    private JLabel totalUnidades;
    private JLabel totalBaixo;
    private JLabel totalValorDashboard;
    private DefaultTableModel modeloResumoDashboard;

    private final String usuarioLogado;
    private final String horarioLogin;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private MovimentacaoUI telaEntrada;
    private MovimentacaoUI telaSaida;

    private JTable tabelaRelatorio;
    private DefaultTableModel modeloRelatorio;
    private JLabel relatorioTotalProdutos;
    private JLabel relatorioTotalUnidades;
    private JLabel relatorioEstoqueBaixo;
    private JLabel relatorioValorTotal;
    private JLabel resultadoQrLabel;

    public TDIalmoxarifadointerface(String usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.horarioLogin = LocalDateTime.now().format(formatoData);

        setTitle("TDI Máquinas Agrícolas | Sistema de Almoxarifado");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new Thread(() -> {
            try {
                ServidorWeb.main(new String[]{});
            } catch (Exception ignored) {
            }
        }).start();

        criarInterface();
        recarregarTudoDoBanco();
    }

    private void recarregarTudoDoBanco() {
        carregarCategoriasDoBanco();
        carregarProdutosDoBanco();
        atualizarTabela();
        atualizarIndicadores();
        atualizarRelatorio();
        atualizarTabelasMovimentacao();
    }

    private void carregarCategoriasDoBanco() {
        mapaCategorias.clear();
        if (comboCategoria != null) {
            comboCategoria.removeAllItems();
        }

        String sql = "SELECT * FROM categorias";
        try (Connection conn = BancoTDI.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idCat = rs.getInt(1);
                String nomeCat = rs.getString(2);
                mapaCategorias.put(idCat, nomeCat);
                if (comboCategoria != null) {
                    comboCategoria.addItem(new CategoriaItem(idCat, nomeCat));
                }
            }
        } catch (SQLException ignored) {
        }

        if (comboCategoria != null && comboCategoria.getItemCount() == 0) {
            comboCategoria.addItem(new CategoriaItem(1, "Geral"));
        }
    }

    private void carregarProdutosDoBanco() {
        produtos.clear();
        String sql = "SELECT id_produto, codigo_peca, descricao, id_categoria, "
                   + "quantidade_estoque, estoque_minimo, localizacao_prateleira, preco_custo "
                   + "FROM produtos ORDER BY id_produto DESC";

        try (Connection conn = BancoTDI.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idCat = rs.getInt("id_categoria");
                String nomeCat = mapaCategorias.getOrDefault(idCat, "Categoria " + idCat);

                produtos.add(new Produto(
                    rs.getInt("id_produto"),
                    rs.getString("codigo_peca"),
                    rs.getString("descricao"),
                    nomeCat,
                    rs.getInt("quantidade_estoque"),
                    rs.getInt("estoque_minimo"),
                    rs.getString("localizacao_prateleira") != null ? rs.getString("localizacao_prateleira") : "Geral",
                    rs.getDouble("preco_custo")
                ));
            }
        } catch (SQLException ex) {
            mensagem("Erro ao carregar produtos do MySQL:\n" + ex.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarInterface() {
        setLayout(new BorderLayout());
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(AZUL_CLARO);

        principal.add(criarMenuLateral(), BorderLayout.WEST);

        conteudo.setBackground(AZUL_CLARO);
        conteudo.add(criarDashboard(), "inicio");
        conteudo.add(criarEstoque(), "estoque");
        conteudo.add(criarCadastro(), "cadastro");
        conteudo.add(criarMovimentacao(true), "entrada");
        conteudo.add(criarMovimentacao(false), "saida");
        conteudo.add(criarRelatorios(), "relatorios");
        conteudo.add(criarTelaQrCode(), "qrcode");
        conteudo.add(criarPerfil(), "perfil");

        principal.add(conteudo, BorderLayout.CENTER);
        add(principal);
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel(new BorderLayout());
        menu.setPreferredSize(new Dimension(225, 0));
        menu.setBackground(AZUL_ESCURO);

        // 1. TOPO: Logotipo fixo
        JPanel logo = new JPanel();
        logo.setOpaque(false);
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBorder(new EmptyBorder(14, 12, 10, 12));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nome = new JLabel("TDI", SwingConstants.CENTER);
        nome.setForeground(BRANCO);
        nome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("MÁQUINAS AGRÍCOLAS", SwingConstants.CENTER);
        subtitulo.setForeground(new Color(220, 232, 243));
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sistema = new JLabel("<html><div style='text-align:center;'>SISTEMA DE ALMOXARIFADO</div></html>", SwingConstants.CENTER);
        sistema.setForeground(new Color(180, 202, 220));
        sistema.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sistema.setAlignmentX(Component.CENTER_ALIGNMENT);

        logo.add(nome);
        logo.add(Box.createVerticalStrut(2));
        logo.add(subtitulo);
        logo.add(Box.createVerticalStrut(4));
        logo.add(sistema);

        menu.add(logo, BorderLayout.NORTH);

        // 2. CENTRO: Opções do menu centralizadas
        JPanel painelBotoesCentro = new JPanel();
        painelBotoesCentro.setOpaque(false);
        painelBotoesCentro.setLayout(new BoxLayout(painelBotoesCentro, BoxLayout.Y_AXIS));

        JLabel tituloMenu = new JLabel("MENU PRINCIPAL", SwingConstants.CENTER);
        tituloMenu.setForeground(new Color(185, 205, 222));
        tituloMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tituloMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloMenu.setBorder(new EmptyBorder(4, 10, 8, 10));
        painelBotoesCentro.add(tituloMenu);

        adicionarBotaoMenu(painelBotoesCentro, "Visão geral", "inicio", AZUL);
        adicionarBotaoMenu(painelBotoesCentro, "Estoque", "estoque", AZUL);
        adicionarBotaoMenu(painelBotoesCentro, "Cadastro de peças", "cadastro", VERDE);
        adicionarBotaoMenu(painelBotoesCentro, "Entrada de materiais", "entrada", DOURADO);
        adicionarBotaoMenu(painelBotoesCentro, "Saída de materiais", "saida", VERMELHO);
        adicionarBotaoMenu(painelBotoesCentro, "Relatórios", "relatorios", AZUL);
        adicionarBotaoMenu(painelBotoesCentro, "Leitor QR Code (Câmera)", "qrcode", new Color(41, 128, 185));

        JButton btnConectarCelular = new JButton("Conectar Celular (QR)");
        estilizarBotao(btnConectarCelular, new Color(46, 134, 193));
        btnConectarCelular.setPreferredSize(new Dimension(195, 35));
        btnConectarCelular.setMinimumSize(new Dimension(195, 35));
        btnConectarCelular.setMaximumSize(new Dimension(195, 35));
        btnConectarCelular.setBorder(new EmptyBorder(5, 8, 5, 8));
        btnConectarCelular.addActionListener(e -> {
            new TelaQRCode().setVisible(true);
        });

        JPanel linhaCelular = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        linhaCelular.setOpaque(false);
        linhaCelular.setAlignmentX(Component.CENTER_ALIGNMENT);
        linhaCelular.setMaximumSize(new Dimension(225, 35));
        linhaCelular.setPreferredSize(new Dimension(225, 35));
        linhaCelular.add(btnConectarCelular);
        painelBotoesCentro.add(linhaCelular);

        JPanel espacoCelular = new JPanel();
        espacoCelular.setOpaque(false);
        espacoCelular.setMaximumSize(new Dimension(225, 5));
        espacoCelular.setPreferredSize(new Dimension(225, 5));
        painelBotoesCentro.add(espacoCelular);

        adicionarBotaoMenu(painelBotoesCentro, "Meu Perfil", "perfil", AZUL);

        JPanel containerCentro = new JPanel(new GridBagLayout());
        containerCentro.setOpaque(false);
        containerCentro.add(painelBotoesCentro);
        menu.add(containerCentro, BorderLayout.CENTER);

        // 3. RODAPÉ: Autoria InfinityCode e SENAI em Português
        JPanel painelRodape = new JPanel();
        painelRodape.setOpaque(false);
        painelRodape.setLayout(new BoxLayout(painelRodape, BoxLayout.Y_AXIS));

        JButton btnVoltarLogin = new JButton("VOLTAR AO LOGIN");
        estilizarBotao(btnVoltarLogin, VERMELHO);
        btnVoltarLogin.setPreferredSize(new Dimension(195, 34));
        btnVoltarLogin.setMaximumSize(new Dimension(195, 34));
        btnVoltarLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVoltarLogin.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(this, "Deseja sair e voltar ao login?", "Encerrar Sessão", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
            }
        });

        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelVoltar.setOpaque(false);
        painelVoltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelVoltar.setMaximumSize(new Dimension(225, 36));
        painelVoltar.add(btnVoltarLogin);
        painelRodape.add(painelVoltar);

        JLabel rodape = new JLabel("<html><div style='text-align:center;'>Criado por: InfinityCode<br>SENAI - Desenvolvimento de Sistemas</div></html>", SwingConstants.CENTER);
        rodape.setForeground(new Color(225, 238, 248));
        rodape.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rodape.setAlignmentX(Component.CENTER_ALIGNMENT);
        rodape.setBorder(new EmptyBorder(8, 10, 10, 10));
        painelRodape.add(rodape);

        menu.add(painelRodape, BorderLayout.SOUTH);

        return menu;
    }

    private void adicionarBotaoMenu(JPanel menu, String nome, String tela, Color cor) {
        JButton botao = new JButton(nome);
        estilizarBotao(botao, cor);

        botao.setPreferredSize(new Dimension(195, 35));
        botao.setMinimumSize(new Dimension(195, 35));
        botao.setMaximumSize(new Dimension(195, 35));
        botao.setHorizontalAlignment(SwingConstants.CENTER);
        botao.setBorder(new EmptyBorder(5, 8, 5, 8));

        botao.addActionListener(e -> {
            recarregarTudoDoBanco();
            layout.show(conteudo, tela);
        });

        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        linhaBotao.setOpaque(false);
        linhaBotao.setAlignmentX(Component.CENTER_ALIGNMENT);
        linhaBotao.setMaximumSize(new Dimension(225, 35));
        linhaBotao.setPreferredSize(new Dimension(225, 35));
        linhaBotao.add(botao);
        menu.add(linhaBotao);

        JPanel espaco = new JPanel();
        espaco.setOpaque(false);
        espaco.setMaximumSize(new Dimension(225, 5));
        espaco.setPreferredSize(new Dimension(225, 5));
        menu.add(espaco);
    }

    private JPanel criarDashboard() {
        JPanel pagina = criarPagina("Painel de Almoxarifado", "Visão geral, indicadores em tempo real e alertas de estoque crítico (MySQL)");

        JPanel centro = new JPanel(new BorderLayout(15, 20));
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel indicadores = new JPanel(new GridLayout(1, 4, 15, 0));
        indicadores.setOpaque(false);

        totalProdutos = new JLabel("0");
        totalUnidades = new JLabel("0");
        totalBaixo = new JLabel("0");
        totalValorDashboard = new JLabel("R$ 0,00");

        indicadores.add(criarCartao("PRODUTOS CADASTRADOS", totalProdutos, AZUL));
        indicadores.add(criarCartao("UNIDADES EM ESTOQUE", totalUnidades, VERDE));
        indicadores.add(criarCartao("ALERTA: ESTOQUE BAIXO", totalBaixo, VERMELHO));
        indicadores.add(criarCartao("VALOR TOTAL EM ESTOQUE", totalValorDashboard, DOURADO));

        centro.add(indicadores, BorderLayout.NORTH);

        JPanel painelInferior = new JPanel(new BorderLayout(15, 15));
        painelInferior.setBackground(BRANCO);
        painelInferior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel topoAlerta = new JPanel(new BorderLayout());
        topoAlerta.setOpaque(false);

        JLabel lblAlerta = new JLabel("Monitoramento de Itens com Estoque Crítico / Reposição Necessária");
        lblAlerta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAlerta.setForeground(AZUL_ESCURO);

        JPanel botoesRapidos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesRapidos.setOpaque(false);

        JButton btnSincronizar = criarBotao("Atualizar MySQL", VERDE, this::recarregarTudoDoBanco);
        btnSincronizar.setPreferredSize(new Dimension(145, 38));

        JButton btnCelularDash = criarBotao("Conectar Celular (QR)", new Color(46, 134, 193), () -> {
            new TelaQRCode().setVisible(true);
        });
        btnCelularDash.setPreferredSize(new Dimension(175, 38));

        JButton btnScanRapido = criarBotao("Ler QR Code", new Color(41, 128, 185), () -> {
            new DialogLeitorWebcam(this, this::processarCodigoLidoGeral).setVisible(true);
        });
        btnScanRapido.setPreferredSize(new Dimension(140, 38));

        JButton btnTestarAlertas = criarBotao("Alertas / Log", VERMELHO, () -> {
            verificarEDispararAlertasEstoque(true);
        });
        btnTestarAlertas.setPreferredSize(new Dimension(135, 38));

        botoesRapidos.add(btnSincronizar);
        botoesRapidos.add(btnCelularDash);
        botoesRapidos.add(btnScanRapido);
        botoesRapidos.add(btnTestarAlertas);

        topoAlerta.add(lblAlerta, BorderLayout.WEST);
        topoAlerta.add(botoesRapidos, BorderLayout.EAST);

        String[] colunasDash = {"Código", "Descrição da Peça", "Categoria", "Qtd. Atual", "Mínimo", "Localização", "Status"};
        modeloResumoDashboard = new DefaultTableModel(colunasDash, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabelaDash = new JTable(modeloResumoDashboard);
        estilizarTabela(tabelaDash);

        painelInferior.add(topoAlerta, BorderLayout.NORTH);
        painelInferior.add(new JScrollPane(tabelaDash), BorderLayout.CENTER);

        centro.add(painelInferior, BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel criarEstoque() {
        JPanel pagina = criarPagina("Consulta de Estoque", "Pesquise por texto ou aponte o QR Code para a webcam para localizar peças");

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel barraBusca = new JPanel(new BorderLayout(12, 0));
        barraBusca.setBackground(BRANCO);
        barraBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblBusca = new JLabel("Pesquisar Peça:");
        lblBusca.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBusca.setForeground(AZUL_ESCURO);

        pesquisa = criarCampoTexto();

        pesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarTabela(); }
            public void removeUpdate(DocumentEvent e) { atualizarTabela(); }
            public void changedUpdate(DocumentEvent e) { atualizarTabela(); }
        });

        JPanel acoesBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoesBusca.setOpaque(false);

        JButton btnWebcamBusca = criarBotao("Buscar via Webcam", new Color(41, 128, 185), () -> {
            new DialogLeitorWebcam(this, codigoLido -> {
                pesquisa.setText(codigoLido);
                atualizarTabela();
            }).setVisible(true);
        });
        btnWebcamBusca.setPreferredSize(new Dimension(175, 36));

        JButton btnExcluir = criarBotao("Excluir Selecionado", VERMELHO, this::excluirProdutoSelecionado);
        btnExcluir.setPreferredSize(new Dimension(170, 36));

        acoesBusca.add(btnWebcamBusca);
        acoesBusca.add(btnExcluir);

        barraBusca.add(lblBusca, BorderLayout.WEST);
        barraBusca.add(pesquisa, BorderLayout.CENTER);
        barraBusca.add(acoesBusca, BorderLayout.EAST);

        String[] colunas = {"ID", "Código", "Descrição", "Categoria", "Qtd.", "Mín.", "Localização", "Custo Unit.", "Situação"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(modelo);
        estilizarTabela(tabela);

        centro.add(barraBusca, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel criarCadastro() {
        JPanel pagina = criarPagina("Cadastro de Peças e Materiais", "Registre novos itens diretamente no banco de dados MySQL");

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(25, 45, 25, 45));

        JPanel cardForm = new JPanel(new BorderLayout(15, 20));
        cardForm.setBackground(BRANCO);
        cardForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(28, 32, 28, 32)
        ));

        JPanel grid = new JPanel(new GridLayout(4, 2, 22, 16));
        grid.setOpaque(false);

        codigo = criarCampoTexto();
        descricao = criarCampoTexto();
        comboCategoria = new JComboBox<>();
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboCategoria.setBackground(Color.WHITE);
        comboCategoria.setForeground(TEXTO);
        
        quantidade = criarCampoTexto();
        minimo = criarCampoTexto();
        localizacao = criarCampoTexto();
        custo = criarCampoTexto();

        JPanel painelCodigoCam = new JPanel(new BorderLayout(8, 0));
        painelCodigoCam.setOpaque(false);
        painelCodigoCam.add(codigo, BorderLayout.CENTER);

        JButton btnLerCamCadastro = criarBotao("QR Webcam", new Color(41, 128, 185), () -> {
            new DialogLeitorWebcam(this, codigoLido -> codigo.setText(codigoLido)).setVisible(true);
        });
        btnLerCamCadastro.setPreferredSize(new Dimension(115, 34));
        painelCodigoCam.add(btnLerCamCadastro, BorderLayout.EAST);

        JPanel espacoVazio = new JPanel();
        espacoVazio.setOpaque(false);

        grid.add(criarCampoComLabel("Código da Peça / QR Code *:", painelCodigoCam));
        grid.add(criarCampoComLabel("Descrição da Peça *:", descricao));
        grid.add(criarCampoComLabel("Categoria *:", comboCategoria));
        grid.add(criarCampoComLabel("Localização / Prateleira:", localizacao));
        grid.add(criarCampoComLabel("Quantidade Inicial *:", quantidade));
        grid.add(criarCampoComLabel("Estoque Mínimo (Alerta) *:", minimo));
        grid.add(criarCampoComLabel("Preço de Custo (R$):", custo));
        grid.add(espacoVazio);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rodape.setOpaque(false);

        JButton btnLimpar = criarBotao("Limpar Campos", AZUL, this::limparCamposCadastro);
        JButton btnSalvar = criarBotao("SALVAR PEÇA NO MYSQL", VERDE, this::salvarNovoProduto);
        btnSalvar.setPreferredSize(new Dimension(230, 43));

        rodape.add(btnLimpar);
        rodape.add(btnSalvar);

        cardForm.add(grid, BorderLayout.CENTER);
        cardForm.add(rodape, BorderLayout.SOUTH);

        centro.add(cardForm, BorderLayout.NORTH);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel criarMovimentacao(boolean isEntrada) {
        JPanel pagina = criarPagina(
                isEntrada ? "Entrada de Materiais" : "Saída de Materiais",
                isEntrada ? "Registre compras e reposições vinculadas ao Fornecedor (salvo no MySQL)"
                          : "Registre saídas e entregas vinculadas ao Cliente (salvo no MySQL)"
        );

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(20, 25, 20, 25));

        MovimentacaoUI ui = new MovimentacaoUI();
        if (isEntrada) telaEntrada = ui;
        else telaSaida = ui;

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(BRANCO);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(18, 22, 18, 22)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ui.comboProdutos = new JComboBox<>();
        ui.comboProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ui.comboProdutos.setBackground(Color.WHITE);
        ui.comboProdutos.setForeground(TEXTO);

        ui.campoQtd = criarCampoTexto();
        ui.campoParceiro = criarCampoTexto();
        ui.campoObs = criarCampoTexto();

        JButton btnQrMov = criarBotao("QR Webcam", new Color(41, 128, 185), () -> {
            new DialogLeitorWebcam(this, codigoLido -> selecionarProdutoNoComboPorCodigo(ui.comboProdutos, codigoLido)).setVisible(true);
        });
        btnQrMov.setPreferredSize(new Dimension(115, 36));

        JButton btnConfirmar = criarBotao(
                isEntrada ? "CONFIRMAR ENTRADA" : "CONFIRMAR SAÍDA",
                isEntrada ? VERDE : VERMELHO,
                () -> registrarMovimentacao(isEntrada, ui)
        );
        btnConfirmar.setPreferredSize(new Dimension(180, 38));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.65; gbc.gridwidth = 2;
        formCard.add(criarCampoComLabel("Selecione o Produto *:", ui.comboProdutos), gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15; gbc.gridwidth = 1;
        formCard.add(criarCampoComLabel("Leitura Óptica:", btnQrMov), gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.20; gbc.gridwidth = 1;
        formCard.add(criarCampoComLabel("Quantidade *:", ui.campoQtd), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.40; gbc.gridwidth = 1;
        formCard.add(criarCampoComLabel(isEntrada ? "Fornecedor *:" : "Cliente *:", ui.campoParceiro), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.40; gbc.gridwidth = 2;
        formCard.add(criarCampoComLabel(isEntrada ? "Nota Fiscal / Observação:" : "Ordem de Serviço / Observação:", ui.campoObs), gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.20; gbc.gridwidth = 1;
        formCard.add(criarCampoComLabel(" ", btnConfirmar), gbc);

        String[] colsHist = {
            "Data / Hora",
            "Código",
            "Produto",
            "Quantidade",
            isEntrada ? "Fornecedor / Detalhes" : "Cliente / Detalhes",
            "Responsável"
        };

        ui.modeloHistorico = new DefaultTableModel(colsHist, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabHist = new JTable(ui.modeloHistorico);
        estilizarTabela(tabHist);

        centro.add(formCard, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabHist), BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel criarRelatorios() {
        JPanel pagina = criarPagina("Relatórios Gerenciais", "Consolidado financeiro, inventário completo e exportação de dados");

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel topoCards = new JPanel(new GridLayout(1, 4, 15, 0));
        topoCards.setOpaque(false);

        relatorioTotalProdutos = new JLabel("0");
        relatorioTotalUnidades = new JLabel("0");
        relatorioEstoqueBaixo = new JLabel("0");
        relatorioValorTotal = new JLabel("R$ 0,00");

        topoCards.add(criarCartao("ITENS DISTINTOS", relatorioTotalProdutos, AZUL));
        topoCards.add(criarCartao("TOTAL DE PEÇAS", relatorioTotalUnidades, VERDE));
        topoCards.add(criarCartao("ITENS EM ALERTA", relatorioEstoqueBaixo, VERMELHO));
        topoCards.add(criarCartao("PATRIMÔNIO EM ESTOQUE", relatorioValorTotal, DOURADO));

        String[] colsRel = {"Código", "Descrição", "Categoria", "Qtd.", "Mín.", "Custo Unit.", "Subtotal", "Status"};
        modeloRelatorio = new DefaultTableModel(colsRel, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaRelatorio = new JTable(modeloRelatorio);
        estilizarTabela(tabelaRelatorio);

        JPanel rodapeAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        rodapeAcoes.setOpaque(false);

        JButton btnExportarTxt = criarBotao("Exportar Relatório (.TXT)", AZUL_ESCURO, this::exportarRelatorioArquivo);
        btnExportarTxt.setPreferredSize(new Dimension(220, 40));
        rodapeAcoes.add(btnExportarTxt);

        centro.add(topoCards, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);
        centro.add(rodapeAcoes, BorderLayout.SOUTH);

        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel criarTelaQrCode() {
        JPanel pagina = criarPagina("Leitor de QR Code", "Utilize a câmera ao vivo (Webcam), carregue uma imagem ou conecte o celular");

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(BRANCO);
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(35, 35, 35, 35)
        ));

        JLabel instrucao = new JLabel("Escolha o modo de leitura ou conexão QR Code:");
        instrucao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        instrucao.setForeground(AZUL_ESCURO);
        instrucao.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        painelBotoes.setOpaque(false);

        JButton btnWebcam = criarBotao(
                "ABRIR CÂMERA AO VIVO (WEBCAM)",
                new Color(41, 128, 185),
                () -> new DialogLeitorWebcam(this, this::processarCodigoLidoGeral).setVisible(true)
        );
        btnWebcam.setPreferredSize(new Dimension(250, 48));

        JButton btnCarregar = criarBotao(
                "CARREGAR IMAGEM DO PC",
                VERDE,
                this::lerQrCodeDeArquivo
        );
        btnCarregar.setPreferredSize(new Dimension(220, 48));

        JButton btnConectarCel = criarBotao(
                "CONECTAR CELULAR (QR)",
                AZUL_ESCURO,
                () -> new TelaQRCode().setVisible(true)
        );
        btnConectarCel.setPreferredSize(new Dimension(220, 48));

        painelBotoes.add(btnWebcam);
        painelBotoes.add(btnCarregar);
        painelBotoes.add(btnConectarCel);

        resultadoQrLabel = new JLabel("Aguardando leitura de QR Code...");
        resultadoQrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        resultadoQrLabel.setForeground(TEXTO);
        resultadoQrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelCentral.add(instrucao);
        painelCentral.add(Box.createVerticalStrut(25));
        painelCentral.add(painelBotoes);
        painelCentral.add(Box.createVerticalStrut(30));
        painelCentral.add(resultadoQrLabel);

        centro.add(painelCentral, BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private void lerQrCodeDeArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            try {
                BufferedImage imagem = ImageIO.read(arquivo);
                if (imagem == null) {
                    mensagem("O arquivo selecionado não é uma imagem válida.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BufferedImageLuminanceSource fonte = new BufferedImageLuminanceSource(imagem);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(fonte));
                Result resultadoQr = new MultiFormatReader().decode(bitmap);

                processarCodigoLidoGeral(resultadoQr.getText().trim());

            } catch (Exception e) {
                mensagem("Não foi possível decodificar o QR Code desta imagem.", "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processarCodigoLidoGeral(String codigoLido) {
        recarregarTudoDoBanco();
        if (resultadoQrLabel != null) {
            resultadoQrLabel.setText("<html><b>Último Código Lido:</b> " + codigoLido + "</html>");
        }

        Produto encontrado = null;
        for (Produto p : produtos) {
            if (p.codigo.equalsIgnoreCase(codigoLido)) {
                encontrado = p;
                break;
            }
        }

        if (encontrado != null) {
            String status = encontrado.quantidade <= encontrado.minimo ? "ALERTA: ESTOQUE BAIXO!" : "Estoque Normal";
            mensagem("Produto localizado no banco MySQL!\n\n"
                    + "Código: " + encontrado.codigo + "\n"
                    + "Descrição: " + encontrado.descricao + "\n"
                    + "Quantidade Atual: " + encontrado.quantidade + " unid.\n"
                    + "Localização: " + encontrado.localizacao + "\n"
                    + "Status: " + status,
                    "QR Code Reconhecido",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            int opcao = JOptionPane.showConfirmDialog(this,
                    "Código lido: " + codigoLido + "\nNenhuma peça cadastrada com este código.\n\nDeseja ir para a tela de cadastro com este código preenchido?",
                    "Código Não Cadastrado",
                    JOptionPane.YES_NO_OPTION);
            if (opcao == JOptionPane.YES_OPTION) {
                codigo.setText(codigoLido);
                layout.show(conteudo, "cadastro");
            }
        }
    }

    private JPanel criarPerfil() {
        JPanel pagina = criarPagina("Perfil do Usuário", "Informações da conta ativa no sistema");

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(AZUL_CLARO);
        centro.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(BRANCO);
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(40, 40, 40, 40)
        ));

        JLabel iconePerfil = new JLabel("👤", SwingConstants.CENTER);
        iconePerfil.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        iconePerfil.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNomeUsuario = new JLabel("Usuário Conectado: " + usuarioLogado);
        lblNomeUsuario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNomeUsuario.setForeground(AZUL_ESCURO);
        lblNomeUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCargo = new JLabel("Banco de Dados: MySQL (tdi_almoxarifado) - Conectado");
        lblCargo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCargo.setForeground(VERDE);
        lblCargo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblHorario = new JLabel("Sessão iniciada em: " + horarioLogin);
        lblHorario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHorario.setForeground(TEXTO);
        lblHorario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnSairConta = criarBotao("SAIR DA CONTA (LOGIN)", VERMELHO, () -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
        });
        btnSairConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelCentral.add(iconePerfil);
        painelCentral.add(Box.createVerticalStrut(15));
        painelCentral.add(lblNomeUsuario);
        painelCentral.add(Box.createVerticalStrut(8));
        painelCentral.add(lblCargo);
        painelCentral.add(Box.createVerticalStrut(8));
        painelCentral.add(lblHorario);
        painelCentral.add(Box.createVerticalStrut(30));
        painelCentral.add(btnSairConta);

        centro.add(painelCentral, BorderLayout.CENTER);
        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    private void salvarNovoProduto() {
        String cod = codigo.getText().trim();
        String desc = descricao.getText().trim();
        String loc = localizacao.getText().trim().isEmpty() ? "Almoxarifado Central" : localizacao.getText().trim();
        CategoriaItem catItem = (CategoriaItem) comboCategoria.getSelectedItem();
        int idCat = (catItem != null) ? catItem.id : 1;

        if (cod.isEmpty() || desc.isEmpty() || quantidade.getText().trim().isEmpty() || minimo.getText().trim().isEmpty()) {
            mensagem("Preencha os campos obrigatórios (*): Código, Descrição, Quantidade e Mínimo.", "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int qtd = Integer.parseInt(quantidade.getText().trim());
            int min = Integer.parseInt(minimo.getText().trim());
            double valCusto = custo.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(custo.getText().trim().replace(",", "."));

            if (qtd < 0 || min < 0 || valCusto < 0) {
                mensagem("Os valores numéricos não podem ser negativos.", "Valor Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO produtos (codigo_peca, descricao, id_categoria, quantidade_estoque, estoque_minimo, localizacao_prateleira, preco_custo) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = BancoTDI.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, cod);
                stmt.setString(2, desc);
                stmt.setInt(3, idCat);
                stmt.setInt(4, qtd);
                stmt.setInt(5, min);
                stmt.setString(6, loc);
                stmt.setDouble(7, valCusto);
                stmt.executeUpdate();
            }

            limparCamposCadastro();
            recarregarTudoDoBanco();

            mensagem("Peça salva permanentemente no banco MySQL!", "Cadastro Concluído", JOptionPane.INFORMATION_MESSAGE);

            if (qtd <= min) {
                Produto pTemp = new Produto(0, cod, desc, catItem != null ? catItem.nome : "Geral", qtd, min, loc, valCusto);
                dispararAlertaIndividual(pTemp);
            }

        } catch (NumberFormatException ex) {
            mensagem("Verifique os valores digitados em Quantidade, Mínimo e Preço de Custo.", "Erro de Formatação", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException sqlEx) {
            mensagem("Erro ao salvar no MySQL (verifique se o código da peça já existe):\n" + sqlEx.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCamposCadastro() {
        codigo.setText("");
        descricao.setText("");
        quantidade.setText("");
        minimo.setText("");
        localizacao.setText("");
        custo.setText("");
    }

    private void excluirProdutoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mensagem("Selecione uma peça na tabela para excluir.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProd = Integer.parseInt(modelo.getValueAt(linha, 0).toString());
        String codSelecionado = modelo.getValueAt(linha, 1).toString();

        int conf = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir permanentemente a peça " + codSelecionado + " do banco MySQL?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM produtos WHERE id_produto = ?";
            try (Connection conn = BancoTDI.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idProd);
                stmt.executeUpdate();
                recarregarTudoDoBanco();
            } catch (SQLException ex) {
                mensagem("Não foi possível excluir a peça (ela pode possuir movimentações vinculadas):\n" + ex.getMessage(),
                        "Erro ao Excluir", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registrarMovimentacao(boolean isEntrada, MovimentacaoUI ui) {
        if (ui.comboProdutos.getSelectedItem() == null) {
            mensagem("Nenhum produto selecionado.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String parceiro = ui.campoParceiro.getText().trim();
        if (parceiro.isEmpty()) {
            mensagem(isEntrada ? "Informe o nome do Fornecedor." : "Informe o nome do Cliente.",
                    "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            ui.campoParceiro.requestFocus();
            return;
        }

        String selecionado = ui.comboProdutos.getSelectedItem().toString();
        String codProduto = selecionado.split(" - ")[0].trim();

        Produto alvo = null;
        for (Produto p : produtos) {
            if (p.codigo.equals(codProduto)) {
                alvo = p;
                break;
            }
        }

        if (alvo == null) return;

        try {
            int qtdMov = Integer.parseInt(ui.campoQtd.getText().trim());
            if (qtdMov <= 0) {
                mensagem("Informe uma quantidade maior que zero.", "Valor Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isEntrada && qtdMov > alvo.quantidade) {
                mensagem("Saldo insuficiente em estoque! Disponível: " + alvo.quantidade + " unid.", "Saldo Insuficiente", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int novoSaldo = isEntrada ? (alvo.quantidade + qtdMov) : (alvo.quantidade - qtdMov);
            String obs = ui.campoObs.getText().trim();
            String motivoCompleto = (isEntrada ? "Fornecedor: " : "Cliente: ") + parceiro
                    + (obs.isEmpty() ? "" : " | Obs: " + obs);

            String sqlUpdate = "UPDATE produtos SET quantidade_estoque = ? WHERE id_produto = ?";
            String sqlMov = "INSERT INTO movimentacoes (id_produto, tipo, quantidade, responsavel, motivo) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = BancoTDI.conectar()) {
                try (PreparedStatement stUp = conn.prepareStatement(sqlUpdate)) {
                    stUp.setInt(1, novoSaldo);
                    stUp.setInt(2, alvo.id);
                    stUp.executeUpdate();
                }
                try (PreparedStatement stMov = conn.prepareStatement(sqlMov)) {
                    stMov.setInt(1, alvo.id);
                    stMov.setString(2, isEntrada ? "ENTRADA" : "SAIDA");
                    stMov.setInt(3, qtdMov);
                    stMov.setString(4, usuarioLogado);
                    stMov.setString(5, motivoCompleto);
                    stMov.executeUpdate();
                }
            }

            ui.campoQtd.setText("");
            ui.campoParceiro.setText("");
            ui.campoObs.setText("");

            recarregarTudoDoBanco();

            mensagem((isEntrada ? "Entrada do fornecedor '" + parceiro + "'" : "Saída para o cliente '" + parceiro + "'")
                    + " salva no MySQL com sucesso!",
                    "Movimentação Concluída", JOptionPane.INFORMATION_MESSAGE);

            if (!isEntrada && novoSaldo <= alvo.minimo) {
                alvo.quantidade = novoSaldo;
                dispararAlertaIndividual(alvo);
            }

        } catch (NumberFormatException ex) {
            mensagem("Digite um número inteiro válido para a quantidade.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException sqlEx) {
            mensagem("Erro ao registrar movimentação no MySQL:\n" + sqlEx.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecionarProdutoNoComboPorCodigo(JComboBox<String> combo, String codigoLido) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.toUpperCase().startsWith(codigoLido.toUpperCase() + " -")) {
                combo.setSelectedIndex(i);
                mensagem("Produto selecionado via QR Code:\n" + item, "Leitura Concluída", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        mensagem("Produto com código '" + codigoLido + "' não encontrado na lista.", "Não Encontrado", JOptionPane.WARNING_MESSAGE);
    }

    private void dispararAlertaIndividual(Produto p) {
        String msg = "ALERTA DE ESTOQUE CRÍTICO!\n"
                + "Peça: " + p.codigo + " - " + p.descricao + "\n"
                + "Saldo Atual: " + p.quantidade + " | Mínimo Exigido: " + p.minimo;

        JOptionPane.showMessageDialog(this, msg, "Reposição Necessária - InfinityCode / TDI", JOptionPane.WARNING_MESSAGE);
        enviarNotificacaoWindows("TDI Almoxarifado - Estoque Baixo",
                p.codigo + " (" + p.descricao + ") atingiu " + p.quantidade + " unid.");
        registrarLogEstoque(p);
    }

    private void verificarEDispararAlertasEstoque(boolean exibirMensagemSeNormal) {
        recarregarTudoDoBanco();
        List<Produto> criticos = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.quantidade <= p.minimo) {
                criticos.add(p);
                registrarLogEstoque(p);
            }
        }

        if (!criticos.isEmpty()) {
            StringBuilder sb = new StringBuilder("Os seguintes itens estão com estoque baixo ou zerado:\n\n");
            for (Produto p : criticos) {
                sb.append("• ").append(p.codigo).append(" - ").append(p.descricao)
                  .append(" (Atual: ").append(p.quantidade).append(" / Mín: ").append(p.minimo).append(")\n");
            }
            sb.append("\nO registro foi salvo em 'log_estoque.txt'.");

            enviarNotificacaoWindows("TDI Almoxarifado - Alerta Geral",
                    criticos.size() + " item(ns) precisam de reposição imediata!");
            JOptionPane.showMessageDialog(this, sb.toString(), "Alerta de Estoque Crítico", JOptionPane.WARNING_MESSAGE);
        } else if (exibirMensagemSeNormal) {
            mensagem("Todos os produtos estão acima do nível mínimo de estoque.", "Estoque Saudável", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void enviarNotificacaoWindows(String titulo, String texto) {
        try {
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage(new byte[0]);
                TrayIcon trayIcon = new TrayIcon(image, "TDI Almoxarifado");
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
                trayIcon.displayMessage(titulo, texto, TrayIcon.MessageType.WARNING);
            }
        } catch (Exception ignored) {
        }
    }

    private void registrarLogEstoque(Produto p) {
        try (FileWriter fw = new FileWriter("log_estoque.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            String dataHora = LocalDateTime.now().format(formatoData);
            pw.printf("[%s] ALERTA ESTOQUE BAIXO | Usuário: %s | Código: %s | Peça: %s | Atual: %d | Mínimo: %d%n",
                    dataHora, usuarioLogado, p.codigo, p.descricao, p.quantidade, p.minimo);
        } catch (Exception ignored) {
        }
    }

    private void exportarRelatorioArquivo() {
        try (FileWriter fw = new FileWriter("relatorio_almoxarifado_tdi.txt");
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("=========================================================================");
            pw.println("TDI MÁQUINAS AGRÍCOLAS - RELATÓRIO GERAL DE ALMOXARIFADO (MYSQL)");
            pw.println("Desenvolvido por InfinityCode Dev Team");
            pw.println("Gerado em: " + LocalDateTime.now().format(formatoData) + " | Operador: " + usuarioLogado);
            pw.println("=========================================================================\n");

            for (Produto p : produtos) {
                String status = p.quantidade <= p.minimo ? "[CRÍTICO]" : "[NORMAL]";
                pw.printf("%-10s | %-32s | Qtd: %-4d | Mín: %-4d | Unit: %s | %s%n",
                        p.codigo, p.descricao, p.quantidade, p.minimo, formatoMoeda.format(p.custo), status);
            }

            mensagem("Relatório exportado com sucesso para 'relatorio_almoxarifado_tdi.txt'!",
                    "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mensagem("Erro ao gerar arquivo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabela() {
        if (modelo == null) return;
        modelo.setRowCount(0);

        String filtro = (pesquisa != null) ? pesquisa.getText().trim().toLowerCase() : "";

        for (Produto p : produtos) {
            if (!filtro.isEmpty()) {
                boolean match = p.codigo.toLowerCase().contains(filtro)
                        || p.descricao.toLowerCase().contains(filtro)
                        || p.categoria.toLowerCase().contains(filtro);
                if (!match) continue;
            }

            String situacao = (p.quantidade <= p.minimo) ? "ESTOQUE BAIXO" : "NORMAL";
            modelo.addRow(new Object[]{
                p.id, p.codigo, p.descricao, p.categoria,
                p.quantidade, p.minimo, p.localizacao, formatoMoeda.format(p.custo), situacao
            });
        }
    }

    private void atualizarIndicadores() {
        int qtdProdutos = produtos.size();
        int somaUnidades = 0;
        int qtdBaixo = 0;
        double valorTotal = 0.0;

        if (modeloResumoDashboard != null) {
            modeloResumoDashboard.setRowCount(0);
        }

        for (Produto p : produtos) {
            somaUnidades += p.quantidade;
            valorTotal += (p.quantidade * p.custo);
            if (p.quantidade <= p.minimo) {
                qtdBaixo++;
                if (modeloResumoDashboard != null) {
                    modeloResumoDashboard.addRow(new Object[]{
                        p.codigo, p.descricao, p.categoria, p.quantidade, p.minimo, p.localizacao, "REPOSIÇÃO URGENTE"
                    });
                }
            }
        }

        if (totalProdutos != null) totalProdutos.setText(String.valueOf(qtdProdutos));
        if (totalUnidades != null) totalUnidades.setText(String.valueOf(somaUnidades));
        if (totalBaixo != null) totalBaixo.setText(String.valueOf(qtdBaixo));
        if (totalValorDashboard != null) totalValorDashboard.setText(formatoMoeda.format(valorTotal));
    }

    private void atualizarRelatorio() {
        if (modeloRelatorio == null) return;
        modeloRelatorio.setRowCount(0);

        int somaUnidades = 0;
        int qtdBaixo = 0;
        double valorTotal = 0.0;

        for (Produto p : produtos) {
            double subtotal = p.quantidade * p.custo;
            somaUnidades += p.quantidade;
            valorTotal += subtotal;
            boolean baixo = p.quantidade <= p.minimo;
            if (baixo) qtdBaixo++;

            modeloRelatorio.addRow(new Object[]{
                p.codigo, p.descricao, p.categoria, p.quantidade, p.minimo,
                formatoMoeda.format(p.custo), formatoMoeda.format(subtotal),
                baixo ? "ESTOQUE BAIXO" : "OK"
            });
        }

        if (relatorioTotalProdutos != null) relatorioTotalProdutos.setText(String.valueOf(produtos.size()));
        if (relatorioTotalUnidades != null) relatorioTotalUnidades.setText(String.valueOf(somaUnidades));
        if (relatorioEstoqueBaixo != null) relatorioEstoqueBaixo.setText(String.valueOf(qtdBaixo));
        if (relatorioValorTotal != null) relatorioValorTotal.setText(formatoMoeda.format(valorTotal));
    }

    private void atualizarTabelasMovimentacao() {
        atualizarUmaMovimentacao(telaEntrada, "ENTRADA");
        atualizarUmaMovimentacao(telaSaida, "SAIDA");
    }

    private void atualizarUmaMovimentacao(MovimentacaoUI ui, String tipoMov) {
        if (ui == null) return;
        if (ui.comboProdutos != null) {
            ui.comboProdutos.removeAllItems();
            for (Produto p : produtos) {
                ui.comboProdutos.addItem(p.codigo + " - " + p.descricao + " (Saldo: " + p.quantidade + ")");
            }
        }
        if (ui.modeloHistorico != null) {
            ui.modeloHistorico.setRowCount(0);
            String sql = "SELECT DATE_FORMAT(m.data_movimentacao, '%d/%m/%Y %H:%i:%s') AS data_fmt, "
                       + "p.codigo_peca, p.descricao, m.quantidade, m.motivo, m.responsavel "
                       + "FROM movimentacoes m "
                       + "INNER JOIN produtos p ON m.id_produto = p.id_produto "
                       + "WHERE m.tipo = ? "
                       + "ORDER BY m.id_movimentacao DESC";

            try (Connection conn = BancoTDI.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tipoMov);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ui.modeloHistorico.addRow(new Object[]{
                            rs.getString("data_fmt"),
                            rs.getString("codigo_peca"),
                            rs.getString("descricao"),
                            rs.getInt("quantidade"),
                            rs.getString("motivo"),
                            rs.getString("responsavel")
                        });
                    }
                }
            } catch (SQLException ignored) {
            }
        }
    }

    private void estilizarBotao(JButton botao, Color cor) {
        botao.setBackground(cor);
        botao.setForeground(BRANCO);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JButton criarBotao(String nome, Color cor, Runnable acao) {
        JButton botao = new JButton(nome);
        estilizarBotao(botao, cor);
        botao.setPreferredSize(new Dimension(180, 42));
        botao.addActionListener(e -> acao.run());
        return botao;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(Color.WHITE);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 202, 218)),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return campo;
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent componente) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(textoLabel);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AZUL_ESCURO);
        p.add(lbl, BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    private void estilizarTabela(JTable tab) {
        tab.setRowHeight(32);
        tab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tab.setSelectionBackground(new Color(214, 230, 245));
        tab.setSelectionForeground(AZUL_ESCURO);
        tab.setGridColor(new Color(225, 232, 239));

        JTableHeader header = tab.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 36));
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(AZUL_ESCURO);
                c.setForeground(BRANCO);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        tab.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BRANCO : new Color(247, 250, 253));
                    c.setForeground(TEXTO);

                    Object lastCol = table.getValueAt(row, table.getColumnCount() - 1);
                    if (lastCol != null && (lastCol.toString().contains("BAIXO") || lastCol.toString().contains("URGENTE"))) {
                        if (column == table.getColumnCount() - 1) {
                            c.setForeground(new Color(180, 40, 40));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    }
                }
                return c;
            }
        });
    }

    private JPanel criarCabecalho(String titulo, String descricao) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BRANCO);
        painel.setBorder(new EmptyBorder(20, 27, 18, 27));

        JPanel esquerda = new JPanel();
        esquerda.setOpaque(false);
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloLabel.setForeground(AZUL_ESCURO);

        JLabel descricaoLabel = new JLabel(descricao);
        descricaoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descricaoLabel.setForeground(new Color(94, 116, 137));

        esquerda.add(tituloLabel);
        esquerda.add(Box.createVerticalStrut(4));
        esquerda.add(descricaoLabel);

        JPanel direita = new JPanel();
        direita.setOpaque(false);
        direita.setLayout(new BoxLayout(direita, BoxLayout.Y_AXIS));

        JLabel administrador = new JLabel("OPERADOR: " + usuarioLogado.toUpperCase());
        administrador.setFont(new Font("Segoe UI", Font.BOLD, 11));
        administrador.setForeground(AZUL_ESCURO);
        administrador.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel empresa = new JLabel("TDI MÁQUINAS AGRÍCOLAS");
        empresa.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        empresa.setForeground(AZUL);
        empresa.setAlignmentX(Component.RIGHT_ALIGNMENT);

        direita.add(administrador);
        direita.add(Box.createVerticalStrut(4));
        direita.add(empresa);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(direita, BorderLayout.EAST);
        return painel;
    }

    private JPanel criarPagina(String titulo, String descricao) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(AZUL_CLARO);
        pagina.add(criarCabecalho(titulo, descricao), BorderLayout.NORTH);
        return pagina;
    }

    private JPanel criarCartao(String titulo, JLabel valor, Color cor) {
        JPanel cartao = new JPanel(new BorderLayout(0, 8));
        cartao.setBackground(BRANCO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 229)),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(new Color(94, 116, 137));

        valor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valor.setForeground(cor);

        cartao.add(lblTitulo, BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);
        return cartao;
    }

    private void mensagem(String texto, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, texto, titulo, tipo);
    }

    static class Produto {
        int id;
        String codigo;
        String descricao;
        String categoria;
        int quantidade;
        int minimo;
        String localizacao;
        double custo;

        public Produto(int id, String codigo, String descricao, String categoria, int quantidade, int minimo, String localizacao, double custo) {
            this.id = id;
            this.codigo = codigo;
            this.descricao = descricao;
            this.categoria = categoria;
            this.quantidade = quantidade;
            this.minimo = minimo;
            this.localizacao = localizacao;
            this.custo = custo;
        }
    }

    static class CategoriaItem {
        int id;
        String nome;

        public CategoriaItem(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        @Override
        public String toString() {
            return nome;
        }
    }

    static class MovimentacaoUI {
        JComboBox<String> comboProdutos;
        JTextField campoQtd;
        JTextField campoParceiro;
        JTextField campoObs;
        DefaultTableModel modeloHistorico;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}