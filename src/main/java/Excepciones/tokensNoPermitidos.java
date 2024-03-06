package Excepciones;

import javax.swing.JOptionPane;

/**
 *
 * @author juani
 */
public class tokensNoPermitidos extends Exception {

    public tokensNoPermitidos() {
        super();
    }

    //constructor que envia un mensaje
    public tokensNoPermitidos(String msg) {
        super(msg);
        JOptionPane.showMessageDialog(null, msg, "ADVERTENCIA",JOptionPane.WARNING_MESSAGE );
    }
}
