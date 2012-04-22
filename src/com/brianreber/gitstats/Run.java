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


public class Run {

	private static List<Commit> commits = new ArrayList<Commit>();

	private static List<HtmlPage> pages = new ArrayList<HtmlPage>();

	static {
		pages.add(new CommitsByMonth("CardGames"));
		pages.add(new CommitsByDay("CardGames"));
		// TODO: lines

	}

	public static void main(String[] args) throws FileNotFoundException, JSONException, ParseException {
		File f = new File(args[0]);
		Scanner scan = new Scanner(f);

		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			temp = temp.replaceAll("\"message\":\"(.+?)?\"(.+?)\"(.+?)?\"", "\"message\":\"$1\\\\\"$2\\\\\"$3\"");
			JSONObject obj = new JSONObject(temp);

			Date d = new Date(obj.getString("date"));

			Commit c = new Commit(obj.getString("commit"), d,
					obj.getString("authorName"), obj.getString("authorEmail"), obj.getString("message"));
			commits.add(c);
		}

		System.out.println("Num Commits: " + commits.size());

		for (HtmlPage p : pages) {
			p.generatePage(commits);
		}
	}

}
