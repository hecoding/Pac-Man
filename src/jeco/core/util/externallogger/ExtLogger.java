package jeco.core.util.externallogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ExtLogger { //Procesa objetos de clase Log

	private static BufferedWriter writer;
	
	public ExtLogger(){
		//Creacion del directorio de logs si no existe
		File dir = new File("logs");
		if(dir.exists() && dir.isDirectory()){
		}
		else{
			dir.mkdir();
			System.out.println("Directorio de logs creado.");
		}
	}
	
	public void generarCSV(ExtLog log, String nombreFichero){
		boolean faltacabecera = false;
		
		//Ruta del fichero
		String pathcsv = "logs/".concat(nombreFichero);

		System.out.println("Log actualizado en: " + pathcsv);
	  	//Comprobar si ya existe, crearlo y marcar para insertar cabecera de no existir
	  	File fich = new File(pathcsv);
	  	if(!fich.exists())
	  		faltacabecera = true;
	  	else{
	  		try {
				fich.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  	}
	  	
		//Apertura del fichero de logeo con append
	    Writer fstream = null;
		try {
			fstream = new OutputStreamWriter (new FileOutputStream(pathcsv, true), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        writer = new BufferedWriter(fstream);
		
		try {
			if(faltacabecera){
				writer.write("Probabilidad_de_mutación,Probabilidad_de_cruce,Tamaño_de_la_población,Número_de_iteraciones,Iteraciones_por_individuo,Función_de_fitness,Fitness,Puntos_promedio,Fenotipo,Tiempo_de_Ejecución" + System.lineSeparator());
			}
			writer.write(log.getMutationProb() + "," + log.getCrossProb() + "," + log.getTamPob() + "," + log.getNumIteraciones() + "," + log.getIteracionesPorIndividuo() + "," + log.getFitnessFunc() + "," + log.getFitness() + "," + log.getAveragePoints() + "," + log.getPhenotype() + "," + log.getExecTime() + System.lineSeparator());
		} catch (IOException e) {
			System.err.println("Error al escribir en el archivo Registro.log");
			e.printStackTrace();
		}
		
		try {
			writer.close();
			fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void generarTXT(ExtLog log, String nombreFichero){
	}
	
}


