/**
 * Acts as the game logic for a new
 * game of connect4. It includes a constructor
 * for setting up a new board, and all of the 
 * methods necessary to place tokens, check for winners,
 * etc. 
 * 
 * @author Jonathan (Jack) MacArthur
 * @version 1.4
 */

package core;

public class Connect4 implements Connect4Constants
{
	// Necessary Variables
	private int turnCounter;
	private String[][] gameBoard;
	
	private int lastMoveMade, lastMoveComputerMade = -1;
	private String lastMarkerPlaced;
	private boolean canWinHoriz, canWinVert, horizIsClose;
	
	/**
	 * Constructor for Connect4 game, initializes a board.
	 */
	public Connect4()
	{
		// Player Turn
		turnCounter = 0;
		
		// Last Marker Placed
		lastMarkerPlaced = "O";
		
		// New Game Board
		gameBoard = new String[ROWS][COLUMNS];
		
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLUMNS; j++)
				gameBoard[i][j] = " ";
		}
	}
	
	/**
	 * Returns the lowest unfilled row in a given column.
	 * Returns -1 if no ROWS are open.
	 * 
	 * @param column the column int for which to find the row
	 * @return the lowest open row int in the given column
	 */
	public int getLowestOpenRow(int column)
	{
		// Final indexed row of board
		int finalRow = ROWS - 1;
		int nextOpenRow = finalRow;
		
		// While the lowest row is filled, keep moving up
		while(gameBoard[nextOpenRow][column] != " ")
		{
			nextOpenRow--;
			
			if(nextOpenRow == -1)
				break;
		}
		
		return nextOpenRow;
	}
	
	/**
	 * Returns the validity of a specified move. Each
	 * move must be within bounds of board, and be in a non-full column.
	 * 
	 * @param column the column int for which a move's validity is tested
	 * @return boolean response to the validity of the move
	 */
	public boolean isValidMove(int column)
	{
		// To be true, column must be within bounds of board,
		// and cannot be full.
		if(column >= 0 && column < COLUMNS)
			if(getLowestOpenRow(column) != -1)
				return true;
		
		return false;
	}
	
	/**
	 * Places the token of a player in specified column.
	 * 
	 * @param column the column int to play the token
	 * @return the row int which the token landed in
	 */
	public int placeMarker(int column)
	{
		int openRow = getLowestOpenRow(column);
		String lastMark = get_lastMarkerPlaced();
		
		if(lastMark.equals("O"))
		{
			gameBoard[openRow][column] = "X";
			lastMarkerPlaced = "X";
		}
		else
		{
			gameBoard[openRow][column] = "O";
			lastMarkerPlaced = "O";
		}
		
		lastMoveMade = column;
		turnCounter++;
		
		return openRow;
	}
	
	/**
	 * Removes a previously placed marker from the board.
	 * 
	 * @param column column from which to remove marker
	 */
	public void removeMarker(int column)
	{
		int lastPlayedRow = getLowestOpenRow(column) + 1;
		
		gameBoard[lastPlayedRow][column] = " ";
	}
	
	/**
	 * Uses the column in which the last turn was made
	 * in order to determine the exact location of the
	 * last piece entered.
	 * 
	 * @param column the column int of the last turn
	 * @return row the row int of the last piece entered
	 */
	public int findLastMove(int column)
	{
		// Begin at the very end
		int lastRow = ROWS - 1;
		int lastPossibleRowUsed = lastRow - 1;
		
		// Work way up the board
		while(gameBoard[lastPossibleRowUsed][column] != " ")
		{
			lastPossibleRowUsed--;
			
			if(lastPossibleRowUsed == -1)
				break;
		}
		
		int lastRowUsed = lastPossibleRowUsed + 1;
		
		return lastRowUsed;
	}
	
	/**
	 * Returns a boolean stating the presence of a win in the vertical direction.
	 * 
	 * @param lastTurnColumn the column int of the last turn made
	 * @param lastTurnRow the row int of the last turn made
	 * @param marker the string marker of the last player who played
	 * @param currRow the current row int to begin at
	 * @param consecCounter the int counting consecutive markers
	 * @return boolean response based on presence of 4 consecutive vertical tiles
	 */
	public boolean checkForVerticalWin(int lastTurnColumn, int lastTurnRow, String marker,
			int currRow, int consecCounter)
	{
		currRow++;
		while(gameBoard[currRow][lastTurnColumn].equals(marker))
		{				
			consecCounter++;
			currRow++;
			
			if(currRow == ROWS) // Ensures checking of only valid locations
				break;			
		}
		
		// Check for win OR potential win (for computer move logic)
		if(consecCounter == 4)
			return true; // Game Won
		else if(consecCounter == 3)
			canWinVert = true; 
		
		return false;
	}
	
	/**
	 * Returns a boolean stating the presence of a win in the horizonatal direction.
	 * 
	 * @param lastTurnColumn the column int of the last turn made
	 * @param lastTurnRow the row int of the last turn made
	 * @param marker the string marker of the last player who played
	 * @param currColumn the current column int to begin at
	 * @param consecCounter the int counting consecutive markers
	 * @return boolean response based on presence of 4 consecutive horizontal tiles
	 */
	public boolean checkForHorizontalWin(int lastTurnColumn, int lastTurnRow, String marker,
			int currColumn, int consecCounter)
	{
		if(currColumn != 0)
		{
			currColumn--;
		
			// Count consecutive markers going left
			while(gameBoard[lastTurnRow][currColumn].equals(marker))
			{
				consecCounter++;
				currColumn--;
				
				if(currColumn == -1)
					break;
			}
		}
		
		// Check if this place is the rightmost of a 4 sequence
		
		if(consecCounter == 4)
			return true;
		else
			currColumn = lastTurnColumn;
		
		// If this place is not on the right border, count that way 
		if(currColumn != COLUMNS - 1)
		{
			currColumn++;
			
			while(gameBoard[lastTurnRow][currColumn].equals(marker))
			{
				consecCounter++;
				currColumn++;
				
				if(currColumn == COLUMNS)
					break;
			}
			
			// Total consecutives counted, check for a win
			if(consecCounter >= 4)
				return true;
		}
		
		// No win, but check for potential wins (used in computer move logic)
		if(consecCounter == 2)
			horizIsClose = true;
		else if(consecCounter == 3)
			canWinHoriz = true;
		
		return false;
	}
	
	/**
	 * Returns a boolean stating the presence of a win in the pos. diagonal direction.
	 * 
	 * @param lastTurnColumn the column int of the last turn made
	 * @param lastTurnRow the row int of the last turn made
	 * @param marker the string marker of the last player who played
	 * @param currColumn the current column int to begin at
	 * @param currRow the current row int to begin at
	 * @param consecCounter the int counting consecutive markers
	 * @return boolean response based on presence of 4 consecutive pos. diagonal tiles
	 */
	public boolean checkFirstDiagonal(int lastTurnColumn, int lastTurnRow, String marker,
			int currColumn, int currRow, int consecCounter)
	{
		if(currRow != ROWS - 1 && currColumn != 0)
		{
			currRow++;
			currColumn--;
		
			while(gameBoard[currRow][currColumn].equals(marker))
			{
				consecCounter++;
				currColumn--;
				currRow++;
				
				if(currColumn == -1 || currRow == ROWS)
					break;
			}
		}
		
		if(consecCounter == 4)
			return true;
		
		currRow = lastTurnRow;
		currColumn = lastTurnColumn;
		
		if(currRow != 0 && currColumn != COLUMNS - 1)
		{
			currRow--;
			currColumn++;	
		
			while(gameBoard[currRow][currColumn].equals(marker))
			{
				consecCounter++;
				currColumn++;
				currRow--;
				
				if(currColumn == COLUMNS || currRow == -1)
					break;
			}
		}
		
		if(consecCounter >= 4)
			return true;
		
		return false;
	}
	
	/**
	 * Returns a boolean stating the presence of a win in the neg. diagonal direction.
	 * 
	 * @param lastTurnColumn the column int of the last turn made
	 * @param lastTurnRow the row int of the last turn made
	 * @param marker the string marker of the last player who played
	 * @param currColumn the current column int to begin at
	 * @param currRow the current row int to begin at
	 * @param consecCounter the int counting consecutive markers
	 * @return boolean response based on presence of 4 consecutive pos. diagonal tiles
	 */
	public boolean checkAltDiagonal(int lastTurnColumn, int lastTurnRow, String marker,
			int currColumn, int currRow, int consecCounter)
	{
		currRow = lastTurnRow;
		currColumn = lastTurnColumn;
		
		if(currRow != 0 && currColumn != 0)
		{
			currRow--;
			currColumn--;
		
			while(gameBoard[currRow][currColumn].equals(marker))
			{
				consecCounter++;
				currColumn--;
				currRow--;
				
				if(currColumn == -1 || currRow == -1)
					break;
			}
		}
		
		if(consecCounter == 4)
			return true;
		
		currRow = lastTurnRow;
		currColumn = lastTurnColumn;
		
		if(currRow != ROWS - 1 && currColumn != COLUMNS - 1)
		{
			currRow++;
			currColumn++;	
		
			while(gameBoard[currRow][currColumn].equals(marker))
			{
				consecCounter++;
				currColumn++;
				currRow++;
				
				if(currColumn == COLUMNS || currRow == ROWS)
					break;
			}
		}
		
		if(consecCounter >= 4)
			return true;
		
		return false;
	}
	
	/**
	 * Returns the boolean response for the presence of a win.
	 * 
	 * @param lastTurnColumn the last column int played
	 * @return the boolean response for the presence of a win
	 */
	public boolean checkForWin(int lastTurnColumn)
	{
		// Reset logic for computer moves
		canWinVert = canWinHoriz = horizIsClose = false;
		
		// Find the exact location of previous piece entered
		int lastTurnRow = findLastMove(lastTurnColumn);
		String lastMarker = gameBoard[lastTurnRow][lastTurnColumn];
		
		// Begin to track consecutive identical markers
		int consecCounter = 1;
		
		// Duplicate starting point values
		int currRow = lastTurnRow;
		int currColumn = lastTurnColumn;
		
		/* Begin by Checking for a Vertical Win*/
		if(currRow < 4)
		{
			if(checkForVerticalWin(lastTurnColumn, lastTurnRow, lastMarker, currRow, consecCounter))
				return true;
		}
		/* No Vertical Win Found */
		
		// Reset
		consecCounter = 1; 
		
		/* Next check for a Horizontal Win */
		if(checkForHorizontalWin(lastTurnColumn, lastTurnRow, lastMarker, currColumn, consecCounter))
			return true;
		/* No Horizontal Win Found */
		
		// Reset
		consecCounter = 1; 
		currColumn = lastTurnColumn;
		currRow = lastTurnRow;
		
		/* Next check for a Win in positive diagonal direction */
		if(checkFirstDiagonal(lastTurnColumn, lastTurnRow, lastMarker, currColumn, currRow, consecCounter))
			return true;
		/* No Diagonal Win Found */
		
		// Reset
		consecCounter = 1;
		currRow = lastTurnRow;
		currColumn = lastTurnColumn;
		
		/* Finally, check for a win in negative diagonal */
		if(checkAltDiagonal(lastTurnColumn, lastTurnRow, lastMarker, currColumn, currRow, consecCounter))
			return true;
		
		return false;
	}
	
	/**
	 * Returns the number of turns taken.
	 * 
	 * @return turn counter integer
	 */
	public int getTurnCounter()
	{
		return turnCounter;
	}
	
	/**
	 * Returns the last player to have played on the board
	 * 
	 * @param lastTurnColumn the column integer last played in
	 * @return String representation of token played
	 */
	public String lastPlayer(int lastTurnColumn)
	{
		// Find the exact location of previous piece entered
		int lastTurnRow = findLastMove(lastTurnColumn);
		
		// Find marker at that location
		return gameBoard[lastTurnRow][lastTurnColumn];
	}
	
	/**
	 * Returns the status of a potential win in the
	 * vertical direction for the opposing player on 
	 * their next turn.
	 * 
	 * @return boolean stating the win possibility
	 */
	public boolean get_canWinVert()
	{
		return canWinVert;
	}
	
	/**
	 * Returns the status of a potential win in the
	 * horizontal direction for the opposing player on 
	 * their next turn.
	 * 
	 * @return boolrsn stating the win possibility
	 */
	public boolean get_canWinHoriz()
	{
		return canWinHoriz;
	}
	
	/**
	 * Returns the status of a potential win in the
	 * horizontal direction for the opposing player on 
	 * their upcoming turns.
	 * 
	 * @return boolean stating the win possibility
	 */
	public boolean get_horizIsClose()
	{
		return horizIsClose;
	}
	
	/**
	 * Returns the status of a potential win in the
	 * horizontal direction for the opposing player on 
	 * their next turn.
	 *  
	 * @return boolean stating the win possibility
	 */
	public String get_lastMarkerPlaced()
	{
		return lastMarkerPlaced;
	}
	
	/**
	 * Allows access to the logical gameBoard
	 * 
	 * @return the gameBoard currently being played on
	 */
	public String[][] get_gameBoard()
	{
		return gameBoard;
	}
	
	/**
	 * Allows access to the last column played.
	 * 
	 * @return an integer representing the last move made.
	 */
	public int get_lastMoveMade()
	{
		return lastMoveMade;
	}

	/**
	 * Allows for outside classes to record the last move
	 * which the computer in specific made.
	 * 
	 * @param lastMove the column played by computer
	 */
	public void set_lastMoveComputerMade(int lastMove)
	{
		lastMoveComputerMade = lastMove;
	}
	
	/**
	 * Allows access to the last move made by the computer.
	 * 
	 * @return the column of the computer's last move
	 */
	public int get_lastMoveComputerMade()
	{
		return lastMoveComputerMade;
	}
	
	/**
	 * Allows for the last marker placed to be altered.
	 * 
	 * @param marker the marker to set as the previous marker.
	 */
	public void set_lastMarkerPlaced(String marker)
	{
		lastMarkerPlaced = marker;
	}
}
