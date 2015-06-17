import java.util.ArrayList;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RealTimeAnalyzer {

	static final int SLEEP = 5400000; // 20000

	public static void main(String[] args) throws Exception {
		JavaSparkContext jsc = configSpark();
		while (true) {
				JSONArray news = ElasticFacade.queryNews();
				JavaRDD<JSONObject> newsStream = jsc.parallelize(news);
				JavaRDD<ArrayList<String>> sentences = StreamingAnalyzer.streamingAnalysis(newsStream);
				ParallelizedApriori.discover(sentences);
				Thread.sleep(SLEEP);
		}
		//jsc.close();
	}

	public static JavaSparkContext configSpark(){
		SparkConf sparkConf = new SparkConf().setAppName("PNews").setMaster("local[2]").set("spark.executor.memory", "1g");
		return new JavaSparkContext(sparkConf);
	}

}