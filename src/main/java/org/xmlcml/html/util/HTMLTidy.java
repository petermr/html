package org.xmlcml.html.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.w3c.tidy.Tidy;
import org.xmlcml.cml.base.CMLUtil;

public class HTMLTidy {
	/**
	 * reads HTML in inputStream and tidies it.
	 * First with HTML tidy (using as many cleaning options as possible
	 * then excises the DOCTYP and namespace from result
	 * Tidy may throw warnings and errors to syserr than cannot be 
	 * removed from console
	 * @param inputStream if null throws IOException
	 * @return document with some HTML root element
	 * @throws IOException
	 */
	public static Document htmlTidy(InputStream inputStream) throws IOException {
	    	
		if (inputStream == null) {
			throw new RuntimeException("Null input for HTMLTidy");
		}
		List<String> lines = IOUtils.readLines(inputStream);
    	lines = preTidy(lines);
    	Tidy tidy = createTidyWithOptions();
    	inputStream = createInputStream(lines);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	tidy.parse(inputStream, baos);
    	baos.close();
    	Document document = null;
    	String baosS0 = ""+new String(baos.toByteArray());
    	if (baosS0.length() > 0) {
    		document = CMLUtil.stripDTDAndOtherProblematicXMLHeadings(baosS0);
    	}
    	return document;
    }

	private static InputStream createInputStream(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line+" ");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes());
		return bais;
	}

	private static List<String> preTidy(List<String> lines) {
		List<String> outLines = new ArrayList<String>();
		for (String line : lines) {
			String outLine = replaceBadTags(line, "it", "i");
			outLines.add(outLine);
		}
		return outLines;
	}

	private static ByteArrayOutputStream preTidy(ByteArrayOutputStream baos) {
		byte[] ba = baos.toByteArray();
		String s = new String(ba);
		System.out.println(ba.length+" "+s);
		int i = 0;
		while (i != -1) {
			i = s.indexOf("it", i);
			System.out.println(">> "+s.substring(Math.max(0, i-5), Math.min(s.length(), i+10)));
		}
		s = replaceBadTags(s, "it", "i");
		baos = new ByteArrayOutputStream();
		try {
			baos.write(s.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("Cannot write BAOS in HTMLTidy", e);
		}
		return baos;
	}

	private static String replaceBadTags(String s, String tag, String newTag) {
		s = s.replaceAll("<"+tag+">", "<"+newTag+">");
		s = s.replaceAll("</"+tag+">", "</"+newTag+">");
		return s;
	}

	private static Tidy createTidyWithOptions() {
		Tidy tidy = new Tidy();
    	tidy.setDocType(null);
    	tidy.setXmlOut(true);
    	tidy.setDropEmptyParas(true);
    	tidy.setDropFontTags(true);
    	tidy.setMakeClean(true);
    	tidy.setNumEntities(true);
    	tidy.setXHTML(true);
    	tidy.setQuiet(true);
    	tidy.setQuoteMarks(true);
    	tidy.setShowWarnings(false);
		return tidy;
	}

	public static Element convertStringToXHTML(String s) {
		Element element = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
		try {
			Document document = htmlTidy(bais);
			if (document == null) {
				return null;
			}
			element = document.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("parse: "+e);
		}
		return element;
	}
}
