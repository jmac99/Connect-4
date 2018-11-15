/**
 * Serves as the AI logic when playing against the computer.
 * 
 * @author Jonathan (Jack) MacArthur
 * @version 1.2
 */
package core;

import java.util.Random;

public class Connect4ComputerPlayer 
{
	private Connect4 gameBoard;
	
	/**
	 * Constructor for a new ai player.
	 * 
	 * @param gameBoard the board which the game is being played upon.
	 */
	public Connect4ComputerPlayer(Connect4 gameBoard)
	{
		this.gameBoard = gameBoard;
	}
	
	/**
	 * Determines the computer's next move.
	 * 
	 * @return an integer with the computer's next move
	 */
	public int makeMove()
	{
		// LOGIC TO DETERMINE COMPUTER MOVE
		
		int lastMovePlayed = gameBoard.get_lastMoveMade();
		int rowOfLastMove = gameBoard.getLowestOpenRow(lastMovePlayed) + 1;
		String lastMarker = gameBoard.get_lastMarkerPlaced();
		int currentColumn = lastMovePlayed;
		
		/*** CHECK TO SEE IF COMPUTER CAN WIN ***/
		
		for(int i = 0; i < 7; i++)
		{
			if(gameBoard.isValidMove(i))
			{
				gameBoard.placeMarker(i);
				gameBoard.set_lastMarkerPlaced(lastMarker);
				if(gameBoard.checkForWin(i))
				{
					gameBoard.removeMarker(i);
					return i;
				}
				else
					gameBoard.removeMarker(i);
			}
		}
		
		//gameBoard.set_lastMarkerPlaced(lastMarker);
		gameBoard.checkForWin(lastMovePlayed);
		
		/*** COMPUTER CANNOT WIN ON THIS TURN ***/
		
		
		/*** BEGIN WITH BLOCKING ***/
		/* VERTICAL BLOCKING */
		
		if(gameBoard.get_canWinVert())
		{
			gameBoard.set_lastMoveComputerMade(lastMovePlayed);
			return lastMovePlayed;
		}
		
		/* END OF VERTICAL BLOCKING */
		
		/* HORIZONTAL BLOCKING */
		
		if(gameBoard.get_canWinHoriz() || gameBoard.get_horizIsClose())
		{
			/* ATTEMPT TO BLOCK ON RIGHT SIDE */
			
			boolean canBlockRight = true;
			// Advance currentCollumn pointer to the right
			if(currentColumn < 6)
				currentColumn++;
			
			if(lastMovePlayed != 6)
			{
				while(gameBoard.getLowestOpenRow(currentColumn) != rowOfLastMove)
				{
					if(gameBoard.get_gameBoard()[rowOfLastMove][currentColumn] != lastMarker)
					{
						canBlockRight = false;
						break;
					}
					
					currentColumn++;
					
					if(currentColumn == 6)
						break;
				}
			
				if(gameBoard.get_gameBoard()[rowOfLastMove][currentColumn] != lastMarker &&
						gameBoard.get_gameBoard()[rowOfLastMove][currentColumn] != " ")
				{
					canBlockRight = false;
				}
				
				if(currentColumn != 6 && canBlockRight)
				{
					gameBoard.set_lastMoveComputerMade(lastMovePlayed + 1);
					return lastMovePlayed + 1;
				}
			}
			
			
			/* ATTEMPT TO BLOCK ON LEFT SIDE */
			
			// See if we can block them to the right
			boolean canBlockLeft = true;
			// Advance currentCollumn pointer to the right
			currentColumn--;
				
			if(lastMovePlayed != 0)
			{
				while(gameBoard.getLowestOpenRow(currentColumn) != rowOfLastMove)
				{
					if(gameBoard.get_gameBoard()[rowOfLastMove][currentColumn] != lastMarker)
					{
						canBlockLeft = false;
						break;
					}
								
					currentColumn--;
								
					if(currentColumn == 0)
						break;
				}
						
				if(gameBoard.get_gameBoard()[rowOfLastMove][currentColumn] != lastMarker &&
						gameBoard.get_gameBoard()[rowOfLastMove][currentColumn] != " ")
				{
					canBlockLeft = false;
				}
				
				gameBoard.set_lastMoveComputerMade(currentColumn);				
				return currentColumn;
			}
		}

		/* END OF HORIZONTAL BLOCKING */
		
		/*
		 * At this point, the computer cannot win on this
		 * turn, but also doesn't need to block any potential wins
		 * from the opponent, so it'll go ahead and generate
		 * a random, valid move to make.
		 */
		
		Random rand = new Random();
		int defaultMove = rand.nextInt(6);
				
		while(!gameBoard.isValidMove(defaultMove))
			defaultMove = rand.nextInt(6); 
			
		gameBoard.set_lastMoveComputerMade(defaultMove);
		
		return defaultMove;
	}
}