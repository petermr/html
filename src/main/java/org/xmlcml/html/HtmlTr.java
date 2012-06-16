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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTr extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlTr.class);
	public final static String TAG = "tr";

	/** constructor.
	 * 
	 */
	public HtmlTr() {
		super(TAG);
	}

	public List<HtmlTh> getThChildren() {
		List<HtmlElement> ths = HtmlElement.getChildElements(this, HtmlTh.TAG);
		List<HtmlTh> thList = new ArrayList<HtmlTh>();
		for (HtmlElement th : ths) {
			thList.add((HtmlTh) th);
		}
		return thList;
	}
	
	public List<HtmlTd> getTdChildren() {
		List<HtmlElement> tds = HtmlElement.getChildElements(this, HtmlTd.TAG);
		List<HtmlTd> tdList = new ArrayList<HtmlTd>();
		for (HtmlElement td : tds) {
			tdList.add((HtmlTd) td);
		}
		return tdList;
	}
	
	public HtmlTd getTd(int col) {
		List<HtmlTd> cells = getTdChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTd) cells.get(col);
	}
	
	public HtmlTh getTh(int col) {
		List<HtmlTh> cells = getThChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTh) cells.get(col);
	}

}
