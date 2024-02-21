package Codigo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author juani
 */
public class Tokens {

    public ArrayList<String> separacionTokens(String texto) {
        //Crea una lista para almacenar los tokens
        ArrayList<String> tokens = new ArrayList<>();
        // Detecta los tokens establecidos a base de una expresión regular
        Pattern pattern = Pattern.compile(
                //comentarios y saltos de línea
                "//[^\\n]*|/\\*.*?\\*/|\\n+"
                //Cadenas de Texto
                + "|\"[^\\\"]*\""
                //combinación de caracteres
                + "|[#?]?\\d*[a-zA-Z_$][\\w$]*[#?]*"
                //combinación de números
                + "|-?\\d+(\\.\\d+)*\\.?"
                //símbolos
                + "|\\;|\\=|\\+\\+|\\-\\-|\\+|\\-|\\)|\\(|\\>|\\<|\\,", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(texto);

        int numeroLinea = 1;
        int numeroColumna = 1; // Inicializa el contador de tokens por línea
        
        while (matcher.find()) {
            String token = matcher.group();

            // Ignora los comentarios para añadirlos al array
            if (token.startsWith("//") || token.startsWith("/*")) {
                // Ajusta el número de línea para comentarios de bloque que contienen saltos de línea
                if (token.startsWith("/*")) {
                    numeroLinea += token.split("\n", -1).length - 1;
                }
                continue; // No agregar comentarios al array
            }

            // Ajuste específico para saltos de línea
            if (token.matches("\\n+")) {
                numeroLinea += token.length(); // Cuenta cada salto de línea individualmente
                numeroColumna = 1; // Restablece el contador si hay un salto de linea
                continue; // No añade saltos de línea al array de tokens
            }

            // Añade separedores entre el numero de linea y columna para despues procesarlos de manera correcta
            tokens.add(token + "°" + numeroLinea + "¬" + numeroColumna);
            numeroColumna++;
        }
        //Envia los tokens
        return tokens;
    }
}
