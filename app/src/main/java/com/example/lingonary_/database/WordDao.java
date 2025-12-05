package com.example.lingonary_.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.lingonary_.models.Word;
import java.util.List;
@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Word word);
    @Query("DELETE FROM words WHERE learning = :spanishWord")
    void deleteByWord(String spanishWord);
    // Old method (Strs only)
    @Query("SELECT learning FROM words")
    List<String> getAllSavedWordStrings();
    // Returns the full Word object so we can check 'timesCorrect' and 'hasBeenInQuiz'
    @Query("SELECT * FROM words ORDER BY dateAdded DESC")
    List<Word> getAllSavedWords();
    @Query("SELECT * FROM words WHERE learning = :spanishWord LIMIT 1")
    Word getWord(String spanishWord);
    @Query("SELECT * FROM words")
    List<Word> getAllWords();
}