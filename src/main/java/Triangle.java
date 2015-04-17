import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.join.TupleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.List;

public class Triangle {

	public static class Mapper
			extends org.apache.hadoop.mapreduce.Mapper<Object, Text, Text, TupleWritable> {

		private Text newKey = new Text();

		public void map(Object key, Text value, Context context
		) throws IOException, InterruptedException {
			System.out.println("===========map in======================");
			System.out.println(value);
			String edgeStr = value.toString();
			String[] edgeArr = edgeStr.split(" ");
			int u = Integer.parseInt(edgeArr[0]);
			int v = Integer.parseInt(edgeArr[1]);

			if ( u == v ) {
				return;
			}

			int hu = Utils.hash(u);
			int hv = Utils.hash(v);
			if (hu > hv) {
				int temp = hu;
				hu = hv;
				hv = temp;
				temp = u;
				u = v;
				v = temp;
			}

			// wystarczy emitować tylko b kluczy dla tej samej krawędzi
			// musze tylko pamietac ktora krawedz kiedy moge uzyc, stad 1,2,3
			// wyznacza to kolejnosc uzywania krawedzi podczas tworzenia trojkata
			// generuje to nam wszystkie trojkaty, poniewaz bez straty ogolnosci
			// moge zaczynac od wierzcholka o najmniejszym hashu etykiety, a nastepnie
			// przeniesc sie do kolejnego w kolejnosci. Innymi słowy - już na etapie map
			// narzucam kolejnosc, w ktorej bedziemy probowac konstruowac trojkaty.
			// ponadto dzieki temu unikne powtorzen, poniewaz dane "kodowanie" trojkata
			// dla wybranej krawedzi i jej miejsca usalonego przez 1,2,3 pojawi sie
			// tylko raz
			for ( int i = 0; i < Utils.MOD; i++ ) {
				if ( hv <= i ) {
					context.write(makeKey(hu, hv, i), makeEdge(u, v, 1));
				}
				if ( i <= hu ) {
					context.write(makeKey(i, hu, hv), makeEdge(u, v, 2));
				}
				if ( hu <= i && i <= hv ) {
					context.write(makeKey(hu, i, hv), makeEdge(u, v, 3));
				}
			}
			/*if ( hu == hv ) {
				context.write(makeKey(hu, hu, hu), makeEdge(u, v, 4));
				makeKey(hu, hu, hu);
			} else {
				context.write(makeKey(hu, hv, hv), makeEdge(u, v, 1));
				context.write(makeKey(hu, hu, hv), makeEdge(u, v, 2));
			}*/
		}

		private TupleWritable makeEdge(int u, int v, int i) {
			Writable[] writables = {new IntWritable(u), new IntWritable(v), new IntWritable(i)};
			TupleWritable res= new TupleWritable(writables);

			System.out.println("edge size: " + res.size() + ", content: " + res);

			return res;
		}

		private Text makeKey(int a, int b, int c) {
			return new Text(a + "#" + b + "#" + c);
		}
	}

	public static class Reducer
			extends org.apache.hadoop.mapreduce.Reducer<Text, TupleWritable, Text, Text> {

		public void reduce(Text key, Iterable<TupleWritable> values,
						   Context context
		) throws IOException, InterruptedException {
			Graph g = new Graph();

			System.out.println("===========reduce in======================");
			System.out.println("key: " + key + ", values: " + values);

			for (TupleWritable val : values) {
				IntWritable uwr = (IntWritable) val.get(0);
				IntWritable vwr = (IntWritable) val.get(1);
				IntWritable twr = (IntWritable) val.get(2);
				g.addEdge(uwr.get(), vwr.get(), twr.get());
			}

			List<String> triangles = g.getTriangles();
			System.out.println("===========triangles======================");
			System.out.println(triangles);

			for (String t : triangles ) {
				context.write(key, new Text(t));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf, "triangle");
		job.setJarByClass(Triangle.class);
		job.setMapperClass(Mapper.class);
		//job.setCombinerClass(Triangle.class);
		job.setReducerClass(Reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(TupleWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
