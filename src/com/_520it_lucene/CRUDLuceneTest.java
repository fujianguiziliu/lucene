package com._520it_lucene;

import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class CRUDLuceneTest {
	String content1 = "hello world";
	String content2 = "hello java world";
	String content3 = "hello lucene world";
	String  indexPath = "crud";
	private Analyzer analyzer = new StandardAnalyzer();
	
	@Test
	public void testCreateIndex() throws Exception{
		
		Directory d = FSDirectory.open(new File(indexPath));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
		IndexWriter writer = new IndexWriter(d, config);
		
		FieldType type = new FieldType();
		type.setIndexed(true);//是否需要索引
		type.setStored(true);//是否需要存储
		
		
		Document doc1 = new Document();
		doc1.add(new Field("title","doc1",type));
		doc1.add(new Field("content",content1,type));
		writer.addDocument(doc1);

		
		Document doc2 = new Document();
		doc2.add(new Field("title","doc2",type));
		doc2.add(new Field("content",content2,type));
		writer.addDocument(doc2);

		
		Document doc3 = new Document();
		doc3.add(new Field("title","doc3",type));
		doc3.add(new Field("content",content3,type));
		writer.addDocument(doc3);
		
		writer.commit();
		writer.close();
		testSearch();
	}
	
	@Test
	public void testUpdate() throws Exception{
		Directory d = FSDirectory.open(new File(indexPath));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
		IndexWriter writer = new IndexWriter(d, config);
		Term term =new Term("title","doc2");//更新条件
		Document updateDoc =new Document();//更新之后的文档对象
		FieldType type = new FieldType();
		type.setIndexed(true);
		type.setStored(true);
		updateDoc.add(new Field("title","doc2",type));
		updateDoc.add(new Field("content","hello,这是更新后的文件",type));
		writer.updateDocument(term, updateDoc);
		//更新对象
		writer.commit();
		writer.close();
		testSearch();
	}
	
	@Test
	public void testDelete() throws Exception{
		Directory d = FSDirectory.open(new File(indexPath));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
		IndexWriter writer = new IndexWriter(d, config);
		QueryParser parser = new QueryParser("title", analyzer);
		Query query = parser.parse("doc3");
		/*Term term =new Term("title","doc2");//更新条件
		writer.deleteDocuments(term);*/
		writer.deleteDocuments(query);
		writer.commit();
		writer.close();
		testSearch();
	}
	public void testSearch() throws Exception{
		//创建一个索引查询器
		Directory d = FSDirectory.open(new File(indexPath));
		IndexReader r = DirectoryReader.open(d);
		IndexSearcher searcher = new  IndexSearcher(r);
		QueryParser parser = new QueryParser("content",analyzer);
		Query query = parser.parse("hello");
		TopDocs tds = searcher.search(query, 1000);
		System.out.println("符合条件的记录数："+tds.totalHits);
	    ScoreDoc[] scoreDocs =  tds.scoreDocs;
	    Document doc = null;
	    for (ScoreDoc sd : scoreDocs) {
	    	System.out.println("*********************");
			System.out.println("文档分数：" +sd.score);
			System.out.println("文档编号：" +sd.doc);
		    doc = searcher.doc(sd.doc);
		    System.out.println("标题内容：" +doc.get("title"));
		    System.out.println("正文内容：" +doc.get("content"));
		}
	}

}
