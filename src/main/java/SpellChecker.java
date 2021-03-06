package main.java;

import java.io.InputStream;
import java.util.*;

/**
 * Provides corrections according to spelling as defined in a dictionary.
 */
public class SpellChecker implements Corrector {
    private HashMap<String, Integer> dictionary;
    private char[] alphabet;
    private int edit_distance;
    private int max_corrections;

    /**
     * @param words stream of words to load into dictionary
     * @param sample_text sample text used to estimate word probabilities
     * @param alphabet array of characters defining the alphabet
     * @param edit_distance maximum edit distance for corrections when searching
     * @param max_corrections maximum number of corrections to find when searching
     */
    public SpellChecker(InputStream words, InputStream sample_text, char[] alphabet, int edit_distance, int max_corrections){
        this.dictionary = DictionaryGenerator.load(words, sample_text);
        this.alphabet = alphabet;
        this.edit_distance = edit_distance;
        this.max_corrections = max_corrections;
    }

    public boolean isValid(String s){
        return this.dictionary.get(s) != null;
    }

    public ArrayList<String> getCorrections(String s){
        ArrayList<String> corrections = new ArrayList<>();
        ArrayList<String> edits = new ArrayList<>();
        edits.add(s.toLowerCase());
        ArrayList<String> next_edits = new ArrayList<>();
        for (int i=0; i < edit_distance; i++){
            for (String e: edits){
                next_edits.addAll(this.getEdits(e));
            }
            corrections.addAll(next_edits);
            edits = next_edits;
            next_edits.clear();
        }

        // sort in order of the Spellchecker dictionary count
        corrections.sort(Comparator.comparing(a -> -this.dictionary.get(a)));
        // remove duplicates
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(corrections);
        corrections = new ArrayList<>(hashSet);
        // trim to size
        if (corrections.size() > this.max_corrections){
            corrections.subList(this.max_corrections, corrections.size()).clear();
        }
        return corrections;
    }

    private ArrayList<String> getEdits(String s){
        ArrayList<String> edits = new ArrayList<>();
        if (!this.isValid(s)){
            for (int i=0; i < s.length(); i++){
                // get deletes
                this.add_candidate(edits, s.substring(0, i) + s.substring(i + 1));

                // get transpositions
                if (i != 0){
                    this.add_candidate(edits, s.substring(0, i - 1) + s.substring(i, i + 1) + s.substring(i - 1, i) + s.substring(i + 1));
                }

                for (char c: this.alphabet){
                    // get additions
                    this.add_candidate(edits, s.substring(0, i) + c + s.substring(i));
                    // get replacements
                    this.add_candidate(edits, s.substring(0, i) + c + s.substring(i + 1));
                }
            }
            // additions to the end
            for (char c: this.alphabet) {
                this.add_candidate(edits, s + c);
            }
        }
        return edits;
    }

    private void add_candidate(ArrayList<String> edits, String candidate){
        if (this.isValid(candidate)){
            edits.add(candidate);
        }
    }
}