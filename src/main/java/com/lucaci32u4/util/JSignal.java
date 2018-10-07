package com.lucaci32u4.util;

public class JSignal {
    private boolean current;

    public JSignal(boolean initialState) {
        current = initialState;
    }
    public synchronized boolean get() {
        return current;
    }
    public synchronized void set(boolean state) {
        current = state;
        this.notifyAll();
    }
    public synchronized boolean waitFor(boolean finalState) {
        boolean immediateExit = true;
        if (current != finalState) {
            immediateExit = false;
            boolean interruptedExc = false;
            while (current != finalState) {
                try {
                    this.wait();
                } catch (InterruptedException exc) {
                    interruptedExc = true;
                }
            }
            if (interruptedExc) Thread.currentThread().interrupt();
        }
        return immediateExit;
    }
}
