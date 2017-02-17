package parser.function;

import pacman.game.Game;

public enum NumericFunc implements Function{

	getDistanceToClosestNonEdibleGhost {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhost";
		}
	},
	getNumberOfActivePowerPills {
		public Object executeFunction(Game game) {
			return game.getNumberOfActivePowerPills();
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhost";
		}
	};

	public abstract Object executeFunction(Game game);
	
	public abstract String toString();
	
}
