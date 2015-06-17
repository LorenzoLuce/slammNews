import org.elasticsearch.client.transport.TransportClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticFacade {

    static final String LOGIN = "";
    static final String URL = "http://54.246.17.253:80/news_production/news/_search?q=articleBody:clinton&size=1000";

    public static JSONArray queryNews() throws IOException, ParseException {
        URL url = new URL(URL);
        String encoded = new sun.misc.BASE64Encoder().encode(LOGIN.getBytes());
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Authorization", "Basic " + encoded);
        String response = org.apache.commons.io.IOUtils.toString(new BufferedReader(new InputStreamReader(conn.getInputStream())));
        JSONParser jsonParser2 = new JSONParser();
        return (JSONArray) ((JSONObject) ((JSONObject) jsonParser2.parse(response)).get("hits")).get("hits");
    }

    public static String indexAnalysis(String relation, long freq) {
        TransportClient client = ElasticClient.getInstance().getClient();
        try {
            client.prepareIndex("analysis", "apriori")
                    .setSource(jsonBuilder().startObject()
                                    .field("relation", relation)
                                    .field("frequency", freq)
                                    .field("timestamp", new Timestamp(new Date().getTime()))
                                    .endObject()
                    ).execute().actionGet();
        } catch (Exception e){}
        return relation;
    }

    public static void indexEntity(String name, Double sentiment) {
        TransportClient client = ElasticClient.getInstance().getClient();
        try {
            client.prepareIndex("entities", "entity")
                    .setSource(jsonBuilder().startObject()
                                    .field("name", name)
                                    .field("sentiment", sentiment)
                                    .endObject()
                    ).execute().actionGet();
        } catch (Exception e){}
    }

    public static void indexLocation(Double lat, Double lon) {
        TransportClient client = ElasticClient.getInstance().getClient();
        try {
            client.prepareIndex("locations", "location")
                    .setSource(jsonBuilder().startObject()
                                    .field("location", new Double[]{lon,lat})
                                    .endObject()
                    ).execute().actionGet();
        } catch (Exception e){}
    }

}