import org.apache.spark.api.java.JavaRDD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import java.io.IOException;
import org.apache.spark.api.java.function.Function;
import java.util.ArrayList;

public class StreamingAnalyzer {

    /* returns all the tokenized entity-relations sentences of all the news of the stream */
    public static JavaRDD<ArrayList<String>> streamingAnalysis(JavaRDD<JSONObject> newsStream) {
        return newsStream.map(new Function<JSONObject, String>() {
            @Override
            public String call(JSONObject json) {
                return (String) ((JSONObject) json.get("_source")).get("articleBody");
            }
        })
        .distinct()
        .map(new Function<String, ArrayList<String>>() {
            @Override
            public ArrayList<String> call(String articleBody) {
                try {
                    return analizeSingleNews(articleBody);
                } catch (IOException e) {}
                return new ArrayList<String>();
            }
        });
    }

    /* returns tokenized sentences which contains relations between Entities */
    public static ArrayList<String> analizeSingleNews(String articleBody) throws IOException {
        String entityJson = RESTCallString.alchemyTextGetEntities(articleBody, "TextGetRankedNamedEntities", "&sentiment=1");
        String keywordJson = RESTCallString.alchemyTextGetEntities(articleBody, "TextGetRankedKeywords", "");
        org.json.JSONObject entitiesObj = new org.json.JSONObject(entityJson);
        org.json.JSONObject keywordObj = new org.json.JSONObject(keywordJson);
        JSONArray keywords = keywordObj.getJSONArray("keywords");
        JSONArray entities = entitiesObj.getJSONArray("entities");
        ArrayList<String> keywordEntitiesList = new ArrayList<String>();
        for (int j = 0; j<keywords.length(); j++) {
            String name = keywords.getJSONObject(j).getString("text");
            keywordEntitiesList.add(name);
        }
        for (int i = 0; i<entities.length(); i++) {
            Boolean location = false;
            Double lat = 0.0;
            Double lon = 0.0;
            Double sentimentValue = 0.0;
            org.json.JSONObject entity = entities.getJSONObject(i);
            String name = entity.getString("text");
            if (!keywordEntitiesList.contains(name))
                keywordEntitiesList.add(name);
            try {
                org.json.JSONObject sentiment = entity.getJSONObject("sentiment");
                sentimentValue = Double.parseDouble(sentiment.getString("score"));
            } catch(JSONException e) {
                sentimentValue = 0.0;
            }
            try {
                org.json.JSONObject disambiguated = entity.getJSONObject("disambiguated");
                String geo = disambiguated.getString("geo");
                String[] latLong = geo.split("\\s+");
                lat = Double.parseDouble(latLong[0]);
                lon = Double.parseDouble(latLong[1]);
                location = true;
            } catch(JSONException e) {}
            if (location){
                ElasticFacade.indexLocation(lat, lon);
            } else {
                ElasticFacade.indexEntity(name, sentimentValue);
            }
        }
        return keywordEntitiesList;
    }
}