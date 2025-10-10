package reportes;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import cuartouta.Conexion;

public class ReporteInscripciones {

    public static String generarPDF() throws Exception {
        String ruta = "reportes/reporte_inscripciones.pdf";
        File outFile = new File(ruta).getAbsoluteFile();
        outFile.getParentFile().mkdirs();

        PDDocument document = new PDDocument();
        PDRectangle pageSize = PDRectangle.A4;
        float margin = 50;
        float yStart = pageSize.getHeight() - margin;
        float tableWidth = pageSize.getWidth() - 2 * margin;
        float yPosition = yStart;
        float rowHeight = 20;

        float[] colWidths = new float[] { 150, 120, 120 }; // curso, cedula, nombre
        String[] headers = new String[] { "Curso", "Cédula", "Estudiante" };

        Conexion conexion = new Conexion();
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT c.nombre AS curso, e.est_cedula AS cedula, e.est_nombre AS nombre FROM estudiante_curso ec JOIN estudiante e ON ec.est_cedula=e.est_cedula JOIN cursos c ON ec.cursoid=c.cursoid ORDER BY c.nombre");
             ResultSet rs = ps.executeQuery()) {

            PDPage page = new PDPage(pageSize);
            PDPageContentStream content = new PDPageContentStream(document, page);
            document.addPage(page);

            // Title
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(margin + tableWidth / 2 - 80, yPosition);
            content.showText("Reporte de Inscripciones");
            content.endText();

            yPosition -= 40;

            // Header
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            float nextX = margin;
            for (int i = 0; i < headers.length; i++) {
                content.beginText();
                content.newLineAtOffset(nextX + 2, yPosition);
                content.showText(headers[i]);
                content.endText();
                nextX += colWidths[i];
            }
            content.moveTo(margin, yPosition - 2);
            content.lineTo(margin + tableWidth, yPosition - 2);
            content.stroke();

            yPosition -= rowHeight;
            content.setFont(PDType1Font.HELVETICA, 11);

            while (rs.next()) {
                String curso = rs.getString("curso");
                String ced = rs.getString("cedula");
                String nombre = rs.getString("nombre");

                if (curso == null) curso = "";
                if (curso.length() > 40) curso = curso.substring(0, 37) + "...";
                if (nombre == null) nombre = "";
                if (nombre.length() > 30) nombre = nombre.substring(0, 27) + "...";

                nextX = margin;
                String[] row = new String[] { curso, ced, nombre };
                for (int i = 0; i < row.length; i++) {
                    content.beginText();
                    content.newLineAtOffset(nextX + 2, yPosition);
                    content.showText(row[i] == null ? "" : row[i]);
                    content.endText();
                    nextX += colWidths[i];
                }

                content.moveTo(margin, yPosition - 2);
                content.lineTo(margin + tableWidth, yPosition - 2);
                content.stroke();

                yPosition -= rowHeight;

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
