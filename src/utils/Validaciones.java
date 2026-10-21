package utils;

import java.text.Normalizer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Validaciones {

    // Normaliza texto para quitar tildes y convertir a minúsculas en las búsquedas
    public static String normalizarTexto(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    // Filtro para formatear automáticamente la cédula dominicana (000-0000000-0)
    public static class CedulaDocumentFilter extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            if ((text == null || text.isEmpty()) && length == 1 && offset > 0 && currentText.charAt(offset) == '-') {
                offset--;
                length = 2;
            }
            String digits = (currentText.substring(0, offset) + (text == null ? "" : text) + currentText.substring(offset + length)).replaceAll("\\D", "");
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }
            
            String formatted = digits;
            if (digits.length() > 3 && digits.length() <= 10) {
                formatted = digits.substring(0, 3) + "-" + digits.substring(3);
            } else if (digits.length() > 10) {
                formatted = digits.substring(0, 3) + "-" + digits.substring(3, 10) + "-" + digits.substring(10);
            }
            
            fb.replace(0, currentText.length(), formatted, attrs);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            replace(fb, offset, length, "", null);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attrs) throws BadLocationException {
            replace(fb, offset, 0, string, attrs);
        }
    }
}
