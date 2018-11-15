/**
 * Serves to Display a game of Connect4,
 * providing all necessary elements of a GUI
 * for a game against a player or computer.
 * 
 * @author Jonathan (Jack) MacArthur
 * @version 1.1
 */
package ui;

import core.Connect4Constants;

import java.io.DataOutputStream;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Connect4GUI implements Connect4Constants
{
	// Constructor Needs
	private DataOutputStream toServer;
	private String[][] visualBoard;
	
	// Display Needs
	private StackPane sPane;
	private Scene scene;
	TilePane columnChoice;
	BorderPane borderPane;
	
	private GridPane grid;
	private Label statusMsg;
	private Button c1, c2, c3, c4, c5, c6, c7;
	
	// Game Progression Needs
	private boolean myTurn, continuePlaying;
	
	/**
	 * Constructor for a new Connect4GUI.
	 * 
	 * @param toServer communication link to server to send moves
	 * @param player the player instantiating the GUI
	 */
	public Connect4GUI(DataOutputStream toServer, int player)
	{
		// Record Game Status
		continuePlaying = true;
		
		if(player == PLAYER1)
			myTurn = true;
		else
			myTurn = false;
		
		// Establish Communication
		this.toServer = toServer;
		
		// Establish Display Features
		initBoard(); 
	}
	
	/**
	 * Sets up a new game board, which is a 2D String array.
	 */
	public void initBoard()
	{
		visualBoard = new String[ROWS][COLUMNS];
		
		for(int i = 0; i < visualBoard.length; i++)
		{
			for(int j = 0; j < visualBoard[0].length; j++)
				visualBoard[i][j] = " ";
		}
	}
	
	/**
	 * Initializes the original display for the firs time the GUI is used.
	 */
	public void initDisplay()
	{
		// Instantiation of GUI Specs
		sPane = new StackPane();
		scene = new Scene(sPane, 1000, 700);
		grid = new GridPane();
				
		columnChoice = new TilePane();
		borderPane = new BorderPane();
				
		// Display of Game Status
		statusMsg = new Label(welcome);
				
		c1 = new Button("Col 1");
		c2 = new Button("Col 2");
		c3 = new Button("Col 3");
		c4 = new Button("Col 4");
		c5 = new Button("Col 5");
		c6 = new Button("Col 6");
		c7 = new Button("Col 7");
		
		c1.setOnAction(buttonHandler);
		c2.setOnAction(buttonHandler);
		c3.setOnAction(buttonHandler);
		c4.setOnAction(buttonHandler);
		c5.setOnAction(buttonHandler);
		c6.setOnAction(buttonHandler);
		c7.setOnAction(buttonHandler);

		// Putting Everything Together
		borderPane.setTop(statusMsg);
		columnChoice.getChildren().addAll(c1,c2,c3,c4,c5,c6,c7);
				
		sPane.getChildren().addAll(borderPane, columnChoice, grid);
				
		// Formatting
		BorderPane.setAlignment(statusMsg, Pos.CENTER);
		statusMsg.setStyle("-fx-font: 24 arial;");
		statusMsg.setTranslateY(10);
						
		columnChoice.setHgap(46);
		columnChoice.setTranslateX(205);
		columnChoice.setTranslateY(50);
						
		grid.setTranslateX(180);
		grid.setTranslateY(100);
	}
	
	/**
	 * Uses the gameboard and status to update the gui,
	 * returning an updated scene which can be displayed on a stage.
	 *
	 * @return The updated scene to display
	 */
	public Scene updateDisplay()
	{
		GridPane newGrid = new GridPane();
		BorderPane newBorder = new BorderPane();
		
		for(int row = 0; row < ROWS; row++)
		{
			for(int col = 0; col < COLUMNS; col++)
			{
				Rectangle newRec = new Rectangle();
				newRec.setWidth(90);
				newRec.setHeight(90);
				
				newRec.setFill(Color.WHITE);
				newRec.setStroke(Color.BLACK);
				
				GridPane.setRowIndex(newRec, row);
				GridPane.setColumnIndex(newRec, col);
				newGrid.getChildren().addAll(newRec);
				
				if(visualBoard[row][col].equals("X"))
				{
					Circle circ = new Circle();
					circ.setRadius(40);
					circ.setFill(Color.BLACK);
					GridPane.setRowIndex(circ, row);
					GridPane.setColumnIndex(circ, col);
					newGrid.getChildren().addAll(circ);
					circ.setTranslateX(5);
				}
				else if(visualBoard[row][col].equals("O"))
				{
					Circle circ = new Circle();
					circ.setRadius(40);
					circ.setFill(Color.RED);
					GridPane.setRowIndex(circ, row);
					GridPane.setColumnIndex(circ, col);
					newGrid.getChildren().addAll(circ);
					circ.setTranslateX(5);
				}
			}
		}
		StackPane newPane = new StackPane();
		newGrid.setTranslateX(180);
		newGrid.setTranslateY(100);
		
		newBorder.setTop(statusMsg);
		BorderPane.setAlignment(statusMsg, Pos.CENTER);
		statusMsg.setStyle("-fx-font: 24 arial;");
		statusMsg.setTranslateY(10);
		
		newPane.getChildren().addAll(newBorder, columnChoice, newGrid);
		Scene updatedScene = new Scene(newPane, 1000, 700);
		
		return updatedScene;
	}
	
	/**
	 * Handles button inputs for each column, sending the move to the server.
	 */
	final EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>()
	{
		@Override
        public void handle(ActionEvent event) 
        {
			// Determine Source
			int desiredMove;
			Object source = event.getSource();
			if(source.equals(c7))
				desiredMove = 6;
			else if(source.equals(c6))
				desiredMove = 5;
			else if(source.equals(c5))
				desiredMove = 4;
			else if(source.equals(c4))
				desiredMove = 3;
			else if(source.equals(c3))
				desiredMove = 2;
			else if(source.equals(c2))
				desiredMove = 1;
			else
				desiredMove = 0;
			
			try 
			{
				if(myTurn)
					sendMove(desiredMove); 

			} 
			catch (IOException e) 
			{
				System.out.println("Communication to Server has broken. "
					+ "Please close out, restart the server, and try again");
			}
        }  	
	};
	
	/**
	 * Takes the desired move of the player and sends it to the server.
	 * 
	 * @param desiredMove the column to play in
	 * @throws IOException thrown if communication link fails
	 */
	public void sendMove(int desiredMove) throws IOException
	{
		toServer.writeInt(desiredMove);
	}
	
	/**
	 * Allows for updates to be made to the status display 
	 * within the gui.
	 * 
	 * @param message the new message to display
	 */
	public void updateStatus(String message)
	{
		statusMsg.setText(message);
	}
	
	/**
	 * Once the game has finished, this method allows for the disabling of 
	 * all buttons.
	 */
	public void endGame()
	{
		c1.setOnAction(null);
		c2.setOnAction(null);
		c3.setOnAction(null);
		c4.setOnAction(null);
		c5.setOnAction(null);
		c6.setOnAction(null);
		c7.setOnAction(null);
	}
	
	/**
	 * Allows for the client to set the turn of the player, 
	 * which controls whether their button presses are 
	 * sent to server.
	 * 
	 * @param myTurn a boolean variable indicating if it is this player's turn
	 */
	public void setMyTurn(boolean myTurn)
	{
		this.myTurn = myTurn;
	}
	
	/**
	 * Returns the string, 2D representation
	 * of the board at hand.
	 * 
	 * @return the visual, 2D game representation
	 */
	public String[][] getVisualBoard()
	{
		return visualBoard;
	}
	
	/**
	 * Allows for the client to set the current
	 * visual display.
	 * 
	 * @param visualBoard the visual game representation, a 2D string array
	 */
	public void setVisualBoard(String[][] visualBoard)
	{
		this.visualBoard = visualBoard;
	}
}
