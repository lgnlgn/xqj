package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * for label and segment
 * @author lgn
 *
 */
public class SentenceSplitter {

	
	public static String replaceTrival(String input){
		String dated = input.replaceAll("20\\d{2}\\D\\d{1,2}\\D(\\d{1,2}日*)*", "△datex");
		String timed = dated.replaceAll("\\d{1,2}时(\\d{1,2}分)*(\\d{1,2}秒)*", "△timex");
		timed = timed.replaceAll("\\d{1,2}:\\d+(:\\d{2,})*(:\\d+)*", "△timex");
		String phoned = timed.replaceAll("\\d{3,4}-\\d{7,8}", "△phonex");
		String ratiod = phoned.replaceAll("\\d{1,2}-\\d{1,2}%", "△ratiox");
		return ratiod.trim();
//		return ratiod.trim().replaceAll(" ", "") ;
	}
	
	
	public static Set<String> splitRaw(String inFile, String labelFile, boolean splitBlank) throws IOException{
		BufferedReader reader = Files.newBufferedReader(Paths.get(inFile));
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(labelFile));
		
		String line = null;
		Set<String> result = new HashSet<>();
		while((line = reader.readLine())!= null){
			if (line.startsWith("归整病历") || line.equals("患者信息")){
				break;
			}
			if (line.length() > 6){
				List<String> paragraphs = filterEmpties(paragraphSplit(line)); //must be a paragraph
//				System.out.println("paragraphs:" + paragraphs.length);
				for (String paragraph : paragraphs){ 
					//sentences
					List<String> sentences = filterEmpties(stopSplit(paragraph));
					for (String s : sentences){
						String replaced = replaceTrival(s);
						
						//for segment
						result.add(replaced);
						//for labeled
						splitBlankAndOLabel(writer, replaced, splitBlank);
					}
				}
			}else{
				writer.write("\n" + line + "\n");
			}
		}
		writer.close();
		reader.close();
		return result;
	}
	
	static List<String> filterEmpties(String[] splited){
		List<String> asList = Arrays.asList(splited);
		return asList.stream().filter(s -> (s.length() > 3)).collect(Collectors.toList());
	}
	
	public static String[] dateSplit(String s ){
		return s.split("(?=[，；][^，]{0,7}?△datex|于△datex)") ;
	}
	
	public static String[] stopSplit(String s){
		return   s.split("[。！？]）{0,1}");
	}
	
	public static String[] paragraphSplit(String line){
		return  line.split("(?<=[。！？])[\\s]{1,}| ");
	}

	public static String[] colonSplit(String sentence){
		return sentence.split("(?<=[^\\、\\.\\d\\w])[，,\\s](?=[^：:\\d\\w]{2,12}[：:])");
	}
	
	public static void debug(String line, String content){
		if (line.contains(content)){
			System.out.println(line);
		}
	}
	
	public static void splitBlankAndOLabel(BufferedWriter writer, String sentence, boolean splitBlank) throws IOException{

		debug(sentence, "病例特点:1.绝经期妇女");
		
		if (sentence.contains("姓名") && sentence.contains("性别") && sentence.contains("年龄")) {//首行特殊处理
			for(String dd : dateSplit(sentence)){
				if (dd.contains("姓名") && dd.contains("性别") && dd.contains("年龄")) {
					writer.write("O\t" + dd + "\n");
//					writer.write("O\t" + seg2Line(dd) + "\n"  );
				}else{
					String[] split = colonSplit(dd);
					for(String ss : split){
						writer.write("O\t" + ss + "\n");
//						writer.write("O\t" + seg2Line(dd) + "\n"  );
					}
				}
					
				
			}
		}else{
			String[] split = colonSplit(sentence);
			for(String ss : split){
				List<String> dateSplit = filterEmpties(dateSplit(ss));
				for(String dd : dateSplit){
					writer.write("O\t" + dd + "\n"  );
//					writer.write("O\t" + seg2Line(dd) + "\n"  );
				}
			}
		}
	}
	
	
	public static String seg2Line(String s){
		List<String> simpleSeg = Segmenter.simpleSeg(s);
		return simpleSeg.stream().collect(Collectors.joining(" "));
	}
	
	public static void segToTerms(String outFile, Set<String> sentences) throws IOException{
		System.out.println("segToTerms.....");
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(outFile));
	
		Set<String> phrases = new HashSet<>();
		
		for(String sentence : sentences){
			
			List<String> collect = Segmenter.simpleSeg(sentence.toLowerCase());
			String w2vLine = collect.stream().collect(Collectors.joining(" "));
			
			if (collect.size() >= 3){
				phrases.add( w2vLine);
				
			}
		}
		for(String w2v : phrases){
			writer.write(w2v + "\n");
		}
		writer.close();
	}
	
	
	
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		
		String inputDir = "D:/xqj/raws";
		String outputDir = "D:/xqj/sentences";
		String trainingFile = "D:/xqj/train.txt";
		boolean splitBlank = false;
		Set<String> sentences = new HashSet<>();
		
		File[] listFiles = new File(inputDir).listFiles();
		for(File f : listFiles){
			System.out.println(f.getName());
			sentences.addAll(splitRaw(f.getAbsolutePath(), outputDir + "/" + f.getName(), splitBlank));
		}
		
		segToTerms(trainingFile, sentences);
		
	}

}
