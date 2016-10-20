import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by lowmaca1 on 10/18/2016.
 */
public class Exercise {
    int id;
    int bodyPartId;
    String name;
    String description;
    String url;

    public static Exercise from(ResultSet resultSet) throws SQLException {
        Exercise exercise = new Exercise();
        exercise.id = resultSet.getInt("id");
        exercise.bodyPartId = resultSet.getInt("bodyPartId");
        exercise.name = resultSet.getString("name");
        exercise.description = resultSet.getString("description");
        exercise.url = resultSet.getString("url");
        return exercise;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("bodyPartId", bodyPartId);
        json.put("name", name);
        json.put("description", description);
        json.put("url", url);
        return json;
    }
}
