package com.conc.analysis.chatbot.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MathService {

    // Detect a simple math expression at the start or after 'calc:' or 'compute:'
    private static final Pattern MATH_PATTERN = Pattern.compile(
            "(?:^|\\b(calc|compute)\\s*:\\s*)([0-9+\\-*/().^%\\s]+)$",
            Pattern.CASE_INSENSITIVE);

    // Also accept plain expression if it looks purely mathematical
    private static final Pattern PURE_EXPR = Pattern.compile("^[0-9+\\-*/().^%\\s]+$");

    public static Double tryEvaluate(String userMessage) {
        if (userMessage == null) return null;
        String trimmed = userMessage.trim();

        Matcher m = MATH_PATTERN.matcher(trimmed);
        String expr = null;
        if (m.find()) {
            expr = m.group(2);
        } else if (PURE_EXPR.matcher(trimmed).matches()) {
            expr = trimmed;
        }
        if (expr == null) return null;

        try {
            Expression e = new ExpressionBuilder(expr).build();
            double val = e.evaluate();
            if (Double.isNaN(val) || Double.isInfinite(val)) return null;
            return val;
        } catch (Exception ex) {
            return null;
        }
    }
}
