package parser.function;

import pacman.game.Game;

public enum BooleanFunc implements Function {

	isJunction {
		public Object executeFunction(Game game) {
			return game.isJunction(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "isJunction";
		}
	};

	@Override
	public abstract Object executeFunction(Game game);

	public abstract String toString();
}
