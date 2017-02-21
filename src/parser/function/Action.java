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
	},
	getDirectionTowardsClosestPowerPill{
        public MOVE getMove(Game game) { 
            return game.getDirectionTowardsClosestPowerPill(game.getPacmanCurrentNodeIndex());
        }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "getDirectionTowardsClosestPowerPill";
		}
	},
	getDirectionTowardsClosestPill{
        public MOVE getMove(Game game) { 
            return game.getDirectionTowardsClosestPill(game.getPacmanCurrentNodeIndex());
        }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "getDirectionTowardsClosestPill";
		}
	},
	getDirectionAwayFromClosestNonEdibleGhost{
        public MOVE getMove(Game game) { 
            return game.getDirectionAwayFromClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex());
        }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "getDirectionAwayFromClosestNonEdibleGhost";
		}
	},
	getDirectionTowardsClosestEdibleGhost{
        public MOVE getMove(Game game) { 
            return game.getDirectionTowardsClosestEdibleGhost(game.getPacmanCurrentNodeIndex());
        }
		public Object executeFunction(Game game) {
			return null;
		}
		public String toString() {
			return "getDirectionTowardsClosestEdibleGhost";
		}
	};
	
    //template method
    public abstract MOVE getMove(Game game);
    public abstract String toString();
	
}
