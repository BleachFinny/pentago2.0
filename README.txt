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
	The 2-D array is like grid, which is what the block essentially are. Each element in the array is a Marble object,
	analogous to the Marble objects stored in the view model.

  2. File I/O

  3. JUnit Testing

  4. Network I/O


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.


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