package business.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AnalysisAttractions {
	private Map<String, String> urls = new HashMap<String, String>();
	private int lastPage = 0;
	private boolean hasMore = false;
	private boolean newLocation = true;
	private List<String> addresses = new ArrayList<String>();

	/**
	 * 
	 * @param document
	 */
	public void analysisAttractionPlaces(final Document document) {
		this.urls.clear();
		this.addresses.clear();
		NodeList attractions = getXpathNode(document,
				"//*[@class='property_title ']");
		for (int i = 0; i < attractions.getLength(); i++) {
			Node item = attractions.item(i);
			if (item.getNodeName().equalsIgnoreCase("a")) {
				final String attractionName = item.getChildNodes().item(0)
						.getNodeValue();
				final String attractionUrl = item.getAttributes().item(0)
						.getNodeValue();
				urls.put(attractionName, attractionUrl);
			}
		}

		if (newLocation) {
			hasMore = hasMore(document);
			newLocation = false;
		}
	}

	/**
	 * 
	 * @param document
	 * @param xpathKey
	 * @return
	 */
	private NodeList getXpathNode(final Document document, String xpathKey) {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes
		// value
		try {
			XPathExpression expr = xpath.compile(xpathKey);
			Object result = expr.evaluate(document, XPathConstants.NODESET);
			return (NodeList) result;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	private boolean hasMore(final Document document) {
		final NodeList xpathNode = getXpathNode(document,
				"//*[@class='paging taLnk ']");
		if (xpathNode.getLength() > 0) {
			Node item = xpathNode.item(xpathNode.getLength() - 1);
			this.lastPage = Integer.parseInt(item.getTextContent());
		}
		return xpathNode.getLength() > 0;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public int getLastPage() {
		return lastPage;
	}

	public Map<String, String> getUrls() {
		return this.urls;
	}

	public void analysisAttractionAddresses(final Document document) {
		final NodeList xpathNode = getXpathNode(document, "//*[@class='addr']");
		if (xpathNode != null && xpathNode.getLength() != 0) {
			addresses.add(xpathNode.item(0).getChildNodes().item(0).getNodeValue());
		}
	}

	public List<String> getAddresses() {
		return addresses;
	}
}
