import org.json.JSONArray;
import org.json.JSONException;

import static spark.Spark.*;

/**
 * Created by lowmaca1 on 10/18/2016.
 */
public class Server {

    private static Database database;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: exercise-tracker <username> <password>");
            System.exit(1);
        }

        database = new Database(Database.DEFAULT_URL, args[0], args[1]);

        exception(Exception.class, (e, req, res) -> e.printStackTrace());

        staticFileLocation("/public");
        port(1234);

        post("/entries", (req, res) -> database.addEntry(req));
        get("/entries", (req, res) -> database.getEntries().toString());
        delete("/entries/:id", (req, res) -> database.removeEntry(req));

        post("/exercises", (req, res) -> database.addExercise(req));
        get("/exercises", (req, res) -> database.getExercises().toString());

        post("/bodyParts", (req, res) -> database.addBodyPart(req));
        get("/bodyParts", (req, res) -> database.getBodyParts().toString());
    }
}
