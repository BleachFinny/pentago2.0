=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: ericzeng
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2-D Arrays
	The game model representation of each rotating block is a 3x3 2-D array. 
	Additionally, the combined form of the four blocks used for win condition checking is also 6x6 2-D array.
	The 2-D array is like grid, which is what the blocks essentially are. Each element in the array is a Marble object,
	analogous to the Marble objects stored in the view model. Since the only important information stored in the Marble
	JComponent is the color (size is homogeneous), board updates are executed by changing the color of the affected
	marbles instead of reordering components.

  2. File I/O
  	A file is used to store a list of users and their game statistics associated with the program. 
  	Statistics include wins, losses (ties count as losses), marbles placed, and blocks turned.
  	Two statistics files are provide in this demo to simulate the two distinct files that would be on two different
  	machines. The data on one file represents users in one machine only; therefore, DO NOT open a single statistics 
  	file on multiple instances of this game on one machine. (see Network I/O for more)
  	
  	The data is parsed in from a Buffered FileReader and stored in a TreeMap of Player objects to passwords.
  	When the user hits the "Return" button in-game, a Buffered FileWriter will write the updated statistics to a
  	temporary file, delete the old stat.txt, and rename the temporary file to the statistics file name. The TreeMap
  	will also be updated accordingly to reflect updated statistics. If at any point there is an I/O error, the program
  	will shutdown and no data will be written to the statistics file.

  3. JUnit Testing
  	JUnit testing covers the model of the game board and its operations. Specifically, the placement of marbles,
  	rotation of blocks, and detection of win conditions are tested. To allow for modification of the game board state,
  	some methods and fields in Board.java are made public. For a practical implementation without a JUnit test, these 
  	methods and fields would be made private.
  	
  	Each test case instantiates a new thread to act as the other player so that the player being tested can connect
  	properly. Both connections are closed at the end of each test regardless of errors/exception in the testing body.
  	Additionally since each test case uses an actual p2p connection, the writing and reading from sockets is tested.

  4. Network I/O
  	The networking here uses Java's native Socket and ServerSocket class to create a host-client relationship. In the
  	actual game, the distinction between host and client is irrelevant; only during p2p connection does one player need
  	to select "Host" and the other "Client" with the appropriate host IP. For the purposes of this demo, it is OK to
  	run the connection locally with "localhost" as the IP (default anyways), but as stated in the File I/O section,
  	one must simulate two different machines by specifying different stat.txt files upon launch.
  	
  	As for the sockets' use in the program, updates to and from the opponent will be sent through the socket as a
  	String of data that indicates a rotation or a marble placement. There is no central server here so both clients'
  	programs update their respective boards without a master version to check. Socket receiving is run on a separate
  	thread dedicated to listening for board updates from the opponent. This prevents the main thread from hanging while
  	the socket waits for new updates from the opponent.


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  Game.java: Sets up the statistics file, login, and networking. It contains mostly GUI code as the back-end for each
  element is fairly easy to maintain. The game board's GUI is modeled here, but its state is handled by a Board object.
  
  Board.java: Maintains the board state while communicating with the login JFrame to display appropriate messages. These
  message inform the players of whose turn it is and when a player wins. As for the board itself, elements are stored in
  nested GridLayouts with each element being a Marble. All operations (placement, rotation) are defined in this class.
  
  Player.java: An object that is uses as a record to store player information. Contains the name, wins, loses, placements,
  and block turns. Equality is defined as having equal name Strings.
  
  Marble.java: A JComponent that represents a marble space. The Color field designates the color of the placed marble,
  while a null color field means the marble has not been placed yet. Although each marble has a non-static size field for
  the diameter of the marble, this will be uniform for each marble in a given board. Equality is determined by having the
  same size and Color.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?



========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  Learning networking sockets
  https://docs.oracle.com/javase/tutorial/networking/sockets/
  
  GridLayout for view model of marbles/blocks
  https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html

  Multithreading for network operations
  http://docs.oracle.com/javase/tutorial/essential/concurrency/index.html
  
  TextFields for login
  https://docs.oracle.com/javase/tutorial/uiswing/components/textfield.html