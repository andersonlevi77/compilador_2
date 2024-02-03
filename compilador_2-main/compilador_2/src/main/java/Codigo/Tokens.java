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
        // Incluye patrones para identificadores (letras y números, comenzando con una letra),
        // números, palabras reservadas específicas, y símbolos.
        Pattern pattern = Pattern.compile("[a-zA-Z]+\\d*|[0-9.]+|\\;|\\=|\\+\\+|\\-\\-|\\+|\\-|\\n+");
        Matcher matcher = pattern.matcher(texto);

        int numeroLinea = 1;
        int inicioLinea = 0;

        while (matcher.find()) {
            String token = matcher.group();
            // Calcula la columna como la posición de inicio del matcher menos el inicio de la línea actual.
            int columna = matcher.start() - inicioLinea + 1;
            tokens.add(token + "°" + numeroLinea + "¬" + columna);

            // Si el token es un salto de línea, actualiza el número de línea y el inicio de la línea.
            if (token.equals("\n")) {
                numeroLinea++;
                inicioLinea = matcher.end();
            }
        }

        return tokens;
    }
}
