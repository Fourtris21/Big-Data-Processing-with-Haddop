package fmi_hadoop.finance;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class TotalExpeditureMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, FloatWritable>{
	private String yearFilter;
	private String industryFilter;
	private String totalIncome;
	private String totalExpenditure;
	
	public void configure(JobConf job) {
		yearFilter = job.get("year", "");
		industryFilter = job.get("industry", "");
		totalIncome = "total income";
		totalExpenditure = "total expenditure";
	}
	
	@Override
	public void map(LongWritable key, Text value, OutputCollector<Text, FloatWritable> output, Reporter reporter)
			throws IOException {
		String[] columns = value.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		
		String yearToLow = columns[0].toLowerCase();
		String industryToLow = columns[3].toLowerCase();
		String variableNameToLow = columns[6].toLowerCase();
		
		if (((yearToLow.contains(yearFilter.toLowerCase()) && industryToLow.contains(industryFilter.toLowerCase())) 
				|| (yearToLow.contains(yearFilter.toLowerCase()) && industryFilter.isEmpty()) 
				|| (yearFilter.isEmpty() && industryToLow.contains(industryFilter.toLowerCase()))) 
				&& (variableNameToLow.contentEquals(totalIncome) || variableNameToLow.contentEquals(totalExpenditure)))
		{
			try
			{
				Text keyOutput = new Text("Year: " + columns[0] + " Industry: " + columns[3]);
				float searchValue = 0;
				if(!columns[8].matches("[a-zA-Z]"))
				{
					String test = columns[8].replaceAll("\"", "");
				
					if(!test.contains(","))
					{
						searchValue = Float.valueOf(test);
					}
					else
					{
						 NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
					     Number number = nf.parse(test, new ParsePosition(0));
					     searchValue = number.floatValue();
					}
					
				}
				FloatWritable valueOutput = new FloatWritable(searchValue);
				
				output.collect(keyOutput, valueOutput);
				
			}catch(NumberFormatException ex)
			{
				System.err.println("something went wrong..." + value.toString()
	
				+ "\n" + ex.getMessage());
			}
		}
	}
}
