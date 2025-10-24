/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cuartouta;

//import reportes.ReporteEstudiantes;
//import reportes.VisorPDF;
import java.awt.Color;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.*;

import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author DELL
 */
public class Principal extends javax.swing.JFrame {

    private String rol;
    // instancia actual para acceder al JDesktopPane desde clases estáticas (VisorPDF)
    private static Principal currentInstance;

    /**
     * Creates new form Principal
     */
    public Principal(String rol) {
        initComponents();
        this.setExtendedState(this.MAXIMIZED_BOTH);
        jdskPrincipal.setBackground(Color.decode("#001F3F"));

        this.rol = rol;
        currentInstance = this;
        configurarAtajos();
        
        // Atajo global: Ctrl + F4 o Esc para cerrar el internal frame activo
jdskPrincipal.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "cerrarInternal");

jdskPrincipal.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrarInternal");

jdskPrincipal.getActionMap().put("cerrarInternal", new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        javax.swing.JInternalFrame frameActivo = jdskPrincipal.getSelectedFrame();
        if (frameActivo != null) {
            try {
                frameActivo.setClosed(true); // cierra correctamente el frame
            } catch (java.beans.PropertyVetoException ex) {
                // si el frame no permite ser cerrado
                System.out.println("No se pudo cerrar el frame: " + ex.getMessage());
            }
        }
    }
});


    }

    private void configurarAtajos() {
        // Ctrl + E → Reporte Estudiantes
        jmniEstudiantesCurso.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK
        ));

        // Ctrl + C → Reporte Cursos
        jmniEstudiantes.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK
        ));

        // Ctrl + I → Reporte Inscripciones
        jmniDistribucionGenero.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK
        ));

        // Ctrl + 1 → Ventana Estudiantes
        jMenuItem1.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK
        ));

        // Ctrl + 2 → Ventana Cursos
        jMenuItem3.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK
        ));

        // Ctrl + 2 → Ventana Inscripciones
        jMenuItem4.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK
        ));

        // Ctrl + Q → Salir
        jMenuItem2.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK
        ));
    }

    public static javax.swing.JDesktopPane getDesktopPane() {
        return currentInstance == null ? null : currentInstance.jdskPrincipal;
    }

    /**
     * Add a JInternalFrame to the desktop and position it so it does not
     * overlap any existing internal frames. It will try a grid of positions and
     * fall back to a simple cascade if none free is found.
     */
    private void addInternalFrameNoOverlap(javax.swing.JInternalFrame frame) {
        javax.swing.JDesktopPane desktop = this.jdskPrincipal;

        // Ensure the frame has a sensible size before positioning
        if (frame.getWidth() <= 0 || frame.getHeight() <= 0) {
            frame.pack();
        }

        int step = 30;
        int startX = 10;
        int startY = 10;

        int desktopW = Math.max(1, desktop.getWidth());
        int desktopH = Math.max(1, desktop.getHeight());

        int maxX = Math.max(startX, desktopW - frame.getWidth());
        int maxY = Math.max(startY, desktopH - frame.getHeight());

        boolean placed = false;
        for (int y = startY; y <= maxY; y += step) {
            for (int x = startX; x <= maxX; x += step) {
                Rectangle candidate = new Rectangle(x, y, frame.getWidth(), frame.getHeight());
                boolean intersects = false;
                for (javax.swing.JInternalFrame f : desktop.getAllFrames()) {
                    if (f == frame) {
                        continue;
                    }
                    if (f.getBounds().intersects(candidate)) {
                        intersects = true;
                        break;
                    }
                }
                if (!intersects) {
                    frame.setLocation(x, y);
                    placed = true;
                    break;
                }
            }
            if (placed) {
                break;
            }
        }

        if (!placed) {
            // fallback: cascade based on number of existing frames
            int offset = desktop.getAllFrames().length * step;
            int x = (startX + offset) % Math.max(1, desktopW - frame.getWidth());
            int y = (startY + offset) % Math.max(1, desktopH - frame.getHeight());
            frame.setLocation(Math.max(startX, x), Math.max(startY, y));
        }

        desktop.add(frame);
        frame.setVisible(true);
    }

    /**
     * Static wrapper so other classes can add an internal frame without
     * overlapping existing frames. Safe to call from static contexts.
     */
    public static void openInternalFrame(javax.swing.JInternalFrame frame) {
        if (currentInstance != null) {
            currentInstance.addInternalFrameNoOverlap(frame);
        } else {
            // No desktop available; fall back to default behavior: just show the frame
            frame.setLocation(50, 50);
            frame.setVisible(true);
        }
    }

    private boolean isAdmin() {
        return rol != null && rol.equalsIgnoreCase("administrador");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jdskPrincipal = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jmnuStudents = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jmniEstudiantesCurso = new javax.swing.JMenuItem();
        jmniEstudiantes = new javax.swing.JMenuItem();
        jmniDistribucionGenero = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jdskPrincipalLayout = new javax.swing.GroupLayout(jdskPrincipal);
        jdskPrincipal.setLayout(jdskPrincipalLayout);
        jdskPrincipalLayout.setHorizontalGroup(
            jdskPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jdskPrincipalLayout.setVerticalGroup(
            jdskPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );

        jmnuStudents.setText("Ventanas");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/students.png"))); // NOI18N
        jMenuItem1.setText("Estudiantes");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jmnuStudents.add(jMenuItem1);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/course.png"))); // NOI18N
        jMenuItem3.setText("Cursos");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jmnuStudents.add(jMenuItem3);

        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/registration.png"))); // NOI18N
        jMenuItem4.setText("Inscripciones");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jmnuStudents.add(jMenuItem4);

        jMenuBar1.add(jmnuStudents);

        jMenu2.setText("Reportes");

        jmniEstudiantesCurso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/guys.png"))); // NOI18N
        jmniEstudiantesCurso.setText("Estudiantes por curso");
        jmniEstudiantesCurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmniEstudiantesCursoActionPerformed(evt);
            }
        });
        jMenu2.add(jmniEstudiantesCurso);

        jmniEstudiantes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/courses.png"))); // NOI18N
        jmniEstudiantes.setText("Estudiantes");
        jmniEstudiantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmniEstudiantesActionPerformed(evt);
            }
        });
        jMenu2.add(jmniEstudiantes);

        jmniDistribucionGenero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/list.png"))); // NOI18N
        jmniDistribucionGenero.setText("Genero por curso");
        jmniDistribucionGenero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmniDistribucionGeneroActionPerformed(evt);
            }
        });
        jMenu2.add(jmniDistribucionGenero);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Salir");

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit.png"))); // NOI18N
        jMenuItem2.setText("Salir");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jdskPrincipal)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jdskPrincipal)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if (isAdmin()) {
            Estudiantes a = new Estudiantes();
            addInternalFrameNoOverlap(a);
        } else {
            JOptionPane.showMessageDialog(this, "No tiene permisos");
        }

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "DESEA SALIR", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (isAdmin()) {
            Inscripcion b = new Inscripcion();
            addInternalFrameNoOverlap(b);
        } else {
            JOptionPane.showMessageDialog(this, "No tiene permisos");
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (isAdmin()) {
            Cursos c = new Cursos();
            addInternalFrameNoOverlap(c);
        } else {
            JOptionPane.showMessageDialog(this, "No tiene permisos");
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jmniEstudiantesCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmniEstudiantesCursoActionPerformed
        try {
            CursoNombre a = new CursoNombre("src\\reportesGestion\\students_by_course.jrxml");
            addInternalFrameNoOverlap(a);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ERROR INTERNO DEL SERVIDOR");
        }

    }//GEN-LAST:event_jmniEstudiantesCursoActionPerformed

    private void jmniEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmniEstudiantesActionPerformed
        try {
            Conexion con = new Conexion();
            Connection cc = con.conectar();

            // Compilar y llenar el reporte
            JasperReport reporte = JasperCompileManager.compileReport("src\\reportesGestion\\students_with_courses.jrxml");
            JasperPrint imprimir = JasperFillManager.fillReport(reporte, null, cc);
if (imprimir.getPages().isEmpty()) {
    JOptionPane.showMessageDialog(this, "No hay datos para mostrar en el reporte.", "Reporte vacío", JOptionPane.INFORMATION_MESSAGE);
    return; // 🔹 Salir sin crear el frame
}

            // Crear visor interno (JRViewer)
            net.sf.jasperreports.swing.JRViewer visor = new net.sf.jasperreports.swing.JRViewer(imprimir);

            // Crear un JInternalFrame para mostrar el reporte dentro del desktop pane
            javax.swing.JInternalFrame frameReporte = new javax.swing.JInternalFrame(
                    "Reporte de Estudiantes",
                    true, // closable
                    true, // resizable
                    true, // maximizable
                    true // iconifiable
            );

            frameReporte.setSize(800, 600);
            frameReporte.setVisible(true);
            frameReporte.setContentPane(visor);

            // Agregar el frame al DesktopPane principal
            Principal.openInternalFrame(frameReporte);

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jmniEstudiantesActionPerformed

    private void jmniDistribucionGeneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmniDistribucionGeneroActionPerformed
        try {
            CursoNombre a = new CursoNombre("src\\reportesGestion\\course_students_chart.jrxml");
            addInternalFrameNoOverlap(a);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ERROR INTERNO DEL SERVIDOR");
        }
    }//GEN-LAST:event_jmniDistribucionGeneroActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JDesktopPane jdskPrincipal;
    private javax.swing.JMenuItem jmniDistribucionGenero;
    private javax.swing.JMenuItem jmniEstudiantes;
    private javax.swing.JMenuItem jmniEstudiantesCurso;
    private javax.swing.JMenu jmnuStudents;
    // End of variables declaration//GEN-END:variables
}
