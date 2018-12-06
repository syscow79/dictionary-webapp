package dictionary;

import java.util.Comparator;
import java.util.Map;

public class WordComparator implements Comparator<String> {

    Map<String, Integer> base;

    public WordComparator(Map<String, Integer> wordMap) {
        this.base = wordMap;
    }

    public int compare(String a, String b) {
        if (base.get(a) < base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }

}