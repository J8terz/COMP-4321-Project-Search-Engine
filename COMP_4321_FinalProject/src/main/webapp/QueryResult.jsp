<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Query Result</title>
</head>
<body>

<!-- Adds the option to return to the URL search web page -->
<br><a href="<c:out value="QueryProcessing.jsp"/>">Return to search</a><br/>
 -----------------------------------------------------------
 <!-- jsp for loop to show each of the relevant URLs -->
 <c:forEach var = "i" begin = "0" end = "${relevantRankedURLsSize-1}">
         <br>
         <c:out value="Rank: ${relevantRankedURLScores[i]}"/>
         <c:out value="${urlData.get(relevantRankedURLs[i])}" escapeXml="false"/>
         <br><c:out value="Link to URL"/>
         <br>
         <a href="<c:out value="${relevantRankedURLs[i]}"/>">${relevantRankedURLs[i]} </a>
         <br/>
         -----------------------------------------------------------
      </c:forEach>    
   
   
<!-- Adds the option to return to the URL search web page -->
<br><a href="<c:out value="QueryProcessing.jsp"/>">Return to search</a><br/>
</body>
</html>