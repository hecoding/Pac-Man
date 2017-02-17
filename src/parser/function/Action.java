package parser.function;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public enum Action implements Function {

	moveUp{
        public MOVE getMove(Game game) { 
            return MOVE.UP; 
       }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "moveUp";
		}
	},
	moveDown{
        public MOVE getMove(Game game) { 
            return MOVE.DOWN; 
       }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "moveDown";
		}
	},
	moveLeft{
        public MOVE getMove(Game game) { 
            return MOVE.LEFT; 
       }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "moveLeft";
		}
	},
	moveRight{
        public MOVE getMove(Game game) { 
            return MOVE.RIGHT; 
       }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "moveRight";
		}
	},
	moveNeutral{
        public MOVE getMove(Game game) { 
            return MOVE.NEUTRAL; 
       }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "moveNeutral";
		}
	};
	
    //template method
    public abstract MOVE getMove(Game game);
    public abstract String toString();
	
}
