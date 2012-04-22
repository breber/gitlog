package com.brianreber.gitstats;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class CommitsByDayAndHour extends BubbleChartPage {

	/**
	 * Creates a new CommitsByDayAndHour page with the given projectTitle
	 * 
	 * @param projectTitle the title of the project
	 * @param outputPath the path to where we want to put the files
	 */
	public CommitsByDayAndHour(String projectTitle, String outputPath) {
		super(projectTitle, outputPath + "/commitsdh.html", "Commits By Day and Hour");
	}

	private class Data {
		public int[][] data = new int[7][24];
	}

	@Override
	protected JSONArray parseData(List<Commit> commits) throws JSONException {
		JSONArray data = new JSONArray();
		JSONArray names = new JSONArray();
		HashMap<String, Data> usersToCommits = new HashMap<String, Data>();

		Collections.sort(commits, new Comparator<Commit>() {
			@Override
			public int compare(Commit o1, Commit o2) {
				return o1.getAuthorName().compareTo(o2.getAuthorName());
			}
		});

		// Add the title of the axis to the array first
		names.put("ID");
		names.put("Day");
		names.put("Hour");
		names.put("User");
		names.put("Weight");

		// Add the names to our JSONArray
		for (Commit c : commits) {
			// Add the user to the list of users
			if (!usersToCommits.containsKey(c.getAuthorName())) {
				usersToCommits.put(c.getAuthorName(), new Data());
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

			// If we already have this month follow this path
			Data d = usersToCommits.get(tmp.getAuthorName());

			d.data[calendar.get(Calendar.DAY_OF_WEEK) - 1][calendar.get(Calendar.HOUR_OF_DAY)]++;

			index++;
		}

		// Generate our JSONArray
		for (String user : usersToCommits.keySet()) {
			Data current = usersToCommits.get(user);

			for (int i = 0; i < current.data.length; i++) {
				for (int j = 0; j < current.data[i].length; j++) {
					if (current.data[i][j] > 0) {
						JSONArray obj = new JSONArray();

						obj.put("");
						obj.put(i + 1);
						obj.put(j);
						obj.put(user);
						obj.put(current.data[i][j]);

						data.put(obj);
					}
				}
			}
		}

		return data;
	}
}
