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
	
	public List<Score> getScoresAbove(int score){
		int index = -1;
		
		for(int i = 0; i < scores.size(); i++){
			if(scores.get(i).getScore() >= score){
				index = i;
				break;
			}
		}
		
		if(index == -1) throw new IllegalArgumentException("No score higher than the specified score exists!");
		else return scores.subList(index, scores.size());
	}
	
	public List<Score> getScoresBelow(int score){
		int index = -1;
		
		for(int i = scores.size() - 1; i >= 0; i--){
			if(scores.get(i).getScore() <= score){
				index = i;
				break;
			}
		}
		
		if(index == -1) throw new IllegalArgumentException("No score lower than the specified score exists!");
		else return scores.subList(0, index + 1);
	}
	
	public float getAverage(int day){
		int n = 0, sum = 0;
		
		for(Score score : scores){
			System.out.println(score.getScore());
		}
		
		return 0;
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
		
//		List<Score> list = leaderBoard.getScoresBelow(15);
//		
//		for(Score score : list){
//			System.out.println(score.getScore());
//		}
	}
}
