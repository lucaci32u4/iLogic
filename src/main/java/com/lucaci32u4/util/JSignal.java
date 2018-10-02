package com.lucaci32u4.util;

public class JSignal {
    private boolean current;

    public JSignal(boolean initialState) {
        current = initialState;
    }
    public synchronized boolean getState() {
        return current;
    }
    public synchronized void setState(boolean state) {
        current = state;
        this.notifyAll();
    }
    public synchronized void invert() {
        current = !current;
        this.notifyAll();
    }
    public synchronized boolean waitForState(boolean finalState) {
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
