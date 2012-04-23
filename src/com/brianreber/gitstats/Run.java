package com.brianreber.gitstats;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Main entry point into the application.
 * 
 * @author breber
 */
public class Run {

	/**
	 * The list of Commits that is used throughout the program
	 */
	private static List<Commit> commits = new ArrayList<Commit>();

	/**
	 * The list of Pages to generate
	 */
	private static List<HtmlPage> pages = new ArrayList<HtmlPage>();

	private static String outputPath = "";
	private static String projectName = "";

	public static void main(String[] args) throws FileNotFoundException, ParseException, JSONException {
		File f = new File(args[0]);
		Scanner scan = new Scanner(f);

		// Parse the arguments
		for (int i = 1; i < args.length; i++) {
			if (args[i].startsWith("--outPath=")) {
				outputPath = args[i].substring("--outPath=".length());

				File output = new File(outputPath);
				if (!output.exists()) {
					output.mkdir();
				}
			} else if (args[i].startsWith("--projectName=")) {
				projectName = args[i].substring("--projectName=".length());
			}
		}

		// Add the pages we want to generate
		addPages();

		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String originalLine = line;

			// Fix some problems with the JSON encoding in the message
			String message = line.replaceAll(".+?(\"message\":\"((.+?)?)\"})", "$2");
			String messageRep = message.replaceAll("\\\\", "\\\\\\\\");
			messageRep = messageRep.replaceAll("\"", "\\\\\"");

			// Fix some problems with the JSON encoding in the author name
			String author = line.replaceAll(".+?(\"authorName\":(\".*?\"),\".+?\":).*", "$2");
			author = author.substring(1, author.length() - 1);
			String authorRep = author.replaceAll("\\\\", "\\\\\\\\");
			authorRep = authorRep.replaceAll("\"", "\\\\\"");

			// Fix some problems with the JSON encoding in the author email
			String authorEmail = line.replaceAll(".+?(\"authorEmail\":(\".*?\"),\".+?\":).*", "$2");
			authorEmail = authorEmail.substring(1, authorEmail.length() - 1);
			String authorEmailRep = authorEmail.replaceAll("\\\\", "\\\\\\\\");
			authorEmailRep = authorEmailRep.replaceAll("\"", "\\\\\"");

			line = line.replace(author, authorRep);
			line = line.replace(authorEmail, authorEmailRep);
			line = line.replace(message, messageRep);

			//			System.out.println(line);

			try {
				JSONObject obj = new JSONObject(line);
				Date d = new Date(obj.getString("date"));

				Commit c = new Commit(obj.getString("commit"), d, obj.getString("authorName"), obj.getString("authorEmail"), obj.getString("message"));
				commits.add(c);
			} catch (JSONException ex) {
				System.out.println("Error with: " + originalLine);
				System.out.println("New Value : " + line);
				System.out.println("Message:    " + message);
				System.out.println("MessageRep: " + messageRep);
				System.out.println("Author:     " + author);
				System.out.println("AuthorRep:  " + authorRep);
				System.out.println("AuthorE:    " + authorEmail);
				System.out.println("AuthorERep: " + authorEmailRep);
				ex.printStackTrace();
				throw ex;
			}
		}

		System.out.println("Num Commits: " + commits.size());

		// Go through each page and generate it
		for (HtmlPage p : pages) {
			System.out.println("Generating page...");
			p.generatePage(commits, pages);
		}

		// Copy CSS to output folder
		File source = new File("css");
		System.out.println("Copying CSS");
		try {
			FileUtil.copyFolder(source, new File(outputPath + "/css"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Copy JS to output folder
		source = new File("js");
		System.out.println("Copying JS");
		try {
			FileUtil.copyFolder(source, new File(outputPath + "/js"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Copy IMG to output folder too
		source = new File("img");
		System.out.println("Copying img");
		try {
			FileUtil.copyFolder(source, new File(outputPath + "/img"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add the pages we want to generate
	 */
	private static void addPages() {
		pages.add(new IndexPage(projectName, outputPath));
		pages.add(new CommitsByMonth(projectName, outputPath));
		pages.add(new CommitsByDay(projectName, outputPath));
		pages.add(new CommitsByDayAndHour(projectName, outputPath));
	}

}
