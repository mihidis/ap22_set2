package anaktisi;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory; 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;



public class Main {

	static private String indexDirectoryPath = "indexedFiles";
	static private final String inputFiles = "inputFiles";
	static private final Path inputPath = Paths.get(inputFiles);
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	
	public static void main (String[] args) throws Exception{
		
		StandardAnalyzer analyzer = new StandardAnalyzer();

		Directory index= FSDirectory.open((Paths.get(indexDirectoryPath)));
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    config.setOpenMode(OpenMode.CREATE_OR_APPEND);
	        
	    IndexWriter w = new IndexWriter(index, config);
	        
		Scanner sc = new Scanner(new File(inputPath+"\\netflix_titles.csv"));
		sc.useDelimiter(",");
		sc.nextLine();
	
		while(sc.hasNextLine()) {
			String[] line = sc.nextLine().split(",");
			for(int j=0;j<line.length;j++) {
				addDoc(w,line);
			}
		}
		w.close();
		sc.close();
		
		
		Scanner input= new Scanner(System.in);
		while(true) {
			System.out.println("What field do u want to search in ? ");
			System.out.println("The fields are listed above. You give the number of the field you want to search! ");
			System.out.println("show_id 1, type 2, title 3, director 4,cast 5, country 6, release_year 8, rating 9, listed_in 11, description 12!");
			int queryIndex=input.nextInt();
	
			input.nextLine();
		
		
			System.out.println("Give the query!");
			String query=input.nextLine();
			query=query+"*";
			
			
			Query q = new QueryParser(findIndex(queryIndex),new StandardAnalyzer()).parse(query.toString());
			
			int hitsPerPage = 10;
	
			IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs foundDocs = searcher.search(q, hitsPerPage);
	    
	        System.out.println("Total Results : " + foundDocs.totalHits);
	        
	        
	        int k=1;
	        for(ScoreDoc sd : foundDocs.scoreDocs)
	        {
	        	Document d = searcher.doc(sd.doc);
	        	System.out.println(k+". "+d.get(findIndex(queryIndex))+". Movie id: "+d.get("show_id"));
	        	k++;
	        }
	        System.out.println("\nDo you want to search another query ?");
	        System.out.println("If yes press 1. If not press 2.");
	        if(input.nextInt()==2) {
	        	reader.close();
		        input.close();
		        break;
	        }else {
	        	input.nextLine();
	        	System.out.println("\n");
	        }
	       
		}
	}
	
	private static void addDoc(IndexWriter w, String[] indexes) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("show_id", indexes[0], Field.Store.YES));
        doc.add(new TextField("type", indexes[1], Field.Store.YES));
        doc.add(new TextField("title", indexes[2], Field.Store.YES));
        doc.add(new TextField("director", indexes[3], Field.Store.YES));
        doc.add(new TextField("cast", indexes[4], Field.Store.YES));
        doc.add(new TextField("country", indexes[5], Field.Store.YES));
        doc.add(new StringField("date_added", indexes[6], Field.Store.YES));
        doc.add(new TextField("release_year", indexes[7], Field.Store.YES));
        doc.add(new TextField("rating", indexes[8], Field.Store.YES));
        doc.add(new StringField("duration", indexes[9], Field.Store.YES));
        doc.add(new TextField("listed_in", indexes[10], Field.Store.YES));
        doc.add(new TextField("description", indexes[11], Field.Store.YES));

        w.updateDocument(new Term("path",indexDirectoryPath),doc);
    }

	private static String findIndex(int q) {
		if(q==1) {
			return "show_id";
		}else if(q==2) {
			return "type";
		}else if(q==3) {
			return "title";
		}else if(q==4) {
			return "director";
		}else if(q==5) {
			return "cast";
		}else if(q==6) {
			return "country";
		}else if(q==7) {
			return "release_year";
		}else if(q==9) {
			return "rating";
		}else if(q==11) {
			return "listed_in";
		}else if(q==12){
			return "description";
		}else {
			return "title";  // default query field
		}
	}
}