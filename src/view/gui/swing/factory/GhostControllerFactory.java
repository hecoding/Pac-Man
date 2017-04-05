package view.gui.swing.factory;

import java.util.EnumMap;
import java.util.HashMap;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class GhostControllerFactory {
	private static GhostControllerFactory instance;
	private HashMap<String, Class<? extends Controller<EnumMap<GHOST,MOVE>>>> registered = new HashMap<>();
	
	private GhostControllerFactory() {}
 	
	public static GhostControllerFactory getInstance() {
		if (instance == null){
			instance = new GhostControllerFactory();
		}
		return instance;
	}
	
	public void register(Class<? extends Controller<EnumMap<GHOST,MOVE>>> obj) {
		this.registered.put(obj.getSimpleName(), obj);
	}
	
	public Controller<EnumMap<GHOST,MOVE>> create(String id) {
		Class<? extends Controller<EnumMap<GHOST,MOVE>>> prod = this.registered.get(id);
		
		if (prod == null)
			return null;
		
		try {
			return prod.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	public String[] getRegisteredKeys() {
		return this.registered.keySet().toArray(new String[this.registered.size()]).clone();
	}
}
