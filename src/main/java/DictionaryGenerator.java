package main.java;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class DictionaryGenerator {
    public static HashMap<String, Integer> load(InputStream words, InputStream sample){
        HashMap<String, Integer> dict = new HashMap<>();
        Scanner sc = new Scanner(words);
        String word = "";
        while (sc.hasNextLine()) {
            // ignore everything after "/" for .dic files
            word = sc.nextLine().toLowerCase().split("/")[0];
            dict.put(word, 0);
        }
        sc = new Scanner(sample);
        String[] line_words;
        while (sc.hasNextLine()) {
            line_words = sc.nextLine().split(" ");
            for (String s : line_words) {
                dict.merge(s, 1, Integer::sum);
            }
        }

        return dict;
    }
}
