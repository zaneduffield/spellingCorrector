package main.java;

/**
 * Manages decorating erroneous words in some document
 */
public interface ErrorDecorator {
    /**
     * decorates a section of the document between the start (inclusive) and end (exclusive) indices
     * @param start the inclusive index to start decorating
     * @param end the exclusive index to end decorating
     */
    void decorate(int start, int end);

    /**
     * removes all decoration from the document
     */
    void undecorateAll();
}
