package org.lgn.xqj;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;

public class W2v {

	public static void main(String[] args) throws IOException {
		Learn learn = new Learn() ;
        
        learn.learnFile(new File(App.out1) );
        
        learn.saveModel(new File("d:/xqj/vector.mod")) ;
        
        Word2VEC word2vec = new Word2VEC();
        word2vec.loadJavaModel("d:/xqj/vector.mod");
        HashMap<String, float[]> wordMap = word2vec.getWordMap();
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("d:/xqj/w2v.bin"))){
        	for(Entry<String, float[]> term : wordMap.entrySet()){
        		writer.write(term.getKey() + "\t");
        		float[] value = term.getValue();
        		for(int i = 0 ; i < value.length; i++){
        			writer.write(String.format("%.7f,", value[i]));
        		}
        		writer.write("\n");
        	}
        }
	}
}
