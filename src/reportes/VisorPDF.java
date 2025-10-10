package reportes;

import cuartouta.Principal;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class VisorPDF {

    public static void mostrarPDF(String ruta) {
        try {
            PDDocument document = PDDocument.load(new File(ruta));
            PDFRenderer renderer = new PDFRenderer(document);

            int pages = document.getNumberOfPages();
            JPanel panel = new JPanel();
            panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

            for (int i = 0; i < pages; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 120);
                JLabel label = new JLabel(new ImageIcon(image));
                label.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                panel.add(label);
            }

            JScrollPane scroll = new JScrollPane(panel);

            JInternalFrame visor = new JInternalFrame("Reporte de Estudiantes", true, true, true, true);
            visor.getContentPane().add(scroll);
            visor.setSize(800, 600);
            visor.setVisible(true);

            // Agregamos el visor al JDesktopPane de Principal si está disponible
            javax.swing.JDesktopPane desktop = Principal.getDesktopPane();
            if (desktop != null) {
                // Use Principal helper to avoid overlap
                Principal.openInternalFrame(visor);
                try { visor.setSelected(true); } catch (Exception ex) {}
            } else {
                // Si no hay desktop (ejecución fuera del frame), abrir en JFrame
                javax.swing.JFrame frame = new javax.swing.JFrame("Reporte de Estudiantes");
                frame.getContentPane().add(scroll);
                frame.setSize(900, 700);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

            document.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al abrir PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
