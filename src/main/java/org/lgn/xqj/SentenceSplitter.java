package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

/**
 * for label and segment
 * @author lgn
 *
 */
public class SentenceSplitter {

	static Segment seg = HanLP.newSegment();
	static HashSet<String> dots = new HashSet<>();
	
	public static void loadDict() throws URISyntaxException{


		String customDict = "words.txt";

		//read file into stream, try-with-resources

		URL systemResource = ClassLoader.getSystemResource(customDict);
		try (Stream<String> stream = Files.lines(Paths.get(systemResource.toURI()))) {
			stream.map(t ->t.toLowerCase()).forEach(CustomDictionary::add);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String replaceTrival(String input){
		String dated = input.replaceAll("\\d{4}\\D\\d{1,2}\\D\\d{1,2}日*", "_datex");
		String timed = dated.replaceAll("\\d{1,2}时(\\d{1,2}分)*(\\d{1,2}秒)*", "_timex");
		timed = timed.replaceAll("\\d{1,2}:\\d+(:\\d{2,})*(:\\d+)*", "_timex");
		String phoned = timed.replaceAll("\\d{3,4}-\\d{7,8}", "_phonex");
		return phoned;
	}
	
	
	public static void splitRaw(String inFile, String labelFile) throws IOException{
		BufferedReader reader = Files.newBufferedReader(Paths.get(inFile));
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(labelFile));
		String line = null;
		while((line = reader.readLine())!= null){
			if (line.startsWith("归整病历")){
				break;
			}
			if (line.length() > 6){
				String[] paragraphs = line.split("(?<=。)\\s{1,}"); //must be a paragraph
				System.out.println("paragraphs:" + paragraphs.length);
				for (String paragraph : paragraphs){
					//sentences
					String[] sentences = paragraph.split("[。！]）{0,1}");
					for (String s : sentences){
						
						//for segment
						segAndTerms(writer,s);
						
						//for labeled
						String replaced = replaceTrival(s);
						splitAndOLabel(writer, replaced);
					}
				}
			}else{
				writer.write("\n" + line + "\n");
			}
		}
		writer.close();
		reader.close();
	}
	
	public static void splitAndOLabel(BufferedWriter writer, String sentence) throws IOException{
		for(String ss : sentence.split("\\s+(?=[^\\_])"))
			writer.write(ss + "\n");
	}
	
	public static void segAndTerms(BufferedWriter writer, String sentence) throws IOException{
		List<Term> seg2 = seg.seg(sentence.toLowerCase());
		List<String> collect = seg2.stream()
				.filter(t -> !(t.word.trim().isEmpty()) && !dots.contains(t.word.trim()) )
				.map(t -> t.word.trim())
				.collect(Collectors.toList());
		writer.write  (collect.stream().collect(Collectors.joining(" ")));
	}
	
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		loadDict();
		
		String inputDir = "D:/xqj/raws";
		String outputDir = "D:/xqj/sentences";
		File[] listFiles = new File(inputDir).listFiles();
		for(File f : listFiles){
			System.out.println(f.getName());
			splitRaw(f.getAbsolutePath(), outputDir + "/" + f.getName());
		}
	}

}
