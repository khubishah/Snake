Khubi Shah - 20764562 - This is my version of Snake.
I have implemented all the required features with an additional feature of a button on 
the high score page called "Play Again", which restarts the game at level 1. I've also
used images to made the snake and apples look real. 

Dealing with edge cases
1. In the case that the snake or another already placed apple is in the spot of an apple about to be placed, we instead
keep trying to randomly generate another apple to replace that apple that doesn't collide 
the snake or other apples already on the board. In that way, there is always a fixed x number of apples on the board.

Design decisions
- Grid is 20x15 squares.

Platform: MacOS
Java Version: openjdk version "15" 2020-09-15
OpenJDK Runtime Environment AdoptOpenJDK (build 15+36)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 15+36, mixed mode, sharing)