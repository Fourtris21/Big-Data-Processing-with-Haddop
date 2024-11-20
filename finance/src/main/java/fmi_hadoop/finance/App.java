package fmi_hadoop.finance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import fmi_hadoop.finance.App;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;


/**
 * Hello world!
 */
public class App extends JFrame {
    public static void main(String[] args) {
    	App form = new App();
    }
    
    private JTextField year;
    private JTextField industry;
    private JTextField category;
    private JButton searchButton;
    private JTextArea resultArea;
    private JComboBox<String> dropdownList;
    private JComboBox<String> dropdownListVariableNames;
    private String selectedItem;
    private int dropDownSelectedItemIndex = 0;
    
    private String[] financialPerformanceVariables = {
    		"Total income",
    		"\"Sales, government funding, grants and subsidies\"",
    		"\"Interest, dividends and donations\"",
    		"Non-operating income",
    		"Total expenditure",
    		"Interest and donations",
    		"Indirect taxes", 
    		"Depreciation",
    		"Salaries and wages paid",
    		"Redundancy and severance",
    		"Salaries and wages to self employed commission agents",
    		"Purchases and other operating expenses",
    		"Non-operating expenses",
    		"Opening stocks",
    		"Closing stocks", 
    		"Surplus before income tax"
    };
    
    private String[] financialPositionVariables = {
    		"Total assets",
    		"Current asset", 
    		"Fixed tangible assets", 
    		"Other assets", 
    		"Total equity and liabilities", 
    		"Shareholders funds or owners equity", 
    		"Current liabilities", 
    		"Other liabilities"
    };
    
    private String[] financialRatiosVariables = {
    		"Total income per employee count",
    		"Surplus per employee count", 
    		"Current ratio", 
    		"Quick ratio",
    		"Return on equity",
    		"Return on total assets",
    		"Liabilities structure"
    };
    
    private String[] allVariables = {
    		"Total income",
    		"\"Sales, government funding, grants and subsidies\"",
    		"\"Interest, dividends and donations\"",
    		"Non-operating income",
    		"Total expenditure",
    		"Interest and donations",
    		"Indirect taxes", 
    		"Depreciation",
    		"Salaries and wages paid",
    		"Redundancy and severance",
    		"Salaries and wages to self employed commission agents",
    		"Purchases and other operating expenses",
    		"Non-operating expenses",
    		"Opening stocks",
    		"Closing stocks", 
    		"Surplus before income tax",
    		"Total assets",
    		"Current asset", 
    		"Fixed tangible assets", 
    		"Other assets", 
    		"Total equity and liabilities", 
    		"Shareholders funds or owners equity", 
    		"Current liabilities", 
    		"Other liabilities",
    		"Total income per employee count",
    		"Surplus per employee count", 
    		"Current ratio", 
    		"Quick ratio",
    		"Return on equity",
    		"Return on total assets",
    		"Liabilities structure"
    };

    public App()
    {
    	init();
    }
    
    private void init()
    {
        setTitle("Financial Data Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        year =  new JTextField(10);
        industry =  new JTextField(10);
        category =  new JTextField(10);
        searchButton = new JButton("Search");
        resultArea = new JTextArea();
        JScrollPane scroll = new JScrollPane (resultArea, 
        		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        dropdownList = new JComboBox<String>();
		dropdownList.addItem("Variable name");
		dropdownList.addItem("Net result");
		
		dropdownListVariableNames = new JComboBox(allVariables);

        JLabel yearLabel = new JLabel("Year:");
        JLabel industryLabel = new JLabel("Industry:");
        JLabel categoryLabel = new JLabel("Category:");        
        JLabel searchLabel = new JLabel("Search type:");        
        JLabel variableLabel = new JLabel("Variables:");
        
        JPanel panel = new JPanel();
        panel.add(yearLabel);
        panel.add(year);
        panel.add(industryLabel);
        panel.add(industry);
        panel.add(categoryLabel);
        panel.add(category);
        
        panel.add(searchLabel);
        panel.add(dropdownList);
        panel.add(variableLabel);
        panel.add(dropdownListVariableNames);
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.add(searchButton, BorderLayout.SOUTH);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        
        frame.setSize(1440, 900);
        frame.setVisible(true);
        
        category.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(category.getText().toLowerCase().contains("perf"))
        		{
        			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(financialPerformanceVariables);
        			dropdownListVariableNames.setModel( model );
        		}
        		else if(category.getText().toLowerCase().contains("posi"))
        		{
        			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(financialPositionVariables);
        			dropdownListVariableNames.setModel( model );
        		}
        		else if(category.getText().toLowerCase().contains("rati"))
        		{
        			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(financialRatiosVariables);
        			dropdownListVariableNames.setModel( model );
        		}
        		else
        		{
        			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(allVariables);
        			dropdownListVariableNames.setModel( model );
        		}
        	}
        });
        
        dropdownList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = dropdownList.getSelectedIndex();
				if(selectedIndex == 0)
				{
					dropdownListVariableNames.setEnabled(true);
				}
				else
				{
					dropdownListVariableNames.setEnabled(false);
				}
			}
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	selectedItem = String.valueOf(dropdownListVariableNames.getSelectedItem());
                startHadoop(year.getText(), industry.getText(), category.getText(), selectedItem);
            }
        });
    }
    
    private void startHadoop(String year, String industry, String category, String variable_name) {
    	Configuration conf = new Configuration();
		JobConf job = new JobConf(conf, App.class);
		
		dropDownSelectedItemIndex = dropdownList.getSelectedIndex();
		
		job.set("year", year);
		job.set("industry", industry);
		job.set("category", category);
		job.set("variable_name", variable_name);
		
		if(dropDownSelectedItemIndex == 0)
		{
			job.setMapperClass(TotalIncomeMapper.class);
			job.setReducerClass(TotalIncomeReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
		}
		else
		{
			job.setMapperClass(TotalExpeditureMapper.class);
			job.setReducerClass(TotalExpenditureReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(FloatWritable.class);
		}

		Path inputPath = new Path("hdfs://127.0.0.1:9000/fmi_hadoop/2.csv");
		Path outputPath = new Path("hdfs://127.0.0.1:9000/result");
		
		FileOutputFormat.setOutputPath(job, outputPath);
		FileInputFormat.setInputPaths(job, inputPath);

		try 
		{
			FileSystem fs = FileSystem.get(URI.create("hdfs://127.0.0.1:9000"), job);
			if (fs.exists(outputPath)) 
			{
				fs.delete(outputPath, true);
			}
			RunningJob task = JobClient.runJob(job);
			
			if (task.isSuccessful()) 
			{
				resultArea.setText("");
				Path resultFile = new Path("hdfs://127.0.0.1:9000/result/part-00000");
				InputStreamReader reader = new InputStreamReader(fs.open(resultFile));
				BufferedReader br = new BufferedReader(reader);
				
				String line = br.readLine();
				while(line != null) 
				{
					resultArea.append(line + "\n");
					line = br.readLine();
				}
				
				br.close();
				reader.close();
			}
			else 
			{
				System.out.println("Something went wrong :(");
			}
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
