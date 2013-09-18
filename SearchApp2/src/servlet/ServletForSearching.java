package servlet;


import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.OptimizedIndexFiles;
import support.SearchApp;

//import NDCG.OptimizedIndexFiles;
//import NDCG.SearchApp;

/**
 * Servlet implementation class ServletForSearching
 */
public class ServletForSearching extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletForSearching() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String keywordWithSpaces=request.getParameterValues("keywordInSearchBox")[0];
		// OptimizedIndexFiles ifiles=new OptimizedIndexFiles();
		SearchApp sApp=new SearchApp();
		ArrayList<String> emptyList=new ArrayList<String>();
		boolean value1=false;
		if(keywordWithSpaces.length()==0)
		{
			RequestDispatcher disp=request.getRequestDispatcher("SearchUI1.jsp");
			request.setAttribute("searchresult",emptyList);
			disp.forward(request, response);
			value1=true;
		}
		
		ArrayList<String> dummy=new ArrayList<String>();
		ArrayList<String> luceneResultsOptimized=new ArrayList<String>();
		ArrayList<String> luceneContent=new ArrayList<String>();
		ArrayList<String> luceneResults=new ArrayList<String>();
		ArrayList<String> luceneTitle=new ArrayList<String>();
		boolean computeNDCG=false;
		String keyword[]=keywordWithSpaces.split("\\s+");
		//String formattedSearchWord=null;
	    ArrayList<String> results=new ArrayList<String>();
	    String formattedSearchWord="";
		int count=0;
		for(String value:keyword)
		{
			if(keyword.length==count+1)
			{
				formattedSearchWord+=value;
			}
			else
			{
			formattedSearchWord+=value+"%20";
			count++;
			}
		}
		//sApp.findTopResults(formattedSearchWord, luceneContent,luceneTitle, luceneResults, computeNDCG);
		//remove this
		System.out.println(luceneResults.size());
		//specify with or without pagerank

		luceneResultsOptimized=sApp.optimizedIndexResults(keywordWithSpaces, luceneResultsOptimized,luceneContent,luceneTitle, dummy,false);
		for(int i=0;i<luceneResultsOptimized.size();i++)
		{
			String urlForSearch=luceneResultsOptimized.get(i).toString();
			String title=luceneTitle.get(i);
//			if(title.contains("'"))
//				title=title.replace("'","");
			String content=luceneContent.get(i);
			if(content==null)
			content="";
			//diplaying only 100 words
			else 
			{
				content=content.replaceAll("Text:", "");
				if(content.length()>240)
				{
				 content=content.substring(0, 240);
				 content+="....";
				}
				else
					content=content;
			}
			 results.add(title);
			 results.add(urlForSearch);
			 results.add(content);
		}
		System.out.println("result size servlet"+results.size());
		String dispatch="SearchUI.jsp";
		if(value1==false)
		{
		RequestDispatcher disp=request.getRequestDispatcher("SearchUI1.jsp");
		request.setAttribute("searchresult",results);
		disp.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
