/**
 * Client for establishing and playing games of Connect4.
 * Establishes a connection to the server and makes necessary
 * updates during the course of the game.
 * 
 * @author Jonathan (Jack) MacArthur
 * @version 1.0
 */
package core;

import ui.Connect4GUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Connect4Client extends Application implements Connect4Constants
{
	// Input and output streams from/to server
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	// Host name or ip
	private String host = "localhost";
	
	// GUI Elements
	private Button pvp, ai;
	int gameType;
	private Connect4GUI gui;
	private Stage primaryStage;
	
	/**
	 * Launches the client display.
	 * 
	 * @param args unneeded
	 */
	public static void main(String[] args) 
	{
	    launch(args);
	}
	
	/**
	 * Provides the functionality for the client display and
	 * connections.
	 */
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) throws InterruptedException 
	{
		// Note Stage - Sends to GUI Later
		this.primaryStage = primaryStage;
		
		// All Elements Needed
		pvp = new Button("PVP");
		ai = new Button("AI");
		Text instructions = new Text("Would you like to utilize "
						+ "the PVP or Text Console?");
		
		// Various Stylings
		instructions.setStyle("-fx-font: 24 arial;");							
		pvp.setStyle("-fx-font: 24 arial;");
		ai.setStyle("-fx-font: 24 arial;");
		
		// AnchorPane For All Components
		AnchorPane all = new AnchorPane(pvp, ai, instructions);
		
		// Placements
		AnchorPane.setTopAnchor(pvp, 200.0);
		AnchorPane.setTopAnchor(ai, 200.0);
		AnchorPane.setRightAnchor(pvp, 400.0);
		AnchorPane.setRightAnchor(ai, 200.0);
		AnchorPane.setTopAnchor(instructions, 100.0);
		AnchorPane.setRightAnchor(instructions, 70.0);
									
		// Button Handlers
		pvp.setOnAction(buttonHandler);
		ai.setOnAction(buttonHandler);
		
		// Vital Elements
		Scene scene = new Scene(all, 700, 400);
		primaryStage.setTitle("Connect4");	
				
		// Set and Show Display
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * This button handler initiates the attempt to connect
	 * to the server once pvp or ai has been chosen.
	 */
	final EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>()
	{
		@Override
        public void handle(ActionEvent event) 
        {
			// Determine Source
			Object source = event.getSource();
			
			if(source.equals(pvp))
				gameType = 0;
			else if(source.equals(ai))
				gameType = 1;
			
			connectToServer(gameType);
        }
    };
	
    /**
     * Given the type of game (ai or pvp), attempts
     * to establish and begin a Connect4 game with the
     * server.
     * 
     * @param gameType the type (pvp = 0, ai = 1) of game desired
     */
	private void connectToServer(int gameType) 
	{
		// Set Up Connection To Server
		try 
		{
			// Create a socket to connect to the server
			@SuppressWarnings("resource")
			Socket socket = new Socket(host, 8000);
	
			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
	
			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}

		// Begin A Different Thread For Game
		new Thread(() -> 
		{
			try 
			{
				// Receive startup notification from the server
				toServer.writeInt(gameType); // 2
				int player = fromServer.readInt(); // 1
		        boolean keepPlaying = true;
				
				gui = new Connect4GUI(toServer, player);
				gui.initDisplay();

				if(player == PLAYER1)
				{
					repaint(waiting);
					fromServer.readInt();
					repaint(welcome);
				}
				else
					repaint(welcome);
				
				int status = CONTINUE;
				
				String alternatePlayerMove = p1Turn;
				if(gameType == 1)
					alternatePlayerMove = p1TurnWithComp;
				
				// Continue to play, update status, check on game
				while(keepPlaying)
				{
					if(player == PLAYER1)
					{
						receiveMove("X", player);
						gui.setMyTurn(false);
						repaint(p2Turn);
						status = fromServer.readInt();
						
						if(status != CONTINUE)
							break;
						
						receiveMove("O", player);
						gui.setMyTurn(true);
						repaint(alternatePlayerMove);
						status = fromServer.readInt();
						
						if(status != CONTINUE)
							break;
					}
					else if(player == PLAYER2)
					{
						receiveMove("X", player);
						gui.setMyTurn(true);
						repaint(p2Turn);
						status = fromServer.readInt();
						
						if(status != CONTINUE)
							break;
						
						receiveMove("O", player);
						gui.setMyTurn(false);
						repaint(p1Turn);
						status = fromServer.readInt();
						
						if(status != CONTINUE)
							break;
					}
				}
				
				// Victory updates
				if(gameType == 0)
				{
					if(status == PLAYER1_WON)
						repaint(p1Victory);
					else if(status == PLAYER2_WON)
						repaint(p2Victory);
					else
						repaint(draw);
				}
				else
				{
					if(status == PLAYER1_WON)
						repaint(p1Victory);
					else if(status == PLAYER2_WON)
						repaint(computerVictory);
					else
						repaint(draw);
				}
				
				// Game completed
				gui.endGame();
			}	
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * Allows for communication from the server to receive moves made,
	 * allowing for GUI updates.
	 * 
	 * @param marker the marker which was used to make the move
	 * @param player the player who is receiving the move info
	 * @throws IOException thrown if communication fails
	 */
	public void receiveMove(String marker, int player) throws IOException
	{
		// Read in status
		int success = fromServer.readInt();
		
		// Wait for valid status
		while(success != CONTINUE)
		{
			if(player == PLAYER1)
				repaint(error1);
			else
				repaint(error2);
			
			success = fromServer.readInt();
		}
		
		// Record and update GUI
		int column = fromServer.readInt();
		int row = fromServer.readInt();
		
		String[][] visualBoard = gui.getVisualBoard();
		visualBoard[row][column] = marker;
		gui.setVisualBoard(visualBoard);
	}
	
	/**
	 * Updates the GUI based off the most recent grid and a 
	 * new string status to display.
	 * 
	 * @param message the message to display on the GUI.
	 */
	public void repaint(String message)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				gui.updateStatus(message);
				Scene scene = gui.updateDisplay();
				primaryStage.setScene(scene);
				primaryStage.show();
			}
		});
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

