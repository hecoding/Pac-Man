package parser.function;

import pacman.game.Game;
import pacman.game.Constants.MOVE;

public enum BooleanFunc implements Function {

	isJunction {
		public Object executeFunction(Game game) {
			return game.isJunction(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "isJunction";
		}
	},isJunctionSafeUp {
		public Object executeFunction(Game game) {
			return game.isJunctionSafe(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "isJunctionSafeUp";
		}
	},isJunctionSafeDown {
		public Object executeFunction(Game game) {
			return game.isJunctionSafe(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "isJunctionSafeDown";
		}
	},isJunctionSafeLeft {
		public Object executeFunction(Game game) {
			return game.isJunctionSafe(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "isJunctionSafeLeft";
		}
	},isJunctionSafeRight {
		public Object executeFunction(Game game) {
			return game.isJunctionSafe(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "isJunctionSafeRight";
		}
	},;

	@Override
	public abstract Object executeFunction(Game game);

	public abstract String toString();
}
