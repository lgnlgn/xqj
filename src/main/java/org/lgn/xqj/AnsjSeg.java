package org.lgn.xqj;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;

public class AnsjSeg {
	
	static {
		try {
			loadDict();
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void loadDict() throws IOException, URISyntaxException{

		String customDict = "words.txt";

		//read file into stream, try-with-resources

		URL systemResource = ClassLoader.getSystemResource(customDict);
		try (Stream<String> stream = Files.lines(Paths.get(systemResource.toURI()))) {
			;
			stream.map(t ->t.toLowerCase()).forEach(line->{UserDefineLibrary.insertWord(line, "n", 30);});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static List<Term> seg(String content){
		List<Term> parse = ToAnalysis.parse(content);
		return parse;
		
	}
	public static void main(String[] args) {
		
    	String comp = " 粒细胞比率65.3%；粒细胞计数2.20×10E9/L；*红细胞计数3.61×10E12/L；:1.肺癌术后（T2aN0M0IB期） 住院医师 ： ＋datex＋timex （CD34(+)；CK7(-)注：本例细胞烧灼、挤压变形，免疫组化表达欠佳，建议必要时做基因重排以明确诊断。）P53(+，阳性细胞40-50%)：";
    	System.out.println(seg(comp));
	}
}
