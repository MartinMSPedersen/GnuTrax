package org.traxgame;

/* 
 Date: 19th of October 2009
 version 0.1
 All source under GPL version 2
 (GNU General Public License - http://www.gnu.org/)
 contact traxplayer@gmail.com for more information about this code
 */

import java.util.ArrayList;

public class ComputerPlayerUct extends ComputerPlayer {
	private int maxSimulations;

	public ComputerPlayerUct() {
		// maxSimulations = 10000;
		maxSimulations = 100000;
	}

	public ComputerPlayerUct(int maxSimulations) {
		this.maxSimulations = maxSimulations;
	}

	private String simpleMove(Traxboard tb) {
		int percent = TraxUtil.getRandom(100);

		if (tb.getNumOfTiles() == 0) {
			if (percent < 50) {
				return new String("@0/");
			} else {
				return new String("@0+");
			}
		}
		if (tb.getNumOfTiles() == 1) {
			if (tb.getAt(1, 1) == Traxboard.NW) {
				if (percent < 40) {
					return new String("B1\\");
				} else {
					return new String("@1/");
				}
			}
			if (tb.getAt(1, 1) == Traxboard.NS) {
				if (percent < 10) {
					return new String("A0+");
				}
				if (percent < 20) {
					return new String("A0/");
				}
				if (percent < 30) {
					return new String("B1/");
				}
				return new String("B1+");
			}
		}
		if (tb.getNumOfTiles() == 2) {
			if (tb.getAt(1, 1) == Traxboard.NW) {
				if (tb.getAt(1, 2) == Traxboard.NE)
					return new String("A2/");
				if (tb.getAt(2, 1) == Traxboard.WS)
					return new String("B1/");
			}
		}
		return null;
	}

	public String computerMove(Traxboard tb) {
		String simple = simpleMove(tb);
		if (simple != null)
			return simple;

		UctNode root = new UctNode(tb);
		int maxSimulations;

		maxSimulations = this.maxSimulations / (65 - tb.getNumOfTiles() / 2);
		for (int simulationCount = 0; simulationCount < maxSimulations; simulationCount++) {
			// System.out.println(root);
			playSimulation(root);
			if (simulationCount % 250 == 0) {
				// System.err.print(".");
				;
			}
		}
		// DEBUG
		/*
		 * ArrayList<UctNode> children=root.getChildren(); for (int i = 0; i <
		 * children.size (); i++) {
		 * System.err.println("Child: "+i+children.get(i)); }
		 */

		return root.getWorse().getMove();
	}

	private int playRandomGame(Traxboard tb) {
		while (tb.isGameOver() == Traxboard.NOPLAYER) {
			try {
				tb.makeMove(TraxUtil.getRandomMove(tb));
			} catch (IllegalMoveException e) {
				throw new RuntimeException();
			}
		}
		return tb.isGameOver();
	}

	private UctNode UCTselect(UctNode node) {
		float value;
		float bestUct = -1;
		int best_index = -1;
		ArrayList<UctNode> children = node.getChildren();

		if (children == null) {
			node.createChildren();
			children = node.getChildren();
		}
		if (children.size() == 0) {
			System.err.println(node);
			throw new RuntimeException("Doh 2!");
		}
		for (int i = 0; i < children.size(); i++) {
			value = children.get(i).UctValue();
			if (value > bestUct) {
				best_index = i;
				bestUct = value;
			}
		}
		// System.out.println(bestUct);
		return children.get(best_index);
	}

	private int playSimulation(UctNode node) {
		int result = -1;

		if (node.getVisits() < 5) {
			result = playRandomGame(new Traxboard(node.getPosition()));
			node.update(result);
			return result;
		} else {
			if ((node.getChildren() == null)
					|| (node.getChildren().size() == 0)) {
				node.createChildren();
			}
			UctNode next = UCTselect(node);
			if (next == null) {
				System.err.println(node);
				System.err.println(next);
				throw new RuntimeException("Doh!");
			}
			if (next.getPosition().isGameOver() != Traxboard.NOPLAYER) {
				result = next.getPosition().isGameOver();
				next.update(result);
				return result;
			}
			result = playSimulation(next);
			next.update(result);
		}
		return result;
	}

}
