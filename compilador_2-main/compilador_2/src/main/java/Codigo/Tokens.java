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
        ArrayList<String> tokens = new ArrayList<>();
        // Actualiza el patrón para incluir los saltos de línea en el manejo de tokens
        Pattern pattern = Pattern.compile("//[^\\n]*|/\\*.*?\\*/|\\n+|[a-zA-Z][a-zA-Z0-9_$]*|\\d+(\\.\\d+)?|\\;|\\=|\\+\\+|\\-\\-|\\+|\\-|\\)|\\(",Pattern.DOTALL);
        Matcher matcher = pattern.matcher(texto);
        
        int numeroLinea = 1;
        int inicioLinea = 0;
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
                inicioLinea = matcher.end(); // Ajusta el inicio de la línea después de los saltos de línea
                continue; // No añade saltos de línea al array de tokens
            }

            // Calcula la columna como la posición de inicio del matcher menos el inicio de la línea actual
            int columna = matcher.start() - inicioLinea + 1;
            tokens.add(token + "°" + numeroLinea + "¬" + columna);
        }

        return tokens;
    }
}
