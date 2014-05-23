package org.xmlcml.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

/** generates HtmlElement from unnamespaced Elements.
 * 
 * Allows for customisation of treatment such as substituing unusual or incorrect elements.
 * 
 * @author pm286
 *
 */
public class HtmlFactory {

	private final static Logger LOG = Logger.getLogger(HtmlFactory.class);

	private Map<String, String> replacementMap;
	private boolean abortOnError;

	public HtmlFactory() {
		setDefaults();
	}
	
	private void setDefaults() {
		abortOnError = true;
		ensureReplacementMap();
	}

	public boolean isAbortOnError() {
		return abortOnError;
	}

	public void setAbortOnError(boolean abortOnError) {
		this.abortOnError = abortOnError;
	}

	public void addReplacement(String old, String replacement) {
		ensureReplacementMap();
		replacementMap.put(old, replacement);
	}
	
	/** creates subclassed elements.
	 * 
	 * continues, else fails;
	 * 
	 * @param element
	 * @param abort 
	 * @return
	 */
	public HtmlElement create(Element element) {
		HtmlElement htmlElement = null;
		String tag = element.getLocalName();
		String namespaceURI = element.getNamespaceURI();
		if (!"".equals(namespaceURI) && !HtmlElement.XHTML_NS.equals(namespaceURI)) {
			if (abortOnError) {
				throw new RuntimeException("Multiple Namespaces NYI "+namespaceURI);
			} else {
				tag = tag.replaceAll(":", "_");
			}
		} else {
			htmlElement = createElementFromTag(tag);
			if (htmlElement == null) {
				String msg = "Unknown html tag "+tag;
				if (HtmlElement.TAGSET.contains(tag.toUpperCase())) {
					htmlElement = new HtmlGeneric(tag.toLowerCase());
				} else {
					if (abortOnError) {
						throw new RuntimeException(msg);
					}
					htmlElement = createElementFromReplacement(tag);
					if (htmlElement == null) {
						LOG.error(msg);
						htmlElement = new HtmlGeneric(tag);
					}
				}
			}
			XMLUtil.copyAttributes(element, htmlElement);
			for (int i = 0; i < element.getChildCount(); i++) {
				Node child = element.getChild(i);
				if (child instanceof Element) {
					HtmlElement htmlChild = this.create((Element)child);
					htmlElement.appendChild(htmlChild);
				} else {
					htmlElement.appendChild(child.copy());
				}
			}
		}
		return htmlElement;
		
	}

	private HtmlElement createElementFromReplacement(String tag) {
		HtmlElement htmlElement = null;
		String replacement = replacementMap.get(tag);
		if (replacement != null) {
			htmlElement = createElementFromTag(replacement);
		}
		return htmlElement;
	}

	private HtmlElement createElementFromTag(String tag) {
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
	
}
