package query_processing;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import Web_Crawler_Indexer.*; 
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;


public class QueryProcessor extends HttpServlet{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * @desc checks if the the checkbox is ticked,
     * is so run the Web Crwler to index the URLs
     * @param {String} check
     */
    public void crawlURL(String check) {
        if(check != null) {
            try {
                WebCrawler.process("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     *@desc The Processes the jsp session input by accessing the {UrlRanking} class and
     *adds their output to the jsp session as parameters
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        crawlURL(req.getParameter("check"));
        String Query = req.getParameter("Query");
        Indexer index = new Indexer("Final", "keyWords", "pageName", 
                "lastModifiedDate", "pageSize", "childLinks"
                , "KeyWordsWithPos", "titleKeywordsWithPos"
                , "parentLinks");
        UrlRanking ranker = new UrlRanking(index, Query);
        HashMap<URL, Double> URLWithRanks = ranker.processKeyword(ranker.processQuery());
        HashMap<URL, Double> relevantURLWithRanks = new HashMap<>();
        Vector<URL> relevantRankedURLs = new Vector<>();
        Vector<Double> relevantRankedURLScores = new Vector<>();
        for(Map.Entry<URL, Double> entry: URLWithRanks.entrySet()) {
            if(entry.getValue() != 0) {
                relevantURLWithRanks.put(entry.getKey(), entry.getValue());
            }
        }
        relevantRankedURLs = ranker.rankURLs(relevantURLWithRanks);
        relevantRankedURLScores = ranker.rankURLsScores(relevantURLWithRanks);
        HashMap<URL, String> urlData = new HashMap<>();
        FileExporter exporter = new FileExporter(index);
        if(relevantRankedURLs.size()>50) {
            relevantRankedURLScores.setSize(50);
            relevantRankedURLs.setSize(50); 
        }
        int urlPortion = 4;
        ExecutorService service = Executors.newFixedThreadPool(20); 
        Vector<URL> urlLinksPortion = new Vector<>();
        for(int i=0; i<relevantRankedURLs.size(); i++) {
            urlLinksPortion.add(relevantRankedURLs.get(i));
            if(urlLinksPortion.size()==urlPortion || i>=relevantRankedURLs.size()-1) {
                MultiThread2 multithread = new MultiThread2(urlLinksPortion, urlData, exporter);
                service.execute(multithread);
                urlLinksPortion = new Vector<>();
            }
        }
        service.shutdown();
        try {
            while(!service.awaitTermination(2, TimeUnit.MINUTES))  {       
               }
        } catch (InterruptedException e1) {
            System.out.println("multhread2 error");
            e1.printStackTrace();
        }
        HttpSession session = req.getSession();
        session.setAttribute("relevantRankedURLScores", relevantRankedURLScores);
        req.setAttribute("relevantRankedURLScores", relevantRankedURLScores);
        session.setAttribute("relevantRankedURLs", relevantRankedURLs);
        req.setAttribute("relevantRankedURLs", relevantRankedURLs);
        session.setAttribute("urlData", urlData);
        req.setAttribute("urlData", urlData);
        session.setAttribute("relevantRankedURLsSize", relevantRankedURLs.size());
        req.setAttribute("relevantRankedURLsSize", relevantRankedURLs.size());
        try {
            req.getRequestDispatcher("QueryResult.jsp").forward(req, res);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.sendRedirect("QueryResult.jsp");  
    }
}
