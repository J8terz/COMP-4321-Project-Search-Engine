package Web_Crawler_Indexer;

import java.io.IOException;
import java.lang.Math;
import java.net.URL;
import java.util.*;

import Web_Crawler_Indexer.keyword_utils.StopStem;

public class UrlRanking {
    //fields
    Indexer index;
    String queries;
    HashMap<String, HashMap<URL, Integer>> keywordContentMatrixes;
    HashMap<String, HashMap<URL, Integer>> keywordTitleMatrixes;
    
    //Constructor
    public UrlRanking(Indexer index, String queries){
        this.index = index;
        this.queries = queries;
        keywordContentMatrixes = new HashMap<>();
        keywordTitleMatrixes = new HashMap<>();
    }

    
    
    /**
     * @desc  Retrieves the Term Matrix of the given {String} input for each indexed URLs content
     * @param {String} input
     * @return {HashMap<URL, Integer>} urlTermMatrix
     */
    public HashMap<URL, Integer> getTermUrlMatrix(String input){
                HashMap<URL, Integer> urlTermMatrix = new HashMap<>();
                HashMap<URL, Vector<Integer>> posInDocs = (HashMap<URL, Vector<Integer>>)  index.getkeywordsWithPos(input);
                posInDocs.forEach((url, positions) -> {
                        urlTermMatrix.put(url, positions.size());
                });
                return urlTermMatrix;
    }
    
    /**
     * @desc  Retrieves the Term Matrix of the given {String} input for each indexed URLs title
     * @param {String} input
     * @return {HashMap<URL, Integer>} titleTermMatrix
     */
    public HashMap<URL, Integer> getTermTitleMatrix(String input){
        HashMap<URL, Integer> titleTermMatrix = new HashMap<>();
        HashMap<URL, Vector<Integer>> posInDocs = (HashMap<URL, Vector<Integer>>)  index.getTitleKeywordsWithPos(input);
            if(index.getTitleKeywordsWithPos(input) != null) {
                posInDocs.forEach((url, positions) -> {
                    titleTermMatrix.put(url, positions.size());
            });
        }
        return titleTermMatrix;
    }
    
    
    /**
     * @desc Retrieves the Term Matrix of the given {String} phrase input for each indexed URLs content
     * Method works by checking the positions of the text and see if the sequence given exists for each url
     * @param {String} input
     * @return {HashMap<URL, Integer>} termMatrix
     */
    public HashMap<URL, Integer> getTermUrlMatrixForPhrase(String input){
        HashMap<String, Vector<Integer>> posOfWordsInUrl = new HashMap<>();
        HashMap<URL, Integer> termMatrix = new HashMap<>();
        Vector<String> wordsInPhrase = new Vector<>(Arrays.asList(input.split(" ")));
        String firstWord = wordsInPhrase.get(0);
        for(URL currURL: index.getIndexedURLs()) {
            for(String word: wordsInPhrase) {
                HashMap<URL, Vector<Integer>> posInURLs = (HashMap<URL, Vector<Integer>>)  index.getkeywordsWithPos(word);
                if(posInURLs != null) {
                    posOfWordsInUrl.put(word, posInURLs.get(currURL));
                }  
            }
            int numOfPhraseinUrl = 0;
            if(posOfWordsInUrl.get(firstWord) != null) {
                outerloop:
                for(Integer pos: posOfWordsInUrl.get(firstWord)) {
                    for(String subphrase: wordsInPhrase.subList(1, wordsInPhrase.size())) {
                        if(posOfWordsInUrl.get(subphrase) == null || 
                           !posOfWordsInUrl.get(subphrase).contains(pos+wordsInPhrase.indexOf(subphrase))) {
                            continue outerloop;
                        } 
                    }
                    numOfPhraseinUrl++;
                    termMatrix.put(currURL, numOfPhraseinUrl);
                }
            }
        }
        return termMatrix;
    }
    
    /** 
     * @desc Retrieves the Term Matrix of the given {String} phrase input for each indexed URLs title
     * Method works by checking the positions of the text and see if the sequence given exists for each url
     * @param {String} input
     * @return {HashMap<URL, Integer>} termMatrix
     */
    public HashMap<URL, Integer> getTermTitleMatrixForPhrase(String input){
        HashMap<String, Vector<Integer>> posOfWordsInUrl = new HashMap<>();
        HashMap<URL, Integer> termMatrix = new HashMap<>();
        Vector<String> wordsInPhrase = new Vector<>(Arrays.asList(input.split(" ")));
        String firstWord = wordsInPhrase.get(0);
        for(URL currURL: index.getIndexedURLs()) {
            for(String word: wordsInPhrase) {
                HashMap<URL, Vector<Integer>> posInURLs = (HashMap<URL, Vector<Integer>>)  index.getTitleKeywordsWithPos(word);
                if(posInURLs != null) {
                    posOfWordsInUrl.put(word, posInURLs.get(currURL));
                }  
            }
            int numOfPhraseinUrl = 0;
            if(posOfWordsInUrl.get(firstWord) != null) {
                outerloop:
                for(Integer pos: posOfWordsInUrl.get(firstWord)) {
                    for(String subphrase: wordsInPhrase.subList(1, wordsInPhrase.size())) {
                        if(posOfWordsInUrl.get(subphrase) == null || 
                           !posOfWordsInUrl.get(subphrase).contains(pos+wordsInPhrase.indexOf(subphrase))) {
                            continue outerloop;
                        } 
                    }
                    numOfPhraseinUrl++;
                    termMatrix.put(currURL, numOfPhraseinUrl);
                }
            }
        }
        return termMatrix;
    }
    
    
    /**
     * @desc Calculates the tf-idf of each of the queries for all the URL content and stores them for later use
     * @param {Vector<String>} inputs
     * @param {HashMap<String, HashMap<URL, Integer>>} keywordTermMatrixes
     * @return {HashMap<URL, HashMap<String, Double>>} urltfidf
     */
    public HashMap<URL, HashMap<String, Double>> getURLTFIDF(Vector<String> inputs, HashMap<String, HashMap<URL, Integer>> keywordTermMatrixes) { //keyword inputs
        WebCrawler crawler = new WebCrawler();
        HashMap<URL, HashMap<String, Double>> urltfidf = new HashMap<>();
            for(URL url: index.getIndexedURLs()) {
               for(String input : inputs) {
                   HashMap<URL, Integer> numOfWordInUrls = keywordTermMatrixes.get(input);
                   HashMap<String, Double> wordtfidf = new HashMap<>();
                   Double numOfWordInUrl = 0.0;
                   if(numOfWordInUrls.get(url) != null) {
                       numOfWordInUrl = (double) numOfWordInUrls.get(url);
                   }
                   wordtfidf.put(input, (numOfWordInUrl / (double) crawler.extractStrings(url).size())
                                        * Math.log( (double) index.getIndexedURLs().size()/ (double) numOfWordInUrls.keySet().size()));
                   if(!urltfidf.containsKey(url)) {
                       urltfidf.put(url, wordtfidf);
                   } else {
                       HashMap<String, Double> storedwordtfidf = urltfidf.get(url);
                       storedwordtfidf.putAll(wordtfidf);
                       urltfidf.put(url, storedwordtfidf);
                   }
               }
            }
            return urltfidf;
    }
    
    /**
     * @desc Calculates the tf-idf of each of the queries for all the URL titles and stores them for later use
     * @param {Vector<String>} inputs
     * @param {HashMap<String, HashMap<URL, Integer>>} keywordTermMatrixes
     * @return {HashMap<URL, HashMap<String, Double>>} urltfidf
     */
    public HashMap<URL, HashMap<String, Double>> getTitleTFIDF(Vector<String> inputs, HashMap<String, HashMap<URL, Integer>> keywordTitleMatrixes) { //keyword inputs
        WebCrawler crawler = new WebCrawler();
        HashMap<URL, HashMap<String, Double>> titletfidf = new HashMap<>();
            for(URL url: index.getIndexedURLs()) {
               for(String input : inputs) {
                   HashMap<URL, Integer> numOfWordInUrls = keywordTitleMatrixes.get(input);
                   HashMap<String, Double> wordtfidf = new HashMap<>();
                   Double numOfWordInUrl = 0.0;
                   if(numOfWordInUrls.get(url) != null) {
                       numOfWordInUrl = (double) numOfWordInUrls.get(url);
                       wordtfidf.put(input, (numOfWordInUrl / (double) crawler.extractTitleStringsWithPos(url).size())
                               * Math.log((double) index.getIndexedURLs().size()/(double) numOfWordInUrls.keySet().size()));
                   }
                      if(!titletfidf.containsKey(url)) {
                          titletfidf.put(url, wordtfidf);
                      } else {
                          HashMap<String, Double> storedwordtfidf = titletfidf.get(url);
                          storedwordtfidf.putAll(wordtfidf);
                          titletfidf.put(url, storedwordtfidf);
                      }
               }
            }
            return titletfidf;
    }
    
    /**
     * @desc Calculates the relevance scores for all URLs given the tf-idfs Matrices for
     * the content and title while prioritizing the title
     * @param {HashMap<URL, HashMap<String, Double>>} urlstfidf
     * @param {HashMap<String, Double>>} titlestfidf
     * @param {HashMap<String, Double>} keywordsContenttfidf
     * @param {HashMap<String, Double>} keywordsTitletfidf
     * @return {HashMap<URL, Double>} urlRank
     */
    public HashMap<URL, Double> getURLranks(HashMap<URL, HashMap<String, Double>> urlstfidf, HashMap<URL,
            HashMap<String, Double>> titlestfidf ,HashMap<String, Double> keywordsContenttfidf,
            HashMap<String, Double> keywordsTitletfidf) {
        HashMap<URL, Double> urlRank = new HashMap<>();
        for(URL url: index.getIndexedURLs()) {
            Double contentCossim = 0.0;
            for(Map.Entry<String, Double> entry: keywordsContenttfidf.entrySet()) {
                Double urltfidf = 0.0;
                if(urlstfidf.get(url).get(entry.getKey())!=null) {
                    urltfidf = urlstfidf.get(url).get(entry.getKey());
                }
                contentCossim = contentCossim + (urltfidf*keywordsContenttfidf.get(entry.getKey()));
            }
            if(keywordsContenttfidf.size()>1) {
                contentCossim = contentCossim/((double) Math.sqrt(urlstfidf.get(url).values().stream().reduce(0.0, (subtotal, num) -> subtotal+ (double) num*num, Double::sum))
                        *(double) Math.sqrt(keywordsContenttfidf.values().stream().reduce(0.0, (subtotal, num) -> subtotal+ (double) num*num, Double::sum)));
            }
            if(Double.isNaN(contentCossim)) {
                contentCossim=0.0;
            }
            Double titleCossim = 0.0;
            for(Map.Entry<String, Double> entry: keywordsTitletfidf.entrySet()) {
                Double titletfidf = 0.0;
                if(titlestfidf.get(url) != null && titlestfidf.get(url).get(entry.getKey()) != null) {
                    titletfidf = titlestfidf.get(url).get(entry.getKey());
                } 
                if(titlestfidf.get(url).get(entry.getKey()) != null && Double.isFinite(titlestfidf.get(url).get(entry.getKey()))) {
                  
                    titleCossim = titleCossim + titletfidf*keywordsTitletfidf.get(entry.getKey());
                } 
            }
            if(keywordsContenttfidf.size()>1 && titlestfidf.get(url)!=null) {
                titleCossim = titleCossim/((double) (Math.sqrt(titlestfidf.get(url).values().stream().reduce(0.0, (subtotal, num) -> subtotal+ (double) num*num, Double::sum))
                        *(double) Math.sqrt(keywordsTitletfidf.values().stream().reduce(0.0, (subtotal, num) -> subtotal+ (double) num*num, Double::sum))));
            }
            if(Double.isNaN(titleCossim)) {
                titleCossim=0.0;
            }
            urlRank.put(url, contentCossim + titleCossim*10.0);
        }
        return urlRank;
    }
    
    /**
     * @desc Returns the URLs given as a {Vector<URL>} with descending scores 
     * @param {HashMap<URL, Double>} urlRanks
     * @return {Vector<URL>} rankedURLs
     */
    public Vector<URL> rankURLs(HashMap<URL, Double> urlRanks){
        Vector<URL> rankedURLs = new Vector<>();
        urlRanks.entrySet().stream()
                          .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                          .forEach(entry -> rankedURLs.add(entry.getKey()));
        return rankedURLs;
    }
    
    /**
     * @desc Returns the URLs given as a {Vector<URL>} with descending scores 
     * @param {HashMap<URL, Double>} urlRanks
     * @return {Vector<URL>} rankedURLs
     */
    public Vector<Double> rankURLsScores(HashMap<URL, Double> urlRanks){
        Vector<Double> rankedURLScores = new Vector<>();
        urlRanks.entrySet().stream()
                           .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                           .forEach(entry -> {
                               rankedURLScores.add(entry.getValue());
                           });
        return rankedURLScores;
    }
    
    /**@desc Processes the queries store in the {Ranker} for usage in the tf-idf and scoring process
     * @return {Vector<String>} 
     */
    public Vector<String> processQuery() {
        if(queries != null) {
            Vector<String> phraseQueries = new Vector<>();
            Vector<String> wordQueries = new Vector<>();
            StopStem stopStem = new StopStem();
            for(String word: queries.split(",")) {
                if(word.contains("\"")) {
                    phraseQueries.add(word);
                } else {
                    if(index.getkeywordsWithPos(stopStem.stem(word.replaceAll(" ", ""))) == null) {
                        continue;
                    }
                    wordQueries.add(stopStem.stem(word.replaceAll(" ", "")));
                }
            }
            for(int i=0; i<phraseQueries.size(); i++) {
                String phrase = phraseQueries.get(i);
                String fixedPhrase = phrase.substring(phrase.indexOf("\"")+1);
                fixedPhrase = fixedPhrase.substring(0, fixedPhrase.indexOf("\""));
                phraseQueries.set(i, fixedPhrase);
            }
            Vector<String> inputs = new Vector<>();
            if(index.checkForSavedDatabase()) {
                for (String word: wordQueries) {
                    String fixedWord = stopStem.stem(word);
                    if(index.getkeywordsWithPos(fixedWord) == null) {
                        
                        continue;
                    }
                    if(!stopStem.getStopWords().contains(fixedWord)
                            ) {
                        this.getTermUrlMatrix(fixedWord);
                        inputs.add(fixedWord);
                    }
                    keywordContentMatrixes.put(fixedWord, this.getTermUrlMatrix(fixedWord));
                    keywordTitleMatrixes.put(fixedWord, this.getTermTitleMatrix(fixedWord));
                }
                for (String phrase: phraseQueries) {
                    StringBuilder stemmedPhrase = new StringBuilder();
                    for(String subphrase : phrase.split(" ")) {
                        String fixedSubphrase = stopStem.stem(subphrase);
                        
                        if(!stopStem.getStopWords().contains(fixedSubphrase)
                                && fixedSubphrase != null) {
                                if(!stemmedPhrase.toString().contains(fixedSubphrase)) {
                                    stemmedPhrase.append(stopStem.stem(fixedSubphrase)+" ");
                                }
                        }
                    }
                    keywordContentMatrixes.put(stemmedPhrase.toString(), this.getTermUrlMatrixForPhrase(stemmedPhrase.toString()));
                    keywordTitleMatrixes.put(stemmedPhrase.toString(), this.getTermTitleMatrixForPhrase(stemmedPhrase.toString()));
                    inputs.add(stemmedPhrase.toString());
                } 
                return inputs;
            } else {
                return null;
            }
            
        } else {
            return null;
        }
    }
    
    /**
     * @desc Runs the overall process if scoring each URL of the given inputs
     * @param {Vector<inputs>} inputs
     * @return {HashMap<URL, Double>} urlRanks
     */
    public HashMap<URL, Double> processKeyword(Vector<String> inputs) {
        if(!inputs.isEmpty()) {
            HashMap<String, Double> keywordContenttfidf = new HashMap<>();
            for(Map.Entry<String, HashMap<URL, Integer>> entry: keywordContentMatrixes.entrySet()) {
                keywordContenttfidf.put(entry.getKey(), (double) (1.0/inputs.size()) * 
                        Math.log((double) index.getIndexedURLs().size()/ (double) entry.getValue().size()));
            }
            HashMap<String, Double> keywordTitletfidf = new HashMap<>();
            for(Map.Entry<String, HashMap<URL, Integer>> entry: keywordTitleMatrixes.entrySet()) {
                if(Double.isFinite((double) (1.0/inputs.size()) * 
                        Math.log((double) index.getIndexedURLs().size()/(double) entry.getValue().size()))) {
                    keywordTitletfidf.put(entry.getKey(), (double) (1.0/inputs.size()) * 
                            Math.log((double) index.getIndexedURLs().size()/(double) entry.getValue().size()));
                } else {
                    keywordTitletfidf.put(entry.getKey(), 0.0);
                }
            }
            HashMap<URL, Double> urlRanks = this.getURLranks(this.getURLTFIDF(inputs, keywordContentMatrixes), this.getTitleTFIDF(inputs, keywordTitleMatrixes)
                                                                , keywordContenttfidf, keywordTitletfidf);
            return urlRanks;
        } else {
            return null;
        }
    }
    
    /**
     * @desc main function used for testing
     * @param args
     * 
     */
    public static void main(String[] args) {
        try {
            Indexer indexer = new Indexer("Final", "keyWords", "pageName", 
                    "lastModifiedDate", "pageSize", "childLinks"
                    , "KeyWordsWithPos", "titleKeywordsWithPos"
                    , "parentLinks");
            System.out.println("Enter Query");
            Scanner inputQuery = new Scanner(System.in);
            String inputQueries = inputQuery.nextLine();
            inputQuery.close();
            UrlRanking ranker = new UrlRanking(indexer, inputQueries);
            HashMap<URL, Double> URLRanks = ranker.processKeyword(ranker.processQuery());
            System.out.println(ranker.rankURLs(URLRanks));
            
        } catch (IOException e) {
            System.out.println("FileExporter main IOException");
        }
    }
}

