package org.xmlcml.html.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.xml.XMLConstants;
import org.xmlcml.xml.XMLUtil;

public class HtmlUtil {

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
		return readAndCreateElement(is);
	}

	public static HtmlElement readAndCreateElement(InputStream is) throws Exception {
		Document doc = (is == null) ? null : new Builder().build(is);
		HtmlElement htmlElement = (doc == null)? null : HtmlElement.create(doc.getRootElement());
		return htmlElement;
	}

	/** read file and subclass elements to HtmlElement.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElement(URL url) throws Exception {
		Document doc = (url == null) ? null : new Builder().build(url.openStream());
		HtmlElement htmlElement = (doc == null)? null : HtmlElement.create(doc.getRootElement());
		return htmlElement;
	}

}
