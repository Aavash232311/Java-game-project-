import java.awt.*;
import java.util.ArrayList;

/* All the messy code we want to store here regarding the grid operation */

/*
*   Let's find a way to draw a grid in effective way, in that grid our character can travel.
* Before moving any directions our character can check if there is coordinate associated with it in
* the array list and move accordingly.
*
* And then we can draw our maize in different way.
*
* First create a method, which can draw those sizes effect.
*  */
public class Grid {
    private final Graphics g2;
    Grid(Graphics g2) { // parameterized constructor
        this.g2 = g2;
    }

    private final ArrayList<Point> moveable = new ArrayList<>(); // using point because it will be easy to sort later on
    // creating this method to automate the hard code needed to draw the grid in which the pac man moves.
    private int[] drawGrid(int x, int y, int moveIndex, int repeat) {
        final int size = 10;
        int[] vectorX = new int[] {0, size, -size, 0};
        int[] vectorY = new int[] {size, 0, 0, -size};
        int drawX = x;
        int drawY = y;

        for (int i = 1; i <= repeat; i++) {
            if (!(moveable.contains(new Point(drawX, drawY)))) {
                g2.drawRect(drawX, drawY, size, size);
                moveable.add(new Point(drawX, drawY));
            }
            drawX += vectorX[moveIndex];
            drawY += vectorY[moveIndex];
        }
        return new int[] {drawX, drawY};
    }
    /* Just for the reference so that I don't have to look back and forth again and again */
    public void buildGrid() {

        int[] line1 = drawGrid(80, 50, 1, 45); // horizontal line top
        int[] line2 = drawGrid(80, 50, 0, 3);
        int[] line3 = drawGrid(line1[0], 50, 0, 3);
        int[] line4 = drawGrid(line3[0], line3[1], 2, 14);
        int[] line5 = drawGrid(line2[0], line2[1], 1, 4);
        int[] line6 = drawGrid(line4[0], line4[1], 3, 4);
        int[] line7 = drawGrid(line4[0], line4[1], 0, 8);
        int[] line8 = drawGrid(line7[0], line7[1], 1, 8);
        int[] line9 = drawGrid(line8[0], line8[1], 3, 4);

        int[] line10 = drawGrid(line9[0], line9[1], 2, 5);
        int[] line11 = drawGrid(line9[0], line9[1], 3, 4);

        int[] line12 = drawGrid(line9[0], line9[1], 1, 7);

        int[] line13 = drawGrid(line4[0], line4[1], 2, 20);
        int[] line14 = drawGrid(line13[0], line13[1], 3, 4);
        int[] line15 = drawGrid(line13[0], line13[1], 2, 10);
        int[] line16 = drawGrid(line15[0], line15[1], 2, 7);
        int[] line17 = drawGrid(line15[0] - 10, line15[1], 0, 8); // hard code which I hate, should I have commented this line referees to what in a map
        int[] line18 = drawGrid(line17[0], line17[1], 1, 11); // horizontal line middle
        int[] line19 = drawGrid(line18[0], line18[1], 3, 8);
        int[] line20 = drawGrid(line18[0], line18[1], 1, 5);
        int[] line21 = drawGrid(line20[0], line20[1], 3, 4); // middle slice
        int[] line22 = drawGrid(line21[0], line21[1], 2, 1);
        int[] line23 = drawGrid(line21[0], line21[1], 2, 12);
        int[] line24 = drawGrid(line21[0], line21[1], 3, 5);
        int[] line25 = drawGrid(line23[0], line23[1], 3, 4);
        int[] line26 = drawGrid(line21[0], line21[1], 1, 8);
        int[] line27 = drawGrid(line26[0], line26[1], 0, 4);
        int[] line28 = drawGrid(line27[0], line27[1], 1, 8);
        /* Before we draw and realize we made smth wrong we will write here the movement logic fast may in other java file */
    }

    public ArrayList<Point> getGrid() {
        return moveable;
    }

}
