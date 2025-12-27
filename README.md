# Java Pac-Man Project

A classic 2D arcade game implementation built from scratch using Java. This project focuses on object-oriented design, real-time input handling, and grid-based collision logic. `CSC 115`.

## Project Overview

The goal of this project was to recreate the core mechanics of Pac-Man to better understand the game development lifecycle, specifically focusing on the "Game Loop" and state management. As a freshman at Lyon College, I used this project to move beyond basic console applications and into GUI-based software development.

## Technical Stack

* **Language:** Java
* **Library:** Java Swing & AWT (Abstract Window Toolkit)
* **Version Control:** Git

## Key Features

* **Grid-Based Movement:** Precise movement logic ensuring entities stay within the maze boundaries.
* **Dynamic Collision Detection:** Real-time calculation of intersections between Pac-Man, the environment (walls/pellets), and Ghost entities.
* **Ghost AI:** Automated movement logic for enemy entities to provide a challenging gameplay experience.
* **Score Management:** System to track and render the player's score based on objective completion.
* **Game State Control:** Managed transitions between 'Ready', 'Playing', and 'Game Over' states.

## Technical Implementation Details

### The Game Loop
The game utilizes a centralized timer to refresh the UI at a consistent frame rate. This ensures that movement and animations remain smooth regardless of hardware speed.

### Object-Oriented Structure
* `GamePanel.java`: All the logic of game.
* `Grid.java`: Pre defined grid in which character and ghost travels.
* `Main.java`: Entry point and interface of our code.

### Key Logic: Collision & Movement
The movement is governed by a 2D array representing the maze. Before any move is executed, the system checks the array index corresponding to the next coordinate to prevent wall clipping.

## How to Run

1. Clone this repository:
   ```bash
   git clone [https://github.com/Aavash232311/Java-game-project-.git](https://github.com/Aavash232311/Java-game-project-.git)
