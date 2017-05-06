package util;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

public class GitConn {

	private Repository repository;
	private Git git;
	private boolean established;
	
	public GitConn(){

			repository = null;
			git = null;
			established = true;
			
			try {
				git = Git.open( new File( "./.git" ) );
			} catch (IOException e2) {
				established = false;
				System.err.println("EXTLOGGER: No se ha podido establecer conexión con git. Logging sin commit hash.");
			}
			repository = git.getRepository();
	}
	
	public boolean isConnected(){
		return established;
	}
	

	public String getLastCommitHash(){
		String hashy = null;
		try {
			ObjectId id = repository.resolve(Constants.HEAD);
			hashy = id.getName();
		} catch (IOException e) {
			established = false;
			System.err.println("EXTLOGGER: No se ha podido obtener el hash del último commit.");
		}

		return hashy;
	}
	
}