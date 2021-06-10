Marupeke JavaFx Project
By candidate number 236636
README text file to accompany the project's source code

I removed classes and tests that were no longer relevant for this coursework. Leaving them could have left space for confusion of what to launch or what I modified.

When launching the program, a number between 4 and 10 must be passed as an argument that will be used as the default size for the Marupeke grid for the user to play the game. This does not mean that as soon as the program recognises the number as valid that it builds a grid for it. It will still prompt the user to choose a size on the GUI in a combo box, but the default size (the combo box's preselected size) will be visible as the number the user has inputted as an argument when launching the game.

The core requirements (displaying the grid, up-to-date display of the grid, marking the grid, congratulations and solve puzzle) are all available when in the GUI the user clicks on the "New Game" button, selects a size and difficulty and clicks on "Randomise Grid". These core requirements appear in other parts of the program that will be explained in the "Optional Extras" section of this document. Therefore, please remember that it is the "Displaying the data" and "Editing the data" sections of this document that the core requirements are explained for their appearance of when the user randomises a grid. 

Displaying the data 
After having created a MarupekeGrid object with the specified size and difficulty, a GridPane consisting of a StackPane is created. In the StackPane, a grid of buttons is created, and the image of each button is in accordance with the state of the MarupekeGrid's tile at the same coordinates. So if the MarupekeGrid tile at 1,1 is an X, the button in the StackPane at 1,1 will have the X image on it. The image's size will cover the entire button. 
When a button is pressed, the specified action is passed to the MarupekeGrid. The result produced to the MarupekeGrid is reflected on the grid of buttons through an image change.


Editing the data

Choosing a tool
On the right side, there are 3 tools used for marking that are available: X, O and Erase.  
Edit a tile
Since the grid is displayed as buttons with images on them that reflect the state of the tile, it's through pressing these buttons that the user can edit the data. Therefore, the user chooses a tool and presses a tile. Depending on the tool chosen and the edibility of the tile, the button will send the information to the MarupekeGrid and whatever the MarupekeGrid accepts as a change will be sent back to the button for it to change its image or not.
 
Marking an X or an O
If the user chooses the X tool or the O tool, when pressing a button, if the tile is uneditable (black X, black O, black Square for solid), nothing will happen. If the tile is editable, even if it's already marked with an X or an O, pressing the button with a tool selected will call the MarupekeGrid object to mark the tile at those coordinates with the state represented by the selected tool. If the mark is not illegal, the image of the button will change to a blue X or a blue O depending on the tool selected. 
Erasing
If the user chooses the Eraser tool, when pressing a button, if the tile is uneditable, nothing will happen. If the tile is editable, pressing it will call the MarupekeGrid to mark the state of the tile at those coordinates to blank. The image of that button will change to a blank square to reflect that it is now blank. 
Making an illegal move
If the user presses an editable tile button with the X tool or the O tool and makes an illegal move (3 Xs in a row, 3 Os in a row), it is the MarupekeGrid that will recognise the illegal move. It will return a false to the mark method of the layout class that calls the MarupekeGrid to mark a tile. This means the tile of the MarupekeGrid will not be marked since it's an illegal move. Therefore, the tile's button's image will not be marked with a blue X or blue O (depending on the tool selected) and will revert to a blank square. There is an added animation for an illegal move that is explained in the extra functionality section of this text file. 

Extra functionality

Cursor change
When playing a Marupeke game or building a grid, if the user chooses X, O, or Erase in the tools section on the right, his cursor changes to the appropriate image (X for X, O for O and Eraser for Erase).
Appears in various parts of the program, mostly in the playGame method (in the MarupekeLayout class) in setMouseOnClicked events for tools.

Hovering
When playing a Marupeke game or building a grid, if the user chooses X or O, if the user hovers over an editable square in the grid (blank or not) it will be replaced with a temporary grey image of an X or an O depending on the tool chosen. Even if a click would result in an illegality, the user can still hover over it and see what the grey result would be.
Appears in various parts of the program, mostly when setting up the buttons for the grid in the playGame method (in the MarupekeLayout class).

Illegality marking
If the user makes an illegal move (3 Xs in a row, 3 Os in a row), the third X or O placed will change to red and blink 3 times before disappearing. Right above the grid, the illegal move will be explicitly explained (what kind of illegal move it is and at what coordinates). Appears in the mark method (the first one) in the MarupekeLayout class.

Change theme
This is a very minimal functionality with three buttons on the menu with the names: blue, black and white. This will change the background's colour to the specified colour. Furthermore, if the background colour is changed to black, any text in the program that is black will be changed to white. If the background colour is changed to blue or white, the text colour of white text is changed to black.
Appears as a method called changeTheme that ultimately just changed the currentTheme variable to change the colour of the text when needed.

Change background image
This functionality is a button on the menu called "Import Image" that will let the user chooses an image file from his desktop and it will subsequently change the background image of the entire program to the imported image. The text colour will not change. Appears in the createMenu method in the MarupekeLayout class.

Sounds
There are sounds added throughout the project. There is background music that plays on a loop, its sound level can be adjusted from the menu with the volume slider. There are sound effects added for when a button is clicked, when an X or O is marked, when an illegal move is made and when the game is finished. The sound level of these sound effects can be adjusted as a group from the menu with the second volume slider. All sounds were found here: https://www.zapsplat.com/
Appears in various parts of the program, the background music is set in the start method. The sounds effects are played when the user clicks a button so inside the setOnMouseClicked methods.

Animations
There are logo animations, meaning that the Marupeke logo and the Candidate Number logo have a traverse animation that will make them appear from out of frame into frame to the centre of the pane. When the Solving menu is selected from the main menu, the Marupeke logo and the Candidate Number logo traverse out of frame and the Solving logo traverses into frame to the centre of the pane. Also, when the user finishes a MarupekeGrid, the individual tiles will do a full spin before the "save score and play again" options appear.
Appears in various parts of the program. The translate animations for the logos appear in the createMenu method in the MarupekeLayout class. The translate animation for the "solving" logo appears in the newSolving method in the MarupekeLayout class. The win animation appears in the gameFinished method in the MarupekeLayout class.

Solving menu
The Solving menu can be accessed through a button in the main menu. This lets the user experiment with the DFS solving algorithm from the MarupekeGrid class. The user needs to select a size, a difficulty and a number of grids and press start. The game will start solving random grids of the specified size and difficulty one by one, showing its status with a progress bar at the bottom. This is just a fun gimmick add on that can be played around with. Appears in the newSolving method in the MarupekeLayout class.

Solving
While playing a Marupeke game, the user can press on the right-hand side the button that says "solve". This will call the solve method in the MarupekeLayout class that will clone the grid as it is an attempt to solve it with the solve DFS method of the MarupekeGrid class. If that doesn't work, it will clear the grid of every marked tile and set them to blank and try to solve it again. If it still isn't successful, it will warn the user that this grid is not solvable. If it does work, it will copy every state of the cloned solved grid into the original grid, if the tile was blank in the original grid, the button's image will show up as a green X or a green O depending on the state marked.
Appears in the solve method in the MarupekeLayout class.

Hint
While playing a Marupeke game, the user can press on the right-hand side the button that says "hint". This will call the solve method in the MarupekeLayout class but with the hint boolean as true. This means it will do just like the solving method, but as soon as one available square is marked in the original grid, it stops there. This permits the method to only mark one grid for the user, effectively giving him a correct answer, so a hint towards the final solution of the grid. Appears in the solve method in the MarupekeLayout class.

High Scores
Upon finishing a Marupeke game, the user will be prompted with a text field to enter his name and a button to "save his score". This will create a new Score object with his name, the time taken to solve the grid, the size and difficulty of the grid. This Score object will be saved to a serialised file in the src folder called listscores.ser. From the main menu, there is a button called "High Scores" that when pressed will display a scene that asks for a size and a difficulty and generates a table with all the scores saved from the grids solved with the name of the player and the time taken. The table is automatically sorted by the least time taken. 
Appears in the highScores method in the MarupekeLayout class.

Difficulty
When pressing "New Game" from the main menu, the user is prompted to choose a size and difficulty for his randomised grid. The difficulty value will be passed on to the new MarupekeGrid object when generating a grid. The difficulty is reflected by how much of Xs, Os and Solids are randomly set into the grid before playing it. Easy difficulty will fill half of the grid. Medium difficulty will fill a third of the grid. Hard difficulty will fill only a third of the grid with only Solids. Appears in various parts of the program. Can be found in the newGame method of the MarupekeLayout class.

Import a grid
Outside of the src folder, there is a savedgrids folder with a MarupekeGrids txt file inside of it. This file contains numbered grids. To play one of these numbered grids, in "New Game", the user can choose "Import Grid" and he will be presented with a dropdown menu with the numbers of each grid present. He can then choose one and play it normally as if he had just played a random grid. When playing any kind of Marupeke grid, the user can press on the "Save Grid" button on the right and it will be saved as it is right now to the MarupekeGrids text file.

Build a grid
In "New Game", the user can press the "Build a Grid" button and a blank grid of the selected size from the dropdown menu will appear. Black Xs, Os and Solids can be placed onto the grid with the use of the tools on the right-hand side. Once the user is done building the grid, he can press on the "Play" button on the right and the grid will be playable with all the initially placed Xs, Os and Solids as uneditable tiles. The user-built grid is therefore playable just like a normal randomised or imported grid.
