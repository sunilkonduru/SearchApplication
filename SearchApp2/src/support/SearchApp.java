
package support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import java.util.Map;
import java.util.Map.Entry;
import org.json.*;
public class SearchApp {
	
	
	HashMap<String,Integer> hMap=new HashMap<String, Integer>();
	ArrayList<String> urlFor=new ArrayList<String>();
	ArrayList<String> titleFor=new ArrayList<String>();
	ArrayList<String> contentFor=new ArrayList<String>();
	public HashMap<String,Integer> pageRank()
	{
		File input=new File("C:/Users/Sunil/Desktop/sunil/crawl.txt");
		Scanner scan=null;
		try
		{
		scan = new Scanner(input);
		}
		catch(Exception e)
		{
			System.out.println("File not found, enter a valid file location and name");
		}
		while(scan.hasNextLine())
		{
			String newLine=scan.nextLine();
			if(hMap.containsKey(newLine))
			{
				int count=hMap.get(newLine);
				count++;
				hMap.put(newLine,count);
			}
			else
			{
				hMap.put(newLine, 1);
			}
		}
	    hMap=sortByComparator(hMap, false);
	    //printMap(hMap);
		return hMap;
		
	}
	private static HashMap<String, Integer> sortByComparator(HashMap<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return (HashMap)sortedMap;
    }
	
	public void printMap(HashMap<String, Integer> map)
    {   int i=0;
        for (Entry<String, Integer> entry : map.entrySet())
        {
        	i++;
            //System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
            if(i==100) break;
        }
    }
	public ArrayList<String> findTopResults(String keyword, ArrayList<String> googleContent,ArrayList<String> titleFrom, ArrayList<String> googleResults,boolean computeNDCG)
	{
		ArrayList<String> content=googleContent;
		ArrayList<String> title=titleFrom;
		int noOfResults;
		if(computeNDCG==true)
		 noOfResults = 7 ;
		else
			 noOfResults=6;
		String query=keyword;
		try
		{
		URL url = new URL( "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&" + "q="+ query +"%20site:ics.uci.edu%20-filetype:pdf%20-filetype:ppt%20-filetype:doc&userip=192.168.153.1&rsz="+noOfResults );
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "http://www.ics.uci.edu/");
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
		builder.append(line);
		}
		JSONObject json = new JSONObject(builder.toString());
		JSONArray jArray = json.getJSONObject("responseData").getJSONArray("results");
		
		for (int i = 0; i< jArray.length(); i++)
		{
		    JSONObject j = jArray.getJSONObject(i);
		    //System.out.println(j.toString());
		    String url_g= j.getString("url");
		    System.out.println(j.getString("url").substring(0,j.getString("url").length()));
		    googleResults.add(j.getString("url").substring(0,j.getString("url").length()));
		    content.add(j.getString("content").substring(0, 100));
		    System.out.println(j.getString("title").substring(0,j.getString("title").length()));
		    title.add(j.getString("title").substring(0,j.getString("title").length()));
		    if(i==3)
		    {
		    	urlFor.add(googleResults.get(i));
		    	titleFor.add(title.get(i));
		    	contentFor.add(content.get(i));
		    	
		    }
		    else if(i==4)
		    {
		    	urlFor.add(googleResults.get(i));
		    	titleFor.add(title.get(i));
		    	contentFor.add(content.get(i));
		    	
		    }
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return googleResults;
		
	}
	

  // from lia2e
  static FastVectorHighlighter getHighlighter() {
    FragListBuilder fragListBuilder = new SimpleFragListBuilder();
    FragmentsBuilder fragmentBuilder =
        new ScoreOrderFragmentsBuilder(
            BaseFragmentsBuilder.COLORED_PRE_TAGS,
            BaseFragmentsBuilder.COLORED_POST_TAGS);
    return new FastVectorHighlighter(true, true,
        fragListBuilder, fragmentBuilder);
  }
  
  public ArrayList<String> optimizedIndexResults(String keyword, ArrayList<String> luceneResultsOptimized,ArrayList<String> luceneResultsCon, ArrayList<String> luceneResultsTitl, ArrayList<String> googleResults, boolean withPageRank)
  {
	    String indexPath = "C:/Users/Sunil/Desktop/New_folder/NewIndex";
	    ArrayList<String> luceneResultsOptimizedTest=new ArrayList<String>();
	    ArrayList<String> luceneResultsContent1=new ArrayList<String>();
	    ArrayList<String> luceneResultsTitle1=new ArrayList<String>();
	    ArrayList<String> luceneResultsContent=luceneResultsCon;
	    ArrayList<String> luceneResultsTitle=luceneResultsTitl;
	    Directory dir;
	    try 
	    {
	      String QUERY = keyword;
	      dir = FSDirectory.open(new File(indexPath));
	      IndexReader ir = IndexReader.open(dir);
	      IndexSearcher searcher = new IndexSearcher(ir);

	      FileWriter writer = new FileWriter("C:/Users/Sunil/Desktop/tejas/abcd.html");
	      writer.write("<html>");
	      writer.write("<body>");
	      writer.write("<p>QUERY : " + QUERY + "</p>");

	      Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_36);
	      QueryParser parser = new QueryParser(Version.LUCENE_36, "contents", analyzer);
	      // Query query = parser.parse(QUERY);
	      Query query = new MultiFieldQueryParser(Version.LUCENE_36,
	          new String[] {"title", "url", "heading", "anchor", "bold", "contents"},
	          analyzer).parse(QUERY);
	      
	      FastVectorHighlighter highlighter = getHighlighter();
	      FieldQuery fieldQuery = highlighter.getFieldQuery(query);
	      TopDocs docs = searcher.search(query, 100);
	      //String abstractContent="";
	      //String abstractUrl="";
	      //String abstractTitle="";

	      for(ScoreDoc scoreDoc : docs.scoreDocs) {
	    	  
	        String snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "title", 100);
	        if (snippet != null) {
	        		
	          writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
	        }
	        
	        snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "url", 100);
	        if (snippet != null) {
	        	
	          writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
	        }
	        
	        snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "contents", 100);
	        if (snippet != null) {
	        		
	          writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
	        }
	         ScoreDoc match = scoreDoc;
		     Document doc = searcher.doc(match.doc);
		     luceneResultsOptimizedTest.add(doc.get("url"));
		     luceneResultsContent.add(snippet);
		     
		      if(doc.get("title").length()!=0)
		      luceneResultsTitle.add(doc.get("title"));
		      else
		      {
		    	  if(snippet.length()<5)
		    		  luceneResultsTitle.add(snippet);
		    	  else
		    		  luceneResultsTitle.add("Click To Go to Link");
		      }
	        
	      }
	      
	      
	      writer.write("</body></html>");
	      writer.close();
	      searcher.close();
	      
	     if(!withPageRank)
	     {
          luceneResultsOptimized=luceneResultsOptimizedTest;
          luceneResultsCon=luceneResultsContent;
          luceneResultsTitl=luceneResultsTitle;
	     }
          
	     else
	     {	 
          //page rank
          SearchApp sApp=new SearchApp();
	  	  sApp.pageRank();
	  	 
	  	 int i=0;
	  	 for (Entry<String, Integer> entry : sApp.hMap.entrySet())
	      {
	      	  int value=1; 
	           if(luceneResultsOptimizedTest.contains(entry.getKey()))
	           {
	        	   if(i==150) break;
	        	   if(entry.getKey().contains("luci.")||entry.getKey().contains("fano."))
	        	   {
	        		   
	        	   }
	        	   else
	        	   { if(value==1)
	        	     {
		        	   luceneResultsOptimized.add(entry.getKey());
		        	   int index=luceneResultsOptimized.indexOf(entry.getKey());
		        	   luceneResultsContent1.add(luceneResultsContent.get(index));
		        	   luceneResultsTitle1.add(luceneResultsTitle.get(index));
		        	   i++;
	        	     }
	        	   
	        	   }
	           }
	          
	      }
	  	 luceneResultsCon=luceneResultsContent1;
	  	 luceneResultsTitl=luceneResultsTitle1;
	     }
	      
        		  
	      ir.close();
	      dir.close();
	    } catch (Exception  e) {
	      e.printStackTrace();
	    }
	  
	    return luceneResultsOptimized;
	  
  }
  

  public static void main(String[] args) {
    
	  String indexPath = "C:/Users/Sunil/Desktop/New_folder/NewIndex";
	    Directory dir;
	    try 
	    {
	      String QUERY = "Information Retrieval";

	      dir = FSDirectory.open(new File(indexPath));
	      IndexReader ir = IndexReader.open(dir);
	      IndexSearcher searcher = new IndexSearcher(ir);

	      FileWriter writer = new FileWriter("C:/Users/Sunil/Desktop/tejas/abcd.html");
	      writer.write("<html>");
	      writer.write("<body>");
	      writer.write("<p>QUERY : " + QUERY + "</p>");

	      Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_36);
	      QueryParser parser = new QueryParser(Version.LUCENE_36, "contents", analyzer);
	      // Query query = parser.parse(QUERY);
	      Query query = new MultiFieldQueryParser(Version.LUCENE_36,
	          new String[] {"title", "url", "heading", "anchor", "bold", "contents"},
	          analyzer).parse(QUERY);
	      
	      FastVectorHighlighter highlighter = getHighlighter();
	      FieldQuery fieldQuery = highlighter.getFieldQuery(query);
	      TopDocs docs = searcher.search(query, 10);
	      String abstractContent="";
	      String abstractUrl="";
	      String abstractTitle="";

	      for(ScoreDoc scoreDoc : docs.scoreDocs) {
	    	  
	        String snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "title", 100);
	        if (snippet != null) {
	          abstractTitle=snippet;
	          writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
	        }
	        
	        snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "url", 100);
	        if (snippet != null) {
	          abstractUrl=snippet;
	          writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
	        }
	        
	        snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "contents", 100);
	        if (snippet != null) {
	          abstractContent=snippet;
	          writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
	        }
	        
	        
	        
	      }
	      writer.write("</body></html>");
	      writer.close();
	      searcher.close();

	      ir.close();
	      dir.close();
	    } catch (Exception  e) {
	      e.printStackTrace();
	    }
  }
}

