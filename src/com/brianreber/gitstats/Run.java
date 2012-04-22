package com.brianreber.gitstats;
import java.io.File;
import java.io.FileNotFoundException;
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

	public static void main(String[] args) throws FileNotFoundException, JSONException, ParseException {
		File f = new File(args[0]);
		Scanner scan = new Scanner(f);

		// Parse the arguments
		for (int i = 1; i < args.length; i++) {
			if (args[i].startsWith("--outPath=")) {
				outputPath = args[i].substring("--outPath=".length());
			} else if (args[i].startsWith("--projectName=")) {
				projectName = args[i].substring("--projectName=".length());
			}
		}

		// Add the pages we want to generate
		addPages();

		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			temp = temp.replaceAll("\"message\":\"(.+?)?\"(.+?)\"(.+?)?\"", "\"message\":\"$1\\\\\"$2\\\\\"$3\"");
			JSONObject obj = new JSONObject(temp);

			Date d = new Date(obj.getString("date"));

			Commit c = new Commit(obj.getString("commit"), d, obj.getString("authorName"), obj.getString("authorEmail"), obj.getString("message"));
			commits.add(c);
		}

		System.out.println("Num Commits: " + commits.size());

		// Go through each page and generate it
		for (HtmlPage p : pages) {
			System.out.println("Generating page...");
			p.generatePage(commits, pages);
		}

		// Copy CSS and JS to output folder too
		File css = new File("css");
		System.out.println("Copying CSS");
		// TODO: actually copy folders
	}

	/**
	 * Add the pages we want to generate
	 */
	private static void addPages() {
		pages.add(new CommitsByMonth(projectName, outputPath));
		pages.add(new CommitsByDay(projectName, outputPath));
		pages.add(new CommitsByDayAndHour(projectName, outputPath));
	}

}
