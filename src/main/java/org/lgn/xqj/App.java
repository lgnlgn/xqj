package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

/**
 * Hello world!
 *
 */
public class App 
{
	static Segment seg = HanLP.newSegment();
	static String raw = "raw.txt";
	static String out1 = "out2.txt";
	static HashSet<String> dots = new HashSet<>();
	public static void loadDict() throws URISyntaxException{


		String customDict = "words.txt";

		//read file into stream, try-with-resources
		Path path = Paths.get(customDict);
		URL systemResource = ClassLoader.getSystemResource(customDict);
		URI uri = systemResource.toURI();
		System.out.println(uri);
		try (Stream<String> stream = Files.lines(Paths.get(uri))) {
			stream.map(t ->t.toLowerCase()).forEach(CustomDictionary::add);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String replaceTrival(String input){
		String dated = input.replaceAll("\\d{4}\\D\\d{1,2}\\D\\d{1,2}日*", "_datex");
		String timed = dated.replaceAll("\\d{1,2}时(\\d{1,2}分)*(\\d{1,2}秒)*", "_timex");
		timed = timed.replaceAll("\\d{1,2}:\\d+(:\\d{2,})*(:\\d+)*", "_timex");
		return timed;
	}


	public static String analyzeSentence(String input){
		String timed = replaceTrival(input);


		List<Term> seg2 = seg.seg(timed.toLowerCase());
		List<String> collect = seg2.stream()
				.filter(t -> !(t.word.trim().isEmpty()) && !dots.contains(t.word.trim()) )
				.map(t -> t.word.trim())
				.collect(Collectors.toList());
		return collect.stream().collect(Collectors.joining(" "));

	}

	public static void splitSentence(String in, String out) throws URISyntaxException{
		try (
				BufferedReader reader = Files.newBufferedReader(Paths.get(in));
				BufferedWriter writer = Files.newBufferedWriter(Paths.get(out))) 
		{
			
			String line = null;
			while((line = reader.readLine())!= null){
				if (line.length() > 10){
					String[] paragraphs = line.split("(?<=。)\\s{1,}");
					System.out.println("paragraphs:" + paragraphs.length);
					for (String paragraph : paragraphs){
						String[] sentences = paragraph.split("[。！]）{0,1}");
						for (String s : sentences){
							writer.write(replaceTrival(s) + "\n");
						}
					}
				}else{
					writer.write("\n" + line + "\n");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void process() throws URISyntaxException{
		URL systemResource = ClassLoader.getSystemResource(raw);
		URI uri = systemResource.toURI();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(uri))) {
			Set<String> sentences = new HashSet<>();
			String line = null;
			while((line = reader.readLine())!= null){
				if (line.length() > 10){
					String[] paragraphs = line.split("(?<=。\\s+)");
					System.out.println("paragraphs:" + paragraphs.length);
					sentences.addAll(
							Arrays.stream(paragraphs) //convert to stream; so we can write in FP-type
							.flatMap(p -> Arrays.stream(p.split("[。！]）{0,1}")))
							.distinct()
							.collect(Collectors.toSet())
							);

				}
			}
			String join = sentences.stream()
					.filter(t -> t.length() > 8)
					.map(App::analyzeSentence)
					.collect(Collectors.joining("\n"));
			Files.write(Paths.get(out1), join.getBytes());

		}



		//			List<String[]> ss =  stream.filter(t->t.length() > 10)
		//			.map(doc -> doc.split("(?<=。）)(）{0,1})")).collect(Collectors.toList());

		//			.flatMap(sens -> Arrays.stream(sens))
		//			.map(s -> s.trim())
		//			.distinct()
		//			.collect(Collectors.toList());

		//			String join = Joiner.on("\n").join(sentences);

		//			Files.write(Paths.get(out1), join.getBytes());

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main( String[] args ) throws URISyntaxException
	{

		Stream<String> stream = Arrays.stream(new String[]{"a cdd", "d cdd"});
		List<String> collect = stream.map(str -> str.split(" "))
				.flatMap(strs -> Arrays.stream(strs))
				.collect(Collectors.toList());
		System.out.println(collect);
		loadDict();
		
		String inputDir = "D:/xqj/raws";
		String outputDir = "D:/xqj/sentences";
		for(int i = 1 ; i <= 3; i++){
			splitSentence(inputDir + "/raw" + i + ".txt", outputDir + "/raw" + i + ".txt");
		}
//		process();
		    	String comp = "（CD34(+)；CK7(-)注：本例细胞烧灼、挤压变形，免疫组化表达欠佳，建议必要时做基因重排以明确诊断。）P53(+，阳性细胞40-50%)：";
		//
		//		Segment enableNumberQuantifierRecognize = HanLP.newSegment();
		//		List<Term> segment = enableNumberQuantifierRecognize.seg(comp.toLowerCase());
		//		
		//    	System.out.println(segment);
		//    	
		    	String[] split = comp.split("<=。）{1,2}|。）{0,2}");
		    	for(String s : split){
		    		System.out.println(s);
		    	}


	}
}
