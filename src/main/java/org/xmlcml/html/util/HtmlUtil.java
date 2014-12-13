package org.xmlcml.html.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.xml.XMLConstants;
import org.xmlcml.xml.XMLUtil;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUtil {

	private final static Logger LOG = Logger.getLogger(HtmlUtil.class);
	
    /** XPathContext for Html.
     */
    public static XPathContext XHTML_XPATH = new XPathContext("h", XMLConstants.XHTML_NS);

	public static List<HtmlElement> getQueryHtmlElements(HtmlElement htmlElement, String xpath) {
		List<Element> elements = XMLUtil.getQueryElements(htmlElement, xpath, XHTML_XPATH);
		List<HtmlElement> htmlElements = new ArrayList<HtmlElement>();
		for (Element element : elements) {
			if (!(element instanceof HtmlElement)) {
				throw new RuntimeException("Element was not HtmlElement: "+element.toXML());
			}
			htmlElements.add((HtmlElement)element);
		}
		return htmlElements;
	}

	/** extracts nodes and their values.
	 * 
	 * @param htmlElement ancestor
	 * @param xpath can include h:* elements
	 * @return list of string values of nodes
	 */
	public static List<String> getQueryHtmlStrings(Element htmlElement, String xpath) {
		Nodes nodes = htmlElement.query(xpath, XHTML_XPATH);
		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < nodes.size(); i++) {
			stringList.add(nodes.get(i).getValue());
		}
		return stringList;
	}

	/** read file and subclass elements to HtmlElement.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElement(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		return readAndCreateElementUsingJsoup(is);
	}
	
	/** NYI - headless browser .
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElementUsingHtmlUnit(URL url) throws Exception {
	    WebClient webClient = new WebClient();
	    HtmlPage page = webClient.getPage(url.toString());
	    String pageAsXml = page.asXml();
	    webClient.closeAllWindows();
		HtmlElement htmlElement = null;
		try {
			Element xmlElement = XMLUtil.parseXML(pageAsXml);
			boolean ignoreNamespaces = true;
			boolean abort = false;
			htmlElement = HtmlElement.create(xmlElement, abort, ignoreNamespaces);
		} catch (Exception e) {
			LOG.error("cannot parse HTML "+pageAsXml, e);
		}
		return htmlElement;
	}


	/** parses HTML into dom if possible.
	 * 
	 * @param is
	 * @return null if fails
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElementUsingJsoup(InputStream is) throws Exception {
		String s = IOUtils.toString(is, "UTF-8");
		org.jsoup.nodes.Document doc = Jsoup.parse(s);
		String xmlDoc = doc.html();
		xmlDoc = xmlDoc.replaceAll("&times;", "&#214;");
		xmlDoc = xmlDoc.replaceAll("&deg;", "&#176;");
		xmlDoc = xmlDoc.replaceAll("&[^;]*;", "[dummy]");
		HtmlElement htmlElement = null;
		try {
			Element xmlElement = XMLUtil.parseXML(xmlDoc);
			boolean ignoreNamespaces = true;
			boolean abort = false;
			htmlElement = HtmlElement.create(xmlElement, abort, ignoreNamespaces);
		} catch (Exception e) {
			LOG.error("cannot parse HTML"+e+"; "+xmlDoc);
		}
		return htmlElement;
	}

	/** JSoup does not add XHTML namespace to all elements, so add it.
	 * 
	 * @param xmlElement
	 */
	private static void addHTMLNamespace(Element xmlElement) {
		Nodes nodes = xmlElement.query("//*[namespace-uri()='']");
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element)nodes.get(i);
			element.addNamespaceDeclaration("",  XMLConstants.XHTML_NS);
		}
	}

	/** read file and subclass elements to HtmlElement.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElement(URL url) throws Exception {
		LOG.debug("opening URL Stream");
//		InputStream is = url.openStream();
		HtmlElement htmlElement = HtmlUtil.readAndCreateElementUsingHtmlUnit(url);
//		if (true) throw new RuntimeException("opened URL Stream: NEEDS JSOUP***");
//		Document doc = (url == null) ? null : new Builder().build(is);
		LOG.debug("built document");
//		HtmlElement htmlElement = (doc == null)? null : HtmlElement.create(doc.getRootElement());
		return htmlElement;
	}

}
