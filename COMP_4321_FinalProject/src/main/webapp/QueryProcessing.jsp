<%@ page language="java" 
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
     %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="Web_Crawler_Indexer.*" %>

<%
  //Scriplet to check whether an index exists
  Indexer index = new Indexer("Final", "keyWords", "pageName", 
          "lastModifiedDate", "pageSize", "childLinks"
          , "KeyWordsWithPos", "titleKeywordsWithPos"
          , "parentLinks"); 
       pageContext.setAttribute("indexCheck", index.checkForSavedDatabase());
       %>
<html>
<head>
<meta charset="UTF-8">
<title>COMP 4321 Project Search Engine</title>
</head>
<body>
    <!-- Processes the index check Scriplet to output result -->
    Index Check:
    <c:choose>
    <c:when test="${indexCheck}">
        <p style="color:green">Index found</p>
        </c:when>    
        <c:otherwise>
            <p style="color:red">Index not Found</p>
        </c:otherwise>
    </c:choose>
    <!-- The code that shows the inputs on the web
         data is then sent to the QueryProcessor.java
         to process the input -->      
    <form action = "process" method ="get" ><br>
        Input Query <input type="text" name="Query"/>
        <br>
        Perform WebCrawling <input type="checkbox" name ="check" >
        <input type="submit"/>
        <br/>
        Note*: Use the web crawler if no index can be found 
       
    </form>
</body>
</html>
