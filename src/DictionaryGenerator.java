import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class DictionaryGenerator {
    public HashMap<String, Integer> load(File words, File sample){
        HashMap<String, Integer> dict = new HashMap<>();
        try {
            Scanner sc = new Scanner(words);
            while (sc.hasNextLine())
                dict.put(sc.nextLine().toLowerCase(), 0);
            sc = new Scanner(sample);
            String[] line_words;
            while (sc.hasNextLine()) {
                line_words = sc.nextLine().split(" ");
                for (String s : line_words) {
                    Integer count = dict.get(s);
                    if (count != null) {
                        dict.put(s, count + 1);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return dict;
    }
}
