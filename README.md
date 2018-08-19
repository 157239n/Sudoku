# Sudoku

This is a versioned Sudoku application to make playing Sudoku less stressful by allowing the player to guess when he or she is stuck.

First when you opened it:

![](https://i.imgur.com/HfF3xoz.png)

You have a Sudoku board on the left and all of the controls are on the right.

When you press new, a window prompts you to open a sudoku file:

![](https://i.imgur.com/jQyMlCH.png)

If you choose to cancel or open an invalid file then it will create a new board:

![](https://i.imgur.com/xX6rSn9.png)

You can then populate the board with values:

![](https://i.imgur.com/BYro6ZO.png)

You can move around using the arrow keys, delete a cell by pressing delete, click to select a cell and click again to deselect it.

So let's say that you got stuck, you can't think of a solution anymore and you really want to guess. Then you can click branch. This will branch the current board into a new board just like the original.

And let's say you have guessed some numbers:

![](https://i.imgur.com/Rk8Euor.png)

The numbers from the moment you choose to branch are colored blue to indicate that they may be wrong.

Occasionally you get a green flash when you have completed a square, a row or a column:

![](https://i.imgur.com/kNA5RAK.png)

Now, if you branch version 2, a version 3 will be created and every guesses from version 2 will be considered right (i.e colored as black) and all the new numbers from then on will be blue:

![](https://i.imgur.com/CaN3mVF.png)

You can delete branches at any time. You can also save a branch into a file. You can set the extension to anything you like, it's still just a text file. The text file is quite easy to understand. This is an example saved board from version 3:

```
2 9 5 7 0 0 8 6 0 
0 3 1 8 6 5 0 2 0 
8 0 6 0 0 0 0 0 0 
0 0 7 -4 5 -9 2 8 6 
0 0 2 3 8 7 0 0 0 
5 0 8 2 1 6 7 0 0 
0 8 0 5 0 0 1 4 9 
0 2 0 6 0 -4 3 5 8 
0 5 4 0 0 8 6 7 2 
```

Pretty simple. "0" for blank, positive number for the values we are sure about (i.e colored black) and negative numbers for values we aren't sure about (i.e colored blue).

Please note that currently, it does not detect illegal states. The only time when it detects them is when you have completed a row, column or square and it flashes green. If if does not flash when you have completed them then the board is in an illegal state.

Hope you guys enjoy it.