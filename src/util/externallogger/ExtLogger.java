package util.externallogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import util.GitConn;

import java.io.OutputStreamWriter;

/*
 * Process Log objects and extracts some metrics as CSV format
 */
public class ExtLogger {

	private static BufferedWriter writer;
	private GitConn gitc;
	
	public ExtLogger() {
		//Establish git connection
		gitc = new GitConn();
		
		//Create logs folder if nonexistant
		File dir = new File("logs");
		
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
			System.out.println("Logs directory created.");
		}
	}
	
	public void generateCSV(ExtLog log, String filename){
		String header = "Mutation_probability,Crossing_probability,Population_size,Number_of_iterations,Iterations_per_element,Fitness_function,Fitness,Average_score,Phenotype,Execution_time,Last_commit_hash";
		boolean isHeaderMissing = false;
		String filePath = "logs/".concat(filename);

		System.out.println("Updated log under: " + filePath);
		
	  	// Check or create file
	  	File fich = new File(filePath);
	  	if(!fich.exists())
	  		isHeaderMissing = true;
	  	else{
	  		try {
				fich.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	  	}
	  	
		// File opening
	    Writer fstream = null;
		try {
			fstream = new OutputStreamWriter (new FileOutputStream(filePath, true), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
        writer = new BufferedWriter(fstream);
		
		try {
			if(isHeaderMissing)
				writer.write(header + System.lineSeparator());
			
			String hash = null;
			if(gitc.isConnected())
				hash = gitc.getLastCommitHash();
			if(hash == null)
				hash = "Git connection not established.";
			writer.write(log.getMutationProb() + "," + log.getCrossProb() + "," + log.getTamPob() + "," + log.getNumIteraciones() + "," + log.getIteracionesPorIndividuo() + "," + log.getFitnessFunc() + "," + log.getFitness() + "," + log.getAveragePoints() + "," + log.getPhenotype() + "," + log.getExecTime() + "," + hash + System.lineSeparator());
			
		} catch (IOException e) {
			System.err.println("Error writing " + filename);
			e.printStackTrace();
		}
		
		try {
			writer.close();
			fstream.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
}
