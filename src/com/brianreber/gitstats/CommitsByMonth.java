package com.brianreber.gitstats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CommitsByMonth extends HtmlPage {


	public CommitsByMonth(String projectTitle) {
		super(projectTitle, "commitsmonth.html", "Commits By Month");
	}

	@Override
	protected String generateBody(List<Commit> commits) {
		System.out.println("generateBody");
		StringBuilder sb = new StringBuilder();

		List<String> users = new ArrayList<String>();
		HashMap<String, Data> data = parseData(commits, users);

		System.out.println("parsedData");

		sb.append("google.load('visualization', '1.0', { 'packages':['corechart'] });");
		sb.append("google.setOnLoadCallback(drawChart);");

		sb.append("function drawChart() {\n");

		sb.append("var data = new google.visualization.arrayToDataTable([");
		sb.append("['Date'");

		for (String s : users) {
			sb.append(", '" + s + "'");
		}

		sb.append("],");

		List<String> tmp = new ArrayList<String>(data.keySet());
		Collections.sort(tmp);
		for (String s : tmp) {
			Data d = data.get(s);

			sb.append("['" + s + "'");

			for (String usr : users) {
				sb.append(", " + d.data.get(usr) + "");
			}
			sb.append("],");
		}

		sb.append("]);");

		sb.append("var chart = new google.visualization.LineChart(document.getElementById('chart_div'));");
		sb.append("chart.draw(data);");

		sb.append("};");

		return sb.toString();
	}

	private HashMap<String, Data> parseData(List<Commit> commits, List<String> users) {
		HashMap<String, Data> toRet = new HashMap<String, Data>();

		Collections.sort(commits, new Comparator<Commit>() {
			@Override
			public int compare(Commit o1, Commit o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		Calendar c = Calendar.getInstance();
		int index = 0;

		while (index < commits.size()) {
			Commit tmp = commits.get(index);

			// Add the user to the list of users
			if (!users.contains(tmp.getAuthorName())) {
				users.add(tmp.getAuthorName());
			}

			//
			c.setTime(tmp.getDate());
			String key = c.get(Calendar.YEAR) + " / " + (c.get(Calendar.MONTH) + 1);
			if (toRet.containsKey(key)){
				Data d = toRet.get(key);
				Integer i = d.data.get(tmp.getAuthorName());

				if (i != null) {
					int val = d.data.get(tmp.getAuthorName());
					val++;
					d.data.put(tmp.getAuthorName(), val);
				} else {
					d.data.put(tmp.getAuthorName(), 1);
				}
			} else {
				Data d = new Data();
				d.data.put(tmp.getAuthorName(), 1);
				toRet.put(key, d);
			}
			index++;
		}

		return toRet;
	}

	private class Data {
		public HashMap<String, Integer> data = new HashMap<String, Integer>();
	}
}
