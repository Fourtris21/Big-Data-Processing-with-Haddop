package fmi_hadoop.finance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class TotalExpenditureReducer extends MapReduceBase implements Reducer<Text, FloatWritable, Text, FloatWritable>{

	@Override
	public void reduce(Text key, Iterator<FloatWritable> values, OutputCollector<Text, FloatWritable> output, Reporter reporter)
			throws IOException {
		List<Float> resultValues = new ArrayList<Float>();
		
		while(values.hasNext()) {
			resultValues.add(values.next().get());
		}
		
		output.collect(key, new FloatWritable(resultValues.get(1) - resultValues.get(0)));
	}
}
