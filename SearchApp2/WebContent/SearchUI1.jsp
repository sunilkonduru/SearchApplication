<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<style>h3{line-height: 0%;}h1{line-height: 0%;}h6{line-height: 0%;}</style>
</head>
<body>
<table>
<center>
    <div class="newSearchEngineForm" >
    <FORM id="uciSearchForm" action=ServletForSearching method="GET" >
    <table>
    <tr>
      <td colspan="2">
      
      <b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ICS Search</b>
      &nbsp;
      <input name="keywordInSearchBox" id="keywordInSearchBox" type="text" size="70">
      
   
     
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  <input id="searchButton" type="submit" name="submit" value="Search ICS" >
	          </td>
	      </tr>
    </table>
    </FORM>
    </div>
    </center>

<table>
<br>
<br>

<% 
    ArrayList results=(ArrayList)request.getAttribute("searchresult");
    if(results==null)System.out.println("luck");
   
    else System.out.println("lucky");
    
    if(results.size()==0)
    {
    	%>
    	<br>
    	<br>
    	<h3>Error: Please verify whether you have entered a valid query  (or No results found for your query)</h3>
   <%  	
    }
    else
    {	
    String title="";
    String url="";
    String text="";
    if(results!=null)
	{
    for(int i=0;i<results.size();i++)
    {
    	
    	title=results.get(i).toString();
    	title=title.replace("'","");
    	System.out.println(title);
    	url=results.get(i+1).toString();
    	text=results.get(i+2).toString();
    	i=i+2;
    	
    %>
    
    <tr>
     <td><H3><A href="<%=url%>"><%=title %></A>
     <H6 style="color:green;"><%=url %></H6>
     <%=text %>
     </td>
     </tr>
   <%}}} %> 
  
   </table>
  </body>
</html>