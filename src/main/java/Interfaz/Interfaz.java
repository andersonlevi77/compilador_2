package Interfaz;

import Codigo.Tokens;
import Codigo.FiltroArchivos;
import Excepciones.tokensNoPermitidos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Anderson
 */
public class Interfaz extends javax.swing.JFrame {

    DefaultTableModel tabla;
    private String tokenAnterior = null;
    private int lineaTokenAnterior = -1;

    public Interfaz() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Compilador");
        tabla = new DefaultTableModel();
        String[] titulo = new String[]{"No.Fila", "No.Columna", "Nombre", "Simbolo", "Tipo"};
        tabla.setColumnIdentifiers(titulo);
        tblDatos.setModel(tabla);
        tblDatos.setEnabled(false);
    }

    private void EstiloJfilechooser() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void agregarDtTabla(int pos, int col, String nombre, String simbolo, String tipo) {
        tabla.addRow(new Object[]{pos, col, nombre, simbolo, tipo});
    }

    private void limpiarTabla_Datos() {
        DefaultTableModel modeloTabla = (DefaultTableModel) tblDatos.getModel();
        modeloTabla.setRowCount(0);
        //Limpiar areas de texto
        txtDatos.setText(null);
        txtTokens.setText(null);
    }

    private void actualizarTabla() throws tokensNoPermitidos {
        //Obtiene el array de los tokens econtrados
        Tokens reservadasEsc = new Tokens();
        reservadasEsc.detectarCaracteresIncorrectosRW(txtDatos.getText());
        ArrayList<String> listaTokens = reservadasEsc.separacionTokens(txtDatos.getText());

        System.out.println("tk: " + listaTokens); //imprimir listaTokens

        //Itera sobre cada token y lo separa por cada separador que encuentre
        for (String tokenLinea : listaTokens) {
            String[] partes = tokenLinea.split("\\°|\\¬"); // Divide el token, el número de línea y número de columna
            System.out.println("tokens: " + Arrays.toString(partes)); //imprime los tokens
            String token = partes[0];
            int numeroLinea = Integer.parseInt(partes[1]);
            int numeroColumna = Integer.parseInt(partes[2]);

            //Identifica posibles excepciones
            if (token.matches(
                    //Números no validos
                    "(?:\\d+\\.\\d+)(?:\\.\\d+)+|\\d+\\.$"
                    //Identificadores no validos
                    + "|(^\\d\\w+)|^[#?]\\w+|\\w+[#?]+")) {

                throw new tokensNoPermitidos("<html> <b>" + token + "</b> no es valido, linea: " + numeroLinea + "</html>");
            }
            //Envia el token, No.linea y No.columnda a los metodos
            Buscar_Palabras_Reservadas(token, numeroLinea, numeroColumna);
            Buscar_Simbolos_Operadores(token, numeroLinea, numeroColumna);
        }
    }

    public void Buscar_Simbolos_Operadores(String token, int numeroLinea, int numeroColumna) {
        //Leer archivo JSON
        JSONParser jsonParser = new JSONParser();
        try (FileReader read = new FileReader("signos.json");) {
            // Leer archivo JSON
            Object obj = jsonParser.parse(read);

            // Lista de objetos JSON
            JSONArray listaObjetos = (JSONArray) obj;
            //System.out.println("JSON: " + listaObjetos);

            // Iterar sobre cada objeto JSON en la lista
            for (Object item : listaObjetos) {
                JSONObject jsonObject = (JSONObject) item;
                // Obtiene el array de simbolos
                JSONArray signosList = (JSONArray) jsonObject.get("simbolos");
                for (Object signo : signosList) {
                    JSONObject simbolo = (JSONObject) signo;
                    //Obtiene el valor de la clave del objeto de simbolos: []
                    String nombre = (String) simbolo.get("nombre");
                    String simb = (String) simbolo.get("simbolo");
                    String tipo = (String) simbolo.get("tipo");
                    //Si encuentra el simbolo en el JSON lo agrega en la tabla
                    if (token.equals(simb)) {
                        agregarDtTabla(numeroLinea, numeroColumna, nombre, simb, tipo);
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(Tokens.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Buscar_Palabras_Reservadas(String token, int numeroLinea, int numeroColumna) throws tokensNoPermitidos {
        boolean encontrado = false; // Bandera para marcar si se encontró el token
        //convertir la reservada a minuscula
        String reservada_minuscula = token.toLowerCase();
        
        // Leer archivo JSON
        JSONParser jsonParser = new JSONParser();
        try (FileReader read = new FileReader("reservadas.json")) {
            // Leer archivo JSON
            Object obj = jsonParser.parse(read);
            JSONArray listaObjetos = (JSONArray) obj;

            // Iterar sobre cada objeto JSON en la lista
            for (Object item : listaObjetos) {
                JSONObject jsonObject = (JSONObject) item;
                JSONArray reservadasList = (JSONArray) jsonObject.get("reservadas");

                for (Object reservada : reservadasList) {
                    JSONObject palabra = (JSONObject) reservada;
                    String nombre = (String) palabra.get("nombre");
                    String palab = (String) palabra.get("palabra");
                    String tipo = (String) palabra.get("tipo");

                    // Convertir la palabra reservada a minúsculas para la comparación
                    String palb_minuscula = palab.toLowerCase();

                    if (reservada_minuscula.equals(palb_minuscula)) {
                        // Mensaje de JOptionPane sobre la corrección
                        if (!token.equals(palab)) {
                            JOptionPane.showMessageDialog(null, "Se ha corregido: '" + token 
                                    + "' a '" + palab + "'", "Corrección", JOptionPane.INFORMATION_MESSAGE);
                        }

                        agregarDtTabla(numeroLinea, numeroColumna, nombre, palab, tipo);
                        encontrado = true; // Marca como encontrado

                        // Comprobar si la palabra reservada actual es consecutiva a otra en la misma línea
                        if (tokenAnterior != null && lineaTokenAnterior == numeroLinea) {
                            throw new tokensNoPermitidos("No se permiten dos palabras reservadas consecutivas en la misma línea: " + numeroLinea);
                        }
                        tokenAnterior = token; // Actualiza el token anterior
                        lineaTokenAnterior = numeroLinea; // Actualiza el número de línea del token anterior
                        break;
                    }
                }
                if (encontrado) {
                    break; // Sale del ciclo si se encuentra la palabra reservada
                }
            }

            // Si después el token no fue encontrado, se maneja como identificador, número o cadena de texto
            if (!encontrado) {
                // Números
                if (token.matches("-?\\b\\d+(\\.\\d+)?\\b")) {
                    agregarDtTabla(numeroLinea, numeroColumna, "Numero", token, "Valor");
                } else if (token.matches("[a-zA-Z][a-zA-Z0-9_$]*")) {
                    // Identificadores
                    agregarDtTabla(numeroLinea, numeroColumna, "Identificador", token, "Id");
                } else if (token.matches("\"[^\\\"]*\"")) {
                    //Cadena de texto
                    agregarDtTabla(numeroLinea, numeroColumna, "Cadena de texto", token, "String");
                }
            }

            // Resetea tokenAnterior si hay un salto de línea
            if (numeroLinea != lineaTokenAnterior) {
                tokenAnterior = null;
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(Tokens.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mostrarTokens() {
        String tokens = "";
        //Itera en cada fila de la tabla
        for (int i = 0; i < tabla.getRowCount(); i++) {
            //Obtiene el valor de cada columna en su respectiva fila
            String simbolo = tabla.getValueAt(i, 3).toString();
            String tipo = tabla.getValueAt(i, 4).toString();

            //Identifica ciertos simbolos para agregarle los '' para que no se confunda con el formato del token
            if (simbolo.matches("\\>|\\<|\\,")) {
                String newSimb = "'" + simbolo + "'";
                tokens += "<" + tipo + " , " + newSimb + ">";
            } else {
                tokens += "<" + tipo + " , " + simbolo + ">";
            }
        }
        //Coloca los token con su respectivo formato
        txtTokens.setText(tokens);
    }

    private void guardarlex() {
        try {
            String contenido = txtTokens.getText();

            // Cambia la ruta y el nombre del archivo
            File fileToSave = new File("Tokens.lex");

            try (FileWriter fileWriter = new FileWriter(fileToSave)) {
                fileWriter.write(contenido);
                JOptionPane.showMessageDialog(null, "El archivo se ha guardado correctamente!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocurrió un error al guardar el archivo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        jLabel1 = new javax.swing.JLabel();
        btnAbrir = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDatos = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDatos = new javax.swing.JTable();
        btnAnalizar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtTokens = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 93, 139));

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 1, 60)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("COMPILADOR");

        btnAbrir.setFont(new java.awt.Font("Roboto Medium", 1, 18)); // NOI18N
        btnAbrir.setForeground(new java.awt.Color(255, 255, 255));
        btnAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-abrir-carpeta-40(1).png"))); // NOI18N
        btnAbrir.setText("Abrir");
        btnAbrir.setBorderPainted(false);
        btnAbrir.setContentAreaFilled(false);
        btnAbrir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrir.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-abrir-carpeta-60.png"))); // NOI18N
        btnAbrir.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-abrir-carpeta-40(1).png"))); // NOI18N
        btnAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Roboto Medium", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-guardar-como-40.png"))); // NOI18N
        btnGuardar.setText("Guardar como");
        btnGuardar.setBorderPainted(false);
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-guardar-como-60.png"))); // NOI18N
        btnGuardar.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-guardar-como-40.png"))); // NOI18N
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        txtDatos.setColumns(20);
        txtDatos.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        txtDatos.setRows(5);
        jScrollPane1.setViewportView(txtDatos);

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Datos Ingresados");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Datos Analizados");

        tblDatos.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        tblDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "No.", "Símbolo", "Nombre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblDatos);
        if (tblDatos.getColumnModel().getColumnCount() > 0) {
            tblDatos.getColumnModel().getColumn(0).setResizable(false);
            tblDatos.getColumnModel().getColumn(1).setResizable(false);
            tblDatos.getColumnModel().getColumn(2).setResizable(false);
        }

        btnAnalizar.setFont(new java.awt.Font("Roboto Medium", 1, 18)); // NOI18N
        btnAnalizar.setForeground(new java.awt.Color(255, 255, 255));
        btnAnalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-ver-50.png"))); // NOI18N
        btnAnalizar.setText("Analizar");
        btnAnalizar.setBorderPainted(false);
        btnAnalizar.setContentAreaFilled(false);
        btnAnalizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAnalizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnalizar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-ver-70.png"))); // NOI18N
        btnAnalizar.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-ver-50.png"))); // NOI18N
        btnAnalizar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAnalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarActionPerformed(evt);
            }
        });

        btnLimpiar.setFont(new java.awt.Font("Roboto Medium", 1, 18)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-limpiar-50.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setContentAreaFilled(false);
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLimpiar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-limpiar-70.png"))); // NOI18N
        btnLimpiar.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-limpiar-50.png"))); // NOI18N
        btnLimpiar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        txtTokens.setColumns(20);
        txtTokens.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        txtTokens.setRows(5);
        jScrollPane2.setViewportView(txtTokens);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(btnAbrir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(119, 119, 119)
                        .addComponent(btnGuardar))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAnalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLimpiar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, 43))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnAbrir)
                        .addComponent(btnGuardar))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(btnAnalizar)
                        .addGap(59, 59, 59)
                        .addComponent(btnLimpiar)))
                .addGap(50, 50, 50)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        EstiloJfilechooser();
        JFileChooser chooser = new JFileChooser();
        //Filtro para buscar archivos
        chooser.setFileFilter(new FiltroArchivos(".java", "Archivo java"));
        chooser.setFileFilter(new FiltroArchivos(".chalk", "CHALK"));

        chooser.showOpenDialog(null);
        File archivo = new File(chooser.getSelectedFile().getAbsolutePath());

        try {
            String ST = new String(Files.readAllBytes(archivo.toPath()));
            txtDatos.setText(ST);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Crea un JFileChooser para seleccionar dónde guardar el archivo
        EstiloJfilechooser();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar");
        //Filtro extensión propia
        fileChooser.setFileFilter(new FiltroArchivos(".chalk", "CHALK"));

        int selection = fileChooser.showSaveDialog(null);

        if (selection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter fileWriter = new FileWriter(fileToSave + ".chalk")) {
                fileWriter.write(txtDatos.getText());
                JOptionPane.showMessageDialog(null, "El archivo se ha guardado correctamente!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocurrió un error al guardar el archivo.");
            }
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnAnalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarActionPerformed
        //Reset de variables
        tokenAnterior = null;
        lineaTokenAnterior = -1;
        tabla.setRowCount(0);

        try {
            actualizarTabla();
            mostrarTokens();
            guardarlex();
        } catch (tokensNoPermitidos ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
            txtTokens.setText(null);
        }
    }//GEN-LAST:event_btnAnalizarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarTabla_Datos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfaz().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnAnalizar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblDatos;
    private javax.swing.JTextArea txtDatos;
    private javax.swing.JTextArea txtTokens;
    // End of variables declaration//GEN-END:variables
}
