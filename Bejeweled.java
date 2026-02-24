/**
 * Bejeweled.java (Skeleton)
 * <p>
 * This class represents a Bejeweled (TM)
 * game, which allows player to make moves
 * by swapping two pieces. Chains formed after
 * valid moves disappears and the pieces on top
 * fall to fill in the gap, and new random pieces
 * fill in the empty slots.  Game ends after a
 * certain number of moves or player chooses to
 * end the game.
 */
 
 /** 
	File Name:  Bejeweled.java
	Name: Youssef El-Aboudy
	Class: ICS3U1-12
	Date: January 17, 2024
	Description: This is the logic section behind my Bejeweled game
*/

import java.awt.Color;
import java.io.*;

public class Bejeweled {

   /*
    * Constants
    */
   // colours used to mark the selected piece and
   // the pieces in the chain to be deleted
   final Color COLOUR_DELETE = Color.RED;
   final Color COLOUR_SELECT = Color.YELLOW;

   BejeweledGUI gui;    // the object referring to the GUI, use it when calling methods to update the GUI


   // declare all constants here
   final int ROW = 8;
   final int COL = 8;
   final int EMPTY = -1;
   final int MIN_CHAIN = 2;
   final int MAX_CHAIN = 7;
   final int FIRST_PLAY = 1;
   final int MAX_PLAY = 2;
   final int MOVE_AMNT = 10;
   final String GAMEFILEFOLDER = "gamefiles";
   // declare all "global" variables here
   boolean firstSelection = true;
   boolean check;
   boolean chainCheck = false;
   boolean checkMove = true;
   int[][] board;
   //score of the player
   int score = 0;
   //stores number of moves
   int numMoveLeft = 10;
   //stores firstclick
   int slot1Row, slot1Col;
   //counts the number of clicks
   int count = 0;
   int temp;
   //stores values of all chain counts
   int chainCountLeft = 0;
   int chainCountRight = 0;
   int chainCountUp = 0;
   int chainCountDown = 0;
   //random number to display on board
   int randomNum;

   public Bejeweled(BejeweledGUI gui) {
      this.gui = gui;
      start();
   }
   
   //initializes everything
   public void start() {
      firstSelection = true;
      //displays game board
      initBoard();
      //displays pieces on the board
      displayBoardPieces();
      //sets the moves
      gui.setMoveLeft(numMoveLeft);
   }

   //sets up the board
   public void initBoard() {
      board = new int[ROW][COL];
      //creates the 7x7 board and fills it with random number, each random number means its a random piece
      for (int i = 0; i < ROW; i++) {
         for (int m = 0; m < COL; m++) {
            randomNum = (int) (Math.random() * gui.NUMPIECESTYLE);
            //initalize each address of board with a random number
            board[i][m] = randomNum;
         }
      }
   }

   //displays the pieces graphically
   public void displayBoardPieces() {
      for (int i = 0; i < ROW; i++) {
         for (int m = 0; m < COL; m++) {
            //loops through each spot in row and col and fills it with the corresponding random number
            gui.setPiece(i, m, board[i][m]);
         }
      }
   }

   //this method stores first click and initiates adjacent slots method which checks swap
   public void play(int row, int column) {
      //highlights selection
      gui.highlightSlot(row, column, COLOUR_SELECT);
      //increases count with each button click
      count++;
      //since count = 1 is the first selection, it stores that information
      if (count == FIRST_PLAY) {
         //stores first click in variables
         slot1Row = row;
         slot1Col = column;
      }
   
      //since count = 2 means your turn is done it will unhighlight the choices and reset variables
      if (count == MAX_PLAY) {
         //swaps the icon or displays if its an invalid move
         adjacentSlots(row, column, slot1Row, slot1Col);
      }
   }
   
   //this method checks if a move is valid and if it is, a swap will occur and then the following steps will be decided in the next method
   public void adjacentSlots(int row, int column, int slot1Row, int slot1Col) {
   //checks if its a valid adjacent move
      checkMove = true;
      //checks if you are doing an adjacent move which is valid
      if ((slot1Row == row && Math.abs(slot1Col - column) == 1) || (slot1Col == column && Math.abs(slot1Row - row) == 1)) {
      //calls the method that swaps the values numerically and graphically
         swapPiecesGUI(row, column, slot1Row, slot1Col);
      }
      //if you are doing an invalid move
      else {
         checkMove = false;
         //calls the method that does a different action since checkMove is false in this instance
         swapPiecesGUI(row, column, slot1Row, slot1Col);
      }
   }

   //checks if you created a chain and highlights the chain red
   public boolean displayChain(int row, int column) {
      chainCheck = false;
      //checks if different shaped chains occured
      if ((countLeft(row, column) + countRight(row, column) >= MIN_CHAIN) && (countUp(row, column) + countDown(row, column) >= MIN_CHAIN)){
         //highlights corresponding pieces in the chain RED
         markDeletePiece(row, column);
         markDeletePieceLeft(row, column);
         markDeletePieceRight(row, column);
         markDeletePieceUp(row, column);
         markDeletePieceDown(row, column);
         //displays the size of the chain
         gui.showChainSizeMessage(countLeft(row, column) + countRight(row, column) + countUp(row,column) + countDown(row,column) + 1);
      }
      //if a horizontal chain occurs
      else if ((countLeft(row, column) + countRight(row, column) >= MIN_CHAIN)) {
      //highlights corresponding pieces in the chain RED
         markDeletePiece(row, column);
         markDeletePieceLeft(row, column);
         markDeletePieceRight(row, column);
         //displays chain size
         gui.showChainSizeMessage(countLeft(row, column) + countRight(row, column) + 1);
      } 
      
      //if vertical chain occurs
      else if (countUp(row, column) + countDown(row, column) >= MIN_CHAIN) {
      //highlights corresponding pieces in the chain RED
         markDeletePiece(row, column);
         markDeletePieceUp(row, column);
         markDeletePieceDown(row, column);
         //displays chain size
         gui.showChainSizeMessage(countUp(row, column) + countDown(row, column) + 1);
      } 
      else {
         //chaincheck is true if no chain occurs
         chainCheck = true;
      
      }
      //returns value of chaincheck
      return chainCheck;
   }
   
   //checks how main pieces are same as selected piece not including the piece itself from the left side
   public int countLeft(int row, int column) {
      check = false;
      chainCountLeft = 0;
   
      //if even one piece isnt the same check will be true which stops the for loop from working
      for (int i = column - 1; i > EMPTY && !check; i--) {
         //if a piece going left isnt the same as the selected piece
         if (board[row][i] != board[row][column]) {
            check = true;
         } else {
         //adds to chain counter if the piece is the same
            chainCountLeft++;
         }
      }
      //returns value of chain from the left size
      return chainCountLeft;
   }

   //checks the pieces that are the same as the swap on the right side
   public int countRight(int row, int column) {
      check = false;
      chainCountRight = 0;
         //if even one piece isnt the same check will be true which stops the for loop from working
      for (int i = column + 1; i < COL && !check; i++) {
         if (board[row][i] != board[row][column]) {
            check = true;
         } 
         
         else {
               //adds to chain counter if the piece is the same
            chainCountRight++;
         }
      }
      //returns value of chain from the right size
      return chainCountRight;
   }

   //checks how many pieces are the same as the selected from the piece from the direction up
   public int countUp(int row, int column) {
      //boolean to see if a piece is the same as selected piece or not
      check = false;
      //counts how many pieces are the same as selected piece
      chainCountUp = 0;
      //loops through the row
      for (int i = row - 1; i > EMPTY && !check; i--) {
         //if piece doesnt equal to selected piece, check will be true
         if (board[i][column] != board[row][column]) {
            check = true;
         } 
         else {
            //increases chain counter
            chainCountUp++;
         }  
      }
      //returns value of chain from top
      return chainCountUp;
   }
   
   //checks how many pieces are the same as the selected from the piece from the direction up
   public int countDown(int row, int column) {
      check = false;
      chainCountDown = 0;
      
      //loops through the row
      for (int i = row + 1; i < ROW && !check; i++) {
         //if board piece does not equal a piece
         if (board[i][column] != board[row][column]) {
            //check will become true, meaning no more checks will be made
            check = true;
         } 
         
         else {
            //increases amount of pieces down are the same as selected piece
            chainCountDown++;
         }
      }
      //returns value of the chain from bottom
      return chainCountDown;
   }
   
   //these following methods all deal with highlighting the chain RED
   public void markDeletePiece(int row, int column) {
   //highlights the chain slot red
      gui.highlightSlot(row, column, COLOUR_DELETE);
   }

   public void markDeletePieceLeft(int row, int column) {
      for (int i = column - 1; i >= column - countLeft(row, column); i--) {
      //highlights the chain slot red if the left side is part of the chain
         gui.highlightSlot(row, i, COLOUR_DELETE);
      }
   }

   public void markDeletePieceRight(int row, int column) {
      for (int i = column + 1; i <= countRight(row, column) + column; i++) {
      //highlights the chain slot red if the right side is part of the chain
         gui.highlightSlot(row, i, COLOUR_DELETE);
      
      }
   }
   
   //method that highlights chain red
   public void markDeletePieceUp(int row, int column) {
      for (int i = row - 1; i >= row - countUp(row, column); i--) {
      //highlights the chain slot red if the top side is part of the chain
         gui.highlightSlot(i, column, COLOUR_DELETE);     
      }
   }
      //method that highlights chain red
   public void markDeletePieceDown(int row, int column) {
     //highlights the chain slot red if the bottom side is part of the chain
      for (int i = row + 1; i <= row + countDown(row, column); i++) {
         gui.highlightSlot(i, column, COLOUR_DELETE);
      }
   }
   
   //this method is only called if a chain has indeed occured
   public void markEmptyCheck(int row, int column) {
      //marks the chain EMPTY (-1)
      if ((countLeft(row, column) + countRight(row, column) >= MIN_CHAIN) && (countUp(row, column) + countDown(row, column) >= MIN_CHAIN)){
         //gives each piece in the chain the value -1
         markEmptyUp(row, column);
         markEmptyDown(row, column);
         markEmptyLeft(row, column);
         markEmptyRight(row, column);
         markEmpty(row, column);
      }
      
      //marks the horizontal chain with -1
      else if ((countLeft(row, column) + countRight(row, column) >= MIN_CHAIN)) {
      //gives the values that occur in a chain EMPTY (-1)
         markEmptyLeft(row, column);
         markEmptyRight(row, column);
         markEmpty(row, column);
      }
      
      //marks the vertical chain with -1
      else if (countUp(row, column) + countDown(row, column) >= MIN_CHAIN) {
       //gives the values that occur in a chain EMPTY (-1)
         markEmptyUp(row, column);
         markEmptyDown(row, column);
         markEmpty(row, column);
      }
   }

   //all these methods mark the chain with the value -1
   public void markEmpty(int row, int column) {
   //marks your first/second selection empty if it results in chain
      board[row][column] = EMPTY;
   }

   public void markEmptyLeft(int row, int column) {
   //stores the count value
      int temp = countLeft(row, column);
      
      for (int i = 1; i <= temp; i++) {
      //marks left side empty if chain occurs
         board[row][column - i] = EMPTY;
      }
   }

   public void markEmptyRight(int row, int column) {
   //stores the count value
      int temp = countRight(row, column);
      
      for (int i = 1; i <= temp; i++) {
      //marks right side empty if chain occurs
         board[row][column + i] = EMPTY;
      }
   }

   public void markEmptyUp(int row, int column) {
   //stores the count value
      int temp = countUp(row, column);
      
      for (int i = 1; i <= temp; i++) {
      //marks top side empty if chain occurs
         board[row - i][column] = EMPTY;
      }
   }

   public void markEmptyDown(int row, int column) {
   //stores the count value
      int temp = countDown(row, column);
      for (int i = 1; i <= temp; i++) {
      //marks bottom side empty if chain occurs
         board[row + i][column] = EMPTY;
      }  
   }
   
   //prints out the board and also calls updategameboard which updates board graphically and shifts everything accordingly
   public void updateBoard() {
      updateGameBoard();
      //this part of the program is for testing purposes, only prints out the board
      // for (int k = 0; k < ROW; k++) {
         // for (int g = 0; g < COL; g++) {
            // System.out.print(board[k][g] + "  ");
         // }
         // System.out.println();
      // }
      // System.out.println();
   }

   //this method unhighlights the 2 selection
   public void markRemoveHighlight(int row, int column, int slot1Row, int slot1Col) {
      gui.unhighlightSlot(row, column);
      gui.unhighlightSlot(slot1Row, slot1Col);
   }

   //this method swaps the 2 pieces and deals with all the chain detection
   public void swapPiecesGUI(int row, int column, int slot1Row, int slot1Col) {
   //checkmove is initialized as true in adjacent slots and the value of checkmove will be false if the move isnt adjacent
      if (checkMove) {
         //removes highlights
         markRemoveHighlight(row, column, slot1Row, slot1Col);
         //swaps the values of the two pieces
         temp = board[slot1Row][slot1Col];
         board[slot1Row][slot1Col] = board[row][column];
         board[row][column] = temp;
         //displays the swap graphically
         gui.setPiece(row, column, board[row][column]);
         gui.setPiece(slot1Row, slot1Col, board[slot1Row][slot1Col]);
         //resets the count to get ready for a new play
         count = 0;
         //stores the 2 chains in separate booleans
         boolean displaySelection1 = displayChain(row,column);
         boolean displaySelection2 = displayChain(slot1Row,slot1Col);
         
         //if both pieces that were swapped do not result in a chain, it will be an invalid move
         if (displaySelection1 && displaySelection2){
            //displays invalid move message
            gui.showInvalidMoveMessage();
            //removes highlights
            markRemoveHighlight(row, column, slot1Row, slot1Col);
            //swaps the pieces back to their original spot
            temp = board[slot1Row][slot1Col];
            board[slot1Row][slot1Col] = board[row][column];
            board[row][column] = temp;
         //swaps the pieces back to their original spot graphically
            gui.setPiece(row, column, board[row][column]);
            gui.setPiece(slot1Row, slot1Col, board[slot1Row][slot1Col]);
            //resets count
            count = 0;
         }
         
         //if both pieces result in a chain
         else if (!displaySelection1 && !displaySelection2) {
         //marks both chains empty
            markEmptyCheck(row, column);
            markEmptyCheck(slot1Row, slot1Col);
            //updates board graphically and numerically
            updateBoard();
            //updates score
            score+=147;
            gui.setScore(score);
            //updates number of moves left
            numMoveLeft--;
            gui.setMoveLeft(numMoveLeft);      
         } 
         
         //if first selection results in a chain
         else if (!displaySelection1) {
         //marks that chain empty (-1)
            markEmptyCheck(row, column);
            //updates board
            updateBoard();
            //updates score
            score+=147;
            gui.setScore(score);
            //updates number of moves left
            numMoveLeft--;
            gui.setMoveLeft(numMoveLeft);   
         } 
         
         //if second selection results in a chain
         else if (!displaySelection2) {
         //marks that chain empty (-1)
            markEmptyCheck(slot1Row, slot1Col);
            //updates board
            updateBoard();
            //updates score
            score+=147;
            gui.setScore(score);
            //updates number of moves left
            numMoveLeft--;
            gui.setMoveLeft(numMoveLeft);
         } 
      } 
      else {
         //removes highlights
         markRemoveHighlight(row, column, slot1Row, slot1Col);
         //shows invalid move
         gui.showInvalidMoveMessage();
         //resets count
         count = 0;
      }
      
      //if you are out of moves
      if (numMoveLeft == 0){
      //displays the score you obtained and number of moves
         gui.showGameOverMessage(score,MOVE_AMNT);
      }
   }

   //returns true if the game is saved successful or false if it didnt
   public boolean saveToFile(String fileName) {
      try{
      //declaring the file writer
         BufferedWriter out = new BufferedWriter(new FileWriter(GAMEFILEFOLDER + "/" + fileName));
         //write the current score prior to saving
         out.write(score + "\n");
         //writes number of moves remaining
         out.write(numMoveLeft + "\n");
         for (int i = 0; i < ROW; i++){
            for (int j = 0; j < COL; j++){
            //loops through and writes the type the piece is
               out.write(board[i][j] + "");
            }
            //skips a row every time
            out.write("\n");
         }
         out.close();
         //returns true which means it saved successfully
         return true;
      
      }catch(IOException iox){
         //returns false if it didnt save successfully
         return false;
      }
   }

    //returns true if game got loaded correctly or false if not
   public boolean loadFromFile(String fileName) {
   //stores current row of the board
      String boardLine;
      //stores specific piece of that row
      String boardPiece; 
      try{
         //declaring the file reader
         BufferedReader in = new BufferedReader(new FileReader(GAMEFILEFOLDER + "/" + fileName));
         //reads in the score from the first line
         score = Integer.parseInt(in.readLine());
         //reads number of moves from second line
         numMoveLeft = Integer.parseInt(in.readLine());
         for (int i = 0; i < ROW; i++){
            //stores current row in variable
            boardLine = in.readLine();
            //loops through column
            for (int j = 0; j < COL; j++){
               //stores the piece on the current row and current column in a variable
               //casting into string so we can do integer.parseint
               boardPiece = "" + boardLine.charAt(j);
               //gives the 2d board array the values of each piece
               board[i][j] = Integer.parseInt(boardPiece);
            }
         }
         //re-initializes the load with the correct score and number of moves left
         gui.setScore(score);
         gui.setMoveLeft(numMoveLeft);
         //returns true if game loaded successfully
         return true;
      }catch(IOException iox){
         //returns false if game did not load successfully
         return false;
      }
   }

   //updates board numerically and graphically
   public void updateGameBoard() {
   //loops through board
      for (int y = 0; y < ROW; y++) {
         for (int x = 0; x < COL; x++) {
            //if the piece on the board is marked empty
            if (board[y][x] == EMPTY) {
               //loops through column
               for (int j = y; j > 0; j--) {
                  //shifts the pieces down
                  board[j][x] = board[j - 1][x];
                  //shifts pieces marked empty up so they are at the top of the board
                  board[j - 1][x] = EMPTY;
               }
            }
         }
      }
      
      //loops through 2d array (board)
      for (int i = 0; i < ROW; i++) {
         for (int j = 0; j < COL; j++) {
            //if board piece isnt marked empty
            if (board[i][j] != EMPTY) {
            //sets it graphically like normal
               gui.setPiece(i, j, board[i][j]);
            }
            //if board piece is marked empty
            if (board[i][j] == EMPTY) {
            //randomizes a piece for it
               randomNum = (int) (Math.random() * gui.NUMPIECESTYLE);
               //sets that random number in the address of that piece
               board[i][j] = randomNum;
               //displays new piece graphically
               gui.setPiece(i, j, board[i][j]);  
            }
         }
      }
      
      //loops through entire board and gets rid of ANY highlights
      for (int i = 0; i < ROW; i++){
         for(int m = 0; m < COL; m++){
         //unhighlights given slot on the board
            gui.unhighlightSlot(i,m);
         }
      }
   }
   
   //ends the game if the user presses the button
   public void endGame() {
   //shows how much score you ended with and the amount of moves used
      gui.showGameOverMessage(score,MOVE_AMNT - numMoveLeft);
   }
    
}//end of Bejeweled class


