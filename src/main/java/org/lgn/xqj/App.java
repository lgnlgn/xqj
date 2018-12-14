package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	static String raw = "d:/xqj/raw.txt";
	static String out1 = "d:/xqj/out1.txt";
	static HashSet<String> dots = new HashSet<>();
	public static void loadDict(){
		
		
		String customDict = "d:/xqj/words.txt";

		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(customDict))) {
			stream.map(t ->t.toLowerCase()).forEach(CustomDictionary::add);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static String analyzeSentence(String input){
		String dated = input.replaceAll("\\d{4}\\D\\d{1,2}\\D\\d{1,2}\\D", " datex");
		String timed = dated.replaceAll("\\d{1,2}时(\\d{1,2}分)*(\\d{1,2}秒)*", " timex");
		timed = timed.replaceAll("\\d{1,2}:\\d+(:\\d{2,})*(:\\d+)*", " timex");
		if (timed.startsWith("主治医师")){
			System.out.println(timed);
		}
		
		
		List<Term> seg2 = seg.seg(timed.toLowerCase());
		List<String> collect = seg2.stream()
			.filter(t -> !(t.word.trim().isEmpty()) && !dots.contains(t.word.trim()) )
			.map(t -> t.word.trim())
			.collect(Collectors.toList());
		return collect.stream().collect(Collectors.joining(" "));
		
	}
	
	public static void process(){

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(raw))) {
			Set<String> sentences = new HashSet<>();
			String line = null;
			while((line = reader.readLine())!= null){
				if (line.length() > 10){
					String[] paragraphs = line.split("(?<=。\\s)");
					System.out.println("paragraphs:" + paragraphs.length);
					sentences.addAll(
							Arrays.stream(paragraphs) //convert to stream; so we can write in FP-type
							.flatMap(p -> Arrays.stream(p.split("(?<=。）|。|！)")))
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

	public static void main( String[] args )
	{

		Stream<String> stream = Arrays.stream(new String[]{"a cdd", "d cdd"});
		List<String> collect = stream.map(str -> str.split(" "))
				.flatMap(strs -> Arrays.stream(strs))
				.collect(Collectors.toList());
		System.out.println(collect);
		loadDict();
		process();
		//    	String comp = "（CD34(+)；CK7(-)注：本例细胞烧灼、挤压变形，免疫组化表达欠佳，建议必要时做基因重排以明确诊断。）P53(+，阳性细胞40-50%)：";
		//
		//		Segment enableNumberQuantifierRecognize = HanLP.newSegment();
		//		List<Term> segment = enableNumberQuantifierRecognize.seg(comp.toLowerCase());
		//		
		//    	System.out.println(segment);
		//    	
		//    	String[] split = comp.split("(?<=。）)(）{0,1})");
		//    	for(String s : split){
		//    		System.out.println(s);
		//    	}


	}
}
