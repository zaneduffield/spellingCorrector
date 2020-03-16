package main.java;

public interface ErrorDecorator {
    void decorate(int start, int end);
    void undecorateAll();
}
