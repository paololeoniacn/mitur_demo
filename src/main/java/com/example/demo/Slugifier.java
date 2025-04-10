package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Slugifier {

    private static final Logger logger = LogManager.getLogger(Slugifier.class);

    public static String slugify(String input) {
        if (input == null) return "";

        String normalized = input;
        normalized = normalized.replaceAll("[àáäÁÀÄÂâÃãÅå]", "a");
        normalized = normalized.replaceAll("[èéÉÈÊêËë]", "e");
        normalized = normalized.replaceAll("[ìíÍÌÎîÏï]", "i");
        normalized = normalized.replaceAll("[ÓóÒÕòōöÖÔôõØø]", "o");
        normalized = normalized.replaceAll("[ÚúÙùÜüÛû]", "u");
        normalized = normalized.replaceAll("[\"”“`‘’,']", "-");
        normalized = normalized.replaceAll("ß", "sss");
        normalized = normalized.replaceAll("[Šš]", "ss");
        normalized = normalized.replaceAll("[¢Çç]", "ccc");
        normalized = normalized.replaceAll("[¥ÝýŸÿ]", "y");
        normalized = normalized.replaceAll("¹", "1");
        normalized = normalized.replaceAll("²", "2");
        normalized = normalized.replaceAll("³", "3");
        normalized = normalized.replaceAll("[Ðð]", "dd");
        normalized = normalized.replaceAll("ƒ", "f");
        normalized = normalized.replaceAll("[Ññ]", "nn");
        normalized = normalized.replaceAll("[Žž]", "zz");

        logger.info("Normalized value after replaceAll: {}", normalized);

        StringBuilder result = new StringBuilder();
        for (char c : normalized.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '-') {
                result.append(c);
            } else {
                result.append('-');
            }
        }

        logger.info("Result after appending '-': {}", result);

        return result.toString()
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "")
                .toLowerCase();
    }
}
