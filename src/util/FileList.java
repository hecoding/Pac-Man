package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileList {

	public static ArrayList<String> listFilesInto(String path, String extension) {
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			Files.walk(Paths.get(path))
			.filter(p -> p.toString().endsWith(extension))
			.forEach(p -> ret.add(p.toString()));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static String cleanFileName(String name) {
		if(name == null)
			return null;
		
		if(name.contains("."))
			name = name.substring(0, name.lastIndexOf("."));
		
		if(name.contains("/"))
			name = name.substring(name.lastIndexOf("/") + 1);
		
		if(name.contains("\\"))
			name = name.substring(name.lastIndexOf("\\") + 1);
		
		return name;
	}
	
	public static ArrayList<String> cleanListFiles(String path, String extension) {
		ArrayList<String> files = listFilesInto(path, extension);
		ArrayList<String> trunc = new ArrayList<String>();
		
		for(String name : files) {
			trunc.add(cleanFileName(name));
		}
		
		return trunc;
	}
	
	public static void main(String[] args) {
		System.out.println(listFilesInto("./grammar", ".bnf"));
	}
}
