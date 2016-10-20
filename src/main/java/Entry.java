import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * Created by lowmaca1 on 10/18/2016.
 */
public class Entry {
    int id;
    String date;
    int exerciseId;
    int numReps;
    int repDuration_sec;
    int repWeight_lbs;
    String note;

    public static Entry from(ResultSet resultSet) throws SQLException {
        Entry entry = new Entry();
        entry.id = resultSet.getInt("id");
        entry.date = resultSet.getString("date");
        entry.exerciseId = resultSet.getInt("exerciseId");
        entry.numReps = resultSet.getInt("numReps");
        entry.repDuration_sec = resultSet.getInt("repDuration_sec");
        entry.repWeight_lbs = resultSet.getInt("repWeight_lbs");
        entry.note = resultSet.getString("note");
        return entry;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("date", date);
        json.put("exerciseId", exerciseId);
        json.put("numReps", numReps);
        json.put("repDuration_sec", repDuration_sec);
        json.put("repWeight_lbs", repWeight_lbs);
        json.put("note", note);
        return json;
    }
}
