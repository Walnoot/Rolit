package team144.rolit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/*
 * @author Willem Siers (@WILL3M #NOFILTER)
 */

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
	
	public float getAverageScore(){
		int sum = 0;
		
		for(Score score : scores){
			sum += score.getScore();
		}
		
		return (float) sum / scores.size();
	}
	
	public float getAverageScoreOfDay(Calendar day){
		int n = 0, sum = 0;
		
		for(Score score : scores){
			Calendar calendar = score.getCalendar();
			if(calendar.get(Calendar.YEAR) != day.get(Calendar.YEAR)) continue;
			if(calendar.get(Calendar.MONTH) != day.get(Calendar.MONTH)) continue;
			if(calendar.get(Calendar.DAY_OF_MONTH) != day.get(Calendar.DAY_OF_MONTH)) continue;
			
			n++;
			sum += score.getScore();
		}
		
		if(n == 0) throw new IllegalArgumentException("No scores exist for that day ");
		return (float) sum / n;
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
	
	public void print(){
		for(Score score : scores){
			System.out.println(score.getScore());
		}
	}
	
	public static void main(String[] args){
		LeaderBoard leaderBoard = new LeaderBoard();
		Player player = new Player(null, null);
		
		Calendar cal = Calendar.getInstance();
		
		leaderBoard.addScore(new Score(14, player, cal, true));
		leaderBoard.addScore(new Score(13, player, cal, false));
		leaderBoard.addScore(new Score(15, player, cal, true));
		leaderBoard.addScore(new Score(9, player, cal, true));
		
		leaderBoard.print();
		
//		List<Score> list = leaderBoard.getScoresBelow(15);
//		
//		for(Score score : list){
//			System.out.println(score.getSco///re());
//		}
	}
	
//	public void print(List<Score> scores){
//		for(Score score : scores){
//			System.out.println(formatScore(score));
//		}
//	}
//	
//	private String formatScore(Score score){
//		return String.format(, arg1)
//	}
}
