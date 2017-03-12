/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.html;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Text;

public class HtmlScript  extends HtmlElement {

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlScript.class);
	public final static String TAG = "script";
	
	private static final String SRC = "src";

	/** constructor.
	 * 
	 */
	public HtmlScript() {
		super(TAG);
	}

	public void setSrc(String src) {
		if (src != null) {
			this.addAttribute(new Attribute(SRC, src));
		}
	}
	
	public void setContent(String content) {
		addSplitLines(content);
	}

	public void addSplitLines(String content) {
		String[] lines = content.split("\\n");
		for (String line : lines) {
			if (!line.trim().startsWith("//")) {
				this.appendChild(new Text(line));
			}
		}
	}
	

	
}
