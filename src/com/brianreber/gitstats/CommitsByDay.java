package com.brianreber.gitstats;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

// TODO: by author as well
public class CommitsByDay extends BarChartPage {

	public CommitsByDay(String projectTitle) {
		super(projectTitle, "commitsday.html", "Commits By Day");
	}

	@Override
	protected JSONArray parseData(List<Commit> commits) throws JSONException {
		JSONArray data = new JSONArray();
		JSONArray names = new JSONArray();

		int[] days = new int[8];

		// Add the title of the axis to the array first
		names.put("Day");
		names.put("Commits");

		// add the names to the return JSONArray
		data.put(names);

		Calendar calendar = Calendar.getInstance();
		int index = 0;

		while (index < commits.size()) {
			Commit tmp = commits.get(index);

			// Get an instance of the time of the commit
			calendar.setTime(tmp.getDate());
			Integer key = calendar.get(Calendar.DAY_OF_WEEK);

			days[key]++;

			index++;
		}

		// Generate our JSONArray
		for (int i = 0; i < 7; i++) {
			JSONArray obj = new JSONArray();
			obj.put(DateTime.DAY_TITLES[i]);
			obj.put(days[i + 1]);

			data.put(obj);
		}

		return data;
	}
}
