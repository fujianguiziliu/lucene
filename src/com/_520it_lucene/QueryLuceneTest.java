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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class QueryLuceneTest {

	String content1 = "hello world";
	String content2 = "hello java world";
	String content3 = "hello lucene world";
	String  indexPath = "query";
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
		doc1.add(new Field("inputtime","20170715",type));
		writer.addDocument(doc1);

		
		Document doc2 = new Document();
		doc2.add(new Field("title","doc2",type));
		doc2.add(new Field("content",content2,type));
		doc2.add(new Field("inputtime","20170716",type));
		writer.addDocument(doc2);

		
		Document doc3 = new Document();
		doc3.add(new Field("title","doc3",type));
		doc3.add(new Field("content",content3,type));
		doc3.add(new Field("inputtime","20170717",type));
		writer.addDocument(doc3);
		writer.commit();
		writer.close();
		//search();
	}
	
	public void search(String content) throws Exception{
		//创建一个索引查询器
		Directory d = FSDirectory.open(new File(indexPath));
		IndexReader r = DirectoryReader.open(d);
		IndexSearcher searcher = new  IndexSearcher(r);
		QueryParser parser = new QueryParser("content",analyzer);
		Query query = parser.parse(content);
		System.out.println(query.getClass());
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
		    System.out.println("录入时间：" +doc.get("inputtime"));
		}
	}
	
	public void search(Query query) throws Exception{
		System.out.println(query.getClass());
		//创建一个索引查询器
		Directory d = FSDirectory.open(new File(indexPath));
		IndexReader r = DirectoryReader.open(d);
		IndexSearcher searcher = new  IndexSearcher(r);
		
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
		    System.out.println("录入时间：" +doc.get("inputtime"));
		}
	}
	//查询所有
	@Test
	public void test1() throws Exception{
		//search("*:*");
		search(new MatchAllDocsQuery());
	}
	
	@Test
	public void test2() throws Exception{
		//search("title:doc1");
		search(new TermQuery(new Term("title","doc1")));
	}
	
	@Test
	public void test3() throws Exception{
		//search("content:\"hello world\"");
		PhraseQuery query = new PhraseQuery();
		query.add(new Term("content","hello"));
		query.add(new Term("content","world"));
		search(query);
	}
	
	@Test
	public void test4() throws Exception{
		//search("content:lu?ene");//?表示单个字符的匹配
		//search("content:l*ne");//*表示多个字符的匹配
		WildcardQuery query = new WildcardQuery(new Term("content","l*ne"));
		search(query);
		
	}
	@Test
	public void test5() throws Exception{
		//search("content:lucenx~1");//~表示容错字符的匹配
		FuzzyQuery query =new FuzzyQuery(new Term("content","lucexe"),1);
		search(query);
		
		
	}
	
	@Test
	public void test6() throws Exception{
		search("content:\"hello world\"~1");//~1表示可以插入一个单词，N表示可以插入n个
		
		PhraseQuery  query =new PhraseQuery();
		query.add(new Term("content","hello"));
		query.add(new Term("content","world"));
		query.setSlop(1);
		search(query);
	}
	
	@Test
	public void test7() throws Exception{
		//search("inputtime:{20170715 TO 20170717]");
		TermRangeQuery query = new TermRangeQuery("inputtime", new BytesRef("20170715"), new BytesRef("20170717"), false, false);
		search(query);
		
	}
	
	@Test
	public void test8() throws Exception{
		//search("content:hello  AND inputtime:{20170715 TO 20170717]");&&这个和and一样
		BooleanQuery query=new BooleanQuery();
		query.add(new TermQuery(new Term("content","hello")),BooleanClause.Occur.MUST);
		query.add(new TermRangeQuery("inputtime", new BytesRef("20170715"), new BytesRef("20170717"), false, false), BooleanClause.Occur.MUST);
		search(query);
	}
	
	@Test
	public void test9() throws Exception{
		//search("content:lucene OR inputtime:{20170715 TO 20170717]");// 这个符号 ||和or一样
		BooleanQuery query=new BooleanQuery();
		query.add(new TermQuery(new Term("content","lucene")),BooleanClause.Occur.SHOULD);
		query.add(new TermRangeQuery("inputtime", new BytesRef("20170715"), new BytesRef("20170717"), false, false), BooleanClause.Occur.SHOULD);
		search(query);
	}
	

	@Test
	public void test10() throws Exception{
		//search("content:hello NOT inputtime:{20170715 TO 20170717]");// 这个符号 !和not一样
		BooleanQuery query=new BooleanQuery();
		query.add(new TermQuery(new Term("content","hello")),BooleanClause.Occur.MUST);
		query.add(new TermRangeQuery("inputtime", new BytesRef("20170715"), new BytesRef("20170717"), false, false), BooleanClause.Occur.MUST_NOT);
		search(query);
	}
	@Test
	public void test11() throws Exception{
		//search("content:lucene^10 java");// 
		BooleanQuery query = new BooleanQuery();
		TermQuery termQuery = new TermQuery(new Term("content","lucene"));
		termQuery.setBoost(10);
		query.add(termQuery,BooleanClause.Occur.SHOULD);
		query.add(new TermQuery(new Term("content","java")),BooleanClause.Occur.SHOULD);
		search(query);
		
	}
}
