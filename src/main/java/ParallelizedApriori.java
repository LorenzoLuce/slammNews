import java.io.*;
import java.util.*;
import com.google.common.base.Joiner;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.apache.spark.rdd.RDD;

public class ParallelizedApriori {

    public static void discover(JavaRDD<ArrayList<String>> transactions) throws IOException {
        double minSupport = 0.1;
        int numPartition = -1;
        FPGrowthModel<String> model = new FPGrowth().setMinSupport(minSupport).setNumPartitions(numPartition).run(transactions);
        RDD<FPGrowth.FreqItemset<String>> teta = model.freqItemsets();
        JavaRDD<FPGrowth.FreqItemset<String>> nano = teta.toJavaRDD();
        nano.map(new Function<FPGrowth.FreqItemset<String>, String>() {
            @Override
            public String call(FPGrowth.FreqItemset<String> s) {
                return ElasticFacade.indexAnalysis(Joiner.on(",").join(s.javaItems()), s.freq());
            }
        }).reduce(new Function2<String, String, String>() {
            @Override
            public String call(String s, String s1) throws Exception {
                return s.concat(s1);
            }
        });
    }
}