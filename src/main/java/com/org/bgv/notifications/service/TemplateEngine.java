package com.org.bgv.notifications.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {

	private static final Pattern VARIABLE_PATTERN =
            Pattern.compile("\\{\\{(.+?)}}");

    public static String render(String template,
                                Map<String, Object> variables) {

        if (template == null || variables == null) {
            return template;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = variables.get(key);

            matcher.appendReplacement(
                    result,
                    value != null
                            ? Matcher.quoteReplacement(value.toString())
                            : ""
            );
        }

        matcher.appendTail(result);
        return result.toString();
    }
	
}
