package org.lgn.xqj;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class Segmenter {

	static Segment seg = HanLP.newSegment();
	static HashSet<String> dots = new HashSet<>();
	
	static {
		seg.enableNameRecognize(false);
		try{
			loadDict();
		}catch(URISyntaxException e){
			e.printStackTrace();
		}
	}
	
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
	
	public static boolean termOk(Term t){
		return 		!(t.nature == Nature.m  )
				&& 	!(t.nature == Nature.w  )
				&&  !(t.word.trim().isEmpty())
				&&  !dots.contains(t.word.trim()) ;
	}
	
	/**
	 * lowercase + trim + stops
	 * @param content
	 * @return
	 */
	public static List<String> simpleSeg(String content){
		List<Term> seg2 = seg.seg(content.toLowerCase());
		List<String> collect = seg2.stream()
				.filter(Segmenter::termOk )
				.map(t -> t.word.trim())
				.collect(Collectors.toList());
		return collect;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
