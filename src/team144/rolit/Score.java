package team144.rolit;

import java.util.Date;

public class Score implements Comparable<Score>{
	private final int score;
	private final Player player;
	private final Date date;
	
	public Score(int score, Player player){
		this(score, player, new Date());
	}
	
	public Score(int score, Player player, Date date){
		this.score = score;
		this.player = player;
		this.date = date;
	}
	
	@Override
	public int compareTo(Score other){
		if(score < other.score) return -1;
		else if(score > other.score) return 1;
		return 0;
	}
	
	public int getScore(){
		return score;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Date getDate(){
		return date;
	}
}
