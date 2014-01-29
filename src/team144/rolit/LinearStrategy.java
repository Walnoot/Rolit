package team144.rolit;

/**
 * not even random :O
 */
public class LinearStrategy implements Strategy{
    
	@Override
	public String getName() {
		return "linear";
	}

    @Override
    public int findMove(Game game) {
        //getLegalMoves() things
        for(int i = 0; i<8*8; i++){
            if(game.isValidMove(i)){
                return i;
            }
        }
        return 0;
    }
}
