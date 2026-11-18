
package br.com.tdi.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.Result;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TelaLeitorWebcam extends JDialog {

    private Webcam webcam = null;
    private WebcamPanel painelWebcam = null;

    private volatile boolean executando = true;

    private RetornoLeitura callback;

    public interface RetornoLeitura {
        void aoLerCodigo(String codigo);
    }

    public TelaLeitorWebcam(
            Frame parent,
            RetornoLeitura callback
    ) {

        super(
            parent,
            "Leitor de QR Code / Código de Barras",
            true
        );

        this.callback = callback;

        setLayout(new BorderLayout());

        setSize(550, 470);

        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // LOCALIZAR WEBCAM
        webcam = Webcam.getDefault();

        if (webcam == null) {

            JOptionPane.showMessageDialog(
                parent,
                "Nenhuma webcam foi detectada no computador.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
            );

            executando = false;
            dispose();
            return;
        }

        try {

            webcam.setViewSize(
                WebcamResolution.VGA.getSize()
            );

            // PAINEL DA WEBCAM
            painelWebcam = new WebcamPanel(webcam);

            painelWebcam.setMirrored(true);

            painelWebcam.setFPSDisplayed(false);

            painelWebcam.setFPSLimited(true);

           

            add(
                painelWebcam,
                BorderLayout.CENTER
            );

            // INSTRUÇÕES
            JLabel lbl = new JLabel(
                "Aponte o QR Code ou código de barras para a câmera",
                SwingConstants.CENTER
            );

            lbl.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
            );

            lbl.setForeground(
                new Color(25, 57, 82)
            );

            lbl.setBorder(
                BorderFactory.createEmptyBorder(
                    12, 10, 12, 10
                )
            );

            add(
                lbl,
                BorderLayout.SOUTH
            );

            // INICIAR LEITURA
            iniciarLeitura();

            // FECHAR CÂMERA AO FECHAR A JANELA
            addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e
                    ) {
                        fecharCamera();
                    }

                    @Override
                    public void windowClosed(
                            java.awt.event.WindowEvent e
                    ) {
                        fecharCamera();
                    }
                }
            );

        } catch (Exception e) {

            fecharCamera();

            JOptionPane.showMessageDialog(
                parent,
                "Erro ao iniciar a webcam:\n"
                + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );

            dispose();
        }
    }

    // ==========================================
    // INICIAR LEITURA DO QR CODE
    // ==========================================

    private void iniciarLeitura() {

        Thread thread = new Thread(() -> {

            MultiFormatReader leitor = new MultiFormatReader();

            while (executando) {

                try {

                    Thread.sleep(150);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }

                if (
                    webcam == null ||
                    !webcam.isOpen()
                ) {
                    continue;
                }

                BufferedImage img;

                try {

                    img = webcam.getImage();

                } catch (Exception e) {

                    continue;
                }

                if (img == null) {
                    continue;
                }

                try {

                    LuminanceSource source =
                        new BufferedImageLuminanceSource(img);

                    BinaryBitmap bitmap =
                        new BinaryBitmap(
                            new HybridBinarizer(source)
                        );

                    Result resultado = leitor.decode(bitmap);

                    if (resultado != null) {

                        String texto = resultado.getText();

                        executando = false;

                        SwingUtilities.invokeLater(() -> {

                            fecharCamera();

                            if (callback != null) {

                                callback.aoLerCodigo(texto);
                            }

                            dispose();
                        });

                        break;
                    }

                } catch (Exception e) {

                    // Nenhum código válido encontrado neste quadro.

                } finally {

                    leitor.reset();
                }
            }

        }, "LeitorQRWebcam");

        thread.setDaemon(true);

        thread.start();
    }

    // ==========================================
    // VERIFICAR DISPONIBILIDADE DA CÂMERA
    // ==========================================

    public boolean isCameraDisponivel() {

        return webcam != null &&
               painelWebcam != null;
    }

    // ==========================================
    // FECHAR WEBCAM
    // ==========================================

    private void fecharCamera() {

        executando = false;

        if (webcam != null && webcam.isOpen()) {

            try {

                webcam.close();

            } catch (Exception e) {

                System.err.println(
                    "Erro ao fechar webcam: "
                    + e.getMessage()
                );
            }
        }
    }

    // ==========================================
    // ENCERRAR JANELA
    // ==========================================

    @Override
    public void dispose() {

        fecharCamera();

        super.dispose();
    }
}
