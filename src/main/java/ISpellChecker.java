package main.java;

import java.util.ArrayList;

public interface ISpellChecker {
    public ArrayList<String> getCorrections(String s);
    public boolean isValid(String s);
}
