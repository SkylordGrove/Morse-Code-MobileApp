package com.example.morsecode;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;

public class MorseCode extends AppCompatActivity
{
    Model model;
    GameView view;
    GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new Model();
        view = new GameView(this, model);
        controller = new GameController(model, view);
        setContentView(view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        controller.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        controller.pause();
    }

    class Model
    {
        String dotsAndDashes;
        String displayText;
        int timer, spaceTimer, cTimer;
        boolean timerGoing;
        boolean spaceTimerGoing;
        boolean buttonPressed;

        Model()
        {
            dotsAndDashes = "";
            displayText = " ";
            timer = 0;
            spaceTimer = 0;
            timerGoing = false;
            spaceTimerGoing = true;
        }

        String morseDict()
        {
            switch(dotsAndDashes)
            {
                case "•-":
                    return "A";
                case "-•••":
                    return "B";
                case "-•-•":
                    return "C";
                case "-••":
                    return "D";
                case "•":
                    return "E";
                case "••-•":
                    return "F";
                case "--•":
                    return "G";
                case "••••":
                    return "H";
                case "••":
                    return "I";
                case "•---":
                    return "J";
                case "-•-":
                    return "K";
                case "•-••":
                    return "L";
                case "--":
                    return "M";
                case "-•":
                    return "N";
                case "---":
                    return "O";
                case "•--•":
                    return "P";
                case "--•-":
                    return "Q";
                case "•-•":
                    return "R";
                case "•••":
                    return "S";
                case "-":
                    return "T";
                case "••-":
                    return "U";
                case "•••-":
                    return "V";
                case "•--":
                    return "W";
                case "-••-":
                    return "X";
                case "-•--":
                    return "Y";
                case "--••":
                    return "Z";
                default:
                    return "Invalid Input";
            }
        }

        //Add functionality
        void update()
        {
            if (buttonPressed)
            {
                cTimer += 1;
            }
            if (timerGoing)
                timer += 1;
            if (spaceTimerGoing)
                spaceTimer += 1;
            if (timer > 200)
            {
                timerGoing = false;
                timer = 0;
                String result = morseDict();
                dotsAndDashes = "";
                displayText += result;
            }
            if (spaceTimer > 400 && !displayText.endsWith(" "))
            {
                displayText += " ";
            }
            return;
        }

    }

    static class GameView extends SurfaceView
    {
        SurfaceHolder ourHolder;
        Canvas canvas;
        Paint paint, paint2, paint3, paint4, paint5;
        Model model;
        GameController controller;
        TextView textBox;
        boolean dotPressed = false, dashPressed = false;
        Bitmap key;

        public GameView(Context context, Model m)
        {
            super(context);
            model = m;

            ourHolder = getHolder();
            key = BitmapFactory.decodeResource(this.getResources(), R.drawable.cheat_sheet);
            paint = new Paint();
            paint2 = new Paint();
            paint3 = new Paint();
            paint4 = new Paint();
            paint5 = new Paint(); //Input/Output Paint
            paint2.setColor(Color.BLACK);
            paint3.setColor(Color.WHITE);
            paint3.setTextSize(72.0f);
            paint4.setColor(Color.DKGRAY);
            paint5.setColor(Color.BLACK);
            paint5.setTextSize(144.0f);
            //textBox.setWidth(880);
            //textBox.setHeight(500);
            //textBox.setX(100);
            //textBox.setY(100);
            //textBox.setText("Hello!");
        }

        void setController(GameController c) { controller = c; }


        public void update()
        {
            if (!ourHolder.getSurface().isValid())
                return;
            canvas = ourHolder.lockCanvas();
            model.update();
            canvas.drawColor(Color.argb(255, 128, 200, 200));
            canvas.drawBitmap(key, 70, 70, paint);

            if (dotPressed)
                canvas.drawRect(302, 1520, 778, 1699, paint4);
            else
                canvas.drawRect(302, 1520, 778, 1699, paint2); //Dot Button

            if (dashPressed)
                canvas.drawRect(302, 1701, 778, 1900, paint4);
            else
                canvas.drawRect(302, 1701, 778, 1900, paint2); //Dash Button
            canvas.drawText(model.dotsAndDashes, 300, 1300, paint5); //Current Dot/Dash input
            canvas.drawText(model.displayText, 100, 1150, paint5);
            canvas.drawText("•", 540, 1640, paint3); //Dot Icon
            canvas.drawText("-", 540, 1820, paint3); //Dash Icon
            ourHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {
            controller.onTouchEvent(motionEvent);
            return true;
        }

    }

    static class GameController implements Runnable
    {
        volatile boolean playing;
        Thread gameThread = null;
        Model model;
        GameView view;

        GameController(Model m, GameView v)
        {
            model = m;
            view = v;
            view.setController(this);
            playing = true;
        }

        @Override
        public void run()
        {
            while(playing)
            {
                view.update();
                model.update();

                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    Log.e("Error:", "sleeping");
                    System.exit(1);
                }
            }
        }

        //Add timer for space, and make sure not to add more than 1 space.
        void onTouchEvent(MotionEvent motionEvent)
        {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // Player touched the screen
                    if(motionEvent.getX() <= 778 && motionEvent.getX() >= 302 && motionEvent.getY() <= 1900 && motionEvent.getY() >= 1701)
                    {
                        view.dashPressed = true;
                        view.model.timerGoing = true;
                        view.model.dotsAndDashes += "-";
                        view.model.spaceTimer = 0;
                    }
                    if(motionEvent.getX() <= 778 && motionEvent.getX() >= 302 && motionEvent.getY() <= 1699 && motionEvent.getY() >= 1520)
                    {
                        view.dotPressed = true;
                        view.model.timerGoing = true;
                        view.model.dotsAndDashes += ("•");
                        view.model.spaceTimer = 0;
                    }
                    break;
                case MotionEvent.ACTION_UP: // Player withdrew finger
                    /*if (view.model.cTimer > 75)
                        //view.model.dotsAndDashes += ("-");
                    else
                        view.model.dotsAndDashes += ("•");*/
                    //view.model.timerGoing = true;
                    //view.model.spaceTimer = 0;
                    view.dotPressed = false;
                    view.dashPressed = false;
                    break;
            }
        }

        // Shut down the game thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
                System.exit(1);
            }
        }

        // Restart the game thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
}
