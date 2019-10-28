package lab3;

import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class ParseCSV {

    static String[] splitComma(String value) {
        return value.split(",");
    }

    static String[] splitComma(String value, int limit) {
        return value.split(",", limit);
    }

    static String getKey(String[] parts){
        return parts[0].split("\"")[1];
    }

    static String getValue(String[] parts) {
        return parts[1].split("\"")[1].split("\"")[0];
    }

    static String getKey(String[] parts, int i){
        return parts[i];
    }

    static String getValue(String[] parts, int i) {
        return parts[i];
    }

    static JavaRDD<String> readCSV(JavaSparkContext sc, String path, String header) {
        JavaRDD<String> data = sc.textFile(path)
    }

}