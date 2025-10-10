package reportes;

import cuartouta.Conexion;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReporteEstudiantes {

    public static String generarPDF() throws Exception {
        String ruta = "reporte_estudiantes.pdf"; // se guarda en la carpeta del proyecto
        java.io.File outFile = new java.io.File(ruta).getAbsoluteFile();
        PDDocument document = new PDDocument();

        // Page and layout settings
        PDRectangle pageSize = PDRectangle.A4;
        float margin = 50;
        float yStart = pageSize.getHeight() - margin;
        float tableWidth = pageSize.getWidth() - 2 * margin;
        float yPosition = yStart;
        float rowHeight = 20;
        float tableTopY = yPosition - 40; // space for title

        // Column widths (sum must be <= tableWidth)
        float[] colWidths = new float[] { 100, 120, 120, 140, 90 }; // ced, nom, ape, dir, tel
        String[] headers = new String[] { "Cédula", "Nombre", "Apellido", "Dirección", "Teléfono" };

        Conexion conexion = new Conexion();
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM estudiante ORDER BY est_nombre");
             ResultSet rs = ps.executeQuery()) {

            PDPage page = new PDPage(pageSize);
            PDPageContentStream content = new PDPageContentStream(document, page);
            document.addPage(page);

            // Title
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(margin + tableWidth / 2 - 80, yPosition);
            content.showText("Reporte de Estudiantes");
            content.endText();

            yPosition = tableTopY;

            // Draw table header
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            float nextX = margin;
            for (int i = 0; i < headers.length; i++) {
                content.beginText();
                content.newLineAtOffset(nextX + 2, yPosition);
                content.showText(headers[i]);
                content.endText();
                nextX += colWidths[i];
            }

            // Draw header line
            content.moveTo(margin, yPosition - 2);
            content.lineTo(margin + tableWidth, yPosition - 2);
            content.stroke();

            yPosition -= rowHeight;
            content.setFont(PDType1Font.HELVETICA, 11);

            while (rs.next()) {
                String cedula = rs.getString("est_cedula");
                String nombre = rs.getString("est_nombre");
                String apellido = rs.getString("est_apellido");
                String direccion = rs.getString("est_direccion");
                String telefono = rs.getString("est_telefono");

                // Truncar campos largos
                if (direccion == null) direccion = "";
                if (telefono == null) telefono = "";
                if (direccion.length() > 40) direccion = direccion.substring(0, 37) + "...";
                if (nombre == null) nombre = "";
                if (nombre.length() > 30) nombre = nombre.substring(0, 27) + "...";

                nextX = margin;
                String[] row = new String[] { cedula, nombre, apellido, direccion, telefono };
                for (int i = 0; i < row.length; i++) {
                    content.beginText();
                    content.newLineAtOffset(nextX + 2, yPosition);
                    content.showText(row[i] == null ? "" : row[i]);
                    content.endText();
                    nextX += colWidths[i];
                }

                // draw row separator
                content.moveTo(margin, yPosition - 2);
                content.lineTo(margin + tableWidth, yPosition - 2);
                content.stroke();

                yPosition -= rowHeight;

                // new page if needed
                if (yPosition < margin + 20) {
                    content.close();
                    page = new PDPage(pageSize);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    yPosition = yStart - 40;
                    content.setFont(PDType1Font.HELVETICA, 11);
                }
            }

            content.close();
        }

        document.save(outFile);
        document.close();

        return outFile.getAbsolutePath();
    }
}
