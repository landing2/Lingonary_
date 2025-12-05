package com.example.lingonary_;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.lingonary_.models.Word;
import com.example.lingonary_.database.WordDatabase;
import com.example.lingonary_.database.WordDao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Word> wordList;
    private List<Word> quizList;
    private Word currentWord;
    private int currentQuestionIndex = 0;
    private TextView titleVamos;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnContinue;
    private ProgressBar progressBar;
    private List<Button> optionButtons;
    // Database
    private WordDatabase db;
    private WordDao wordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizactivity_main); // Ensure matches XML filename

        //1.Initialize DB
        db = WordDatabase.getInstance(this);
        wordDao = db.wordDao();
        SharedPreferences sharedPreferences = getSharedPreferences("lingonary_prefs", Context.MODE_PRIVATE);
        int quizLength = sharedPreferences.getInt("quiz_length", 10);
        wordList = getIntent().getParcelableArrayListExtra("wordList");
        if (wordList != null) {
            List<Word> availableWords = new ArrayList<>(wordList);
            Collections.shuffle(availableWords);
            quizList = availableWords.stream().limit(quizLength).collect(Collectors.toList());
        }
        titleVamos = findViewById(R.id.titleVamos);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnContinue = findViewById(R.id.btnContinue);
        progressBar = findViewById(R.id.progressBar);
        ImageView backArrow = findViewById(R.id.backArrow);
        optionButtons = new ArrayList<>();
        optionButtons.add(btnOption1);
        optionButtons.add(btnOption2);
        optionButtons.add(btnOption3);
        optionButtons.add(btnOption4);

        // Set a consistent line count for all buttons to ensure uniform height
        for (Button button : optionButtons) {
            button.setLines(2); // Reserve space for 2 lines of text
            button.setOnClickListener(this);
        }

        btnContinue.setOnClickListener(v -> {
            currentQuestionIndex++;
            loadNewQuestion();
        });

        backArrow.setOnClickListener(v -> finish());
        if (quizList != null && !quizList.isEmpty()) {
            progressBar.setMax(quizList.size());
            loadNewQuestion();
        } else {
            finish();
        }
    }
    private void loadNewQuestion() {
        if (currentQuestionIndex >= quizList.size()) {
            finish();
            return;
        }

        resetButtonStyles();
        currentWord = quizList.get(currentQuestionIndex);

        // 2. Mark as Seen and Save immediately
        currentWord.setHasBeenInQuiz(true);
        saveWordProgress(currentWord);

        titleVamos.setText(currentWord.getLearning());
        progressBar.setProgress(currentQuestionIndex + 1);
        List<Word> options = new ArrayList<>(wordList);
        options.remove(currentWord);
        Collections.shuffle(options);
        Collections.shuffle(optionButtons);
        optionButtons.get(0).setText(currentWord.getNativeLang());
        for (int i = 1; i < optionButtons.size(); i++) {
            optionButtons.get(i).setText(options.get(i - 1).getNativeLang());
        }
    }

    @Override
    public void onClick(View v) {
        Button selectedButton = (Button) v;
        String selectedAnswer = selectedButton.getText().toString();

        if (selectedAnswer.equals(currentWord.getNativeLang())) {
            // Correct answer we have: Green+increase score
            selectedButton.setBackground(ContextCompat.getDrawable(this, R.drawable.quiz_button_correct));
            currentWord.incrementTimesCorrect();
        } else {
            // Incorrect answser we have: Red+decrease score
            selectedButton.setBackground(ContextCompat.getDrawable(this, R.drawable.quiz_button_incorrect));
            int currentScore = currentWord.getTimesCorrect();
            if (currentScore > 0) {
                currentWord.setTimesCorrect(currentScore - 1);
            }

            // Highlight correct answser
            for (Button button : optionButtons) {
                if (button.getText().toString().equals(currentWord.getNativeLang())) {
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.quiz_button_correct));
                    break;
                }
            }
        }
        //3.Save progress
        saveWordProgress(currentWord);

        for (Button button : optionButtons) {
            button.setEnabled(false);
        }
        btnContinue.setVisibility(View.VISIBLE);
    }

    private void resetButtonStyles() {
        for (Button button : optionButtons) {
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.quiz_button_default));
            button.setEnabled(true);
        }
        btnContinue.setVisibility(View.INVISIBLE);
    }
    private void saveWordProgress(Word wordToUpdate) {
        Executors.newSingleThreadExecutor().execute(() -> {
            wordDao.insert(wordToUpdate);
        });
    }
}