/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cuartouta;

import java.awt.Color;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DELL
 */
public class Cursos extends javax.swing.JInternalFrame {

    /**
     * Creates new form cursos
     */
    public Cursos() {
        initComponents();
        //Color color = Color.decode("#FFFFFF");
        //jPanel1.setBackground(color);
        // permitir cerrar la ventana interna con el botón X
        this.setClosable(true);
        initLogic();
    }

    // --- lógica similar a Estudiantes ---
    private java.sql.Connection cc;
    private Conexion con = new Conexion();
    private DefaultTableModel table;

    private void initLogic() {
        table = new DefaultTableModel();
        this.jtblCursos.setModel(table);
        table.addColumn("NOMBRE");
        getData("");
        cargardatos();
        // búsqueda dinámica en jtxtNombreCurso
        jtxtNombreCurso.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                getData(jtxtNombreCurso.getText().trim());
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                getData(jtxtNombreCurso.getText().trim());
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                getData(jtxtNombreCurso.getText().trim());
            }
        });
    }

    public void saveCourse() {
        try {
            if (jtxtCurso.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Es obligatorio el nombre del curso");
                jtxtCurso.requestFocus();
                return;
            }
            Connection cc = con.conectar();
            String sql = "INSERT INTO cursos (nombre) VALUES (?)";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, jtxtCurso.getText().trim());
            int n = ps.executeUpdate();
            if (n > 0) {
                JOptionPane.showMessageDialog(this, "Curso registrado correctamente.");
                getData("");
                jtxtCurso.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public void getData(String filter) {
        try {
            table.setRowCount(0);
            Connection cc = con.conectar();
            String sql = "SELECT nombre FROM cursos" + (filter != null && !filter.isEmpty() ? " WHERE nombre LIKE ?" : "");
            PreparedStatement ps = cc.prepareStatement(sql);
            if (filter != null && !filter.isEmpty()) {
                ps.setString(1, "%" + filter + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] row = new String[1];
                row[0] = rs.getString("nombre");
                table.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public void deleteCourse() {
        try {
            int row = jtblCursos.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un curso para eliminar");
                return;
            }
            String nombre = jtblCursos.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(null, "ESTAS SEGURO DE ELIMINAR", "ELIMINAR CURSO", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_NO_OPTION) {
                Connection cc = con.conectar();
                String sql = "DELETE FROM cursos WHERE nombre = ?";
                PreparedStatement ps = cc.prepareStatement(sql);
                ps.setString(1, nombre);
                int n = ps.executeUpdate();
                if (n > 0) {
                    JOptionPane.showMessageDialog(this, "SE ELIMINO CORRECTAMENTE");
                    getData("");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public void updateCourse() {
        try {
            int row = jtblCursos.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un curso para editar");
                return;
            }
            String oldName = jtblCursos.getValueAt(row, 0).toString();
            String newName = jtxtNombreCurso.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre nuevo no puede estar vacío");
                return;
            }
            Connection cc = con.conectar();
            String sql = "UPDATE cursos SET nombre = ? WHERE nombre = ?";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setString(2, oldName);
            int n = ps.executeUpdate();
            if (n > 0) {
                JOptionPane.showMessageDialog(this, "SE ACTUALIZÓ CORRECTAMENTE");
                getData("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public void cargardatos() {
        jtblCursos.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (jtblCursos.getSelectedRow() != -1) {
                    int row = jtblCursos.getSelectedRow();
                    jtxtNombreCurso.setText(jtblCursos.getValueAt(row, 0).toString());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblCursos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtCurso = new javax.swing.JTextField();
        jbtnGuardar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jtxtNombreCurso = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jtblCursos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jtblCursos);

        jLabel1.setText("Gestion de Cursos");

        jLabel2.setText("Nombre");

        jtxtCurso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCursoKeyTyped(evt);
            }
        });

        jbtnGuardar.setText("Guardar");
        jbtnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnGuardarActionPerformed(evt);
            }
        });

        jLabel3.setText("Buscar Curso");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(165, 165, 165)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(38, 38, 38)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxtCurso)
                                    .addComponent(jtxtNombreCurso, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
                                .addGap(52, 52, 52)
                                .addComponent(jbtnGuardar)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnGuardar))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxtNombreCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        saveCourse();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jtxtCursoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCursoKeyTyped
        char a = evt.getKeyChar();
        if (!Character.isAlphabetic(a)) {
            evt.consume();
        }
    }//GEN-LAST:event_jtxtCursoKeyTyped

    // agregar handlers para editar y eliminar si deseas (NetBeans GUI builder puede haber botones adicionales)

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JTable jtblCursos;
    private javax.swing.JTextField jtxtCurso;
    private javax.swing.JTextField jtxtNombreCurso;
    // End of variables declaration//GEN-END:variables
}
