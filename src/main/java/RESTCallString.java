import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

public class RESTCallString {

    static final String API_KEY = "";
    static final String OUTPUT = "json";
    static final String USER_AGENT = "Mozilla/5.0";

    public static String alchemyTextGetRelations(String content) throws IOException {
        final String TEXT = URLEncoder.encode(content, "UTF-8");
        URL urlObj = new URL("http://access.alchemyapi.com/calls/text/TextGetRelations" +
                "?apikey=" + API_KEY +
                "&text=" + TEXT +
                "&outputMode=" + OUTPUT
        );
        URLConnection connection = urlObj.openConnection();
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) !=  null) {
            builder.append(line + "\n");
        }
        return new String(builder);
    }

    public static String alchemyTextGetEntities(String content, String api_call, String sentiment) throws IOException {
        final String TEXT = URLEncoder.encode(content, "UTF-8");
        String url = "http://access.alchemyapi.com/calls/text/"+api_call;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept-encoding", "gzip");
        String urlParameters = "apikey=" + API_KEY +
                "&text=" + TEXT +
                "&maxRetrieve=100" +
                sentiment +
                "&outputMode=" + OUTPUT;
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader( new InputStreamReader(new GZIPInputStream(con.getInputStream())));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return new String(response.toString());
    }

}