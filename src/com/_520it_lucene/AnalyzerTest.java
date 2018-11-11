package com._520it_lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class AnalyzerTest {

	String en = "oh my lady god";
	String cn = "迅雷不及掩耳盗铃大部分人";
	String msg = "小码哥教育lucene的课程了";
	
	public void analyzerMethod(Analyzer analyzer,String content) throws Exception{
		
		//Analyzer analyzer = new SimpleAnalyzer();//空格控制器，有空格就会分词
		TokenStream stream =analyzer.tokenStream("content",content);
		stream.reset();
		while (stream.incrementToken()) {
			System.out.println(stream);
			
		}
	}
	@Test
	public void testSimpleAnalyzer() throws Exception{
		analyzerMethod(new SimpleAnalyzer(), cn);
	}

	@Test
	public void testStandardAnalyzer() throws Exception{
		analyzerMethod(new StandardAnalyzer(), cn);
	}

	@Test
	public void testCJKAnalyzer() throws Exception{
		analyzerMethod(new CJKAnalyzer(), cn);
	}
	
	@Test
	public void testSmartChineseAnalyzer() throws Exception{
		analyzerMethod(new SmartChineseAnalyzer(), msg);
	}

	@Test
	public void testIKAnalyzer() throws Exception{
		analyzerMethod(new IKAnalyzer(), msg);
	}


	@Test
	public void testPerFieldAnalyzerWrapper() throws Exception{
		Map<String,Analyzer> analyzerMap = new HashMap<>();
		analyzerMap.put("en", new SimpleAnalyzer());
		analyzerMap.put("cn", new StandardAnalyzer());
		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(),analyzerMap);////如果都没有，则以默认的分词器，这里写的
		//会根据传入的字段名找对应的字段的分词器
		//如果都没有，则以默认的分词器
		TokenStream stream = wrapper.tokenStream("content", cn);
		stream.reset();
		while (stream.incrementToken()) {
			System.out.println(stream);
			
		}
	}
}
