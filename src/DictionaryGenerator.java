import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class DictionaryGenerator {
    public static HashMap<String, Integer> load(File words, File sample){
        HashMap<String, Integer> dict = new HashMap<>();
        try {
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
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return dict;
    }
}
