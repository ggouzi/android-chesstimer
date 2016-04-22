package com.mailexample.premiere_appli;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity for loading chessTimer
 *
 * This activity is used to display timers, buttons...
 *
 * @author Gaetan GOUZI
 * @version 1.1
 * @since 1.0
 * 03/2016
 */

public class MainActivity extends Activity{

    private MalibuCountDownTimer countDownTimer1, countDownTimer2;

    private boolean timer1isRunning = false;
    private boolean timer2isRunning = false;
    private boolean premierTap = true;
    private boolean vibration;
    private boolean sonFinPartie;
    private boolean sonTour;

    private String joueur1;
    private String joueur2;

    private int nbMove1 = 0;
    private int nbMove2 = 0;

    private View boutonTemps1, boutonTemps2;

    private Button pause_resume1, pause_resume2;
    private boolean paused = false;
    private boolean terminated = false;

    private TextView temps1, temps2, temps1bis, temps2bis, increment2, increment1, nomJoueur1, nomJoueur2, move1, move2;

    private ImageView tour;

    private long timeRemaining1, timeRemaining2;

    private long startTime = 10000;
    private long increment = 2000;

    private final long interval = 47;
    private final int NOIRS = 0;
    private final int BLANCS = 1;
    private final int VIBRATION_MILLIS = 500;

    private int quiJoueApresPause = NOIRS;
    private int quiJoueAvantPause = BLANCS;
    private int quiAPerdu = NOIRS; // By default

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        joueur1 = SP.getString("player1_key", "Player 1");
        joueur2 = SP.getString("player2_key", "Player 2");

        sonTour = SP.getBoolean("beepTurn_key", true);
        sonFinPartie = SP.getBoolean("beepFinish_key", true);
        vibration = SP.getBoolean("vibrateFinish_key", true);

        String timeKey = getResources().getString(R.string.time_key);
        String incrementKey = getResources().getString(R.string.increment_key);
        String defaultIncrementValue = getResources().getString(R.string.defaultIncrementValue);
        String defaultTimeValue = getResources().getString(R.string.defaultTimeValue);

        increment = Long.parseLong(SP.getString(incrementKey, defaultIncrementValue));
        startTime = Long.parseLong(SP.getString(timeKey, defaultTimeValue));

        boutonTemps1 = (View) this.findViewById(R.id.layoutTemps1);
        boutonTemps2 = (View) this.findViewById(R.id.layoutTemps2);

        temps1 = (TextView) this.findViewById(R.id.temps1);
        temps2 = (TextView) this.findViewById(R.id.temps2);
        temps1bis = (TextView) this.findViewById(R.id.temps1bis);
        temps2bis = (TextView) this.findViewById(R.id.temps2bis);
        increment2 = (TextView) this.findViewById(R.id.increment2);
        increment1 = (TextView) this.findViewById(R.id.increment1);
        nomJoueur1 = (TextView) this.findViewById(R.id.nomJoueur1);
        nomJoueur2 = (TextView) this.findViewById(R.id.nomJoueur2);

        move1 = (TextView) this.findViewById(R.id.nbMove1);
        move2 = (TextView) this.findViewById(R.id.nbMove2);

        pause_resume1 = (Button) this.findViewById(R.id.pause1);
        pause_resume2 = (Button) this.findViewById(R.id.pause2);
        tour = (ImageView) this.findViewById(R.id.tour);

        countDownTimer1 = new MalibuCountDownTimer(startTime, interval, temps1, temps1bis);
        countDownTimer2 = new MalibuCountDownTimer(startTime, interval, temps2, temps2bis);

        // To display the right format time by default
        countDownTimer1.displayTime(startTime);
        countDownTimer2.displayTime(startTime);

        updateMoves(nbMove1, move1);
        updateMoves(nbMove2, move2);

        displayOrGone();

        pause_resume1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

        pause_resume2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

        boutonTemps1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(sonTour){playSoundTurn();}
                if(!premierTap){
                    nbMove1++;
                    increment1.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.move_blancs));
                    timeRemaining1 = countDownTimer1.addIncAndGetTime(increment);
                    updateMoves(nbMove1, move1);
                }
                else if(premierTap){
                    premierTap=!premierTap;
                }

                playBlack();
            }
        });

        boutonTemps2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(sonTour){playSoundTurn();}
                if(!premierTap){
                    nbMove2++;
                    increment2.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.move_noirs));
                    timeRemaining2 = countDownTimer2.addIncAndGetTime(increment);
                    updateMoves(nbMove2, move2);
                }

                else if(premierTap){
                    premierTap=!premierTap;
                }

                playWhite();
            }
        });
    }

    /**
     * Method that remove Player name and increment TextViews if their value is null
     */
    public void displayOrGone(){
        if(increment==0){
            increment1.setVisibility(View.GONE);
            increment2.setVisibility(View.GONE);
        }
        else{
            increment1.setText("+"+increment/1000);
            increment2.setText("+"+increment/1000);
        }

        if(joueur1==""){
            nomJoueur1.setVisibility(View.GONE);
        }
        else{
            nomJoueur1.setText(joueur1);
        }

        if(joueur2==""){
            nomJoueur2.setVisibility(View.GONE);
        }
        else{
            nomJoueur2.setText(joueur2);
        }
    }

    /**
     * Method to create the menu which will be displayed on the action bar
     * @param menu The menu to be displayed
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                doPause();
                Intent activitySettings = new Intent(MainActivity.this, Settings.class);
                startActivity(activitySettings);

                return true;

            case R.id.action_replay:
                replay();
                return true;

            case R.id.action_quit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method called when the use wants to pause the timer
     * It will check if the game is already running or if it is already paused
     * Then it will pause both timers ans change both pause button text
     */
    public void pause(){
        if(terminated){
            replay();
        }
        else {
            if (!premierTap) {
                if (paused) {
                    setPauseText(getResources().getString(R.string.pause));

                    if (quiJoueApresPause == NOIRS) {
                        playBlack();
                    } else {
                        playWhite();
                    }
                } else {

                    doPause();
                }
                paused = !paused;
            }
        }
    }

    /**
     * Method to change the current text on both pause button
     *
     * @param s String to display on the buttons
     */
    public void setPauseText(String s){
        pause_resume1.setText(s);
        pause_resume2.setText(s);
    }

    /**
     * Method to pause timers
     * It will save which player is playing now
     * and values of both timers
     * It will cancel timers and create others timers with these values on replay
     */
    public void doPause(){
        if(!paused && !premierTap){
            setPauseText(getResources().getString(R.string.resume));
            boutonTemps2.setEnabled(false);
            boutonTemps1.setEnabled(false);

            if(quiJoueAvantPause == NOIRS){
                quiJoueApresPause = NOIRS;
            }

            else if(quiJoueAvantPause == BLANCS){
                quiJoueApresPause = BLANCS;
            }

            timeRemaining2 = countDownTimer2.getTimeRemaining();
            countDownTimer2.cancel();

            timeRemaining1 = countDownTimer1.getTimeRemaining();
            countDownTimer1.cancel();
        }
    }

    /**
     * Method to reset timers, buttons and TextViews and start a new game
     */
    public void replay(){
        endGame();
        boutonTemps2.setEnabled(true);
        boutonTemps1.setEnabled(true);
        countDownTimer1.displayTime(startTime);
        countDownTimer2.displayTime(startTime);
        updateMoves(nbMove1, move1);
        updateMoves(nbMove2, move2);
        tour.setImageResource(R.drawable.down); // White should start at the beginning so Black should tap the first time
        terminated = false;
        setPauseText(getResources().getString(R.string.pause));
    }

    /**
     * Method to start the timer of the white player
     */
    public void playWhite(){
        tour.setImageResource(R.drawable.up);
        quiJoueAvantPause = BLANCS;

        if (!timer1isRunning) {
            countDownTimer1.start();
            timeRemaining2 = countDownTimer2.getTimeRemaining();
            countDownTimer2.cancel();
            timer1isRunning = true;

        } else {
            /*increment2.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.move_blancs));
            timeRemaining2 = countDownTimer2.addIncAndGetTime(increment);*/
            timeRemaining2 = countDownTimer2.getTimeRemaining();
            countDownTimer2.cancel();
            countDownTimer1 = new MalibuCountDownTimer(timeRemaining1, interval, temps1, temps1bis);
            countDownTimer1.start();
        }

        boutonTemps2.setEnabled(false);
        boutonTemps1.setEnabled(true);
    }

    /**
     * Method to start the timer of the black player
     */
    public void playBlack(){
        tour.setImageResource(R.drawable.down);
        quiJoueAvantPause = NOIRS;

        if (!timer2isRunning) {
            countDownTimer2.start();
            timeRemaining1 = countDownTimer1.getTimeRemaining();
            countDownTimer1.cancel();
            timer2isRunning = true;
        } else {
            timeRemaining1 = countDownTimer1.getTimeRemaining();
            countDownTimer1.cancel();
            countDownTimer2 = new MalibuCountDownTimer(timeRemaining2, interval, temps2, temps2bis);
            countDownTimer2.start();
        }

        boutonTemps1.setEnabled(false);
        boutonTemps2.setEnabled(true);
    }

    /**
     * Method to update TextViews representing the number of moves.
     * @param nbMove The number of moves to display
     * @param move The TextView to display the number of moves
     */
    public void updateMoves(int nbMove, TextView move){
        if(nbMove>1) {
            move.setText(nbMove + " "+ getResources().getString(R.string.moves));
        }
        else{
            move.setText(nbMove + " "+ getResources().getString(R.string.move));
        }
    }

    /**
     * Method to reset all parameters and called at the end of the game
     */
    public void endGame(){
        premierTap = true;
        paused = false;
        boutonTemps2.setEnabled(false);
        boutonTemps1.setEnabled(false);

        nbMove1 = 0;
        nbMove2 = 0;
        timer1isRunning = false;
        timer2isRunning = false;

        countDownTimer1.cancel();
        countDownTimer2.cancel();

        countDownTimer1 = new MalibuCountDownTimer(startTime, interval, temps1, temps1bis);
        countDownTimer2 = new MalibuCountDownTimer(startTime, interval, temps2, temps2bis);

        setPauseText(getResources().getString(R.string.replay));
        terminated = true;
    }

    /**
     * Method to display messages on timer TextViews at the end of the game
     * @param t TextView representing the timer which stopped first at 00:00:00 (loser player)
     */
    public void displayMessagesEndGame(TextView t){
        if(t.equals(temps1)){
            temps1.setText(getResources().getString(R.string.youLose));
            temps1bis.setText(getResources().getString(R.string.heLoses));
            temps2.setText(getResources().getString(R.string.youWin));
            temps2bis.setText(getResources().getString(R.string.heWins));
        }
        else if(t.equals(temps2)){
            temps2.setText(getResources().getString(R.string.youLose));
            temps2bis.setText(getResources().getString(R.string.heLoses));
            temps1.setText(getResources().getString(R.string.youWin));
            temps1bis.setText(getResources().getString(R.string.heWins));
        }
    }

    /**
     * Method to get one of the two timer TextView from the other
     * @param t TextView you have
     * @return TextView you want
     */
    public TextView getOtherTextView(TextView t) {
        if (t.equals(temps1)) {
            return temps2;
        }
        else{
            return temps1;
        }
    }

    /**
     * Method to get the loser player (black or white)
     * @return String (Black/White)
     */
    public String whoLost(){
        if(quiAPerdu==BLANCS){
            return getResources().getString(R.string.white);
        }
        else{
            return getResources().getString(R.string.black);
        }
    }

    /**
     * Method to open a dialog and display stats on both players
     */
    public void dialogEndGame(){
        String title = getResources().getString(R.string.endGame)+" : ";
        String message = "";
        if(quiAPerdu==NOIRS){
            title += getResources().getString(R.string.white);
            long millis = ( timeRemaining1 % 1000)/10;
            long second = ( timeRemaining1 / 1000) % 60;
            long minute = ( timeRemaining1 / (1000 * 60)) % 60;
            String time = String.format("%02d:%02d:%02d", minute, second, millis);
            message +=  getResources().getString(R.string.black)+" :\n"+
                    getResources().getString(R.string.remainingTime)+" : 00:00:00\n"+
                    getResources().getString(R.string.nbMove)+" : "+nbMove2+"\n\n"+
                    getResources().getString(R.string.white)+" : \n"+
                    getResources().getString(R.string.remainingTime)+" "+time+"\n"+
                    getResources().getString(R.string.nbMove)+" : "+nbMove1+"\n";
        }
        else{
            title += getResources().getString(R.string.black);
            long millis = ( timeRemaining2 % 1000)/10;
            long second = ( timeRemaining2 / 1000) % 60;
            long minute = ( timeRemaining2 / (1000 * 60)) % 60;
            String time = String.format("%02d:%02d:%02d", minute, second, millis);
            message +=  getResources().getString(R.string.white)+" :\n"+
                    getResources().getString(R.string.remainingTime)+" : 00:00:00\n"+
                    getResources().getString(R.string.nbMove)+" : "+nbMove1+"\n\n"+
                    getResources().getString(R.string.black)+" : \n"+
                    getResources().getString(R.string.remainingTime)+" "+time+"\n"+
                    getResources().getString(R.string.nbMove)+" : "+nbMove2+"\n";
        }
        title += " "+getResources().getString(R.string.won);
        message += "\n"+getResources().getString(R.string.whatToDo);


        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getResources().getString(R.string.no), null)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        replay();
                    }
                })
                .create()
                .show();
    }

    /**
     * Method to play a sound. This method will be called at the end each turn
     */
    public void playSoundTurn(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    /**
     * Method to do a vibration
     * @param millis The duration of vibration in milliseconds
     */
    public void vibrate(int millis){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(millis);
    }

    /**
     * Method to play a sound. This method will be called at the end of the game
     */
    public void playSoundEndGame(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 500);
    }

    // CountDownTimer class
    public class MalibuCountDownTimer extends CountDownTimer {

        private long interval, millis;
        private TextView textviewbis, textview;

        public MalibuCountDownTimer(long startTime, long interval, TextView textview, TextView textviewbis) {
            super(startTime, interval);
            this.interval = interval;
            this.millis = startTime;
            this.textview = textview;
            this.textviewbis = textviewbis;
        }
        @Override
        public void onFinish(){
            if(textview.equals(temps1)){
                quiAPerdu=BLANCS;
            }
            else{
                quiAPerdu=NOIRS;
            }


            dialogEndGame();
            displayMessagesEndGame(textview);

            if(vibration){
                vibrate(VIBRATION_MILLIS);
            }

            if(sonFinPartie){
                playSoundEndGame();
            }

            endGame();
        }

        @Override
        public void onTick(long millisUntilFinished) {

            long millis, second, minute;

            this.millis = millisUntilFinished;
            displayTime(millisUntilFinished);

        }

        public void addIncrement(long increment){
            this.millis += increment;
        }

        public void displayTime(long milliseconds){
            long millis = ( milliseconds % 1000)/10;
            long second = ( milliseconds / 1000) % 60;
            long minute = ( milliseconds / (1000 * 60)) % 60;
            String time = String.format("%02d:%02d:%02d", minute, second, millis);

            textview.setText(time);
            textviewbis.setText(time);
        }

        public long getTimeRemaining(){
            return this.millis;
        }

        public long addIncAndGetTime(long increment){
            this.millis += increment;
            displayTime (this.millis);
            return this.millis;
        }
    }
}