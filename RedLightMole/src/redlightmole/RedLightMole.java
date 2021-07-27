/**
 * @ProgramTitle: RedLightMole
 * @author: Alexander Grady
 * @version: 1.0
 * @lastUpdate: 5/2/18
 * 
 * @HowToPlay: To begin the game, the player must
 * press the start button.  The player will then
 * have 30 seconds to whack as many moles possible.
 * After the time has run out the player will NOT
 * be able to score any more points. If the player
 * gets a little too excited and hits an empty hole
 * they will lose one point.  Each mole has four stages:
 * 1 Gray = Down
 * 2 Green = Up
 * 3 Red = Up
 * 4 Gray = Down
 * Up stages allow players to score points. Down stages 
 * take points away.
 */
package redlightmole;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * RedLightMole class will create the GUI
 * for the program. The class implements 
 * ActionListener to handle button functionality.
 * Class level variables:
 * @param mole will be button used to score with   
 * @param startButton will start the game
 * @param timeLeftField keeps track of the games timer
 * @param scoreField keeps track of total points scored
 * @param score should equal zero before the game begins
 * @param end will control the games run state
 */
public class RedLightMole implements ActionListener{

    private JButton[] mole;

    private JButton startButton;

    private JTextField timeLeftField, scoreField;

    private int score = 0;

    private boolean end = true;

    /**
     * constructor
     */
    public RedLightMole(){
        /**
         * new frame titled Red Light Mole
         * @param frame
         */
        JFrame frame = new JFrame("Red Light Mole");
        /**
         * frame size
         */
        frame.setSize(420, 290);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
        
        /**
         * JPanel for startButton, timeField, scoreField
         * @param pane
         */
        JPanel pane = new JPanel();
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        startButton.setFont(font);
        startButton.setOpaque(true);
        pane.add(startButton);
        
        /**
         * JLabel for timeField and scoreField
         * @param timeLeft
         * @param scoreLabel
         */
        JLabel timeLeft = new JLabel("Time Left: ");
        pane.add(timeLeft);
        timeLeftField = new JTextField(8);
        pane.add(timeLeftField);
        JLabel scoreLabel = new JLabel("Score: ");
        pane.add(scoreLabel);
        scoreField = new JTextField(8);
        scoreField.setText("" + score);
        pane.add(scoreField);
        
        /**
         * for loop used to create and set
         * mole buttons
         * @param mole
         */
        mole = new JButton[30];
        for (int i = 0; i < mole.length; i++) {
            mole[i] = new JButton("     ");
            mole[i].setFont(font);
            mole[i].setBackground(Color.LIGHT_GRAY);
            mole[i].setOpaque(true);
            mole[i].setEnabled(false);
            mole[i].addActionListener(this);
            pane.add(mole[i]);
        }
        frame.setContentPane(pane);
        frame.setVisible(true);
        
        /**
         * center-screen start 
         */
        frame.setLocationRelativeTo(null); 
    }

    /**
     * Main Method to build GUI
     *
     */
    public static void main(String[] args) {
        new RedLightMole();
    }

    /**
     * Button control method. Controls ALL 
     * button functionality for the program
     * @param e
     */
    public void actionPerformed(ActionEvent e) {     

        /**
         * Statement is meant for the startButton functionality. 
         * When the player selects start, the score and timer fields
         * are reset.  Each mole is loaded into its own thread for processing 
         * and sent to the HelperThread() thread.
         */
        if (e.getSource() == startButton && end == true) {
            score = 0;
            scoreField.setText("" + score);
            timeLeftField.setText("");
            startButton.setEnabled(false);
            Thread timer = new TimerThread(timeLeftField, startButton);
            timer.start();
            for (int i = 0; i < mole.length; i++) {
                mole[i].setEnabled(true);
                Thread moles = new HelperThread(mole[i]);
                moles.start();
            }
            
            /**
             * Else statement to control user input during the duration 
             * of the game.
             */
        } else {
            
            for (int i = 0; i < mole.length; i++) {              
                
                /**
                 * If the player selects a button that is not a standing
                 * mole, then the player will lose one point for each click.
                 */
                if(e.getSource() == mole[i] && mole[i].getBackground().equals(Color.LIGHT_GRAY) && end == false){
                    score--;
                    
                    /**
                     * If statement updates score. Used in the next
                     * if-else statement as well.
                     */
                    if (!timeLeftField.getText().equals("0")) {
                        scoreField.setText("" + score);
                    }
                }
                
                /**
                 * Checks if mole is out of the hole and the timer
                 * has not stopped.  If true the mole will return user 
                 * feedback by labeling the mole hole, changing its color, 
                 * and updating the score. The hole selected
                 * will not be able to be selected again until the 
                 * mole disappears.
                 */
                if (e.getSource() == mole[i] && mole[i].getBackground().equals(Color.GREEN) && end == false) {                   
                    mole[i].setEnabled(false);
                    mole[i].setBackground(Color.RED);
                    mole[i].setText("Ouch!"); 
                    
                    score++;
                
                    if (!timeLeftField.getText().equals("0")) {
                        scoreField.setText("" + score);
                    }
                }          
                
                /**
                 * Else if statement will allow player to select the mole if
                 * it is still in its final Up stage.
                 */
                else if (e.getSource() == mole[i] && mole[i].getBackground().equals(Color.RED) && end == false) {                                      
                    mole[i].setEnabled(false);
                    mole[i].setText("Ouch!");                     
                    score++;
                    
                    if (!timeLeftField.getText().equals("0")) {
                        scoreField.setText("" + score);
                    }
                }
            }
        }
    }              

    
    /**
     * TimerThread used for run the game's timer.  Extends the thread class.
     * @param myTimeLeftField 
     * @param myStartButton 
     * @param time, total time for game
     */
    private class TimerThread extends Thread {
        
        private JTextField myTimeLeftField;
        
        private JButton myStartButton;
        
        private int time = 30;
        
        /**
         * @param timeLeftField 
         * @param startButton 
         */
        public TimerThread(JTextField timeLeftField, JButton startButton) {
            myTimeLeftField = timeLeftField;
            myStartButton = startButton;
        }
        
        /**
         * Set the program's run status to false.
         * Timer starts and will count down by 1
         * second.
         */
        public void run() {
            end = false;
            while (time >= 0) {
                try {
                    myTimeLeftField.setText("" + time);
                    Thread.sleep(1000);
                    time--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /**
             * Set program's run state to true to end game.
             * reset score before player selects the 
             * start button again.  Wait 5 seconds to allow
             * all threads to finish processing.
             */
            try {
                end = true;
                score = 0;
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Should not happen
                e.printStackTrace();
            }
            myStartButton.setEnabled(true);
        }
    }
 
    /**
     * HelperThread used to control the status
     * of each mole during the game's run state.
     * @param myMole, used for each mole button created in 
     * the constructor
     * @param random, random number
     * @param myDownTime, used to control the amount
     * of mole threads active at one time. 
     */
    private class HelperThread extends Thread {

        private JButton myMole;

        private Random random = new Random();

        private int myDownTime = random.nextInt(25) + 2;


        /**
         * 
         * @param mole 
         */
        public HelperThread(JButton mole) {
            myMole = mole;
        }
        
        
        /**
         * run method for each thread.
         */
        public void run() {
            myMole.setText("     ");
            myMole.setBackground(Color.LIGHT_GRAY);
            
            /**
             * keeps all moles down when game starts.
             */
            try {
                Thread.sleep(myDownTime * 1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            
            /**
             * run mole threads until timer has finished.
             */
            while (!end) {            
                try {
                    /**
                     * mole phase 1
                     */
                    myMole.setBackground(Color.GREEN);
                    Thread.sleep(1500);
                    
                    /**
                     * mole phase 2
                     */
                    myMole.setBackground(Color.RED);
                    Thread.sleep(1500);                  
                    
                    /**
                     * mole back to hole
                     */
                    myMole.setText("     ");
                    myMole.setBackground(Color.LIGHT_GRAY);
                    myMole.setEnabled(true);
                    Thread.sleep(myDownTime * 1000);
                } catch (InterruptedException e) {}
                    
                }                    
            }   
        }          
    }

