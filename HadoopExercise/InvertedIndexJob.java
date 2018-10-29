import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexJob {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private Text id = new Text();
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            StringTokenizer tokens = new StringTokenizer(line);
            id = new Text(tokens.nextToken());
            while (tokens.hasMoreTokens()) {
                String[] arr = tokens.nextToken().split("[^a-zA-Z]+");
                for(String text : arr) {
                    word.set(text.toLowerCase());
                    context.write(word, id);
                }
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            HashMap<String, Integer> count = new HashMap<String, Integer>();
            for (Text val : values) {
                count.put(val.toString(), count.getOrDefault(val.toString(), 0) + 1);
            }
            StringBuilder sb = new StringBuilder();
            for (String id : count.keySet()) {
                sb.append(id).append(":").append(count.get(id)).append("\t");
            }
            context.write(key, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Inverted Index");
        job.setJarByClass(InvertedIndexJob.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
