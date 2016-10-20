import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import org.eclipse.jetty.http.HttpGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lowmaca1 on 10/18/2016.
 */
public class Database {
    public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/exercise_tracker";

    private static final String EXERCISES_TABLE = "exercises";
    private static final String ENTRIES_TABLE = "entries";
    private static final String BODYPARTS_TABLE = "bodyparts";

    private Connection connection;

    private Map<Integer, BodyPart> bodyParts;
    private Map<Integer, Exercise> exercises;
    private Map<Integer, Entry> entries;

    public Database(String url, String user, String password) {
        bodyParts = new HashMap<>();
        exercises = new HashMap<>();
        entries = new HashMap<>();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url + "?user=app&password=db1passwd2");

            ResultSet resultSet = select("*", BODYPARTS_TABLE);
            while (resultSet.next()) {
                bodyParts.put(resultSet.getInt("id"), BodyPart.from(resultSet));
            }

            resultSet = select("*", EXERCISES_TABLE);
            while (resultSet.next()) {
                exercises.put(resultSet.getInt("id"), Exercise.from(resultSet));
            }

            resultSet = select("*", ENTRIES_TABLE);
            while (resultSet.next()) {
                entries.put(resultSet.getInt("id"), Entry.from(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> parseToMap(Request request) {
        Map<String, String> map = new HashMap<>();
        String[] values = request.body().split("\r\n");
        for (String val : values) {
            String[] pair = val.split("=");
            if (pair.length > 1) {
                map.put(pair[0].trim(), pair[1].trim());
            } else {
                map.put(pair[0].trim(), "");
            }
        }
        return map;
    }

    private static final String ADD_BODY_PART = "insert into " + BODYPARTS_TABLE + " (name) values (?)";

    public boolean addBodyPart(Request request) throws SQLException {
        System.out.println("Adding bodyPart " + request.body());

        Map<String, String> reqParams = parseToMap(request);

        PreparedStatement preparedStatement = connection.prepareStatement(ADD_BODY_PART);
        preparedStatement.setString(1, reqParams.get("name"));
        return preparedStatement.execute();
    }

    private static final String ADD_EXERCISE = "insert into " + EXERCISES_TABLE + " (bodyPartId, name, description, url)" +
            " values (?, ?, ?, ?)";

    public boolean addExercise(Request request) throws SQLException {
        System.out.println("Adding exercise " + request.body());

        Map<String, String> reqParams = parseToMap(request);

        PreparedStatement preparedStatement = connection.prepareStatement(ADD_EXERCISE);
        preparedStatement.setInt(1, Integer.parseInt(reqParams.get("bodyPartId")));
        preparedStatement.setString(2, reqParams.get("name"));
        preparedStatement.setString(3, reqParams.get("description"));
        preparedStatement.setString(4, reqParams.get("url"));
        return preparedStatement.execute();
    }

    private static final String ADD_ENTRY = "insert into " + ENTRIES_TABLE + " (date, exerciseId, numReps, repDuration_sec, repWeight_lbs, note)" +
            " values (?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_ENTRY = "delete from " + ENTRIES_TABLE + " where id=?";

    public boolean addEntry(Request request) throws SQLException {
        System.out.println("Adding entry for " + request.body());

        Map<String, String> reqParams = parseToMap(request);

        PreparedStatement preparedStatement = connection.prepareStatement(ADD_ENTRY);
        preparedStatement.setString(1, reqParams.get("date"));
        preparedStatement.setInt(2, Integer.parseInt(reqParams.get("exerciseId")));
        preparedStatement.setInt(3, Integer.parseInt(reqParams.get("numReps")));
        preparedStatement.setInt(4, Integer.parseInt(reqParams.get("repDuration_sec")));
        preparedStatement.setInt(5, Integer.parseInt(reqParams.get("repWeight_lbs")));
        preparedStatement.setString(6, reqParams.get("note"));
        return preparedStatement.execute();
    }

    public boolean removeEntry(Request request) throws SQLException {
        int id = Integer.parseInt(request.params(":id"));
        System.out.println("Removing entry " + id);

        PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_ENTRY);
        preparedStatement.setInt(1, id);
        return preparedStatement.execute();
    }

    public JSONArray getEntries() throws SQLException, JSONException {
        JSONArray json = new JSONArray();
        ResultSet resultSet = select("*", ENTRIES_TABLE);
        while (resultSet.next()) {
            Entry entry = Entry.from(resultSet);
            JSONObject obj = entry.toJson();
            // TODO fix this... if anything is added to exercises || body parts after start up this needs to be udpated
            obj.put("exerciseName", exercises.get(entry.exerciseId).name);
            obj.put("bodyPartName", bodyParts.get(exercises.get(entry.exerciseId).bodyPartId).name);
            json.put(obj);
        }
        return json;
    }

    public JSONArray getExercises() throws SQLException, JSONException {
        JSONArray json = new JSONArray();
        ResultSet resultSet = select("*", EXERCISES_TABLE);
        while (resultSet.next()) {
            JSONObject obj = Exercise.from(resultSet).toJson();
            json.put(obj);
        }
        return json;
    }

    public JSONArray getBodyParts() throws SQLException, JSONException {
        JSONArray json = new JSONArray();
        ResultSet resultSet = select("*", BODYPARTS_TABLE);
        while (resultSet.next()) {
            JSONObject obj = BodyPart.from(resultSet).toJson();
            json.put(obj);
        }
        return json;
    }

    private ResultSet select(String field, String table) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("select " + field + " from " + table);
    }
}
