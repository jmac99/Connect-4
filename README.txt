There are two different files to run in order to utilize the networked game:

	1) Connect4Server.java - Running one instance of the server will allow for networked games to begin.
	2) Connect4Client.java - The client can be run multiple times, simulating both PVP and AI games.

The Connect4TextConsole.java class was included, though is deprected and unused, due to the scope of the fifth deliverable. It was not updated or altered since the last assignment, and does not have a role in the networked game. Thus, it is left out of javaDoc.

I included event handling when necessary, but with a GUI, it is far simpler and better designed to make the only possible interactions with the software (hitting buttons and what not) have conditionals that ensure an error is never thrown as it is. Therefore, much is taken care of by simple conditionals, though error handling is included as necessary. Due to slack conversations, I feel that this meets all project requirements.