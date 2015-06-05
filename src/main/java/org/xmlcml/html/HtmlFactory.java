package org.xmlcml.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.xmlcml.graphics.svg.SVGConstants;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.html.util.HtmlUtil;
import org.xmlcml.xml.XMLUtil;

/** generates HtmlElement from unnamespaced Elements.
 * 
 * Allows for customisation of treatment such as substituing unusual or incorrect elements.
 * 
 * 
 * @author pm286
 *
 */
public class HtmlFactory {

	private static final Logger LOG = Logger.getLogger(HtmlFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Map<String, String> DEFAULT_REPLACEMENT_MAP = null;
    static final String[][] ESCAPES_XML = {
        {"\"",     "quot"}, // " - double-quote
        {"&",      "amp"}, // & - ampersand
        {"<",      "lt"}, // < - less-than
        {">",      "gt"}, // > - greater-than
    };
    
    private static final String[][] ESCAPES_HTML = {
        // Mapping to escape ISO-8859-1 characters to their named HTML 3.x equivalents.
        {"\u00A0", "nbsp"}, // non-breaking space
        {"\u00A1", "iexcl"}, // inverted exclamation mark
        {"\u00A2", "cent"}, // cent sign
        {"\u00A3", "pound"}, // pound sign
        {"\u00A4", "curren"}, // currency sign
        {"\u00A5", "yen"}, // yen sign = yuan sign
        {"\u00A6", "brvbar"}, // broken bar = broken vertical bar
        {"\u00A7", "sect"}, // section sign
        {"\u00A8", "uml"}, // diaeresis = spacing diaeresis
        {"\u00A9", "copy"}, // �� - copyright sign
        {"\u00AA", "ordf"}, // feminine ordinal indicator
        {"\u00AB", "laquo"}, // left-pointing double angle quotation mark = left pointing guillemet
        {"\u00AC", "not"}, // not sign
        {"\u00AD", "shy"}, // soft hyphen = discretionary hyphen
        {"\u00AE", "reg"}, // �� - registered trademark sign
        {"\u00AF", "macr"}, // macron = spacing macron = overline = APL overbar
        {"\u00B0", "deg"}, // degree sign
        {"\u00B1", "plusmn"}, // plus-minus sign = plus-or-minus sign
        {"\u00B2", "sup2"}, // superscript two = superscript digit two = squared
        {"\u00B3", "sup3"}, // superscript three = superscript digit three = cubed
        {"\u00B4", "acute"}, // acute accent = spacing acute
        {"\u00B5", "micro"}, // micro sign
        {"\u00B6", "para"}, // pilcrow sign = paragraph sign
        {"\u00B7", "middot"}, // middle dot = Georgian comma = Greek middle dot
        {"\u00B8", "cedil"}, // cedilla = spacing cedilla
        {"\u00B9", "sup1"}, // superscript one = superscript digit one
        {"\u00BA", "ordm"}, // masculine ordinal indicator
        {"\u00BB", "raquo"}, // right-pointing double angle quotation mark = right pointing guillemet
        {"\u00BC", "frac14"}, // vulgar fraction one quarter = fraction one quarter
        {"\u00BD", "frac12"}, // vulgar fraction one half = fraction one half
        {"\u00BE", "frac34"}, // vulgar fraction three quarters = fraction three quarters
        {"\u00BF", "iquest"}, // inverted question mark = turned question mark
        {"\u00C0", "Agrave"}, // �� - uppercase A, grave accent
        {"\u00C1", "Aacute"}, // �� - uppercase A, acute accent
        {"\u00C2", "Acirc"}, // �� - uppercase A, circumflex accent
        {"\u00C3", "Atilde"}, // �� - uppercase A, tilde
        {"\u00C4", "Auml"}, // �� - uppercase A, umlaut
        {"\u00C5", "Aring"}, // �� - uppercase A, ring
        {"\u00C6", "AElig"}, // �� - uppercase AE
        {"\u00C7", "Ccedil"}, // �� - uppercase C, cedilla
        {"\u00C8", "Egrave"}, // �� - uppercase E, grave accent
        {"\u00C9", "Eacute"}, // �� - uppercase E, acute accent
        {"\u00CA", "Ecirc"}, // �� - uppercase E, circumflex accent
        {"\u00CB", "Euml"}, // �� - uppercase E, umlaut
        {"\u00CC", "Igrave"}, // �� - uppercase I, grave accent
        {"\u00CD", "Iacute"}, // �� - uppercase I, acute accent
        {"\u00CE", "Icirc"}, // �� - uppercase I, circumflex accent
        {"\u00CF", "Iuml"}, // �� - uppercase I, umlaut
        {"\u00D0", "ETH"}, // �� - uppercase Eth, Icelandic
        {"\u00D1", "Ntilde"}, // �� - uppercase N, tilde
        {"\u00D2", "Ograve"}, // �� - uppercase O, grave accent
        {"\u00D3", "Oacute"}, // �� - uppercase O, acute accent
        {"\u00D4", "Ocirc"}, // �� - uppercase O, circumflex accent
        {"\u00D5", "Otilde"}, // �� - uppercase O, tilde
        {"\u00D6", "Ouml"}, // �� - uppercase O, umlaut
        {"\u00D7", "times"}, // multiplication sign
        {"\u00D8", "Oslash"}, // �� - uppercase O, slash
        {"\u00D9", "Ugrave"}, // �� - uppercase U, grave accent
        {"\u00DA", "Uacute"}, // �� - uppercase U, acute accent
        {"\u00DB", "Ucirc"}, // �� - uppercase U, circumflex accent
        {"\u00DC", "Uuml"}, // �� - uppercase U, umlaut
        {"\u00DD", "Yacute"}, // �� - uppercase Y, acute accent
        {"\u00DE", "THORN"}, // �� - uppercase THORN, Icelandic
        {"\u00DF", "szlig"}, // �� - lowercase sharps, German
        {"\u00E0", "agrave"}, // �� - lowercase a, grave accent
        {"\u00E1", "aacute"}, // �� - lowercase a, acute accent
        {"\u00E2", "acirc"}, // �� - lowercase a, circumflex accent
        {"\u00E3", "atilde"}, // �� - lowercase a, tilde
        {"\u00E4", "auml"}, // �� - lowercase a, umlaut
        {"\u00E5", "aring"}, // �� - lowercase a, ring
        {"\u00E6", "aelig"}, // �� - lowercase ae
        {"\u00E7", "ccedil"}, // �� - lowercase c, cedilla
        {"\u00E8", "egrave"}, // �� - lowercase e, grave accent
        {"\u00E9", "eacute"}, // �� - lowercase e, acute accent
        {"\u00EA", "ecirc"}, // �� - lowercase e, circumflex accent
        {"\u00EB", "euml"}, // �� - lowercase e, umlaut
        {"\u00EC", "igrave"}, // �� - lowercase i, grave accent
        {"\u00ED", "iacute"}, // �� - lowercase i, acute accent
        {"\u00EE", "icirc"}, // �� - lowercase i, circumflex accent
        {"\u00EF", "iuml"}, // �� - lowercase i, umlaut
        {"\u00F0", "eth"}, // �� - lowercase eth, Icelandic
        {"\u00F1", "ntilde"}, // �� - lowercase n, tilde
        {"\u00F2", "ograve"}, // �� - lowercase o, grave accent
        {"\u00F3", "oacute"}, // �� - lowercase o, acute accent
        {"\u00F4", "ocirc"}, // �� - lowercase o, circumflex accent
        {"\u00F5", "otilde"}, // �� - lowercase o, tilde
        {"\u00F6", "ouml"}, // �� - lowercase o, umlaut
        {"\u00F7", "divide"}, // division sign
        {"\u00F8", "oslash"}, // �� - lowercase o, slash
        {"\u00F9", "ugrave"}, // �� - lowercase u, grave accent
        {"\u00FA", "uacute"}, // �� - lowercase u, acute accent
        {"\u00FB", "ucirc"}, // �� - lowercase u, circumflex accent
        {"\u00FC", "uuml"}, // �� - lowercase u, umlaut
        {"\u00FD", "yacute"}, // �� - lowercase y, acute accent
        {"\u00FE", "thorn"}, // �� - lowercase thorn, Icelandic
        {"\u00FF", "yuml"}, // �� - lowercase y, umlaut
    };


    static final HashMap<String, CharSequence> lookupMapXML;
    static final HashMap<String, CharSequence> lookupMapHTML;
    static {
        lookupMapXML = new HashMap<String, CharSequence>();
        for (final CharSequence[] seq : ESCAPES_XML) 
            lookupMapXML.put(seq[1].toString(), seq[0]);
        lookupMapHTML = new HashMap<String, CharSequence>();
        for (final CharSequence[] seq : ESCAPES_HTML) 
            lookupMapHTML.put(seq[1].toString(), seq[0]);
    }
    
	static {
		DEFAULT_REPLACEMENT_MAP = new HashMap<String, String>();
		DEFAULT_REPLACEMENT_MAP.put("it", "i"); // italic
	}
	
	private Map<String, String> replacementMap;
	private boolean stripDoctype = true;
	private boolean useJsoup = true;
	private List<String> contentList;
	private List<String> noContentList;
	private List<String> balanceList;
	private boolean abortOnError = false;
	private boolean ignoreNamespaces = true;
	private List<String> tagToDeleteList;
	private List<String> attributeToDeleteList;
	private List<String> missingNamespacePrefixes;

	public HtmlFactory() {
		setDefaults();
	}
	
	private void setDefaults() {
		abortOnError = false;
		ignoreNamespaces = true;
		ensureReplacementMap();
	}
	
	/**
	 * @return the missingNamespacePrefixes
	 */
	public List<String> getMissingNamespacePrefixes() {
		ensureMissingNamespacePrefixes();
		return missingNamespacePrefixes;
	}

	private void ensureMissingNamespacePrefixes() {
		if (missingNamespacePrefixes == null) {
			missingNamespacePrefixes = new ArrayList<String>();
		}
	}

	/**
	 * @param missingNamespacePrefixes the missingNamespacePrefixes to set
	 */
	public void addMissingNamespacePrefix(String p) {
		ensureMissingNamespacePrefixes();
		if (!missingNamespacePrefixes.contains(p)) {
			missingNamespacePrefixes.add(p);
		}
	}

	public boolean isAbortOnError() {
		return abortOnError;
	}

	public void setAbortOnError(boolean abortOnError) {
		this.abortOnError = abortOnError;
	}

	/**
	 * @return the ignoreNamespaces
	 */
	public boolean isIgnoreNamespaces() {
		return ignoreNamespaces;
	}

	/**
	 * @param ignoreNamespaces the ignoreNamespaces to set
	 */
	public void setIgnoreNamespaces(boolean ignoreNamespaces) {
		this.ignoreNamespaces = ignoreNamespaces;
	}

	public void addReplacement(String old, String replacement) {
		ensureReplacementMap();
		replacementMap.put(old, replacement);
	}
	
	public List<String> getContentList() {
		return contentList;
	}

	public void setContentList(List<String> removeList) {
		this.contentList = removeList;
	}

	
	public List<String> getNoContentList() {
		return noContentList;
	}

	public void setNoContentList(List<String> noContentList) {
		this.noContentList = noContentList;
	}

	public List<String> getBalanceList() {
		return balanceList;
	}

	public void setBalanceList(List<String> balanceList) {
		this.balanceList = balanceList;
	}

	/**
	 * @return the useJsoup
	 */
	public boolean isUseJsoup() {
		return useJsoup;
	}

	/**
	 * @param useJsoup the useJsoup to set
	 */
	public void setUseJsoup(boolean useJsoup) {
		this.useJsoup = useJsoup;
	}

	
	/**
	 * @return the stripDoctype
	 */
	public boolean isStripDoctype() {
		return stripDoctype;
	}

	/**
	 * @param stripDoctype the stripDoctype to set
	 */
	public void setStripDoctype(boolean stripDoctype) {
		this.stripDoctype = stripDoctype;
	}

	/**
	 * @return the replacementMap
	 */
	public Map<String, String> getReplacementMap() {
		return replacementMap;
	}

	/**
	 * @param replacementMap the replacementMap to set
	 */
	public void setReplacementMap(Map<String, String> replacementMap) {
		this.replacementMap = replacementMap;
	}
	
	/** remove any tags causing problems.
	 * 
	 * typical examples are those that contain Javascript (e.g. <script> or <button>
	 * 
	 * @param tag
	 */
	public void addTagToDelete(String tag) {
		ensureTagToDeleteList();
		this.tagToDeleteList.add(tag);
	}

	/** remove any attributes causing problems.
	 * 
	 * typical examples are those that contain Javascript (e.g. onclick)
	 * 
	 * @param attribute
	 */
	public void addAttributeToDelete(String attribute) {
		ensureAttributeToDeleteList();
		this.attributeToDeleteList.add(attribute);
	}


	/** creates subclassed elements.
	 * 
	 * continues, else fails;
	 * 
	 * @param element
	 * @param abort 
	 * @return
	 */
	public HtmlElement parse(Element element) {
		return (HtmlElement) parse0(element);
	}

	/** creates subclassed elements.
	 * 
	 * continues, else fails;
	 * 
	 * @param element
	 * @param abort 
	 * @return
	 */
	public Element parse0(Element element) {
		Element xmlElement = null;
		String tag = element.getLocalName();
		String namespaceURI = element.getNamespaceURI();
		if (!"".equals(namespaceURI) && !HtmlElement.XHTML_NS.equals(namespaceURI)) {
			if (SVGConstants.SVG_NAMESPACE.equals(namespaceURI)) {
				xmlElement = SVGElement.readAndCreateSVG(element);
			} else if (abortOnError) {
				throw new RuntimeException("Multiple Namespaces NYI "+namespaceURI);
			} else {
//				tag = tag.replaceAll(":", "_");
				xmlElement = new HtmlDiv();
				((HtmlElement)xmlElement).setClassAttribute(tag);
			}
		} else {
			xmlElement = createElementFromTag(tag);
			if (xmlElement == null) {
				String msg = "Unknown html tag "+tag;
				if (HtmlElement.TAGSET.contains(tag.toUpperCase())) {
					xmlElement = new HtmlGeneric(tag.toLowerCase());
				} else {
					if (abortOnError) {
						throw new RuntimeException(msg);
					}
					xmlElement = createElementFromReplacement(tag);
					if (xmlElement == null) {
						LOG.trace(msg);
						xmlElement = new HtmlGeneric(tag);
					}
				}
			}
		}
		XMLUtil.copyAttributes(element, xmlElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				Element htmlChild = this.parse0(childElement);
				if (htmlChild == null) {
					LOG.error("NULL child "+childElement.toXML());
				} else {
					xmlElement.appendChild(htmlChild);
				}
			} else {
				xmlElement.appendChild(child.copy());
			}
		}
		return xmlElement;
		
	}

	private HtmlElement createElementFromReplacement(String tag) {
		HtmlElement htmlElement = null;
		String replacement = replacementMap.get(tag);
		if (replacement != null) {
			htmlElement = createElementFromTag(replacement);
		}
		return htmlElement;
	}

	public HtmlElement createElementFromTag(String tag) {
		HtmlElement htmlElement = null;
		if(HtmlA.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlA();
		} else if(HtmlB.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlB();
		} else if(HtmlBig.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlBig();
		} else if(HtmlBody.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlBody();
		} else if(HtmlBr.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlBr();
		} else if(HtmlCaption.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlCaption();
		} else if(HtmlDiv.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlDiv();
		} else if(HtmlEm.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlEm();
		} else if(HtmlFrame.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlFrame();
		} else if(HtmlFrameset.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlFrameset();
		} else if(HtmlH1.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlH1();
		} else if(HtmlH2.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlH2();
		} else if(HtmlH3.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlH3();
		} else if(HtmlHead.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlHead();
		} else if(HtmlHr.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlHr();
		} else if(HtmlHtml.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlHtml();
		} else if(HtmlI.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlI();
		} else if(HtmlImg.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlImg();
		} else if(HtmlLi.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlLi();
		} else if(HtmlLink.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlLink();
		} else if(HtmlMeta.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlMeta();
		} else if(HtmlOl.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlOl();
		} else if(HtmlP.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlP();
		} else if(HtmlS.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlS();
		} else if(HtmlSmall.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSmall();
		} else if(HtmlSpan.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSpan();
		} else if(HtmlStrong.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlStrong();
		} else if(HtmlStyle.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlStyle();
		} else if(HtmlSub.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSub();
		} else if(HtmlSup.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSup();
		} else if(HtmlTable.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTable();
		} else if(HtmlTbody.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTbody();
		} else if(HtmlTfoot.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTfoot();
		} else if(HtmlThead.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlThead();
		} else if(HtmlTd.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTd();
		} else if(HtmlTh.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTh();
		} else if(HtmlTr.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTr();
		} else if(HtmlTt.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTt();
		} else if(HtmlUl.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlUl();
			
/** HTML5
 * article, aside, audio, bdi, canvas, command, data, datalist, details, embed, figcaption, 
 * figure, footer, header, keygen, mark, meter, nav, output, progress, rp, rt, ruby, section,
 * source, summary, time, track, video, wbr
 */
			
/**
 * HTML5
 * dates and times, email, url, search, number, range, tel, color
 */
			
/** HTML5 deprecations
  * acronym, applet, basefont, big, center, dir, font, frame, frameset, isindex, noframes, strike, tt
  */
		} 
		return htmlElement;
	}
	


	private void ensureReplacementMap() {
		if (replacementMap == null) {
			this.replacementMap = new HashMap<String, String>();
		}
	}

	public void addReplacement(Map<String, String> replacementMap) {
		ensureReplacementMap();
		if (replacementMap != null) {
			for (Entry<String, String> entry : replacementMap.entrySet()) {
				this.addReplacement(entry.getKey(), entry.getValue());
			}
		}
	}

	public HtmlElement parse(String xmlString) throws Exception {
		InputStream is = IOUtils.toInputStream(xmlString);
		return parse(is);
	}
	
	public HtmlElement parse(File file) throws Exception {
		return parse(new FileInputStream(file));
	}

	public HtmlElement parse(URL url) throws Exception {
		return url == null ? null : parse(url.openStream());
	}

	public HtmlElement parse(InputStream is) throws Exception {
		String ss = IOUtils.toString(is, "UTF-8");
		ensureContentLists();
		for (String remove : contentList) {
			ss = XMLUtil.removeTags(remove, ss);
		}
		for (String remove : noContentList) {
			ss = removeElementsWithoutContent(remove, ss);
		}
		for (String balance : balanceList) {
			ss = balanceElements(balance, ss);
		}
		ss = removeNamespacePrefixes(ss);
		ss = parseLegacyHtmlToWellFormedXML(ss);
		ss = tidyTidyingErrors(ss);
		HtmlElement htmlElement = parseToXHTML(ss);
//		FileUtils.write(new File("target/debug/"+ss.length()+".html"), htmlElement.toXML());
		return htmlElement;
	}

	private void ensureContentLists() {
		if (contentList == null) {
			contentList = new ArrayList<String>();
		}
		if (noContentList == null) {
			noContentList = new ArrayList<String>();
		}
		if (balanceList == null) {
			balanceList = new ArrayList<String>();
		}
		// TODO Auto-generated method stub
		
	}

	private String removeNamespacePrefixes(String ss) {
		ss = ss.replaceAll("<[a-z]+:", "<");
		ss = ss.replaceAll("</[a-z]+:", "</");
		return ss;
	}

	private String tidyTidyingErrors(String ss) {
		return ss.replaceAll("\"=\"\"", "");
	}

	private String balanceElements(String balance, String ss) {
//		ss = ss.replaceAll("<"+balance+"[^>]*>", "");
//		ss = ss.replaceAll("</"+balance+"></"+balance+">", "");
		return ss;
	}

	private String removeElementsWithContent(String remove, String ss) {
		ss = ss.replaceAll("<"+remove+"[^>]*>[^<]*</"+remove+">", "");
		return ss;
	}

	private String removeElementsWithoutContent(String remove, String ss) {
		ss = ss.replaceAll("<"+remove+"[^>]*/?>", "");
		ss = ss.replaceAll("</"+remove+">", "");
		return ss;
	}

	private HtmlElement parseToXHTML(String ss) throws IOException {
		Element element;
		HtmlElement htmlElement = null;
		try {
			element = XMLUtil.parseXML(ss);
			htmlElement = this.parse(element);
		} catch (Exception e) {
//			e.printStackTrace();
			File file = new File("target/debug/htmlFactory"+System.currentTimeMillis()+".xml");
			FileUtils.write(file, ss);
			LOG.debug("wrote BAD XML to "+file);
		}
		return htmlElement;
	}

	private String parseLegacyHtmlToWellFormedXML(String ss) {
		if (ss == null || ss.trim().length() == 0) {
			LOG.error("legacy HTML has no content");
			return "";
		}
		if (stripDoctype) {
			ss = HtmlUtil.stripDOCTYPE(ss);
		}
		ss = insertMissingNamespacesIntoRoot(ss);
		// do this before any unescaping as some attributes have escaped characters
		ss = stripAttributesToDelete(ss); 
		ss = HtmlUtil.unescapeHtml3(ss, lookupMapXML);
		ss = HtmlUtil.replaceProblemCharacters(ss);
		if (useJsoup) {
			org.jsoup.nodes.Document doc = Jsoup.parse(ss);
			ss = doc.html();
		}
		// ARGH Jsoup re-escapes characters - have to turn them back again, but NOT &amp; 
		ss = HtmlUtil.unescapeHtml3(ss, lookupMapHTML);
		return ss;
	}

	/** this is awful, but so is the HTML we have to process.
	 * 
	 * inserts a dummy namespace which prevents parsers failing
	 * 
	 * @param ss
	 * @return
	 */
	private String insertMissingNamespacesIntoRoot(String ss) {
		ensureMissingNamespacePrefixes();
		for (String prefix : missingNamespacePrefixes) {
			ss = ss.replace("<html", "<html xmlns:"+prefix+"=\"http://foo/"+prefix+"/\""); // Missing namespace 
		}
		return ss;
	}

	private String stripTagsToDelete(String ss) {
		ensureTagToDeleteList();
		for (String problemTag : tagToDeleteList) {
			ss = HtmlUtil.stripElementFromTextString(ss, problemTag);
		}
		return ss;
	}

	private void ensureTagToDeleteList() {
		if (tagToDeleteList == null) {
			tagToDeleteList = new ArrayList<String>();
		}
	}

	private String stripAttributesToDelete(String ss) {
		ensureAttributeToDeleteList();
		for (String attribute : attributeToDeleteList) {
			ss = HtmlUtil.stripAttributeFromText(ss, attribute);
		}
		return ss;
	}

	private void ensureAttributeToDeleteList() {
		if (attributeToDeleteList == null) {
			attributeToDeleteList = new ArrayList<String>();
		}
	}

	
}
