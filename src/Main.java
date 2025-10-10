/* Introduction to programming in Java, end semester project.
* Beginner level project, so I might end up doing things in un efficient way but still learning.
* */

// No tutorial watched, our goal is to fours on logical thinking. Game may not look identical to pac man
// concept inherited from my own github repos doing smaller projects like this over the years.
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// I am not the best programmer we could have organize things in better way but that will work for this project.
// If we are building a scalable application we might need to take care of runtime complexity and also make code efficient to reuse.
class LoadWindow extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // calling constructor from super class, clears the screen

        try {
            Image bg = ImageIO.read(new File("./textures/bg.jpeg")); // Gemini generated image


            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderWindow() {
        JFrame frame = new JFrame();
        /* Without that the code keeps running even when the UI is closed. like in the Frame class */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 300); // declaring dimensions same as the canvas of Grid
        JButton playButton = new JButton("Play");
        JButton quitButton = new JButton("Quit");

        final Dimension fixedDimension = new Dimension(120, 40);
        final Font fixedFOnt = new Font("Arial", Font.BOLD, 20);

        // this styling is nothing to worry about
        playButton.setPreferredSize(fixedDimension);
        playButton.setFont(fixedFOnt);
        playButton.setFocusPainted(false); // if true then it displays border like thing
        playButton.setContentAreaFilled(false); // if true then button is not transparent
        playButton.setBorderPainted(false); // its like outline width in CSS
        playButton.setForeground(Color.WHITE);

        quitButton.setPreferredSize(fixedDimension);
        quitButton.setFont(fixedFOnt);
        quitButton.setFocusPainted(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorderPainted(false);
        quitButton.setForeground(Color.WHITE);

        playButton.addActionListener(e -> {
            /* I am new to java but if it worked like react typescript then we could poke a state change based on button clicked and,
            * organize code properly */
            frame.dispose(); // quit this window and render other window
            Frame window = new Frame();
            window.renderWindow();
        });

        quitButton.addActionListener(e -> { // I believe this is anonymous lambda like in any other language
            System.exit(0); // exit code 0 no error, exit code 1 error basic rule of thumb
        });

        /* I find these sort of things confusing epically when being used to using markups for everything but not using
        * markup is common in android dev like kotlin kmp, I have to look dos for these things.  */
        setLayout(new GridBagLayout()); // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(playButton, gbc);
        gbc.gridy = 1;
        add(quitButton, gbc);


        frame.add(this);
        frame.setVisible(true);
    }
}


public class Main {
    public static void main(String[] args) {
        /* Here we can add windows like start game, count and score at the end */

        LoadWindow firstWindow = new LoadWindow();
        firstWindow.renderWindow();
    }
}
