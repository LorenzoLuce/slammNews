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
        FPGrowthModel<String> model = new FPGrowth().setMinSupport(MINSUPPORT)
        	.setNumPartitions(NUMPARTITION).run(transactions);
        JavaRDD<FPGrowth.FreqItemset<String>> javardd = model.freqItemsets().toJavaRDD();
        javardd.forEach((s) -> {
	        ElasticFacade.indexAnalysis(Joiner.on(",").join(s.javaItems()), s.freq());
        });
    }
}
