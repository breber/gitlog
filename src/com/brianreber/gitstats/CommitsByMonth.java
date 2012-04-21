package com.brianreber.gitstats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class CommitsByMonth extends LineChartPage {


	public CommitsByMonth(String projectTitle) {
		super(projectTitle, "commitsmonth.html", "Commits By Month");
	}

	private class Data {
		public HashMap<String, Integer> data = new HashMap<String, Integer>();
	}

	@Override
	protected JSONArray parseData(List<Commit> commits) throws JSONException {
		JSONArray data = new JSONArray();
		JSONArray names = new JSONArray();
		List<String> users = new ArrayList<String>();
		HashMap<String, Data> monthToCommits = new HashMap<String, Data>();

		Collections.sort(commits, new Comparator<Commit>() {
			@Override
			public int compare(Commit o1, Commit o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		// Add the title of the axis to the array first
		names.put("Month");

		// Add the names to our JSONArray
		for (Commit c : commits) {
			// Add the user to the list of users
			if (!users.contains(c.getAuthorName())) {
				names.put(c.getAuthorName());
				users.add(c.getAuthorName());
			}
		}

		// add the names to the return JSONArray
		data.put(names);

		Calendar calendar = Calendar.getInstance();
		int index = 0;

		while (index < commits.size()) {
			Commit tmp = commits.get(index);

			// Get an instance of the time of the commit
			calendar.setTime(tmp.getDate());
			String key = calendar.get(Calendar.YEAR) + " / " + (calendar.get(Calendar.MONTH) + 1);

			// If we already have this month follow this path
			if (monthToCommits.containsKey(key)){
				Data d = monthToCommits.get(key);
				Integer i = d.data.get(tmp.getAuthorName());

				if (i != null) {
					int val = d.data.get(tmp.getAuthorName());
					val++;
					d.data.put(tmp.getAuthorName(), val);
				} else {
					d.data.put(tmp.getAuthorName(), 1);
				}
			} else {
				// We need to create a new month
				Data d = new Data();
				d.data.put(tmp.getAuthorName(), 1);
				monthToCommits.put(key, d);
			}
			index++;
		}

		List<String> months = new ArrayList<String>(monthToCommits.keySet());
		Collections.sort(months);

		// Generate our JSONArray
		for (String date : months) {
			Data current = monthToCommits.get(date);
			JSONArray obj = new JSONArray();
			obj.put(date);

			for (int i = 1; i < names.length(); i++) {
				String name = names.getString(i);
				obj.put(current.data.get(name));
			}

			data.put(obj);
		}

		return data;
	}
}
