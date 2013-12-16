package team144.rolit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

//@author Willem Siers
public class LeaderBoard {
	private ArrayList<Score> scores = new ArrayList<Score>();

	public LeaderBoard() {
	}

	public void addScore(Score score) {
		scores.add(score);

		Collections.sort(scores);
	}

	public List<Score> getTopScores(int n) {
		n = Math.min(n, scores.size() - 1);

		return scores.subList(scores.size() - n, scores.size());
	}

	public List<Score> getScoresAbove(int score) {
		int index = -1;

		for (int i = 0; i < scores.size(); i++) {
			if (scores.get(i).getScore() >= score) {
				index = i;
				break;
			}
		}

		if (index == -1)
			throw new IllegalArgumentException(
					"No score higher than the specified score exists!");
		else
			return scores.subList(index, scores.size());
	}

	public int getAverageScore() {
		int totalScore = 0;
		for (Score score : scores) {
			totalScore += score.getScore();
		}

		return totalScore / scores.size();
	}

	public int getAverageScoreOfDay(Calendar day) {
		return 0;
	}

	public void print() {
		for (Score score : scores) {
			System.out.println(score.getScore());
		}
	}

	public static void main(String[] args) {
		LeaderBoard leaderBoard = new LeaderBoard();
		HumanPlayer player = new HumanPlayer();

		leaderBoard.addScore(new Score(14, player));
		leaderBoard.addScore(new Score(13, player));
		leaderBoard.addScore(new Score(15, player));
		leaderBoard.addScore(new Score(9, player));

		leaderBoard.print();
	}
}
