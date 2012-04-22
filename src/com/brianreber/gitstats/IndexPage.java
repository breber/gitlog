package com.brianreber.gitstats;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

public class IndexPage extends HtmlPage {

	public IndexPage(String projectTitle, String fileName) {
		super(projectTitle, fileName + "/index.html", "GitLog");
	}

	@Override
	protected String generateBody(List<Commit> commits) throws JSONException {
		StringBuilder finalSb = new StringBuilder();
		StringBuilder sb = new StringBuilder();

		Stats stats = getStats(commits);

		sb.append("<div class='container-fluid'><div class='hero-unit'>");
		sb.append("<div><b>Project Name:</b> " + projectTitle + "</div>");
		sb.append("<div><b>Stats Generated:</b> " + new Date() + "</div>");
		sb.append("<div><b>Initial Commit:</b> " + stats.initialCommit + " (" + stats.timeSinceInitialCommit + " days)</div>");
		sb.append("<div><b>Total Commits:</b> " + commits.size() + "</div>");
		sb.append("<div><b>Authors:</b> " + stats.authors.keySet().size() + " (~" + stats.averageCommitsPerAuthor + " commits / author)</div>");
		sb.append("</div></div>");

		finalSb.append("var chartDiv = document.getElementById('chart_div');\n");
		finalSb.append("chartDiv.innerHTML = \"" + sb.toString() + "\";");
		finalSb.append("chartDiv.className = \"notChart\";");

		return finalSb.toString();
	}

	private Stats getStats(List<Commit> commits) {
		Stats stats = new Stats();

		Collections.sort(commits, new Comparator<Commit>() {
			@Override
			public int compare(Commit o1, Commit o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		stats.initialCommit = commits.get(0).getDate();
		Calendar now = Calendar.getInstance();
		Calendar initial = Calendar.getInstance();
		initial.setTime(stats.initialCommit);

		stats.timeSinceInitialCommit = (int) ((now.getTimeInMillis() - initial.getTimeInMillis()) / (24 * 60 * 60 * 1000));

		for (Commit c : commits) {
			if (stats.authors.containsKey(c.getAuthorName())) {
				stats.authors.put(c.getAuthorName(), stats.authors.get(c.getAuthorName()) + 1);
			} else {
				stats.authors.put(c.getAuthorName(), 1);
			}
		}

		stats.averageCommitsPerAuthor = commits.size() / (stats.authors.keySet().size());

		return stats;
	}

	private class Stats {
		public Date initialCommit;
		public int timeSinceInitialCommit;
		public Map<String, Integer> authors = new HashMap<String, Integer>();
		public int averageCommitsPerAuthor;
	}

}
