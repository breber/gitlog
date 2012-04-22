package com.brianreber.gitstats;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The generic outline of a visualization page
 * 
 * @author breber
 */
public abstract class HtmlPage {

	/**
	 * The title of the project
	 */
	protected final String projectTitle;

	/**
	 * The title of the page
	 */
	protected final String pageTitle;

	/**
	 * The file to write the page to
	 */
	protected final String fileName;

	/**
	 * Creates a new HTMLPage with the given project title, file name, and page title
	 * 
	 * @param projectTitle the title of the project
	 * @param fileName the file to write to
	 * @param pageTitle the title of the page
	 */
	public HtmlPage(String projectTitle, String fileName, String pageTitle) {
		this.fileName = fileName;
		this.projectTitle = projectTitle;
		this.pageTitle = pageTitle;
	}

	/**
	 * Generates the content of the page, containing the visualization
	 * 
	 * @param commits the list of commits to use to generate the visualization
	 * @return the string to put in for the body of the main JS block
	 * @throws JSONException
	 */
	protected abstract String generateBody(List<Commit> commits) throws JSONException;

	/**
	 * Generates the HTML of the page and writes it to the file
	 * 
	 * @param commits the list of commits to use
	 */
	public void generatePage(List<Commit> commits, List<HtmlPage> allPages) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("html");
			doc.appendChild(rootElement);

			Element head = doc.createElement("head");

			// Generate include for JSAPI
			Element script = doc.createElement("script");
			script.setAttribute("type", "text/javascript");
			script.setAttribute("src", "https://www.google.com/jsapi");
			head.appendChild(script);

			// Generate the JS for Bootstrap
			Element bootstrapScript = doc.createElement("script");
			bootstrapScript.setAttribute("type", "text/javascript");
			bootstrapScript.setAttribute("src", "js/bootstrap.min.js");
			head.appendChild(bootstrapScript);

			// Generate the JS for generating the graphs
			Element script2 = doc.createElement("script");
			script2.setAttribute("type", "text/javascript");
			script2.setTextContent(generateBody(commits));
			head.appendChild(script2);

			// Generate the CSS for Bootstrap
			Element bootstrapCSS = doc.createElement("link");
			bootstrapCSS.setAttribute("rel", "stylesheet");
			bootstrapCSS.setAttribute("href", "css/bootstrap.min.css");
			head.appendChild(bootstrapCSS);

			// Create the title
			Element title = doc.createElement("title");
			title.setTextContent(projectTitle + ": " + pageTitle);
			head.appendChild(title);

			// Add the head to the page
			rootElement.appendChild(head);

			// Generate the body
			Element body = doc.createElement("body");

			// Generate the top section of the page
			Element bodyHeading = doc.createElement("div");
			bodyHeading.setAttribute("class", "navbar navbar-fixed-top");
			body.appendChild(bodyHeading);

			Element navbarInner = doc.createElement("div");
			navbarInner.setAttribute("class", "navbar-inner");
			bodyHeading.appendChild(navbarInner);

			Element navbarContainer = doc.createElement("div");
			navbarContainer.setAttribute("class", "container");
			navbarInner.appendChild(navbarContainer);

			Element navbarBrand = doc.createElement("a");
			navbarBrand.setAttribute("class", "brand");
			navbarBrand.setAttribute("href", "#");
			navbarBrand.setTextContent(projectTitle);
			navbarContainer.appendChild(navbarBrand);

			Element navbarNavCollapse = doc.createElement("div");
			navbarNavCollapse.setAttribute("class", "nav-collapse");
			navbarContainer.appendChild(navbarNavCollapse);

			Element navbarNavUl = doc.createElement("ul");
			navbarNavUl.setAttribute("class", "nav");
			navbarNavCollapse.appendChild(navbarNavUl);

			// Homepage link
			Element navbarNavUlHomeLi = doc.createElement("li");
			if (fileName.contains("index.html")) {
				navbarNavUlHomeLi.setAttribute("class", "active");
			}
			navbarNavUl.appendChild(navbarNavUlHomeLi);

			Element navbarNavUlHomeLiAnchor = doc.createElement("a");
			navbarNavUlHomeLiAnchor.setAttribute("href", "index.html");
			navbarNavUlHomeLiAnchor.setTextContent("Home");
			navbarNavUlHomeLi.appendChild(navbarNavUlHomeLiAnchor);

			// Generate all other page links
			for (HtmlPage p : allPages) {
				Element navbarNavUlLi = doc.createElement("li");
				if (fileName.equals(p.fileName)) {
					navbarNavUlLi.setAttribute("class", "active");
				}
				navbarNavUl.appendChild(navbarNavUlLi);

				Element navbarNavUlLiAnchor = doc.createElement("a");
				navbarNavUlLiAnchor.setAttribute("href", p.fileName.substring(p.fileName.lastIndexOf("/") + 1));
				navbarNavUlLiAnchor.setTextContent(p.pageTitle);
				navbarNavUlLi.appendChild(navbarNavUlLiAnchor);
			}

			// Generate Bootstrap container
			Element bootstrapContainer = doc.createElement("div");
			bootstrapContainer.setAttribute("class", "container");
			body.appendChild(bootstrapContainer);

			// Generate the Chart Div
			Element chartDiv = doc.createElement("div");
			chartDiv.setAttribute("id", "chart_div");
			chartDiv.setAttribute("style", "width: 100%; height: 700px;");
			bootstrapContainer.appendChild(chartDiv);

			// TODO: generated by, copyright

			// Add the body to the html page
			rootElement.appendChild(body);

			// Generate HTML text
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = tf.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}