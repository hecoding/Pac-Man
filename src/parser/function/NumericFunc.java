package parser.function;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public enum NumericFunc implements Function{

	getDistanceToClosestNonEdibleGhost {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhost";
		}
	},getDistanceToClosestNonEdibleGhostCurrent
	{
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhostCurernt";
		}
	},getDistanceToClosestNonEdibleGhostUp {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhostUp";
		}
	},getDistanceToClosestNonEdibleGhostDown {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhostDown";
		}
	},getDistanceToClosestNonEdibleGhostLeft {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhostLeft";
		}
	},getDistanceToClosestNonEdibleGhostRight {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestNonEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getDistanceToClosestNonEdibleGhostRight";
		}
	},getDistanceToClosestEdibleGhost {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestEdibleGhost(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getDistanceToClosestEdibleGhost";
		}
	},getDistanceToClosestEdibleGhostCurrent {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getDistanceToClosestEdibleGhostCurrent";
		}
	},getDistanceToClosestEdibleGhostUp {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getDistanceToClosestEdibleGhostUp";
		}
	},getDistanceToClosestEdibleGhostDown {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getDistanceToClosestEdibleGhostDown";
		}
	},getDistanceToClosestEdibleGhostLeft {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getDistanceToClosestEdibleGhostLeft";
		}
	},getDistanceToClosestEdibleGhostRight {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestEdibleGhost(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getDistanceToClosestEdibleGhostRight";
		}
	},
	getNumberOfActivePowerPills {
		public Object executeFunction(Game game) {
			return game.getNumberOfActivePowerPills();
		}
		public String toString() {
			return "getNumberOfActivePowerPills";
		}
	},
	getDistToClosestPill {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPill(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getDistToClosestPill";
		}
	},
	getDistToClosestPillCurrent {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPill4d(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getDistToClosestPillCurrent";
		}
	},
	getDistToClosestPillUp {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPill4d(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getDistToClosestPillUp";
		}
	},
	getDistToClosestPillDown {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPill4d(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getDistToClosestPillDown";
		}
	},
	getDistToClosestPillLeft {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPill4d(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getDistToClosestPillLeft";
		}
	},
	getDistToClosestPillRight {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPill4d(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getDistToClosestPillRight";
		}
	},
	getDistToClosestPowerPill {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPowerPill(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getDistToClosestPowerPill";
		}
	},
	getDistToClosestPowerPillCurrent {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPowerPill4d(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getDistToClosestPowerPillCurrent";
		}
	},
	getDistToClosestPowerPillUp {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPowerPill4d(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getDistToClosestPowerPillUp";
		}
	},
	getDistToClosestPowerPillDown {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPowerPill4d(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getDistToClosestPowerPillDown";
		}
	},
	getDistToClosestPowerPillLeft {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPowerPill4d(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getDistToClosestPowerPillLeft";
		}
	},
	getDistToClosestPowerPillRight {
		public Object executeFunction(Game game) {
			return game.getDistToClosestPowerPill4d(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getDistToClosestPowerPillRight";
		}
	},
	getClosestJunctionExitsNumberCurrent {
		public Object executeFunction(Game game) {
			return game.getClosestJunctionExitsNumber(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getClosestJunctionExitsNumberCurrent";
		}
	},
	getClosestJunctionExitsNumberUp {
		public Object executeFunction(Game game) {
			return game.getClosestJunctionExitsNumber(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getClosestJunctionExitsNumberUp";
		}
	},
	getClosestJunctionExitsNumberDown {
		public Object executeFunction(Game game) {
			return game.getClosestJunctionExitsNumber(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getClosestJunctionExitsNumberDown";
		}
	},
	getClosestJunctionExitsNumberLeft {
		public Object executeFunction(Game game) {
			return game.getClosestJunctionExitsNumber(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getClosestJunctionExitsNumberLeft";
		}
	},
	getClosestJunctionExitsNumberRight {
		public Object executeFunction(Game game) {
			return game.getClosestJunctionExitsNumber(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getClosestJunctionExitsNumberRight";
		}
	},getDistanceToClosestJunctionCurrent {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getDistanceToClosestJunctionCurrent";
		}
	},
	getDistanceToClosestJunctionUp {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getDistanceToClosestJunctionUp";
		}
	},
	getDistanceToClosestJunctionDown {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getDistanceToClosestJunctionDown";
		}
	},
	getDistanceToClosestJunctionLeft {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getDistanceToClosestJunctionLeft";
		}
	},
	getDistanceToClosestJunctionRight {
		public Object executeFunction(Game game) {
			return game.getDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getDistanceToClosestJunctionRight";
		}
	},
	getClosestNonEdibleGhostDistanceToClosestJunctionCurrent {
		public Object executeFunction(Game game) {
			return game.getClosestNonEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getClosestNonEdibleGhostDistanceToClosestJunctionCurrent";
		}
	},
	getClosestNonEdibleGhostDistanceToClosestJunctionUp {
		public Object executeFunction(Game game) {
			return game.getClosestNonEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getClosestNonEdibleGhostDistanceToClosestJunctionUp";
		}
	},
	getClosestNonEdibleGhostDistanceToClosestJunctionDown {
		public Object executeFunction(Game game) {
			return game.getClosestNonEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getClosestNonEdibleGhostDistanceToClosestJunctionDown";
		}
	},
	getClosestNonEdibleGhostDistanceToClosestJunctionLeft {
		public Object executeFunction(Game game) {
			return game.getClosestNonEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getClosestNonEdibleGhostDistanceToClosestJunctionLeft";
		}
	},
	getClosestNonEdibleGhostDistanceToClosestJunctionRight {
		public Object executeFunction(Game game) {
			return game.getClosestNonEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getClosestNonEdibleGhostDistanceToClosestJunctionRight";
		}
	},
	getClosestEdibleGhostDistanceToClosestJunctionCurrent {
		public Object executeFunction(Game game) {
			return game.getClosestEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.NEUTRAL);
		}
		public String toString() {
			return "getClosestEdibleGhostDistanceToClosestJunctionCurrent";
		}
	},
	getClosestEdibleGhostDistanceToClosestJunctionUp {
		public Object executeFunction(Game game) {
			return game.getClosestEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		}
		public String toString() {
			return "getClosestEdibleGhostDistanceToClosestJunctionUp";
		}
	},
	getClosestEdibleGhostDistanceToClosestJunctionDown {
		public Object executeFunction(Game game) {
			return game.getClosestEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		}
		public String toString() {
			return "getClosestEdibleGhostDistanceToClosestJunctionDown";
		}
	},
	getClosestEdibleGhostDistanceToClosestJunctionLeft {
		public Object executeFunction(Game game) {
			return game.getClosestEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		}
		public String toString() {
			return "getClosestEdibleGhostDistanceToClosestJunctionLeft";
		}
	},
	getClosestEdibleGhostDistanceToClosestJunctionRight {
		public Object executeFunction(Game game) {
			return game.getClosestEdibleGhostDistanceToClosestJunction(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		}
		public String toString() {
			return "getClosestEdibleGhostDistanceToClosestJunctionRight";
		}
	},
	getGeometricMeanDistanceToNonEdibleGhosts {
		public Object executeFunction(Game game) {
			return game.getGeometricMeanDistanceToNonEdibleGhosts(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getGeometricMeanDistanceToNonEdibleGhosts";
		}
	},
	getGeometricMeanDistanceToEdibleGhosts {
		public Object executeFunction(Game game) {
			return game.getGeometricMeanDistanceToEdibleGhosts(game.getPacmanCurrentNodeIndex());
		}
		public String toString() {
			return "getGeometricMeanDistanceToEdibleGhosts";
		}
	};

	public abstract Object executeFunction(Game game);
	
	public abstract String toString();
	
}
