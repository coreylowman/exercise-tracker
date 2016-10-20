import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lowmaca1 on 10/18/2016.
 */
public class BodyPart {
    public int id;
    public String name;

    public static BodyPart from(ResultSet resultSet) throws SQLException {
        BodyPart bodyPart = new BodyPart();
        bodyPart.id = resultSet.getInt("id");
        bodyPart.name = resultSet.getString("name");
        return bodyPart;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        return json;
    }
}
