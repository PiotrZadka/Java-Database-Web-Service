import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class handles restful inserting into database
 */
public class insertHandler implements HttpHandler {
    /**
     * This method handles retrieving post request from REST client to delete specific student.
     * @param he Stores http request for post method
     * @throws IOException SQL error
     */
    public void handle(HttpExchange he) throws IOException {
        Gson gson = new Gson();

        HashMap<String, String> insertResult = new HashMap<String, String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
        String line = "";
        String request = "";
        while ((line = in.readLine()) != null) {
            request = request + line;
        }
        String[] pairs = request.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            insertResult.put(URLDecoder.decode(pair.split("=")[0], "UTF-8"), URLDecoder.decode(pair.split("=")[1], "UTF-8"));
        }
        StudentDAO dao = new StudentDAO();
        Student student = gson.fromJson(insertResult.get("student"), Student.class);
        int apiKey = Integer.valueOf(insertResult.get("key"));

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        try {
            if (dao.checkApiKey(apiKey)) {
                try {
                    dao.insertStu(student);
                    he.sendResponseHeaders(200, 0); //HTTP 200 (OK)
                    out.write("Student added!");
                } catch (SQLException se) {
                    he.sendResponseHeaders(500, 0); //HTTP 500 (Internal Server Error)
                    out.write("Error adding Student");
                }

            }
            else
            {
                he.sendResponseHeaders(403, 0); //HTTP 403 (FORBIDDEN ACCESS)
                out.write("Invalid key!");
                out.close();
            }
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }

        out.close();

    }
}
