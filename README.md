GnuTrax
=======

The goal is a trax playing program which can beat the commerciel
closed sourced program called "Doby".

My idea is to reach this goal in the following steps:

1. Make a text based 8x8Trax playing program which can play
   well and win most of the time.
2. Make a GUI for the program.
3. Improve the AI for 8x8Trax
4. Extend the program so it also can play Standard (unlimited) Trax and
   LoopTrax

Status: 
The UCT-code is currently broken. 
Please help fix it ( computerplayer_uct.cpp )

Command-line options:
Currently gnutrax supports three options.

--uct turn on the uct-code
--random the AI plays random moves
--pbem the program reads from moves from standard input,
  computes and display the best reply and exist.

I will write a couple of documents how these goals can be reached
in details.

All questions about this code just email me at: martin@linux.com and
you will normally get an answer within 48 hours.

Martin M. S. Pedersen

The GUI version:

Run with:

```
java org.traxgame.gui.GnuTraxGui
```

or run the ant script in the src directory:

```
ant
```

afterwards use jar. Run from the commandline like this:

```
java -jar gnutraxgui.jar
```

