package dictionary;

import java.util.ArrayList;
import java.util.List;

public class WordQueue {
	List<String> queue = new ArrayList<>();
	Integer maxItemNumber = 0;
	
	public WordQueue(Integer maxItemNumber) {
		this.maxItemNumber = maxItemNumber;
	}

	public void push(String item) {
		if (queue.size() >= maxItemNumber) {
			queue.remove(0);
		}
		queue.add(item);
	}
	
	public String getAllWords() {
		String result = "";
		for (String word : queue) {
			result += word + " ";
		}
		result = result.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
		return result;
	}
	
}
