package it.paskinomercato.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates Italian postal addresses.
 * Delivery is only available in Italy — foreign addresses are rejected.
 * Validation rules:
 *   1. Country code must be "IT" (case-insensitive)
 *   2. CAP must be exactly 5 digits
 *   3. Provincia must be one of the 110 valid Italian province codes
 */
public class IndirizzoItaliaValidator {

    private static final Pattern CAP_PATTERN = Pattern.compile("^\\d{5}$");

    /** All 110 Italian province abbreviations */
    private static final Set<String> PROVINCE_ITALIANE = new HashSet<String>();

    static {
        String[] province = {
            "AG","AL","AN","AO","AP","AQ","AR","AT","AV",
            "BA","BG","BI","BL","BN","BO","BR","BS","BT","BZ",
            "CA","CB","CE","CH","CL","CN","CO","CR","CS","CT","CZ",
            "EN","FC","FE","FG","FI","FM","FR",
            "GE","GO","GR",
            "IM","IS",
            "KR",
            "LC","LE","LI","LO","LT","LU",
            "MB","MC","ME","MI","MN","MO","MS","MT",
            "NA","NO","NU",
            "OR",
            "PA","PC","PD","PE","PG","PI","PN","PO","PR","PT","PU","PV","PZ",
            "RA","RC","RE","RG","RI","RM","RN","RO",
            "SA","SI","SO","SP","SR","SS","SU","SV",
            "TA","TE","TN","TO","TP","TR","TS","TV",
            "UD",
            "VA","VB","VC","VE","VI","VR","VS","VT","VV"
        };
        for (int i = 0; i < province.length; i++) {
            PROVINCE_ITALIANE.add(province[i]);
        }
    }

    /**
     * Validates an address for Italian delivery.
     * @param paese    Country code (must be "IT")
     * @param cap      Postal code (must be 5 digits)
     * @param provincia Province abbreviation (must be a valid Italian province)
     * @return ValidationResult with isValid flag and error message
     */
    public static ValidationResult valida(String paese, String cap, String provincia) {
        if (paese == null || !"IT".equalsIgnoreCase(paese.trim())) {
            return new ValidationResult(false,
                "La consegna è disponibile solo in Italia. / Delivery is only available in Italy.");
        }
        if (cap == null || !CAP_PATTERN.matcher(cap.trim()).matches()) {
            return new ValidationResult(false,
                "Il CAP deve essere composto da 5 cifre. / The postal code must be 5 digits.");
        }
        if (provincia == null || !PROVINCE_ITALIANE.contains(provincia.trim().toUpperCase())) {
            return new ValidationResult(false,
                "Provincia non valida. / Invalid province code.");
        }
        return new ValidationResult(true, null);
    }

    /** Simple result holder */
    public static class ValidationResult {
        private final boolean valid;
        private final String  errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid        = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid()          { return valid; }
        public String  getErrorMessage()  { return errorMessage; }
    }
}
