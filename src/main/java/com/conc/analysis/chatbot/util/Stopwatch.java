package com.conc.analysis.chatbot.util;

public class Stopwatch implements AutoCloseable {
    private final long start = System.currentTimeMillis();
    private long end = -1;
    public static Stopwatch startNew() { return new Stopwatch(); }
    public long elapsedMs() { return (end < 0 ? System.currentTimeMillis() : end) - start; }
    @Override public void close() { end = System.currentTimeMillis(); }
}

