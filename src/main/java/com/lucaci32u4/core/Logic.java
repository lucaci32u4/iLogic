package com.lucaci32u4.core;

import org.jetbrains.annotations.NotNull;

public class Logic {
    public static final boolean LOW = false;
    public static final boolean HIGH = true;

    public static final int FULL_DRIVER = 0b11;
    public static final int HIGH_DRIVER = 0b01;
    public static final int LOW_DRIVER = 0b10;
    public static final int TRISTATE = 0b00;

    public boolean state;
    public boolean defined;
    public Logic(boolean initialState, boolean define) { state = initialState; defined = define; }
    public Logic(@NotNull Logic source) { state = source.state; defined = source.defined; }
    public void copy(@NotNull Logic src) { state = src.state; defined = src.defined; }
    @Override
    public String toString() {
    	return (defined ? (state ? "HIGH" : "LOW") : "TRISTATE");
    }

    public static boolean senseChange(@NotNull Logic from, @NotNull Logic to) { return ((from.defined) && (from.state != to.state)) || (from.defined != to.defined); }
}
