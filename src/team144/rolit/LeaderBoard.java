package team144.rolit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoard{
	private ArrayList<Score> scores = new ArrayList<Score>();
	
	public LeaderBoard(){
	}
	
	public void addScore(Score score){
		scores.add(score);
		
		Collections.sort(scores);
	}
	
	public List<Score> getTopScores(int n){
		n = Math.min(n, scores.size() - 1);
		
		return scores.subList(scores.size() - n, scores.size());
	}
	
	public void print(){
		for(Score score : scores){
			System.out.println(score.getScore());
		}
	}
	
	public static void main(String[] args){
		LeaderBoard leaderBoard = new LeaderBoard();
		HumanPlayer player = new HumanPlayer();
		
		leaderBoard.addScore(new Score(14, player));
		leaderBoard.addScore(new Score(13, player));
		leaderBoard.addScore(new Score(15, player));
		leaderBoard.addScore(new Score(9, player));
		
		leaderBoard.print();
	}
}
