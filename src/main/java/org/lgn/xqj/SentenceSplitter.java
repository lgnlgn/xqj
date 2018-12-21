package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		String dated = input.replaceAll("\\d{4}\\D\\d{1,2}\\D\\d{1,2}日*", "＋datex");
		String timed = dated.replaceAll("\\d{1,2}时(\\d{1,2}分)*(\\d{1,2}秒)*", "＋timex");
		timed = timed.replaceAll("\\d{1,2}:\\d+(:\\d{2,})*(:\\d+)*", "＋timex");
		String phoned = timed.replaceAll("\\d{3,4}-\\d{7,8}", "＋phonex");
		String ratiod = phoned.replaceAll("\\d{1,2}-\\d{1,2}%", "＋ratiox");
		return ratiod;
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
				String[] paragraphs = line.split("(?<=。)\\s{1,}"); //must be a paragraph
//				System.out.println("paragraphs:" + paragraphs.length);
				for (String paragraph : paragraphs){
					//sentences
					String[] sentences = paragraph.split("[。！]）{0,1}");
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
	
	public static void splitBlankAndOLabel(BufferedWriter writer, String sentence, boolean splitBlank) throws IOException{
		for(String ss : sentence.split("\\s+(?=[^\\_])", splitBlank==true?10000:1))
			writer.write(ss + "\n");
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
