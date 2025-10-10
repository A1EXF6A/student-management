/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cuartouta;

import java.sql.*;

/**
 *
 * @author DELL
 */
public class Inscripcion extends javax.swing.JInternalFrame  {

    /**
     * Creates new form Inscripcion
     */
    public Inscripcion() {
        initComponents();
        initLogic();
    }

    // lógica similar a Estudiantes
    private Conexion con = new Conexion();
    private javax.swing.table.DefaultTableModel table;

    private void initLogic() {
        table = new javax.swing.table.DefaultTableModel();
        this.jtblDatosCursos.setModel(table);
        table.addColumn("CURSO");
        table.addColumn("CEDULA");
        table.addColumn("ESTUDIANTE");

        loadEstudiantes();
        loadCursos();
        loadInscripciones("");
        // búsqueda dinámica en el campo jtxtBuscarEstudiante
        jtxtBuscarEstudiante.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadInscripciones(jtxtBuscarEstudiante.getText().trim()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadInscripciones(jtxtBuscarEstudiante.getText().trim()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadInscripciones(jtxtBuscarEstudiante.getText().trim()); }
        });
    }

    private void loadEstudiantes() {
        try {
            jcbxEstudiantes.removeAllItems();
            Connection cc = con.conectar();
            String sql = "SELECT est_cedula, est_nombre, est_apellido FROM estudiante";
            java.sql.Statement st = cc.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String ced = rs.getString("est_cedula");
                String nom = rs.getString("est_nombre");
                String ape = rs.getString("est_apellido");
                jcbxEstudiantes.addItem(ced + "-" + nom+"-"+ape);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error cargando estudiantes: " + ex.getMessage());
        }
    }

    private void loadCursos() {
        try {
            jcbxCursos.removeAllItems();
            Connection cc = con.conectar();
            String sql = "SELECT cursoid, nombre FROM cursos";
            java.sql.Statement st = cc.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                // Sólo añadimos el nombre al combobox (no el id)
                String nom = rs.getString("nombre");
                jcbxCursos.addItem(nom);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error cargando cursos: " + ex.getMessage());
        }
    }

    private void loadInscripciones(String cedFilter) {
        try {
            table.setRowCount(0);
            Connection cc = con.conectar();
            String sql = "SELECT c.nombre AS curso, e.est_cedula AS cedula, e.est_nombre AS nombre "
                    + "FROM estudiante_curso ec "
                    + "JOIN estudiante e ON ec.est_cedula = e.est_cedula "
                    + "JOIN cursos c ON ec.cursoid = c.cursoid";
            java.sql.PreparedStatement ps;
            if (cedFilter != null && !cedFilter.isEmpty()) {
                sql += " WHERE e.est_cedula LIKE ?";
                ps = cc.prepareStatement(sql);
                ps.setString(1, "%" + cedFilter + "%");
            } else {
                ps = cc.prepareStatement(sql);
            }
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] row = new String[3];
                row[0] = rs.getString("curso");
                row[1] = rs.getString("cedula");
                row[2] = rs.getString("nombre");
                table.addRow(row);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error cargando inscripciones: " + ex.getMessage());
        }
    }

    private void saveInscripcion() {
        try {
            if (jcbxEstudiantes.getItemCount() == 0 || jcbxCursos.getItemCount() == 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "No hay estudiantes o cursos para inscribir");
                return;
            }
            String estItem = (String) jcbxEstudiantes.getSelectedItem();
            String cursoNombre = (String) jcbxCursos.getSelectedItem();
            if (estItem == null || estItem.trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un estudiante válido");
                return;
            }
            // estItem was populated as "cedula-nombre-apellido" (no spaces).
            // Extract cedula robustly: take substring before the first '-' if present.
            String ced;
            int dashPos = estItem.indexOf('-');
            if (dashPos > 0) {
                ced = estItem.substring(0, dashPos).trim();
            } else {
                ced = estItem.trim();
            }
            // Basic validation: cedula length should be reasonable (avoid inserting huge strings)
            if (ced.length() == 0 || ced.length() > 50) {
                javax.swing.JOptionPane.showMessageDialog(this, "Cédula de estudiante inválida: '" + ced + "'");
                return;
            }
            Connection cc = con.conectar();
            // Buscar cursoid por nombre (usamos PreparedStatement por si el nombre tiene caracteres especiales)
            String q = "SELECT cursoid FROM cursos WHERE nombre = ? LIMIT 1";
            java.sql.PreparedStatement qst = cc.prepareStatement(q);
            qst.setString(1, cursoNombre);
            java.sql.ResultSet qrs = qst.executeQuery();
            if (!qrs.next()) {
                javax.swing.JOptionPane.showMessageDialog(this, "No se encontró el curso seleccionado.");
                return;
            }
            int cursoid = qrs.getInt("cursoid");
            // Verify that the estudiante exists (to avoid FK error)
            String checkEst = "SELECT est_cedula FROM estudiante WHERE est_cedula = ?";
            java.sql.PreparedStatement psCheck = cc.prepareStatement(checkEst);
            psCheck.setString(1, ced);
            java.sql.ResultSet rsCheck = psCheck.executeQuery();
            if (!rsCheck.next()) {
                javax.swing.JOptionPane.showMessageDialog(this, "El estudiante seleccionado no existe en la base de datos: " + ced);
                return;
            }

            // Get column max size for est_cedula to avoid data truncation errors
            int maxSize = -1;
            try {
                java.sql.DatabaseMetaData md = cc.getMetaData();
                java.sql.ResultSet cols = md.getColumns(null, null, "estudiante", "est_cedula");
                if (cols.next()) {
                    maxSize = cols.getInt("COLUMN_SIZE");
                }
                if (cols != null) cols.close();
            } catch (Exception mm) {
                // ignore metadata failures, we'll rely on basic validation
            }
            if (maxSize > 0 && ced.length() > maxSize) {
                javax.swing.JOptionPane.showMessageDialog(this, "La cédula es demasiado larga (" + ced.length() + " > " + maxSize + ")");
                return;
            }

            String sql = "INSERT INTO estudiante_curso (est_cedula, cursoid) VALUES (?, ?)";
            java.sql.PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, ced);
            ps.setInt(2, cursoid);
            int n = ps.executeUpdate();
            if (n > 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Inscripción realizada");
                loadInscripciones("");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "La inscripción ya existe o hay un error: " + ex.getMessage());
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al inscribir: " + ex.getMessage());
        }
    }

    private void deleteInscripcion() {
        try {
            int row = jtblDatosCursos.getSelectedRow();
            if (row == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Seleccione una inscripción para eliminar");
                return;
            }
            String ced = jtblDatosCursos.getValueAt(row, 1).toString();
            String curso = jtblDatosCursos.getValueAt(row, 0).toString();
            if (javax.swing.JOptionPane.showConfirmDialog(null, "ESTAS SEGURO DE ELIMINAR", "ELIMINAR INSCRIPCION", javax.swing.JOptionPane.YES_NO_OPTION)
                    == javax.swing.JOptionPane.YES_NO_OPTION) {
                Connection cc = con.conectar();
                String sql = "DELETE ec FROM estudiante_curso ec "
                        + "JOIN cursos c ON ec.cursoid = c.cursoid "
                        + "WHERE ec.est_cedula = ? AND c.nombre = ?";
                java.sql.PreparedStatement ps = cc.prepareStatement(sql);
                ps.setString(1, ced);
                ps.setString(2, curso);
                int n = ps.executeUpdate();
                if (n > 0) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Inscripción eliminada");
                    loadInscripciones("");
                }
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }

    private void searchByCedula(String ced) {
        loadInscripciones(ced);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblDatosCursos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jcbxEstudiantes = new javax.swing.JComboBox<>();
        jcbxCursos = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jtxtBuscarEstudiante = new javax.swing.JTextField();
        jbtnBuscar = new javax.swing.JButton();
        jbtnInscribir = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnNuevo = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jtblDatosCursos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jtblDatosCursos);

        jLabel1.setText("Gestion De Inscripciones");

        jLabel2.setText("Estudiante");

        jLabel3.setText("Curso");

        jLabel4.setText("Buscar Estudiante");

        jtxtBuscarEstudiante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtBuscarEstudianteActionPerformed(evt);
            }
        });

        jbtnBuscar.setText("Buscar");
        jbtnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBuscarActionPerformed(evt);
            }
        });

        jbtnInscribir.setText("Inscribir");
        jbtnInscribir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInscribirActionPerformed(evt);
            }
        });

        jbtnEliminar.setText("Eliminar");
        jbtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEliminarActionPerformed(evt);
            }
        });

        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNuevoActionPerformed(evt);
            }
        });

        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 96, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jcbxEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcbxCursos, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addGap(33, 33, 33)
                        .addComponent(jtxtBuscarEstudiante, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jbtnInscribir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)))
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jcbxEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnNuevo))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jbtnInscribir)
                    .addComponent(jcbxCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnEliminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtBuscarEstudiante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnBuscar))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtBuscarEstudianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtBuscarEstudianteActionPerformed
        searchByCedula(jtxtBuscarEstudiante.getText().trim());
    }//GEN-LAST:event_jtxtBuscarEstudianteActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        // limpiar y recargar
        loadEstudiantes();
        loadCursos();
        loadInscripciones("");
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        // preparar para nueva inscripción
        jcbxEstudiantes.setSelectedIndex(0);
        jcbxCursos.setSelectedIndex(0);
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnInscribirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInscribirActionPerformed
        saveInscripcion();
    }//GEN-LAST:event_jbtnInscribirActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        deleteInscripcion();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBuscarActionPerformed
        searchByCedula(jtxtBuscarEstudiante.getText().trim());
    }//GEN-LAST:event_jbtnBuscarActionPerformed

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnBuscar;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnInscribir;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JComboBox<String> jcbxCursos;
    private javax.swing.JComboBox<String> jcbxEstudiantes;
    private javax.swing.JTable jtblDatosCursos;
    private javax.swing.JTextField jtxtBuscarEstudiante;
    // End of variables declaration//GEN-END:variables
}
