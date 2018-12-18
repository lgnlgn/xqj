package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hankcs.hanlp.dependency.nnparser.parser_dll;

public class SentenceSplitter {

	public static String replaceTrival(String input){
		String dated = input.replaceAll("\\d{4}\\D\\d{1,2}\\D\\d{1,2}日*", "_datex");
		String timed = dated.replaceAll("\\d{1,2}时(\\d{1,2}分)*(\\d{1,2}秒)*", "_timex");
		timed = timed.replaceAll("\\d{1,2}:\\d+(:\\d{2,})*(:\\d+)*", "_timex");
		String phoned = timed.replaceAll("\\d{3,4}-\\d{7,8}", "_phonex");
		return phoned;
	}
	
	
	public static void splitRaw(String in, String out) throws IOException{
		BufferedReader reader = Files.newBufferedReader(Paths.get(in));
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(out));
		String line = null;
		while((line = reader.readLine())!= null){
			if (line.startsWith("归整病历")){
				break;
			}
			if (line.length() > 6){
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
		writer.close();
		reader.close();
	}
	
	public static void main(String[] args) throws IOException {
		String inputDir = "D:/xqj/raws";
		String outputDir = "D:/xqj/sentences";
		File[] listFiles = new File(inputDir).listFiles();
		for(File f : listFiles){
			System.out.println(f.getName());
			splitRaw(f.getAbsolutePath(), outputDir + "/" + f.getName());
		}
	}

}
