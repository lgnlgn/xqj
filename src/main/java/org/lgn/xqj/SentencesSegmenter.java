package org.lgn.xqj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SentencesSegmenter {

	public static void process(String inDir, String outDir) throws IOException{
		File[] files = new File(inDir).listFiles();
		for(File f : files){
			File out = new File(outDir + "/" + f.getName());
			if (f.getName().endsWith(".txt")){
				System.out.println(f.getName());
				seg(f, out);
			}
		}
	}
	
	public static void seg(File in, File out) throws IOException{
		BufferedReader reader = Files.newBufferedReader(Paths.get(in.getAbsolutePath()));
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(out.getAbsolutePath()));
		String line = null;
		while((line = reader.readLine())!= null){
			String[] split = line.split("\t");
			if (split.length == 2){
				writer.write(split[0] + "\t" );
				writer.write(Segmenter.seg2Line(split[1]) + "\n");
			}else{
				writer.write(line + "\n");
			}
		}
		reader.close();
		writer.close();
	}
	
	
	public static void main(String[] args) throws IOException {

		process("D:/xqj/sentences", "D:/xqj/sentences_term");
	}

}
