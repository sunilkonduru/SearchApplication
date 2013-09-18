//package ir.assignments.indexing;
//package NDCG;
package support;

import java.util.ArrayList;

public class ComputeNDCGForSearch {

  // We assume these values of relevance for the top 5 results
  private final static int[] relevanceValues = {4, 3, 2, 3, 2, 1};
  
  public static double calculateIDCG(int[] relevanceValues) {
    double idcg = relevanceValues[0];

    for(int counter = 1; counter < relevanceValues.length; counter++)
      idcg += ((double)relevanceValues[counter]) / Math.log(counter + 1);
      
    return idcg;
  }

  public static double calculateIDCG() {
    // TODO need to hard code the value
    return calculateIDCG(relevanceValues);
  }

  public static double calculateNDCG(ArrayList<String> googleResults, ArrayList<String> luceneResults)  {
    return calculateNDCG(googleResults, luceneResults, 5);
  }

  public static double calculateNDCG(ArrayList<String> googleResults, ArrayList<String> luceneResults, int maxResults)  {

    if(luceneResults == null || googleResults == null || googleResults.size() == 0 || luceneResults.size() == 0)
      return 0.0;

    double dcg = 0;
    for(int counter = 0, score = 0, index = -1; counter < luceneResults.size(); counter++) {
      // find the position where the given result lies wrt the google search results
      index = googleResults.indexOf(luceneResults.get(counter));

      if(index >= 0 && index < relevanceValues.length) {  
        // get the score of the result from the index of the result in the google results set  
        score = relevanceValues[index];
      
        /**
         * Update the DCG with the contribution from this result. 
         * For the first result (here 0th result is the first result), the contribution to DCG is its score itself. 
         * For all results apart from the first one , the contribution to DCG is:                                                 
         *    (relevance of the result)  /   (log to base 2 of the index in the result set)
         */         
        if(counter > 0)
          dcg += ((double)score) / Math.log(counter+1);
        else
          dcg += (double)score;
      }
      else
        score = 0;
    }
    
    return (dcg / calculateIDCG());
  }

  public static void computeNDCG(ArrayList<String> googleResults,ArrayList<String> luceneResults) {
    // Test hub
	  System.out.println("NDCG = "+ calculateNDCG(googleResults, luceneResults));  
  }
}
