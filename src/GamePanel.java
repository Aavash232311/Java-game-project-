import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
/* According to my experience making these small projects in different languages,
 * like js, python in order to draw and render something in frame we need canvas. Of course there
 * is a limitation here like let's say if we want to detect weather a complex polygon has overlapped I don't
 * think we can do it in easy way.
 *
 * Trying my best to make it readable.
 */

// the points here does not work like the xy plane in maths its like a single quadrant graph, with (0,0) at the top
class Frame {
    // static class is a nested class in java, compared to what I was used to in c#
    static class UnderFrame extends JPanel implements KeyListener, ActionListener { // interface

        public UnderFrame() {
            setFocusable(true);
            requestFocusInWindow();
            addKeyListener(this); // https://www.geeksforgeeks.org/java/interfaces-in-java/ reference link about interface and implements
            /* In our pacman game we need to draw some fix boundaries, which we are going to declare some
             * fix coordinates here */
            // init delay: 20
            Timer timer = new Timer(100, this);
            timer.start();
        }

        final int stdSize = 10;
        int[] vectorX = new int[]{0, stdSize, -stdSize, 0, 0};
        int[] vectorY = new int[]{stdSize, 0, 0, -stdSize, 0};
        int initX = 80;
        int initY = 50;
        int direction = 1;
        int changeDirectionWithin = 5;
        ArrayList<Point> moveable = new ArrayList<>();
        ArrayList<Point> check = new ArrayList<>();

        Point lastGrid = new Point();

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
        private void movementLogic(Grid grid, Graphics g) {
            ArrayList<Point> moveable = grid.getGrid();
            this.moveable = moveable;
            /* Okay from my previous project experience: if this was running in 1FPS then,
             * what would be is the position of our character would perfectly align with our grid.
             * Since it's running in a high frame rate the increment of rate is a bit smooth and once in a while
             * the coordinates of our character with the items in our array list.
             */


            g.setColor(Color.YELLOW);

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
            g.drawRect(initX, initY, stdSize, stdSize); // this is the box of the character.
            if (moveable.contains(new Point(initX, initY))) {
                lastGrid = new Point(initX, initY);
            }

            initX += vectorX[direction];
            initY += vectorY[direction];
            frameCount++;
        }

        /* another problem is okay the way we draw the grid, it's by connecting the blocks right,
         * and if we loop and connect when changing the direction the character might move on the far end
         * of the turn because the coordinate of the later coordinate might come first. We need to figure out a
         * way to check, if we found the point did we found the nearest point?
         *
         * if we did it with new method it increases the run time complexity and stuff because we would need to loop again and again */

        private void vectorChange(int vectorChangeMag) {
            /*
            The idea here is to check if withing few blocks of request direction change the item lies in the array,
            since the user can't directly click in perfect time.

            And, how we do it, in the similar way we drawn blocks in our grid class.java file. Since we didin't use some complex
            data structures to relate and connect a maize. We could have done that in fact. I have seen course in Harvard using maize
            to visualize search algorithms like BFS and DFS in a graph.

            Okay problem here is we want to turn back it loops till 5 blocks and messes things up we will fix that asap so that we wont
            have any problems.
            index: 0 -> opposite 3
            index: 1 -> opposite 2
            index: 2 -> opposite 1
            index: 3 -> opposite 0

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

            int projectedX = initX;
            int projectedY = initY;

            int reqX;
            int reqY;

            int count = 0;
            ArrayList<Point> turnPointInRange = new ArrayList<>();
            ArrayList<Point> changeDirectionInRange = new ArrayList<>();

            while ((count) <= changeDirectionWithin) {
                /* we want to continue movement in current direction and check for movement. */
                /* and now check if the block in which he wants to go up or down, or left or right lies in the maize,
                 * and then when the character lies in the edge we can change the direction. */

                // here we check the direction which the user is trying to move contains in a predefined gird
                reqX = projectedX + vectorX[vectorChangeMag]; // proj x (current coordinate) and other part is what if we change direction.
                reqY = projectedY + vectorY[vectorChangeMag];

                Point currentPoint = new Point(reqX, reqY); // this is the point if it was to turn
                check.add(currentPoint);
                if (moveable.contains(currentPoint)) {
                    /* Sometimes we might be able to turn in different points within some blocks apart that are close.
                     * In that case we store and find the minimum so we can take the nearest exit. */
                    if (!(turnPointInRange.contains(currentPoint))) {
                        turnPointInRange.add(currentPoint);
                        changeDirectionInRange.add(new Point(projectedX, projectedY));
                    }
                }

                // here we move forward
                projectedX += vectorX[direction];
                projectedY += vectorY[direction];
                count++;
            }
            changeDirectionWithin = 5; // reset that

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