# Chinese-Checkers
A repo for our 12AP CS chinese checkers AI client

Some notes:

- Use minmax and player is max and others is just a big min with 5 moves
	- Alpha beta pruning

- We can eliminate moves where the final position is behind where the initial is
	- Set value of move to -1
	- Unless exception where pieces are at end and have to move back (maybe???)

- Calculating score:
	- Due to complexity can't evaluate to win/loss case
	- Can use a combo of distance between pieces, distance from end, # of pieces, # moves
	- Games uses score = scorex10 - # moves

- Ways to cheat time:
	- At 4.99 seconds just best move
	- Speed up io as fast as possible
	- 