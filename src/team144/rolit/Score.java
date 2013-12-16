package team144.rolit;

import java.util.Calendar;

public class Score implements Comparable<Score>{
	private final int score;
	private final Player player;
	private final Calendar calendar;
	
	public Score(int score, Player player){
		this(score, player, Calendar.getInstance());
	}
	
	public Score(int score, Player player, Calendar date){
		this.score = score;
		this.player = player;
		this.calendar = date;
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
	
	public Calendar getCalendar(){
		return calendar;
	}
}
