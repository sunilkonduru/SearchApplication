//package ir.assignments.indexing;
//package NDCG;
package support;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

/** 
 * Index all text files under a directory.
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFiles {

  // Pattern to identify urls belonging to the ics.uci.com
  public static final Pattern ICS_FILTER = Pattern.compile("^(https?)://(.*\\.)?ics.uci.edu/.*");

  /** 
   * based on 
   * http://www.mkyong.com/regular-expressions/how-to-validate-image-file-extension-with-regular-expression/  
   **/
  public static final Pattern IMAGE_PATTERN = Pattern.compile("([^\\s]+(\\.(?i)(" +
      "icsgz|css|png|js|bmp|gif|jpg|jpeg|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|" +
      "mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|exe|bin|deb|tgz|bz2|ova|sas|tar|mso|iso|jar" + 
      "cbr|ar|bin|rpm|java|py|cpz|cpio|lzma|z|lz|xz|7z|c|cpp|ico))$)");

  static final String RECORD_DELIMITER = "!@#$%^&*()!@#$%^&*()!@#$%^&*()";
  private IndexFiles() {}

  /** 
   * Index all text files under a directory. 
   */
  public static void main(String[] args) {
    String usage = "java -jar <name.of.the.jar.file>"
        + " [-index INDEX_PATH] [-crawlFile PATH_TO_CRAWL_DATA_FILE] [-update]\n\n"
        + "This indexes the urls from PATH_TO_CRAWL_DATA_FILE, creating a Lucene index"
        + "in INDEX_PATH that can be searched with SearchFiles";
    String indexPath = "index";
    String crawlFile = null;

    for(int i=0;i<args.length;i++) {
      if ("-index".equals(args[i])) {
        indexPath = args[i+1];
        i++;
      } else if ("-crawlFile".equals(args[i])) {
        crawlFile = args[i+1];
        i++;
      }
    }

    if (crawlFile == null) {
      System.err.println("Usage: " + usage);
      System.exit(1);
    }

    final File docDir = new File(crawlFile);
    if (!docDir.exists() || !docDir.canRead()) {
      System.out.println("Document '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }

    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");

      Directory dir = FSDirectory.open(new File(indexPath));
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, analyzer);

      iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g)
      iwc.setRAMBufferSizeMB(2000.0);

      IndexWriter writer = new IndexWriter(dir, iwc);

      int counter = 1;
      // do not try to index files that cannot be read
      if (docDir.canRead()) {

        FileInputStream fis;
        try {
          fis = new FileInputStream(docDir);
        } catch (FileNotFoundException fnfe) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help
          return;
        }

        try {
          BufferedReader br = new BufferedReader(new FileReader(docDir));
          String line;
          // make a new, empty document
          Document doc = new Document();

          while((line = br.readLine()) != null) {
            if(line.startsWith("URL:") && shouldStore(line.substring(line.indexOf(":", 0) + 1, line.length()))) {
              doc.removeField("url");
              doc.removeField("title");
              doc.removeField("anchor");
              doc.removeField("heading");
              doc.removeField("contents");
              doc.removeField("bold");

              Field urlField = new Field("url", line.substring(line.indexOf(":", 0) + 1, line.length()), Field.Store.YES, Field.Index.ANALYZED);
              doc.add(urlField);
              line = br.readLine();  // html length
              line = br.readLine();  // Link size
              line = br.readLine();  // title size
              Field titleField = new Field("title", line.substring(line.indexOf(":", 0) + 1, line.length()), Field.Store.YES, Field.Index.ANALYZED);
              doc.add(titleField);

              line = br.readLine();  // anchor text
              Field anchorField = new Field("anchor", line.substring(line.indexOf(":", 0) + 1, line.length()), Field.Store.NO, Field.Index.ANALYZED);
              doc.add(anchorField);

              line = br.readLine();  // Heading text
              Field headingField = new Field("heading", line.substring(line.indexOf(":", 0) + 1, line.length()), Field.Store.NO, Field.Index.ANALYZED);
              doc.add(headingField);
              
              line = br.readLine();  // Bold text
             Field boldField = new Field("bold", line.substring(line.indexOf(":", 0) + 1, line.length()), Field.Store.NO, Field.Index.ANALYZED);
              doc.add(boldField);

              StringBuilder content = new StringBuilder();
              line = br.readLine(); // Text
              line.concat(line.substring(line.indexOf(":", 0) + 1, line.length()));
              content.append(line);

              line = br.readLine(); // next line in text
              while(line.compareTo(RECORD_DELIMITER) != 0) {
                try {
                  //                 content.append(" " + line);
                  line = br.readLine();
                } catch (Exception e) {
                  System.out.println(line + " " + e.getMessage());
                  e.printStackTrace();
                }
              }

              Field textField = new Field("contents", content.toString(), Field.Store.NO, Field.Index.ANALYZED);
              doc.add(textField);

              // Existing index (an old copy of this document may have been indexed) so 
              // we use updateDocument instead to replace the old one matching the exact 
              // path, if present:
              System.out.println(counter + " " + urlField.stringValue());
              writer.updateDocument(new Term("path", docDir.getPath()), doc);      
              counter++;
            }
          }
          br.close();
        } finally {
          fis.close();
        }
      }

      writer.close();
      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
          "\n with message: " + e.getMessage());
    }
  }

  /**
   * Runs all the filters to check of the given url must be allow
   * 
   * @param url 
   * @return bool indicating if the url must be allowed
   */
  public static boolean shouldStore(String url) { 
    if (ICS_FILTER.matcher(url).matches()) {
      if( !url.contains("https") &&  
          !url.contains("drzaius.ics.uci.edu") &&
          !url.contains("calendar.ics.uci.edu") && 
          !url.contains("ftp") &&
          !url.contains("ftp.uci.edu") &&
          !url.contains("informatics.uci.edu") &&
          !url.contains("archive") &&
          !url.contains(".jpg") &&
          !url.contains(".css") &&
          !url.contains(".pdf") &&
          !url.contains(".png") &&
          !url.contains(".ppt") &&
          !url.contains("image=")) {
        return true; //!IMAGE_PATTERN.matcher(url).matches();
      }
    }
    return false;
  }
}