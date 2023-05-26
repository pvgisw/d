Mymapper:

package wordcount;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Mymapper extends Mapper<Object,Text,Text,IntWritable> {

	public void map(Object key,Text value,Context context)
	throws IOException, InterruptedException
	{
		StringTokenizer itr = new StringTokenizer(value.toString());
		
		//while(itr.hasMoreTokens()) {
			context.write(new Text(itr.nextToken()), new IntWritable(1));
		//}
		
	}
}
-----

Myreducer:

package wordcount;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Myreducer extends Reducer <Text,IntWritable,Text,IntWritable> {
	
	public void reduce(Text key,Iterable <IntWritable> values,Context context) 
	throws IOException, InterruptedException
	{
		int sum = 0;
		for(IntWritable value:values) {
			sum += value.get();
		}
		context.write(key, new IntWritable(sum));
	}
	
}
-----

Wordcount:

package wordcount;

import java.net.URI;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Wordcount {

	public static void main(String[] args) 
	throws Exception
	{
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf,"Wordcount");
		job.setJarByClass(Wordcount.class);
		job.setMapperClass(Mymapper.class);
		job.setReducerClass(Myreducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
		
		FileSystem fs = FileSystem.get(URI.create("hdfs://localhost:9000"), conf);
		Path fname = fs.listStatus(new Path(args[1]))[1].getPath();
		
		Scanner sc = new Scanner(fs.open(fname));
		
		String str = null;
		String ans = null;
		int max = 0;
		
		while(sc.hasNext()) {
			str = sc.nextLine();
			String[] strOfArr = str.trim().split("\\s+");
			int n = Integer.parseInt(strOfArr[1]);
			if(n>max) {
				max = n;
				ans = str;
			}
		}
		
		System.out.println("Maximum count of IP = " + ans);
		
	}

}





hadoop jar /home/hduser/Desktop/test2.jar proj2.counter /shreek/access_log_short.txt /shreek/out
