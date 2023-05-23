package com.example.tictactoe;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements Runnable {
    boolean turn = true;
    int turnIntForDB = 1;
    int[] board = new int[10];
    int[] initialBoard = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;

    private final Handler handler = new Handler();

    TextView tvScore;

    Random rand = new Random();

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictactoe-b8f35-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("input");

    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        button1 = findViewById(R.id.topLeft);
        button2 = findViewById(R.id.topMiddle);
        button3 = findViewById(R.id.topRight);
        button4 = findViewById(R.id.middleLeft);
        button5 = findViewById(R.id.middleMiddle);
        button6 = findViewById(R.id.middleRight);
        button7 = findViewById(R.id.bottomLeft);
        button8 = findViewById(R.id.bottomMiddle);
        button9 = findViewById(R.id.bottomRight);

        board[0] = addButtonToBoard(button1);
        board[1] = addButtonToBoard(button2);
        board[2] = addButtonToBoard(button3);
        board[3] = addButtonToBoard(button4);
        board[4] = addButtonToBoard(button5);
        board[5] = addButtonToBoard(button6);
        board[6] = addButtonToBoard(button7);
        board[7] = addButtonToBoard(button8);
        board[8] = addButtonToBoard(button9);
        board[9] = turnIntForDB;

        tvScore = findViewById(R.id.score);

        setInitialBoardValues();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the board data snapshot
                    DataSnapshot boardSnapshot = dataSnapshot.child("boardValues");

                    // Clear the existing board array
                    Arrays.fill(board, 0);

                    // Iterate through the board snapshot and populate the board array
                    for (DataSnapshot cellSnapshot : boardSnapshot.getChildren()) {
                        int index = Integer.parseInt(cellSnapshot.getKey());
                        int value = cellSnapshot.getValue(Integer.class);
                        board[index] = value;
                    }

                    // Enable or disable buttons based on board values
                    button1.setEnabled(board[0] == 0);
                    button2.setEnabled(board[1] == 0);
                    button3.setEnabled(board[2] == 0);
                    button4.setEnabled(board[3] == 0);
                    button5.setEnabled(board[4] == 0);
                    button6.setEnabled(board[5] == 0);
                    button7.setEnabled(board[6] == 0);
                    button8.setEnabled(board[7] == 0);
                    button9.setEnabled(board[8] == 0);




                    // Update button texts based on board values
                    updateButtonText();

                    Boolean turnValue = dataSnapshot.child("Turn").getValue(Boolean.class);
                    turn = turnValue;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during data retrieval
                Log.w(TAG, "Failed to retrieve board data.", databaseError.toException());
            }
        });
    }
    private void updateButtonText() {
        button1.setText(getButtonText(board[0]));
        button2.setText(getButtonText(board[1]));
        button3.setText(getButtonText(board[2]));
        button4.setText(getButtonText(board[3]));
        button5.setText(getButtonText(board[4]));
        button6.setText(getButtonText(board[5]));
        button7.setText(getButtonText(board[6]));
        button8.setText(getButtonText(board[7]));
        button9.setText(getButtonText(board[8]));
    }
    private String getButtonText(int value) {
        if (value == 1) {
            return "X";
        } else if (value == 2) {
            return "O";
        } else {
            return "";
        }
    }
    private int addButtonToBoard(Button button) {
        String buttonText = button.getText().toString();
        if (buttonText.equals("X")) {
            return 1;
        } else if (buttonText.equals("O")) {
            return 2;
        } else {
            return 0;
        }
    }
    public void setInitialBoardValues() {
        List<Integer> initialBoard = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0);

        Map<String, Object> boardData = new HashMap<>();
        boardData.put("boardValues", initialBoard);
        boardData.put("Turn", turn);

        myRef.setValue(boardData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Initial board values set successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to set initial board values", e);
                    }
                });
    }

    public void clickButton(View v) {
        DatabaseReference turnRef = database.getReference("Turn");
        turnRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean turnValue = dataSnapshot.child("Turn").getValue(Boolean.class);

                handleButtonClick(v, turnValue);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if necessary
            }
        });
    }

/*TEST*/

    private void handleButtonClick(View v, Boolean turnValue) {
        Button btn = findViewById(v.getId());
        if (btn.getText().toString().isEmpty()) {
            if(turnValue!=null){
                if (turnValue) {
                    btn.setText("X");
                    turnIntForDB -= 1;
                    turn = false;

                    disableAllButtons();

                    updateButtonInBoard(btn.getId(), 1, turnIntForDB);
                } else {
                    btn.setText("O");
                    turnIntForDB += 1;
                    turn = true;

                    disableAllButtons();

                    updateButtonInBoard(btn.getId(), 2, turnIntForDB);
                }
            }
            else {
                if (turn) {
                    btn.setText("X");
                    turnIntForDB -= 1;
                    turn = false;

                    disableAllButtons();

                    updateButtonInBoard(btn.getId(), 1, turnIntForDB);
                } else {
                    btn.setText("O");
                    turnIntForDB += 1;
                    turn = true;

                    disableAllButtons();

                    updateButtonInBoard(btn.getId(), 2, turnIntForDB);
                }
            }
        }
        checkGameOver();
    }

    private void disableAllButtons() {
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);
        button7.setEnabled(false);
        button8.setEnabled(false);
        button9.setEnabled(false);
    }


    /* DONT DELETE DOES WORK
    public void clickButton(View v){
        Button btn = findViewById(v.getId());
        if(btn.getText().toString().isEmpty()){
            if(turn){
                btn.setText("X");
                turnIntForDB -= 1;
                turn = false;

                button1.setEnabled(false);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
                button5.setEnabled(false);
                button6.setEnabled(false);
                button7.setEnabled(false);
                button8.setEnabled(false);
                button9.setEnabled(false);

                updateButtonInBoard(btn.getId(), 1,turnIntForDB);
            }
            else if(!turn){
                btn.setText("O");
                turnIntForDB += 1;

                turn = true;
                button1.setEnabled(false);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
                button5.setEnabled(false);
                button6.setEnabled(false);
                button7.setEnabled(false);
                button8.setEnabled(false);
                button9.setEnabled(false);


                updateButtonInBoard(btn.getId(), 2, turnIntForDB);
            }
        }
        checkGameOver();
    }*/

    private void updateButtonInBoard(int buttonId, int value, int x) {
        int index = -1;

        switch (buttonId) {
            case 2131231197:
                index = 0;
                break;
            case 2131231198:
                index = 1;
                break;
            case 2131231200:
                index = 2;
                break;
            case 2131230998:
                index = 3;
                break;
            case 2131230999:
                index = 4;
                break;
            case 2131231000:
                index = 5;
                break;
            case 2131230816:
                index = 6;
                break;
            case 2131230817:
                index = 7;
                break;
            case 2131230818:
                index = 8;
                break;
            default:
                break;
        }

        if (index >= 0) {
            board[index] = value;
        }

        board[9] = x;
        Map<String, Object> boardData = new HashMap<>();
        Map<String, Integer> boardValues = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            boardValues.put(String.valueOf(i), board[i]);
        }

        boardData.put("boardValues", boardValues);
        boardData.put("Turn", turn);

        DatabaseReference inputRef = database.getReference("input");
        inputRef.setValue(boardData);

// Update the "Turn" value separately
        inputRef.child("Turn").setValue(turn);

    }



    public void restart(View v){
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
        button5.setText("");
        button6.setText("");
        button7.setText("");
        button8.setText("");
        button9.setText("");

        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
        button5.setEnabled(true);
        button6.setEnabled(true);
        button7.setEnabled(true);
        button8.setEnabled(true);
        button9.setEnabled(true);

        setInitialBoardValues();
        int turnDecider = rand.nextInt(10);
        tvScore.setText("Player 1 Beginn");
        if((turnDecider%2) == 0){
            turn = false;
        }
        else {
            turn = true;
        }
    }

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

                if (!button1Text.isEmpty() && button1Text.equals(button2Text) && button2Text.equals(button3Text)) {                        button1.setEnabled(false);
                    button2.setEnabled(false);
                    button3.setEnabled(false);
                    button4.setEnabled(false);
                    button5.setEnabled(false);
                    button6.setEnabled(false);
                    button7.setEnabled(false);
                    button8.setEnabled(false);
                    button9.setEnabled(false);
                    if(!turn){
                        tvScore.setText("PLAYER 'X' WON");
                        return true; // Game over
                    }
                    else if(turn){
                        tvScore.setText("PLAYER 'O' WON");
                        return true; // Game over
                    }else{
                        return false;
                    }
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

                button1.setEnabled(false);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
                button5.setEnabled(false);
                button6.setEnabled(false);
                button7.setEnabled(false);
                button8.setEnabled(false);
                button9.setEnabled(false);
                return true; // Game over
            }
            return false; // Not game over yet
        }


    @Override
    public void run() {
        checkGameOver();
    }
}

