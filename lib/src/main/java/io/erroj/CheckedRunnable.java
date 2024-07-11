package io.erroj;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
