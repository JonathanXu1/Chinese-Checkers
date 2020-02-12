# Chinese-Checkers
A repo for a Chinese Checkers AI client, made in my 12AP CS class

[Basic rules for the game](https://www.wikihow.com/Play-Chinese-Checkers)


### Score calculation
- Due the nature of the game, we can't evaluate to a win/loss case
- Can use a combo of distance between pieces, distance from end, # of pieces, # moves
- The score for any board is calculated by subtracting a constant value by:
  - The vertical distance of the individual pieces to the other side of the starting point
  - The horizontal distance of the individual pieces from the edges of the board (the logcal reasoning being that the best opportunitiy for consecutive jumps for the majority of the game lies within the center of the board)
- The score for any board is calculated by adding adding to a constant value:
  - The number of friendly pieces in the 6 adjacent holes
  
- The multiplier for each factor in the score calculation is a float, determined by the genetic training algorithm to optimize the decision making of the algorithm

## The board
The board state is sent and recieved as a string
Specifically, the type of piece and its location is sent for each piece in the game.
The number of players remaining in the game is sent at the end of the string.

## Decision making
- The algorithm calculates the score for all the possible boards that can result if it moves a single piece (including consecutive jumps over a single turn)
- This is repeated a variable depthLayer, based on the following:

| Condition | depthLayer |
| --- | --- |
| 1 if 5 or more players remain, and the game has progressed more than 6 rounds | 1 |
| 2 if 3 or more players remain, and the game has progressed more than 8 rounds | 2 |
| otherwise | 3 |

- The reasoning for this is that given the simplicity of the alogirithm and the limitations on computation time, the AI would be overplanning its moveset when multiple players are in the middle of the board, where the board changes too quickly for the AI to adapt
- At the beginning of the game, and when there are fewer players left, the AI is better able to plan in advance, as there would fewer moving pieces

- Finally, the moveset that has the highest score will be chosen, and sent to a server via the socket protocol

### Genetic training
- The aggression parameters are optimized by training the AI against itself
- Individual personality genes are predefinied at the start from a working value
- The algorithm mutates personalities and populations, and crosses over personalities and populations to maximize genetic diversity
- Fitness is calculated by emulating a 1v0 board, and counting the number of turns it takes the agent to fully reach the end

### Other features
- Overtime error catch: Send a decision right before the 5sec mark so that the AI will not get disqualified
- Optimized socket.io
- There is a printGrid() function that displays the game board in ASCII on the terminal, for easier debugging

### TODOs for the future:
Implement Alpha beta pruning
- The player will be treated as a max and the others should be reated as a min with 5 moves
- Will help to remove redundancies and improve calculation speeds
