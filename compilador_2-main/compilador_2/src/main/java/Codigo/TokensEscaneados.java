package Codigo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author juani
 */
public class TokensEscaneados {
    public ArrayList<String> separacionTokens(String texto) {
        ArrayList<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\+\\+|\\-\\-|[a-zA-Z]+|\\d+|[;=\\+\\-,)(]|\\n");
        Matcher matcher = pattern.matcher(texto);

        int numeroLinea = 1;
        while (matcher.find()) {
            String token = matcher.group();
            if ("\n".equals(token)) {
                numeroLinea++;
            } else {
                tokens.add(token + "°" + numeroLinea); // Añade el número de línea al token
            }
        }
        return tokens;
    }
}
