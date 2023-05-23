package com.example.tictactoe;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements Runnable {
    boolean turn;
    private boolean myTurn;
    private boolean player1;
    int board;

    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;

    com.example.tictactoe.GameSession session;

    private final Handler handler = new Handler();

    TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictac-a0da5-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("object");

        Random rand = new Random();
        /*int sessID = rand.nextInt(256);
        board = 1;
        session = new GameSession(sessID, "bob", board);

        myRef.child("Session").setValue("test");
        myRef.child("Player1").setValue("test2");
        myRef.child("Board").setValue("test3");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GameSession session =  new GameSession((Map<String, String>) dataSnapshot.getValue());
                //Log.d(TAG, "Value is: " + map.get("Session"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });¨*/

        button1 = findViewById(R.id.topLeft);
        button2 = findViewById(R.id.topMiddle);
        button3 = findViewById(R.id.topRight);
        button4 = findViewById(R.id.middleLeft);
        button5 = findViewById(R.id.middleMiddle);
        button6 = findViewById(R.id.middleRight);
        button7 = findViewById(R.id.bottomLeft);
        button8 = findViewById(R.id.bottomMiddle);
        button9 = findViewById(R.id.bottomRight);

        tvScore = findViewById(R.id.score);

    }

public void clickButton(View v){
        Button btn = findViewById(v.getId());
        if(myTurn){
            if(player1){
            btn.setText("X");
            }
            else{
            btn.setText("O");
            }
        }
        checkGameOver();
    }

/* OLD VERSION DO NOT DELETE IT WORKS !!!!!!
    public void clickButton(View v){
        Button btn = findViewById(v.getId());
        if(btn.getText().toString().isEmpty()){
            if(turn){
                btn.setText("X");
                turn = false;
            }
            else if(!turn){
                btn.setText("O");
                turn = true;
            }
        }
        checkGameOver();
    }*/


    public boolean checkGameOver(){

            String[] buttonTexts = new String[9];

            buttonTexts[0] = button1.getText().toString();
            buttonTexts[1] = button2.getText().toString();
            buttonTexts[2] = button3.getText().toString();
            buttonTexts[3] = button4.getText().toString();
            buttonTexts[4] = button5.getText().toString();
            buttonTexts[5] = button6.getText().toString();
            buttonTexts[6] = button7.getText().toString();
            buttonTexts[7] = button8.getText().toString();
            buttonTexts[8] = button9.getText().toString();

            int[][] winCombinations = {
                    {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                    {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
                    {0, 4, 8}, {2, 4, 6} // Diagonals
            };


            for(int i = 0; i < winCombinations.length; i++){
                int[] combination = winCombinations[i];

                String button1Text = buttonTexts[combination[0]];
                String button2Text = buttonTexts[combination[1]];
                String button3Text = buttonTexts[combination[2]];

                if (!button1Text.isEmpty() && button1Text.equals(button2Text) && button2Text.equals(button3Text)) {
                    // Highlight the winning combination
                    System.out.println("!!!! GAME OVER !!!!!");
                    tvScore.setText("!!! GAME OVER !!!");
                    return true; // Game over
                }
            }

            // Check for a draw
            boolean isDraw = true;
            for (String buttonText : buttonTexts) {
                if (buttonText.isEmpty()) {
                    isDraw = false;
                    break;
                }
            }
            if (isDraw) {
                System.out.println("DRAW");
                tvScore.setText("DRAW");
                return true; // Game over
            }
            System.out.println("GAME NOT OVER KEEP PLAYING");
            tvScore.setText("LOL");
            return false; // Not game over yet
        }


    @Override
    public void run() {
        checkGameOver();
    }
}

