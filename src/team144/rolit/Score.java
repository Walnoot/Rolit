package team144.rolit;

import java.util.Calendar;

public class Score implements Comparable<Score>{
	private final Player player;
	private final int points;
	private final Calendar date;
	private final boolean winner;
	
	public Score(int score, Player player, Calendar date, boolean winner){
		this.points = score;
		this.player = player;
		this.date = date;
		this.winner = winner;
	}
	
	@Override
	public int compareTo(Score other){
		if(points < other.points) return -1;
		else if(points > other.points) return 1;
		return 0;
	}
	
	public int getScore(){
		return points;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Calendar getDate(){
		return date;
	}
	
	public boolean getWinner(){
		return winner;
	}
}
