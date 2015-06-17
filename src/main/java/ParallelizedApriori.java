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

    static final double minSupport = 0.1;
    static final int numPartition = 0.1;

    public static void discover(JavaRDD<ArrayList<String>> transactions) throws IOException {
        FPGrowthModel<String> model = new FPGrowth().setMinSupport(minSupport).setNumPartitions(numPartition).run(transactions);
        RDD<FPGrowth.FreqItemset<String>> rdd = model.freqItemsets();
        JavaRDD<FPGrowth.FreqItemset<String>> javardd = teta.toJavaRDD();
        javardd.map((s) -> {
	        return ElasticFacade.indexAnalysis(Joiner.on(",").join(s.javaItems()), s.freq());
        }).reduce((s,s1) -> { 
            return s.concat(s1); 
        });
    }
}
