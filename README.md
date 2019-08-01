# Minesweeper - Ver 1.0.0
Version 1.0.0

Simple minesweeper game made using JavaFX.

## Features
* 3 Difficulty settings
* Multiple sized game grids
* Right-click to flag mines
* Sound can be toggled on/off

## How to play
The goal of Minesweeper is to locate and flag all the bombs. The grid begins with all tiles unmarked. Selecting a tile will either reveal a bomb or a number. If a bomb gets revealed the game is over and the grid explodes. However, if a number is revealed it can be used to deduce where bombs are located. The number represents how many bombs are adjacent to the square. For example: if a square has an 8 then all the surrounding squares must be bombs so they shoudn't be clicked but instead right-clicked to be flagged. The game is won once all tiles containing a bomb have been successfully flagged.

## Screeenshots
![alt text](https://i.ibb.co/JvH02cV/Screenshot.png "Difficulty selection")

## Planned Features
* Code refactor to adhere to Java design principles
* Hint system
* High scores
