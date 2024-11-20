package fmi_hadoop.finance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class TotalIncomeMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
	private String yearFilter;
	private String industryFilter;
	private String search;
	private  List<String> filter2 ;
	
	@Override
	public void configure(JobConf job) {
		filter2 = new ArrayList<>();
		
		yearFilter = job.get("year", "");
		industryFilter = job.get("industry", "");
		search = job.get("variable_name", "");
		
		String[] filter = yearFilter.split("-");
		int startYear = Integer.parseInt(filter[0]);
		int endYear = Integer.parseInt(filter[1]);
		
		for (int i = startYear; i <= endYear; i++) {
			filter2.add(Integer.toString(i));
		}
		
	}

	@Override
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		String[] columns = value.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		
		Boolean emptyIndustry = industryFilter.isEmpty();
		Boolean emptyYear = yearFilter.isEmpty();
		String yearToLow = columns[0].toLowerCase();
		String industryToLow = columns[3].toLowerCase();
		String variableNameToLow = columns[6].toLowerCase();
		
	
		boolean containsAny = filter2.stream().anyMatch(element -> yearToLow.toLowerCase().contains(element.toLowerCase()));
		
		if (((containsAny && industryToLow.contains(industryFilter.toLowerCase())) 
				|| (containsAny) && emptyIndustry) 
				|| (emptyYear && industryToLow.contains(industryFilter.toLowerCase()))
				&& variableNameToLow.contentEquals(search.toLowerCase()))
		{
			try
			{
				Text keyOutput = new Text("Year: " + columns[0] + " Industry: " + columns[3]);
				Text valueOutput = new Text(columns[6] + ": " + columns[8]);
				
				output.collect(keyOutput, valueOutput);
				
			}catch(NumberFormatException ex)
			{
				System.err.println("Something went wrong..." + value.toString()
	
				+ "\n" + ex.getMessage());
			}
		}
	}
}
