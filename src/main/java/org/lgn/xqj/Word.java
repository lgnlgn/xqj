package org.lgn.xqj;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;


public class Word {

	public static String readWord(String path) {
		String buffer = "";
		try {
			if (path.endsWith(".doc")) {
				InputStream is = new FileInputStream(new File(path));
				WordExtractor ex = new WordExtractor(is);
				buffer = ex.getText();
				ex.close();
			} else if (path.endsWith("docx")) {
				OPCPackage opcPackage = POIXMLDocument.openPackage(path);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
				buffer = extractor.getText();
				extractor.close();
			} else {
				System.out.println(path + " 此文件不是word文件！");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	public static void processDir(String dir, String out) throws IOException{
		Stream<Path> list = Files.list(Paths.get(dir));
		List<Path> collect = list.collect(Collectors.toList());
		for(Path p : collect)
		{
			String string = p.toString();
			System.out.println(string);
			String fileName = string.split("\\\\")[string.split("\\\\").length -1];
			String id = fileName.split("\\+")[0] + ".txt";
			String content = readWord(string);
			if (content.equals("")){
				continue;
			}
			try(BufferedWriter w= Files.newBufferedWriter(Paths.get(out + File.separator + id))){
				w.write(content);
			}catch (IOException e) {
				System.err.println("error:" + string);
			}
			
		};
		list.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String readWord = readWord("D:/xqj/病历和归整100份（20181129）/295957+连炳烈.docx");
		System.out.println(readWord);
		processDir("D:/xqj/病历和归整100份（20181129）", "D:/xqj/raws");
	}

}
