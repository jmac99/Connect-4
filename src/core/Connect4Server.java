/**
 * Server for creating threads of Connect4 games, both
 * between players and against the computer. Modeled
 * heavily off of example code provided by Dr. Bansal.
 * 
 * @author Jonathan (Jack) MacArthur
 * @version 1.0
 */
package core;

import java.io.*;
import java.net.*;
import java.util.*;

import core.Connect4Constants;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Connect4Server extends Application implements Connect4Constants
{
	// Needed Variables
	private int sessionNo = 1;
	private ServerSocket serverSocket;
	
	/**
	 * Launches Server
	 * 
	 * @param args unnneded values for establishing stage
	 */
	public static void main(String[] args)
	{
		// Begins Server
		launch(args);
	}
	 
	/**
	 * Begins the Server display and functions
	 * 
	 * @param primaryStage the stage for which to display updates on
	 */
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) 
	{
	    TextArea status = new TextArea();
	
	    // Create a scene and place it in the stage
	    Scene scene = new Scene(new ScrollPane(status), 500, 250);
	    primaryStage.setTitle("Connect4 Server"); 	// Set the stage title
	    primaryStage.setScene(scene); 				// Place the scene in the stage
	    primaryStage.show(); 						// Display the stage
	
	    // Begin Server Thread
	    new Thread( () -> 
	    {
	      try 
	      {
	        // Create a server socket
	        serverSocket = new ServerSocket(8000);
	        Platform.runLater(() -> status.appendText(new Date() +
	          ": Server started at socket 8000\n\n"));
	  
	        // Ready to create PVP or AI sessions
	        while (true) 
	        {
	        	// Record session which this player is connected to
        		int currSessionNo = sessionNo;
        		sessionNo++;
	        	
	        	Platform.runLater(() -> status.appendText(new Date() +
	        			": Waiting for player(s) to join session " + currSessionNo + '\n'));
	  
	        	// Connect to player 1
	        	Socket player1 = serverSocket.accept();
	  
	        	// Notify that the player is Player 1
	        	DataInputStream dataInStream = new DataInputStream(player1.getInputStream()); // 1
	        	int gameType = dataInStream.readInt(); // 2
	        	new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);
	        	
	        	// Record if they have a partner to play
	        	boolean partnered = false;
	        	
	        	// IF DESIRING A PVP GAME
	        	if(gameType == 0)
	        	{
	        		
	        		Platform.runLater(() -> 
			        {
			        	status.appendText(new Date() + ": Player 1 joined session " 
			        			+ currSessionNo + '\n');
			        	status.appendText("Player 1's IP address" +
			        			player1.getInetAddress().getHostAddress() + '\n');
			        });
		        	
	        		while(!partnered) // Partner not already assigned
	        		{
		        		// Connect to player 2
		        		Socket player2 = serverSocket.accept();
		        		DataInputStream dataInStream2 = new DataInputStream(player2.getInputStream()); // 1
			        	gameType = dataInStream2.readInt(); // 2
			  
			        	if(gameType == 0)
			        	{
					        Platform.runLater(() -> {
					          status.appendText(new Date() +
					            ": Player 2 joined session " + currSessionNo + '\n');
					          status.appendText("Player 2's IP address" +
					            player2.getInetAddress().getHostAddress() + '\n');
					        });
				  
					        // Notify that the player is Player 2
					        new DataOutputStream(
					        		player2.getOutputStream()).writeInt(PLAYER2);
				  
				         	// Display this session and increment session number
					        Platform.runLater(() -> 
					        	status.appendText(new Date() + 
					        			": Start a thread for session " + (sessionNo - 1) + '\n'));
				          
					        // Create a Game for Players
					        Connect4 game = new Connect4();
					        
					        // Notify Partnership
					        partnered = true;
				          
					        // Launch a new thread for this session of two players
					        new Thread(new BeginASession(player1, player2, game)).start();
			        	}
			        	else
			        	{
			        		// Notify that the player is Player 1 against AI
					        new DataOutputStream(
					        		player2.getOutputStream()).writeInt(PLAYER1);
					        
					        // Record New Session No
					        int newSession = sessionNo;
					        sessionNo++;
					        
					        Platform.runLater(() -> 
				        	status.appendText(new Date() + 
				        			" Start a thread for session " + newSession + 
				        			" , player1 faces the computer" + '\n'));

					        Connect4 game = new Connect4();
				        	Connect4ComputerPlayer comp = new Connect4ComputerPlayer(game);
				        	new Thread(new BeginASession(player2, comp, game)).start();
			        	}
	        		}
	          }
	          else
	          {
	        	  Connect4 game = new Connect4();
	        	  
	        	  Platform.runLater(() -> 
		        	status.appendText(new Date() + 
		        			" Start a thread for session " + currSessionNo + 
		        			" , player1 faces the computer" + '\n'));
	        	  
	        	  Connect4ComputerPlayer comp = new Connect4ComputerPlayer(game);
	        	  new Thread(new BeginASession(player1, comp, game)).start();
	          }
	        }
	      }
	      catch(SocketException ex)
	      {
	    	 /* DO NOTHING
	    	  * 
	    	  * If a Socket Exception occurs, all it means
	    	  * is that the server window was closed. There's
	    	  * not really an error, it's just that the server
	    	  * was terminated.
	    	  */
	      }
	      catch(IOException ex) 
	      {
	        ex.printStackTrace();
	      }	      
	    }).start();
	}
	
	  // Define the thread class for handling a new session for two players
	  // OR a game against the computer
	  class BeginASession implements Runnable, Connect4Constants 
	  {
		  // Create Sockets for Both Players
		  private Socket player1;
		  private Object player2;
		  
		  // Create and initialize cells
		  private Connect4 game;
		  
		  // Create Data Streams for Players to Utilize
		  private DataInputStream fromPlayer1;
		  private DataOutputStream toPlayer1;
		  private DataInputStream fromPlayer2;
		  private DataOutputStream toPlayer2;
		
		  // Open A New Game on a New Thread
		  public BeginASession(Socket player1, Object player2, Connect4 game) 
		  {
			  this.player1 = player1;
		  	
		  	  if(player2.getClass() == player1.getClass())
		  		  this.player2 = (Socket) player2;
		  	  else
		  		  this.player2 = (Connect4ComputerPlayer) player2;
		  		
		  	  this.game = game;
		  }
	  
	    /**
	     * Runs the Session server, awaiting and joining players into games of Connect4.
	     */
	    public void run() 
	    {
	    	try 
	    	{
		        // Initialize all Player Data Streams
		        fromPlayer1 = new DataInputStream(player1.getInputStream());
		        toPlayer1 = new DataOutputStream(player1.getOutputStream());
		        
		        if(player2.getClass() == player1.getClass())
		        {
		        	fromPlayer2 = new DataInputStream(((Socket) player2).getInputStream());
		        	toPlayer2 = new DataOutputStream(((Socket) player2).getOutputStream());
		        
			        // Notify Player One to Begin The Game
			        toPlayer1.writeInt(1);
			  
			        // Continue to Serve, Determine, and Report Game Status
			        while (true) 
			        {
			        	// *** PLAYER ONE TURN *** //
			        	int column = fromPlayer1.readInt();
			        	
			        	while(!game.isValidMove(column))
			        	{
			        		toPlayer1.writeInt(INVALID);
			        		column = fromPlayer1.readInt();
			        	}
			        	
			        	toPlayer1.writeInt(CONTINUE);
			        	toPlayer2.writeInt(CONTINUE);
			        	
			        	// Find Row of Move
			        	int rowOfMove = game.getLowestOpenRow(column);
			        		
			        	// Make the Move
			        	game.placeMarker(column);
			        	
			        	// Update Boards
			        	toPlayer1.writeInt(column);
			        	toPlayer1.writeInt(rowOfMove);
			        	
			        	// Not sure if this is right, but both must be updated
			        	toPlayer2.writeInt(column);
			        	toPlayer2.writeInt(rowOfMove);
			        	
			        	// Check for Wins
			        	if(game.checkForWin(column))
			        	{
			        		toPlayer1.writeInt(PLAYER1_WON);
			        		toPlayer2.writeInt(PLAYER1_WON);
			        	}
			        	else if(game.getTurnCounter() == MAXMOVES)
			        	{
			        		toPlayer1.writeInt(DRAW);
			        		toPlayer2.writeInt(DRAW);
			        	}
			        	else
			        	{
			        		toPlayer1.writeInt(CONTINUE);
			        		toPlayer2.writeInt(CONTINUE);
			        	}
			        		
			        	column = fromPlayer2.readInt();
			        	
			        	while(!game.isValidMove(column))
			        	{
			        		toPlayer2.writeInt(INVALID);
			        		column = fromPlayer2.readInt();
			        	}
			        	toPlayer2.writeInt(CONTINUE);
			        	toPlayer1.writeInt(CONTINUE);
			        	
			        	// Find Row of Move
			        	rowOfMove = game.getLowestOpenRow(column);
			        		
			        	// Make the Move
			        	game.placeMarker(column);
			        	
			        	// Update Boards
			        	toPlayer2.writeInt(column);
			        	toPlayer2.writeInt(rowOfMove);
			        	
			        	toPlayer1.writeInt(column);
			        	toPlayer1.writeInt(rowOfMove);
			        	
			        	// Check for Wins
			        	if(game.checkForWin(column))
			        	{
			        		toPlayer1.writeInt(PLAYER2_WON);
			        		toPlayer2.writeInt(PLAYER2_WON);
			        	}
			        	else if(game.getTurnCounter() == MAXMOVES)
			        	{
			        		toPlayer1.writeInt(DRAW);
			        		toPlayer2.writeInt(DRAW);
			        	}
			        	else
			        	{
			        		toPlayer1.writeInt(CONTINUE);
			        		toPlayer2.writeInt(CONTINUE);
			        	}
			        } 
		        }
		        else
		        {
		        	 // Notify Player One to Begin The Game
			        toPlayer1.writeInt(1);
			        Connect4ComputerPlayer comp = (Connect4ComputerPlayer) player2;
			  
			        // Continue to Serve, Determine, and Report Game Status
			        while (true) 
			        {
			        	// *** PLAYER ONE TURN *** //
			        	int column = fromPlayer1.readInt();
			        	
			        	while(!game.isValidMove(column))
			        	{
			        		toPlayer1.writeInt(INVALID);
			        		column = fromPlayer1.readInt();
			        	}
			        	
			        	toPlayer1.writeInt(CONTINUE);
			        	
			        	// Find Row of Move
			        	int rowOfMove = game.getLowestOpenRow(column);
			        		
			        	// Make the Move
			        	game.placeMarker(column);
			        	
			        	// Update Boards
			        	toPlayer1.writeInt(column);
			        	toPlayer1.writeInt(rowOfMove);
			        	
			        	// Check for Wins
			        	if(game.checkForWin(column))
			        		toPlayer1.writeInt(PLAYER1_WON);
			        	else if(game.getTurnCounter() == MAXMOVES)
			        		toPlayer1.writeInt(DRAW);
			        	else
			        		toPlayer1.writeInt(CONTINUE);
			        		
			        	column = comp.makeMove();
			        	
			        	toPlayer1.writeInt(CONTINUE);
			        	
			        	// Find Row of Move
			        	rowOfMove = game.getLowestOpenRow(column);
			        		
			        	// Make the Move
			        	game.placeMarker(column);
			        
			        	toPlayer1.writeInt(column);
			        	toPlayer1.writeInt(rowOfMove);
			        	
			        	// Check for Wins
			        	if(game.checkForWin(column))
			        		toPlayer1.writeInt(PLAYER2_WON);
			        	else if(game.getTurnCounter() == MAXMOVES)
			        		toPlayer1.writeInt(DRAW);
			        	else
			        		toPlayer1.writeInt(CONTINUE);
			        } 
		        }
	    	}
	    	catch(IOException ex) 
	    	{
	    		ex.printStackTrace();
	    	}
	    }
	  }
	  
	  /**
	   * Closes the Display and shuts down the server upon app closure.
	   */
	  @Override
	  public void stop()
	  {
		  try
		  {
			  Platform.exit();
			  System.exit(0);
		  }
		  catch (Exception e){  
		  }
	  }
}
