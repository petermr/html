package org.xmlcml.html.util;

import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.html.HtmlElement;

public class HtmlUnitTest {

	@Test
	@Ignore // fails because of Xerces xml-apis problem - see POM
	public void testHtmlUnit() throws Exception {
		URL url = new URL("http://www.biomedcentral.com/1471-2229/14/106");
		HtmlElement htmlElement = HtmlUtil.readAndCreateElementUsingHtmlUnit(url);
		Assert.assertNotNull("read element", htmlElement);
	}

}

