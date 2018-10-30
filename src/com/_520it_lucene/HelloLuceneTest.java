package com._520it_lucene;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class HelloLuceneTest {

	String content1 = "hello world";
	String content2 = "hello java world";
	String content3 = "hello lucene world";
	String  indexPath = "hello";
	private Analyzer analyzer = new StandardAnalyzer();
	
	@Test
	public void testCreateIndex() throws Exception{
		//锟斤拷锟斤拷锟斤拷锟斤拷写锟斤拷锟斤拷
		Directory d = FSDirectory.open(new File(indexPath));//锟斤拷锟斤拷锟斤拷要锟斤拷诺锟轿伙拷锟�
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
		IndexWriter writer = new IndexWriter(d, config);
		//通锟斤拷写锟斤拷锟斤拷写锟斤拷锟侥碉拷锟斤拷息
		FieldType type = new FieldType();
		type.setIndexed(true);
		type.setStored(true);
		
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
	}
}
