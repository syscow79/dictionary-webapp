package dictionary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class LinkReader {

	private List<LinkReader> oldReaders = null;
	private Integer words = 1;
	private Reader reader;
	private URL url;
	private String host;
	private String source = "";
	private Map<String, Integer> wordMap;
	private Map<String, Integer> sortedWordMap;
	private Set<String> keySet;
	private int totalCount;
	private String collectedDictionary;
	private String sourceAsText;

	public static void main(String[] args){
		new LinkReader("https://teacherluke.co.uk/archive-of-episodes-1-149/", 1);
	}

	public LinkReader() throws IOException {
		// setReader(new FileReader("src/main/resources/com_maven_maven_overview.htm"));
	}

	public LinkReader(String url, Integer words){
		long startTime = System.currentTimeMillis();
		this.words = words;
		setUrlAndHost(url);
		setReader();
		setSource();
		setSourceAsText();
		setWordMap();
		setSortedWordMap();
		collectedDictionary = createDictionary();

		System.out.println("Links: ");
		getLinks().forEach(System.out::println);

		// System.out.println(collectedDictionary);

		System.out.println("Words: " + getWordsCount());
		System.out.println("TotalCount: " + getTotalCount());
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public LinkReader(String url, Integer words, List<LinkReader> oldReaders){
		this(url, words);
		this.oldReaders = oldReaders;
	}

	private void setSourceAsText() {
		sourceAsText = html2text(source);
	}

	private void setWordMap() {
		wordMap = collectMultipleWords(sourceAsText);
	}

	private void setSource() {
		source = readAllText();
	}

	private void setUrlAndHost(String url) {
		System.out.println(url);
		try {
			this.url = new URL(url);
			host = this.url.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void setSortedWordMap() {
		WordComparator wc = new WordComparator(wordMap);
		sortedWordMap = new TreeMap<>(wc);
		sortedWordMap.putAll(wordMap);
	}

	public String createDictionary() {
		keySet = new LinkedHashSet<>(getSortedWordMapKeySet());
		totalCount = 0;
		String dictionary = "";
		for (String word : keySet) {
			totalCount += wordMap.get(word);
			dictionary += word + " : " + wordMap.get(word) + "\n";
		}
		return dictionary;
	}

	public String createNewWordsDictionary() {
		Set<String> newKeySet = new LinkedHashSet<>(getSortedWordMapKeySet());
		String dictionary = "";
		for (String word : newKeySet) {
			if (oldReaders.stream()
					.filter(oldReader -> oldReader.getSortedWordMapKeySet().stream()
							.anyMatch(oldWord -> oldWord.equals(word)))
			.count() == 0) {
				dictionary += word + " : " + wordMap.get(word) + "\n";
			}
		}
		return dictionary;
	}

	private Set<String> getSortedWordMapKeySet() {
		return sortedWordMap.keySet();
	}

	private int getTotalCount() {
		return totalCount;
	}

	private int getWordsCount() {
		return keySet.size();
	}

	private Map<String, Integer> collectMultipleWords(String source) {
		Map<String, Integer> wordMap = new HashMap<>();
		List<String> allWords = readWordsFromText(source);
		WordQueue doubleWordQueue = new WordQueue(words);

		for (String word : allWords) {
			doubleWordQueue.push(word);
			String doubleWord = doubleWordQueue.getAllWords();
			if (wordMap.containsKey(doubleWord)) {
				wordMap.put(doubleWord, wordMap.get(doubleWord) + 1);
			} else {
				wordMap.put(doubleWord, 1);
			}
		}
		return wordMap;
	}

	private String readAllText() {
		String line, source = "";
		try (BufferedReader br = new BufferedReader(getReader())) {
			while ((line = br.readLine()) != null) {
				source += line + "\n";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return source;
	}

	private String html2text(String html) {
		return Jsoup.parse(Jsoup.parse(html).text()).text();
	}

	public Set<String> getInnerLinks() {
		Document doc = Jsoup.parse(source);
		Elements links = doc.select("a[href]");
		Set<String> linkSet = new HashSet<>();

		for (Element link : links) {
			String linkhref = link.attr("href");
			if (linkhref.startsWith("/")) {
				if (linkhref.indexOf('#') > 0) {
					linkhref = linkhref.substring(0, linkhref.indexOf('#'));
				}
				linkSet.add(linkhref);
			}
		}
		return linkSet;
	}

	public Set<String> getLinks() {
		Document doc = Jsoup.parse(source);
		Elements links = doc.select("a[href]");
		Set<String> linkSet = new HashSet<>();
		for (Element link : links) {
			String linkhref = link.attr("href");
				linkSet.add(linkhref);
		}
		return linkSet;
	}

	private List<String> readWordsFromText(String line) {
		line = line.replaceAll("[,:;(){}=*/@\\-]", " ");
		line = line.replaceAll("\\s+", " ");
		line = line.replaceAll("\\s", " ");
		line = line.replaceAll(" +", " ");
		Pattern pattern = Pattern.compile(" +");
		String[] words = pattern.split(line);
		List<String> returnWords = new ArrayList<String>();
		for (String word : words) {
			if (containOnlyAZ(word)) {
				returnWords.add(word.toLowerCase());
			}
		}
		return returnWords;
	}

	private boolean containOnlyAZ(String word) {
		return word.matches("[A-Za-z]+");
	}

	public Reader getReader() {
		return reader;
	}

	public void setReader() {
		try {
			this.reader = new InputStreamReader(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
