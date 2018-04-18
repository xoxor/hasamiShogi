# hasamiShogi

## Rules:
Hasami Shogi is one of the most popular Shogi variants in Japan. The word *Hasami* means *sandwiching* which shortly describes the way of capturing in this game.
The game is played on 9x9 board. Each player starts with 18 pieces which are placed on the first two rows on each side. Black player starts.

### How to move pieces:
In every turn, a player takes one stone of his colour and moves it horizontally or vertically any number of spaces (as a Rook in Chess) and no other piece (either colour) can block the way. However, a stone can jump over another stone (either colour) if it stands on an adjacent square and the next square in the same direction is empty. 

### How to capture opponent's pieces:
A player captures one or more opponent's stones if he/she makes a move which "sandwiches" them. It means that a connected line of opponent's stones must be surrounded by two player's stones either vertically or horizontally. 

### How finish the game:
The game is finished if one of the following conditions is reached:
- One player creates a connected line of 5 stones of his colour vertically or diagonally (not horizontally). No stone of this line can be placed in player's starting rows. This player wins the game.
- One player has only 1 or no pieces left on the board. This player loses the game.
- The maximum number of moves is reached. The player with more pieces on the board wins the game.


## Usage:
For running **hasamiShogi** through the command line, type:

java -jar hasamiShogi.jar OPTIONS

The options are:

\-tm		(--threshold_move)		->	maximum number of complete moves (i.e. 1 black move + 1 white move). 
										**Default value:** no threshold. 
\-tt		(--threshold_time)		->	maximum time for a player to move (milliseconds).
										**Default value:** 1000ms.
\-wt		(--waiting_time)		->	sleeping time for the automatic players before moving (milliseconds).
										**Default value:** 60ms.
\-p1		(--algorithm_player1)	->	searching strategy for the black player (see below for options).
										**Default value:** 1.
\-p2		(--algorithm_player2)	->	searching strategy for the white player (see below for options).
										**Default value:** 2.

*Strategy options:*
- 0		Human player;
- 1		Iterative Deepening AlphaBeta Pruning with best move ordering;
- 2		Iterative Deepening AlphaBeta Pruning;
- 3		Minimax with two levels searching;
- 4		AlphaBeta Pruning with four levels searching;
- 5		Random moves;
- 6		Monte-Carlo Tree Search (MCTS) and Upper Confidence Bounds for Trees (UCT).

Please note that the automatic player was presented to a competition with the final strategy **IDAlphaBetaSearch_BestMove** since it was the one presenting the best performances. Others strategies were implemented for training and testing purpose.

##Examples:

**java -jar hasamiShogi.jar**

The game starts with the default options.


**java -jar hasamiShogi.jar -p1 0 -p2 1**

The black player is the user and the white player is an automatic player using IDAlphaBetaSearch_BestMove.


**java -jar hasamiShogi.jar -tm 8 -p2 4**

Both players are automatic; the black player uses IDAlphaBetaSearch_BestMove research strategy, the white, instead, uses AlphaBetaSearch strategy. If none wins in 8 moves the game ends and the player with more pieces on the board wins.




