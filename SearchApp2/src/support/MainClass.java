package support;
//
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.List;
//import com.google.gson.Gson;
// 
//public class MainClass {
//	public static void main(String[] args) throws IOException {
//		String keyword="bren school";
//        for(int i=0;i<10;i++)
//        {
//		String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=5&start="+ i +"&q=";
//		String query = keyword + " site:ics.uci.edu";
//		String charset = "UTF-8";
// 
//		URL url = new URL(address + URLEncoder.encode(query, charset));
//		Reader reader = new InputStreamReader(url.openStream(), charset);
//		GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
// 
//		// Show title and URL of 1st result and the the 4th results
// 
//	    System.out.println(results.getResponseData().getResults().get(0).getTitle());
//		System.out.println(results.getResponseData().getResults().get(0).getUrl());
// 
//		System.out.println(results.getResponseData().getResults().get(3).getTitle());
//		System.out.println(results.getResponseData().getResults().get(3).getUrl());
//        }
// 
//	}
//}
// 
//class GoogleResults{
// 
//    private ResponseData responseData;
//    public ResponseData getResponseData() { return responseData; }
//    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
//    public String toString() { return "ResponseData[" + responseData + "]"; }
// 
//    static class ResponseData {
//        private List<Result> results;
//        public List<Result> getResults() { return results; }
//        public void setResults(List<Result> results) { this.results = results; }
//        public String toString() { return "Results[" + results + "]"; }
//    }
// 
//    static class Result {
//        private String url;
//        private String title;
//        public String getUrl() { return url; }
//        public String getTitle() { return title; }
//        public void setUrl(String url) { this.url = url; }
//        public void setTitle(String title) { this.title = title; }
//        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
//    }
//}
