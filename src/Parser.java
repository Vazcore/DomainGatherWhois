import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;


public class Parser {
	
	private String readFile(String filename) throws IOException{
		FileInputStream file = new FileInputStream(filename);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(file)); 
		
		String res = null;
		String line = null;
		
		while((line = br.readLine()) != null){
			res = res + line;
		}
		
		String[] h1 = res.split("serp-url__popup-content");
		for(String h : h1){
			System.out.println(h);
		}
		
		return res;
	}
	
	
	public String get_domains() throws IOException{
		String data = readFile("/home/alex/Documents/test.html");
		//System.out.println(data);
		return data;
	}
}
