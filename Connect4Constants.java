/**
 * An interface filled with constants used throughout
 * the game.
 * 
 * @author Jonathan (Jack) MacArthur
 * @version 1.0
 */

package core;

public interface Connect4Constants 
{
	// Game Specifics Information
	public static int ROWS = 6;
	public static int COLUMNS = 7;
	public static int MAXMOVES = ROWS * COLUMNS;
	
	// Game Status Information
	public static int PLAYER1 = 1; 		// Indicate player 1
	public static int PLAYER2 = 2; 		// Indicate player 2
	public static int PLAYER1_WON = 10; // Indicate player 1 won
	public static int PLAYER2_WON = 20; // Indicate player 2 won
	public static int DRAW = 30; 		// Indicate a draw
	public static int CONTINUE = 40;	// Indicate to continue
	public static int INVALID = 50; 	// Indicate to continue
	
	// Output Displays
	public static String waiting = "Waiting for Player 2 to Connect...";
	public static String welcome = "Welcome to Connect4! Player One Goes First.";
	public static String p1Turn = "Player Two Makes a Move. Player One's Turn";
	public static String p2Turn = "Player One Makes a Move. Player Two's Turn";
	public static String p1TurnWithComp = "The Computer Makes a Move. Player One's Turn.";
	public static String p1Victory = "Player One Wins!";
	public static String p2Victory = "Player Two Wins!";
	public static String computerVictory = "The Computer Wins!";
	public static String draw = "The game ends in a draw";
	public static String error1 = "Invalid Move. Player 1 Please Try Again.";
	public static String error2 = "Invalid Move. Player 2 Please Try Again.";
}	
