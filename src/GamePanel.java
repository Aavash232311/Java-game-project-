import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;

/* According to my experience making these small projects in different languages,
 * like js, python in order to draw and render something in frame we need canvas. Of course there
 * is a limitation here like let's say if we want to detect weather a complex polygon has overlapped I don't
 * think we can do it in easy way.
 *
 * Trying my best to make it readable.
 */

// the points here does not work like the xy plane in maths its like a single quadrant graph, with (0,0) at the top
class Frame {
    /* we need to return this object because when we find that we could change range within a certain point then we need to reuse that code in our
    * enemy logic. Following that "Don't repeat yourself" principle we can recycle that. I need to document this code inorder for me to understand if I went on
    * vacation for 1 month and comeback again. */
    static class VectorChangeRangeParams {
        public boolean vectorChange = false; // that's that default state
        public Point currentPoint;
        final int[] coordinate = new int[2];

        public void setCoordinateProjected(int x, int y) {
            coordinate[0] = x;
            coordinate[1] = y;
        }

        public int[] getCoordinate() {
            return this.coordinate;
        }

        // not loading the constructor because we need to initialize this object in our method
        public void setCurrentPoint(Point point) { // I don't know but that's how getter and setter should work here
            this.currentPoint = point;
        }
        public void setRange(boolean r) {
            this.vectorChange = r;
        }

        public boolean inRange() {
            return this.vectorChange;
        }
        public Point currentPoint() {
            return this.currentPoint;
        }
    }

    static class EnemyCoordinateTrack { // simple getter setter like class for each of our enemy character. I'm used to c# so I don't know if we have better way of doing these things
        public Point currentCoordinate = new Point();
        public int location;
        public int id;

        public EnemyCoordinateTrack(Point loadedDefaultPoint) {
            this.currentCoordinate = loadedDefaultPoint;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public void setEnemyCoordinate(Point p) {
            this.currentCoordinate.x = p.x;
            this.currentCoordinate.y = p.y;
        }

        public Point getEnemyCoordinate() { // gets the current enemy position
            return this.currentCoordinate;
        }

        public void setDirection(int dir) {
            this.location = dir;
        }

        public int getDirection() {
            return this.location;
        }
    }
    // static class is a nested class in java, compared to what I was used to in c#
    static class UnderFrame extends JPanel implements KeyListener, ActionListener { // interface
        public final ArrayList<String> characterTextureSeq = new ArrayList<>();
        public final ArrayList<String> opponentTextureSeq = new ArrayList<>();
        public final ArrayList<EnemyCoordinateTrack> enemyCoordinateTrack = new ArrayList<>();
        public final Random random = new Random();
        public int characterTextureIndex = 0;
        final int enemyPathX = 430; // these are the initial path, i.e. the home in which the enemy will span from, we can keep track of other coordinates when later defining the array
        final int enemyPathY = 120;
        int timeCounter = 0;
        Map<Integer, Integer> dictionaryOfOppositeTurn = new HashMap<>();
        /*
            Okay problem here is we want to turn back it loops till 5 blocks and messes things up we will fix that asap so that we wont
            have any problems.
            index: 0 -> opposite 3
            index: 1 -> opposite 2
            index: 2 -> opposite 1
            index: 3 -> opposite 0
         */

        public UnderFrame() {
            // we are loading dictionary with opposite turn for our character
            dictionaryOfOppositeTurn.put(0, 3); // ex: if current direction is 0 then the opposite direction is 3
            dictionaryOfOppositeTurn.put(1, 2);
            dictionaryOfOppositeTurn.put(2, 1);
            dictionaryOfOppositeTurn.put(3, 0);
            // okay so we need to add the character texture in sequences, we can create index and character can loop back and forth between the textures
            characterTextureSeq.add("./textures/character_open.png");
            characterTextureSeq.add("./textures/character_open_full.png");

            // here we add the textures for the opponent
            opponentTextureSeq.add("./textures/o1.png");
            opponentTextureSeq.add("./textures/o2.png");
            opponentTextureSeq.add("./textures/o3.png");
            opponentTextureSeq.add("./textures/o4.png"); // here we have 4 opponents, later we define how we are going to create logic for them.

            // let's initialize enemy span point fix coordinate from then we can add the movement logic for the enemy.
            for (int i =0; i <= opponentTextureSeq.size() - 1; i++) {
                EnemyCoordinateTrack positionTrack = new EnemyCoordinateTrack(new Point(enemyPathX, enemyPathY));
                positionTrack.setDirection(4); // by default the enemy moves to the right
                enemyCoordinateTrack.add(positionTrack); // this is the default enemy position based on the texture size i.e how many enemy is there we span each of them from a fixed coordinate.
            }

            setFocusable(true);
            requestFocusInWindow();
            addKeyListener(this); // https://www.geeksforgeeks.org/java/interfaces-in-java/ reference link about interface and implements
            /* In our pacman game we need to draw some fix boundaries, which we are going to declare some
             * fix coordinates here */
            // init delay: 20
            Timer timer = new Timer(100, this);
            timer.start();
        }

        /* Okay for rendering our enemy I have faced problem, if I add the timer in the middle then it works find for the first time
        * and then does not work in 5 sec interval for the second time. I wanted it to work like setInterval(() => {}, time) in javascript,
        * for that let's just simple unitary method and note down the interval, okay so the initial timer runs every 0.1 sec = 100ms
        * for 1 sec it needs to run 10 times, for 5 sec it needs to run 50times.
        *  */

        public final int stdSize = 10;
        public int[] vectorX = new int[]{0, stdSize, -stdSize, 0, 0};
        public int[] vectorY = new int[]{stdSize, 0, 0, -stdSize, 0};

        int[] vectorXBin = new int[] {0, 1, -1, 0, 0};
        int[] vectorYBin = new int[] {1, 0, 0, -1, 0};
        int initX = 80;
        int initY = 50;
        int direction = 1;
        int changeDirectionWithin = 5;
        ArrayList<Point> moveable = new ArrayList<>();

        Point lastGrid = new Point();
        final int stdTrim = 3; // some constant for making sure the character fits

        Point changeIn = null;
        Point changeTo = null;
        int requestedVector = -1;

        int frameCount = 1;
        /*
         * This a concept from the snake game I made, earlier in my projects
         * if we have vectorX[0] and vectorY[0] then we have no increment on x but on Y */
        @Override
        public void keyTyped(KeyEvent e) {

        }

        private double degreeToRad(double theta) {
            return theta * (180/Math.PI);
        }

        private double arcTangent(int y, int x) { // this will return in degree, since I will make it return
            return degreeToRad(Math.atan2(y, x));
        }

        @Override
        public void keyReleased(KeyEvent e) {
            /* key code: 40 = down */
            if (e.getKeyCode() == 40) {
                vectorChange(0);
            } else if (e.getKeyCode() == 37) {
                vectorChange(2);
            } else if (e.getKeyCode() == 39) {
                vectorChange(1);
            } else if (e.getKeyCode() == 38) {
                vectorChange(3);
            }
        }


        /* Whole idea here is to loop through few blocks and in each block check the turn direction and check if that point lies in our
         * array of moveable. */

        int enemyMoveCount = 0; // this counts how many enemy have moved in like 5 second interval


        private void movementLogic(Grid grid, Graphics g) {
            ArrayList<Point> moveable = grid.getGrid();
            this.moveable = moveable;
            /* Okay from my previous project experience: if this was running in 1FPS then,
             * what would be is the position of our character would perfectly align with our grid.
             * Since it's running in a high frame rate the increment of rate is a bit smooth and once in a while
             * the coordinates of our character with the items in our array list.
             */
            Graphics2D g2d = (((Graphics2D) g));
            AffineTransform oldTransform = g2d.getTransform();

            g.setColor(Color.BLUE);

            if (changeIn != null && changeTo != null) {
                /* Here this init x is the coordinate of the near end of the block. */
                if (changeIn.x == initX && changeIn.y == initY) {
                    /* now we change the directions  */
                    direction = requestedVector;
                    requestedVector = -1;
                    changeIn = null;
                    changeTo = null;
                }
            }

            /* like in the original pac man game we might want to check if the pac man is in the what's called
             * an edge or on the border box we want it to stop so let's check that in the same way we did when adding movement logic. */

            if ((!moveable.contains(new Point(initX + vectorX[direction], initY + vectorY[direction])))) {
                /* 30, 80 one escape portal */
                boolean firstEscapeThreshold = new Point(30, 80).equals(new Point(initX, initY));
                boolean secondEscapePoint = new Point(530, 120).equals(new Point(initX, initY));
                if (firstEscapeThreshold) {
                    /* we know the fact that we are in the point where we need to respawn the character back of the maize,
                     * point pre-defined 530, 120  */
                    direction = 2;
                    initX = 530;
                    initY = 120;
                } else if (secondEscapePoint) {
                    direction = 1;
                    initX = 30; initY = 80;
                } else {
                    direction = 4;
                }
            }
            /* Here we want to create a point from which we can send the character to the other end like a portal,
             * for that we need to predefine such grid and teleport the characters. If the character is going towards that point
             * that we define in a direction variable we can send that charter to the other end. Normally we stop at the edge but in the
             * predefined case we can teleport the character through the portal */

            /* Here what we can do is flip the character image based on the direction we are going, this
             * is significant only in the main character, since it's just a circle,
             * we can use the arc tangent formula */

            // this block for flipping back and forth between the textures so that it looks like the character is making movements.
            try {
                Image main_character = ImageIO.read(new File(characterTextureSeq.get(characterTextureIndex))); // Gemini generated image
                new Timer(800, e -> { // this thing runs every 2000ms i.e. 2 seconds
                    characterTextureIndex++;
                    if (characterTextureIndex > characterTextureSeq.size() - 1) {
                        characterTextureIndex = 0;
                    }
                }).start();

                /*
                 * thera = arc tan2(dy,dx), the result is in rad, so we need to explicitly convert to degree,
                 * this concept is something new, so I will label it here okay, so staring off with how we define the movement logic in our program,
                 * THE INCREMENT AND DECREMENT OF THE CHARACTER ON THE RESPECTED AXIS DEPENDS ON THE TWO, VECTOR-X AND VECTOR-Y variables.
                 * v = (1,0) -> theta = 0 rad -> 0 deg (right)
                 * v = (0, 1) -> theta = pi/2 -> 90 degree
                 * v = (-1, 0) -> theta = pi -> 180 (left)
                 * v = (0, -1) -> theta = -pi/2 -> -90 or 270 okay and the way we move direction in the same is through "Std size"
                 * now what we can do is convert that into 0, or 1. If something is greater than 0 then 1. because of that we are getting incorrect transformation,
                 * so I simply created another array to reduce runtime and code complexity, okay to woks little better,
                 *
                 * Problem: when switching between the textures there is a glitch weird, okay, so I tried to use only one texture, so I am sure
                 * that it's not the problem with the coordinates, also to be noted that the transformation is not perfectly transformed.
                 * The problem is the way I downloaded the textures it's not symmetric alr neither is the switching texture
                */

                double angle = arcTangent(vectorYBin[direction], vectorXBin[direction]); // output in degrees
                g2d.rotate(angle, initX + (stdSize + stdTrim)/2.0, initY + (stdSize + stdTrim)/2.0); // now that we know how much to rotate can rotate it, we need to trin it alr
                // if we were to do it the transformation from scratch without g2d then more math, simple g2d is used for transforming takes angle and two position as an args, for 3d we have some other complex concepts like orthographic projection.
                g.drawImage(main_character, initX, initY, stdSize + stdTrim, stdSize + stdTrim, this); // bad of me I trimmed based on visuals, also written in sucha way that it does not flies to the moon
                g2d.setTransform(oldTransform); // when we transform using the arc tangent 2 function it glitches so we are re-storing the old transform state so it does not affect our opponent character
                /* We can span all the opponent on 430, 120 */
                g.setColor(Color.RED);

                int enemyRenderLoopCount = 0;
                for (String enemyPath: opponentTextureSeq) { // from this loop we render all the enemy
                    Image imageEnemy = ImageIO.read(new File(enemyPath));
                    // we need to make this move
                    EnemyCoordinateTrack currentEnemy = enemyCoordinateTrack.get(enemyRenderLoopCount);
                    Point currentEnemyPoint = currentEnemy.getEnemyCoordinate();

                    g.drawImage(imageEnemy,currentEnemyPoint.x, currentEnemyPoint.y, stdSize + stdTrim, stdSize + stdTrim, this); // this is the image of our opponent, in the game
                    enemyRenderLoopCount++;
                } // here we have initialized the default state of our character
                // todo: keep track of coordinates of all the characters alr, and then design a movement logic
                /* It's kind of tricky the way we want to add the movement logic here,
                *  First, when the character moves after few seconds other should move i.e. there should be spacing between the characters.
                * Then we added that logic when we want to turn the character that logic should be there every second and we randomly pick the value where the character wants to go
                * THe speed of the opponent should be little slower so that we can catch them and eliminate them.
                *
                * Let make one character move and after 4 sec interval another character will move.  */

                // okay everything apart we want the character to move in certain second interval
                // so what we want is we want our enemy to move one after the other
                if (timeCounter % 50 == 0 && enemyMoveCount <= opponentTextureSeq.size() - 1) {
                    // okay so timeCounter gets incremented every 100ms at 1000ms its 1 okay if we divide by 5 when it becomes after like 5000ms then we get the reminder 0
                    EnemyCoordinateTrack currentEnemy = enemyCoordinateTrack.get(enemyMoveCount); // since that block runs in some interval for some amount of time we can only change the boolean here and then in the main frame where the game runs in full frame we can make the character move
                    currentEnemy.setDirection(1); // if you want to save memory then simple change the direction to 4 instead of bool
                    enemyMoveCount++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            g.drawRect(initX, initY, stdSize, stdSize); // this is the box of the character.
            if (moveable.contains(new Point(initX, initY))) {
                lastGrid = new Point(initX, initY);
            }
            initX += vectorX[direction];
            initY += vectorY[direction];

            enemyMovementLogic(g); // for better readability
            frameCount++;
        }

        // this is for generating random values for the enemy to move excluding something
        public int generateRandomValuesExcluding(int ex) {
            int randomVal = random.nextInt(0, 3);
            if (randomVal == ex) {
                randomVal = generateRandomValuesExcluding(ex);
            }
            return randomVal;
        }

        private void enemyMovementLogic(Graphics g) {

            for (EnemyCoordinateTrack currentEnemy: enemyCoordinateTrack) {
                if (currentEnemy.getDirection() != 4) { // why 4 because it increments by 0

                    // this is for updating the coordinate
                    Point currentPoint = currentEnemy.getEnemyCoordinate();
                    /* Here is when things get little complicated,
                     * How are we going to make the enemy move,
                     * First thing is we will check if we can move, using the method "vectorChangeRange()",
                     * That method will hold properties like how many "turning" points are in range from which we can change the range.
                     * If it's a list then we can make choices right.
                     *  ## There should be an option for character not to change range as well, how many ways the "ghost" can do it may run back go left right anything.
                     *  ## When are we going to decide when the character can change the range when we have "turning points"
                     *  ## We had that movement problem with our character where there is delay in input let's make that intentional and fix that problem here.
                     *
                     * Sudo code:
                     *    First get the characters current block. And for each block that the character "ghost character" goes check left, right, up and down.
                     *    Then add choices in an array, from there which we can shuffle and change the direction of our character " ghost character"
                     *   */
                    enemyMovementChoices(currentEnemy, g); // it's going to check for possible options.
                    currentPoint.x += vectorX[currentEnemy.getDirection()];
                    currentPoint.y += vectorY[currentEnemy.getDirection()];
                    // let's update that
                    currentEnemy.setEnemyCoordinate(currentPoint);
                }
            }
        }

        private void enemyMovementChoices(EnemyCoordinateTrack currentEnemy, Graphics g) {
            Point currentEnemyPosition = currentEnemy.getEnemyCoordinate();
            int currentEnemyDirection = currentEnemy.getDirection();
            // no need to external loop, it will be just increate the complexity of our code.
            // since we won't let the enemy turn where it's not allowed to turn.

            int possibleTurnOptionsRange = vectorX.length - 2; // the last one index means simply stop
            int[] movementChoiceArray = new int[] {-1, -1, -1, -1};
            // in the above array if the enemy cannot move then its initial state will be -1

            // straight forward this loop checks in all the possible direction if our character can go there or not
            for (int i = 0; i <= possibleTurnOptionsRange; i++) {
                int requestedX = currentEnemyPosition.x + vectorX[i]; // remember, vectorX and vectorY are the list of grid from which we increment or decrement
                int requestedY = currentEnemyPosition.y + vectorY[i];
                Point requestedPoint = new Point(requestedX, requestedY);

                if (moveable.contains(requestedPoint)) {
                    movementChoiceArray[i] = i;
                }
            }
            /* Here the "movementChoiceArray" is the array which results "direction integer" in the corresponding index in which our character can move.
            * Now with the few set of restrictions we need to make it go on random path.  */
            int changeDirection = currentEnemyDirection;
            // For now let's see the options in which our "enemy" character can go except the current direction.
            int oppositeToTheCurrentDirection = dictionaryOfOppositeTurn.get(currentEnemyDirection);
            // for finding where else can our character turn expect going the opposite way and going forward
            int[] choicesForEnemyExcept = Arrays.stream(movementChoiceArray).filter( x -> x != currentEnemyDirection && x != oppositeToTheCurrentDirection).toArray();
            // to check for that where else we not to sort out where else not i.e. "-1"
            int[] choicesLeft = Arrays.stream(choicesForEnemyExcept).filter(x -> x != -1).toArray();
            if (choicesLeft.length > 0) {
                // if we do have choices then
                int randomIndex = random.nextInt(choicesLeft.length);
                changeDirection = choicesLeft[randomIndex];
            }
            /*
                # Here are few set of rules that enemy will follow when moving.
                # It's going to turn back if it cannot move in current direction it's moving and no choices are left.
            */
            System.out.println(Arrays.toString(choicesLeft));
//            boolean allAreMinusOne = Arrays.stream(choicesForEnemyExcept)  // we could do this manually for introductory course project but code is getting long
//                    .allMatch(x -> x == -1);                           // and we might make mistake in small things.
            currentEnemy.setDirection(changeDirection);
        }

        /*  | Index     | VectorX     |  VectorY    | Direction     |
            | --------- | ----------- | ----------- | ------------- |
            | 0         | 0           | 2           | Up            |
            | 1         | 1           | 0           | Right         |
            | 2         | 1           | 0           | Left          |
            | 3         | 0           | 1           | Down          |
         */

        /* another problem is okay the way we draw the grid, it's by connecting the blocks right,
         * and if we loop and connect when changing the direction the character might move on the far end
         * of the turn because the coordinate of the later coordinate might come first. We need to figure out a
         * way to check, if we found the point did we found the nearest point?
         *
         * if we did it with new method it increases the run time complexity and stuff because we would need to loop again and again */

        private VectorChangeRangeParams vectorChangeRange(int vectorChangeMag) { // we might want to reuse this because we need to use this logic in our enemy code, which might run every frame to check and randomly generate a change.
            // this vector change mag is generated randomly with natural length of array - 1, because the last index of array is simply to stop
            int count = 0;
            int projectedX = initX;
            int projectedY = initY;

            int reqX;
            int reqY;
            VectorChangeRangeParams changeP = new VectorChangeRangeParams();

            while ((count) <= changeDirectionWithin) {
                reqX = projectedX + vectorX[vectorChangeMag]; // proj x (current coordinate) and other part is what if we change direction.
                reqY = projectedY + vectorY[vectorChangeMag];
                Point currentPoint = new Point(reqX, reqY);
                if (moveable.contains(currentPoint)) {
                    /* Sometimes we might be able to turn in different points within some blocks apart that are close.
                     * In that case we store and find the minimum so we can take the nearest exit. */
                    changeP.setRange(true);
                    changeP.setCurrentPoint(currentPoint);
                    break; // no need to go further
                }

                // here we move forward
                projectedX += vectorX[direction];
                projectedY += vectorY[direction];
                count++;
            }
            changeP.setCoordinateProjected(projectedX, projectedY);
            return changeP;
        }

        private void vectorChange(int vectorChangeMag) {
            /*
            The idea here is to check if withing few blocks of request direction change the item lies in the array,
            since the user can't directly click in perfect time.

            And, how we do it, in the similar way we drawn blocks in our grid class.java file. Since we didn't use some complex
            data structures to relate and connect a maize. We could have done that in fact. I have seen course in Harvard using maize
            to visualize search algorithms like BFS and DFS in a graph.
            *  */

            // if going to the opposite direction or turning around we don't want to see for the 5 blocks.
            if (direction == 0 && vectorChangeMag == 3) {
                changeDirectionWithin = 0; // meaning turn around is instant
            } else if (direction == 1 && vectorChangeMag == 2) {
                changeDirectionWithin = 0;
            } else if (direction == 2 && vectorChangeMag == 1) {
                changeDirectionWithin = 0;
            } else if (direction == 3 && vectorChangeMag == 0) {
                changeDirectionWithin = 0;
            }

            if (vectorChangeMag == direction) return; // if in same direction then we don't want to do any operation

            /* What we can do is we can like round of the block he is in. So that the movement is accurate, when he wants to move.
             * We can try that. we keep track of last coordinate in the gird and calculate from there.  */

            ArrayList<Point> turnPointInRange = new ArrayList<>();
            ArrayList<Point> changeDirectionInRange = new ArrayList<>();

            changeDirectionWithin = 5; // reset that
            VectorChangeRangeParams vectorChangeParams = vectorChangeRange(vectorChangeMag);
            if (vectorChangeParams.inRange()) {
                Point currentPoint = vectorChangeParams.currentPoint();

                turnPointInRange.add(currentPoint);
                changeDirectionInRange.add(new Point(vectorChangeParams.getCoordinate()[0], vectorChangeParams.getCoordinate()[1]));
            }

            /* Here we are getting the list of points from where we can turn, why we are doing that can be addressed in the
             * line, 108,
             * given that the problem is addressed.
             * we might want to, calculate the cost, if the cost is less meaning if it takes less time to turn in a point
             * even if its listed far in the array we need to turn. we might want to check like how many blocks we might need to reach
             * the particular point  */
            ArrayList<Integer> costArray = new ArrayList<>();

            for (Point turnPoint : turnPointInRange) {
                // current position of the character, we need to find the nearest edge wrt to the current position.
                costArray.add(blockCost(turnPoint, vectorChangeMag));
            }
            if (costArray.isEmpty()) return;
            int minValue = Collections.min((costArray));
            int indexOfMin = costArray.indexOf(minValue);
            changeTo = turnPointInRange.get(indexOfMin); // both the index is same since the size of List in same in both case
            changeIn = changeDirectionInRange.get((indexOfMin));
            requestedVector = vectorChangeMag;
            // here lets find the index with the minimum cost and do, we won't want to manually write the code to do that since it will increase the line of code.

        }

        private int blockCost(Point finalPoint, int requestedMagnitude) {
            /* The whole point is here to find the distance from initial point that the character is currently in to the turn point. */
            int currentCharacterPositionX = initX;
            int currentCharacterPositionY = initY;

            int count = 0;

            while (true) {

                Point checkPoint = new Point(currentCharacterPositionX + vectorX[requestedMagnitude], currentCharacterPositionY + vectorY[requestedMagnitude]);
                if (finalPoint.equals(checkPoint)) {
                    break;
                }

                // in the same way, maybe I am not thinking a bit broad to clean this up maybe we could do that up but its tricky
                // feels like im increasing the run time complexity but the array list itself is small since the points that lies in the gird are already sorted above.
                currentCharacterPositionX += vectorX[direction];
                currentCharacterPositionY += vectorY[direction];
                count++;
            }
            return count;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // calling the constructor from the super class, for cleaning content in the canvas

            Graphics2D g2 = (Graphics2D) g; // graphics 2d have more features like transformation and more. So we are converting it.
            g2.setStroke(new BasicStroke(5)); // thickness of line
            g2.setColor(Color.BLUE);
            setBackground(Color.black);

            Grid maizeGrid = new Grid(g2);
            maizeGrid.buildGrid(); // after we build the grid then the coordinates get loaded here.
            movementLogic(maizeGrid, g);
            if (enemyMoveCount <= opponentTextureSeq.size() - 1) { // no need to load memory, if all enemy have moved from their default place
                timeCounter++; // here that count++ is done for the enemy span sequence
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            /* key code, like in every language,
             * we need to have key code assigned with particular key
             * w = 87, s = 83, a = 65, d = 68,
             *int[] vectorX = new int[] {0, 1, -1, 0, 0};
              int[] vectorY = new int[] {1, 0, 0, -1, 0};
              */
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            /* I think we need to add the collision logic here because I don't think we could use those array's value
             * in other class it's fine though should be a small project like: https://github.com/Aavash232311/SNAKE-GAME-JS/blob/main/index.js
            /*
                    int[] vectorX = new int[] {0, 1, -1, 0, 0};
                    int[] vectorY = new int[] {1, 0, 0, -1, 0};

             */
            repaint();
        }
    }


    public void renderWindow() {
        JFrame frame = new JFrame();
        /* Without that the code keeps running even when the UI is closed */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 300); // declaring dimensions
        frame.setResizable(false); // we don't want to deal with resizing, it will make it way complex

        UnderFrame canvas = new UnderFrame();
        frame.add(canvas);
        frame.setVisible(true);
    }
}