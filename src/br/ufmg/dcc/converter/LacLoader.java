package br.ufmg.dcc.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;

public class LacLoader{

	public static Instances convert(String file, String rel) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		Map<String, Set<String>> features = new HashMap<String, Set<String>>();
		Set<String> labels = new HashSet<String>();
		
		List<String> instances = new ArrayList<String>();
		
		while(br.ready()){
			String line = br.readLine();
			instances.add(line);
			
			String[] splitted = line.split(" ");
			
//			String id = splitted[0];
			String clazz = splitted[1].replace("CLASS=", "");
			
			labels.add(clazz);
			
			for(int f = 2; f < splitted.length; f++){
				String feature = String.format("w[%d]", (f-2));
				String value = splitted[f].replace("w=", "");
				
				Set<String> values = null;
				if(features.containsKey(feature)){
					values = features.get(feature);
				}else{
					values = new HashSet<String>();
				}
				
				values.add(value);
				
				features.put(feature, values);
			}
		}
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		for(int f = 0; f < features.size(); f++){
			String feature = String.format("w[%d]", f);
			List<String> values = new ArrayList<String>(features.get(feature));
			
			Attribute a = new Attribute(feature, values);
			
			attributes.add(f, a);			
		}
		
		List<String> labelsValues = new ArrayList<String>(labels);
		Attribute classes = new Attribute("_class_", labelsValues);
		
		attributes.add(classes);
		
		Instances dataset = new Instances(rel, attributes, instances.size());
		dataset.setClassIndex(dataset.numAttributes()-1);
		
		for(int i = 0; i < instances.size(); i++){
			double[] valsRel = new double[attributes.size()];
			
			String[] inst_str = instances.get(i).split(" ");
//			String id = inst_str[0];
			String label = inst_str[1].replace("CLASS=", "");
			int att = 0;
			
			for(int f = 2; f < inst_str.length; f++){
				String value = inst_str[f].replace("w=", "");

				valsRel[att] = attributes.get(att).indexOfValue(value);
				
				att++;
			}
			
			for(int f = att; f < valsRel.length; f++){
				valsRel[f] = Utils.missingValue();
			}
			
			valsRel[valsRel.length-1] = attributes.get(attributes.size()-1).indexOfValue(label);
			
			dataset.add(new DenseInstance(1, valsRel));
		}
		
		return dataset;
	}
	
	public static void main(String[] args) throws Exception{
		String file_format_name = "/home/rloliveirajr/disambiguation/or_converted/disambiguation/sort_%s_GDE_LAC.all";
		String[] times = {"cruzeiro", "galo", "inter", "flu", "palmeiras", "spfc"};
		
//		for(String time : times){
//			String file = String.format(file_format_name, time);
//			Instances dataset = convert(file, time);
//			
//			ArffSaver saver = new ArffSaver();
//			saver.setInstances(dataset);
//			saver.setFile(new File("/home/rloliveirajr/disambiguation/or_converted/disambiguation/" + time + ".arff"));
//			saver.setDestination(new File("/home/rloliveirajr/disambiguation/or_converted/disambiguation/" + time + ".arff"));   // **not** necessary in 3.5.4 and later
//			saver.writeBatch();
//		}
		file_format_name = "/home/rloliveirajr/disambiguation/or_converted/disambiguation/sort-%s.tst";
		for(String time : times){
			String file = String.format(file_format_name, time);
			Instances dataset = convert(file, time);
			
			ArffSaver saver = new ArffSaver();
			saver.setInstances(dataset);
			saver.setFile(new File("//home/rloliveirajr/disambiguation/or_converted/disambiguation/" + time + ".arff"));
			saver.setDestination(new File("/home/rloliveirajr/disambiguation/or_converted/disambiguation/" + time + ".arff"));   // **not** necessary in 3.5.4 and later
			saver.writeBatch();
		}
		
		
		
	}
	
}
