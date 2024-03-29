import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;

public class createAccountHandler implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        HashMap<String,String> post = new HashMap<String,String>();
        // Read the request body
        BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
        String line = "";
        String request = "";
        while((line = in.readLine()) != null) {
            request = request + line;
        }
        // Individual key=value pairs are delimited by ampersands. Tokenize.
        String[] pairs = request.split("&");
        for(int i=0;i<pairs.length;i++) {
            // Each key=value pair is separated by an equals, and both halves require URL decoding.
            String pair = pairs[i];
            post.put(URLDecoder.decode(pair.split("=")[0],"UTF-8"),URLDecoder.decode(pair.split("=")[1],"UTF-8"));
        }
        StudentDAO dao = new StudentDAO();
        // Retrieving provided in html form username and password
        String username = post.get("username");
        String password = post.get("password");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        try{
            if(dao.createAccount(username,password)){
                // Display successfull message
                out.write("Account successfully created!");
                he.sendResponseHeaders(200,0);
            }
            else{
                out.write("Account creation failed!");
                he.sendResponseHeaders(400,0);
            }
        }
        catch (SQLException se) {
            /**
             * @exception Catches any SQL database error that might occur.
             */
            he.sendResponseHeaders(500, 0); //HTTP 500 (Internal Server Error)
        }
        out.close();

    }
}
