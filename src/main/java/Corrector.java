package main.java;

import java.util.ArrayList;

/**
 * Provides corrections and checks for validity on and given String
 */
public interface Corrector {
    /**
     * Finds corrections for any given word from the dictionary
     * @param s the String to be corrected
     * @return the corrections found
     */
    ArrayList<String> getCorrections(String s);

    /**
     * Checks if a given word is in the dictionary or not
     * @param s the String to check
     * @return whether or not it is in the dictionary
     */
    boolean isValid(String s);
}
