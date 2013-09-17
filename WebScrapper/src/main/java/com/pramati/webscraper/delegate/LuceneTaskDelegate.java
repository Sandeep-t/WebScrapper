package com.pramati.webscraper.delegate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.db.model.ExtractedDataDetails;
import com.pramati.webscraper.service.WebScrapperDbService;

public class LuceneTaskDelegate {

	@Autowired
	WebScrapperDbService dbService;

	@Autowired
	ExecutorService executorService;
	
	
	private static final Logger LOGGER = Logger.getLogger(LuceneTaskDelegate.class);

	StandardAnalyzer analyzer = null;

	IndexWriterConfig config = null;

	Directory index = null;

	IndexWriter idxWriter = null;

	private BlockingQueue<ExtractedDataDetails> dataDetailsQueue = new LinkedBlockingQueue<ExtractedDataDetails>();

	public LuceneTaskDelegate() {
		super();
	}

	public LuceneTaskDelegate(String indexDirPath) throws IOException {
		//clearIndexDirectory(indexDirPath);
		index = FSDirectory.open(new File(indexDirPath));

		analyzer = new StandardAnalyzer(Version.LUCENE_44);
		config = new IndexWriterConfig(Version.LUCENE_44, analyzer);
		idxWriter = new IndexWriter(index, config);

	}

	public void addTodataDetailsQueue(ExtractedDataDetails details) throws InterruptedException {
		dataDetailsQueue.put(details);
		
	}

	public List<Document> searchForString(String queryString) throws ParseException, IOException {

		List<Document> docList = new ArrayList<Document>();

		Query q = new QueryParser(Version.LUCENE_44, "htmldata", analyzer).parse(queryString);

		int topHits = 100000;

		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		ScoreDoc[] hits = searcher.search(q, topHits).scoreDocs;

		for (int i = 0; i < hits.length; ++i) {

			docList.add(searcher.doc(hits[i].doc));

		}
		reader.close();
		return docList;
	}

	public Document getDocumentForLucene(ExtractedDataDetails details) {
		Document doc = new Document();
		doc.add(new TextField("htmldata", details.getHtmlData(), Field.Store.NO));
		doc.add(new StringField("docId", details.getId(), Field.Store.YES));
		return doc;
	}

	public  void  createIndexer(List<Document> docs) throws IOException {
		idxWriter.addDocuments(docs, analyzer);
		//closeIndexWriter();
	}

	public void createIndexer(Document doc) throws IOException {
		idxWriter.addDocument(doc, analyzer);
		commitIndexWriter();
	}

	public void indexDbDataonStartup() throws IOException {
		List<Document> docs = new ArrayList<Document>();
		List<ExtractedDataDetails> details = dbService.getAllRecords();
		for (ExtractedDataDetails detail : details) {
			docs.add(getDocumentForLucene(detail));
		}
		createIndexer(docs);
	}

	public void closeIndexWriter() throws IOException {
		idxWriter.close();
	}
	
	public void commitIndexWriter() throws IOException {
		idxWriter.commit();
	}

	public void updateIndexData(ExtractedDataDetails detail) throws IOException {
		//LOGGER.debug("Updating Lucne indexing for "+detail);
		createIndexer(getDocumentForLucene(detail));
	}

	public void clearIndexDirectory(String indexDirPath) throws IOException {
		FileUtils.cleanDirectory(new File(indexDirPath));

	}

	public void startIndexer() {
		Runnable pooler = new Runnable() {
			@Override
			public void run() {
				
				while (!Thread.interrupted()) {
					
				
					ExtractedDataDetails data;
					while((data=dataDetailsQueue.poll())!=null ) {
					
						try {
						
							LOGGER.debug("Processsing For Lucene indexing of "+ data.getUrl() + " ID " + data.getId());
							updateIndexData(data);
						}
						catch (IOException ioe) {
							LOGGER.error("IOException occured while applying index using lucene for the record mentioned here "
											+ data.getUrl() + " ID " + data.getId(),ioe);
							ioe.printStackTrace();
						}
					}
				}
				try {
					LOGGER.debug("Closing index writer");
					closeIndexWriter();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		LOGGER.debug("Submitting lucene index pooler for processing");
		executorService.submit(pooler);
	}

}
