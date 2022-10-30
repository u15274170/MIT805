	import java.io.IOException;
	import java.util.*;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	
	public class TransactionsAmountMonthly {
	
	  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
	    
        private Text year = new Text();
	    private final static IntWritable one = new IntWritable(1);
        private final static int COLUMN_USED = 6;
        private final static String CSV_SEPARATOR = ",";

	     public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
           
           String line = value.toString();
           // split the data with CSV_SEPARATOR
           String[] columnData = line.split(CSV_SEPARATOR);

           // collect the data with defined column
           output.collect(new Text(columnData[COLUMN_USED]), one);
	     }
	   }
	
   public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
	     public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
	       int sum = 0;
	       while (values.hasNext()) {
	         sum += values.next().get();
	       }

	       output.collect(key, new IntWritable(sum));
	     }
	   }
	
	   public static void main(String[] args) throws Exception {
	     JobConf conf = new JobConf(TransactionsAmountYearly.class);
	     conf.setJobName("TransactionAmountMonthly");
	
	     conf.setOutputKeyClass(Text.class);
	     conf.setOutputValueClass(IntWritable.class);
	
	     conf.setMapperClass(Map.class);
	     conf.setCombinerClass(Reduce.class);
	     conf.setReducerClass(Reduce.class);

	     conf.setInputFormat(TextInputFormat.class);
	     conf.setOutputFormat(TextOutputFormat.class);

	     FileInputFormat.setInputPaths(conf, new Path(args[0]));
	     FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	
	     JobClient.runJob(conf);
	   }
	}