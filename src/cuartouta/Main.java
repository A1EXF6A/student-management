/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cuartouta;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DELL
 */
public class Main {
    public static void main(String[] args) {
        // Reducir logs informativos de PDFBox (font cache u otros) para que no llenen la salida
        try {
            Logger.getLogger("org.apache.pdfbox").setLevel(Level.WARNING);
        } catch (Exception e) {
            // ignore
        }

        Login log = new Login();
        log.setVisible(true);
    }
}
