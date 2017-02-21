package util.externallogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;

/*
 * Process Log objects and extracts some metrics as CSV format
 */
public class ExtLogger {

	private static BufferedWriter writer;
	
	public ExtLogger() {
		File dir = new File("logs");
		
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
			System.out.println("Logs directory created.");
		}
	}
	
	public void generateCSV(ExtLog log, String filename){
		String header = "Probabilidad_de_mutación,Probabilidad_de_cruce,Tamaño_de_la_población,Número_de_iteraciones,Iteraciones_por_individuo,Función_de_fitness,Fitness,Puntos_promedio,Fenotipo,Tiempo_de_Ejecución";
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
				
			writer.write(log.getMutationProb() + "," + log.getCrossProb() + "," + log.getTamPob() + "," + log.getNumIteraciones() + "," + log.getIteracionesPorIndividuo() + "," + log.getFitnessFunc() + "," + log.getFitness() + "," + log.getAveragePoints() + "," + log.getPhenotype() + "," + log.getExecTime() + System.lineSeparator());
			
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
