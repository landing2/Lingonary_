package com.example.lingonary_.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String learning;   // Spanish
    public String nativeLang; // English
    public int timesCorrect;
    public boolean hasBeenInQuiz;
    public long dateAdded;

    public int startTime;
    public int endTime;

    public Word() { }

    @Ignore
    public Word(String learning, String nativeLang, int startTime, int endTime) {
        this.learning = learning;
        this.nativeLang = nativeLang;
        this.startTime = startTime;
        this.endTime = endTime;

        this.timesCorrect = 0;
        this.hasBeenInQuiz = false;
        this.dateAdded = System.currentTimeMillis();
    }

    protected Word(Parcel in) {
        id = in.readInt();
        learning = in.readString();
        nativeLang = in.readString();
        timesCorrect = in.readInt();
        hasBeenInQuiz = in.readByte() != 0;
        dateAdded = in.readLong();
        startTime = in.readInt();
        endTime = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(learning);
        dest.writeString(nativeLang);
        dest.writeInt(timesCorrect);
        dest.writeByte((byte) (hasBeenInQuiz ? 1 : 0));
        dest.writeLong(dateAdded);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) { return new Word(in); }
        @Override
        public Word[] newArray(int size) { return new Word[size]; }
    };
    public boolean isHasBeenInQuiz() { return hasBeenInQuiz; }
    public int getTimesCorrect() { return timesCorrect; }
    public String getLearning() { return learning; }
    public String getNativeLang() { return nativeLang; }
    public void setTimesCorrect(int timesCorrect) { this.timesCorrect = timesCorrect; }
    public void setHasBeenInQuiz(boolean hasBeenInQuiz) { this.hasBeenInQuiz = hasBeenInQuiz; }
    public void incrementTimesCorrect() { this.timesCorrect++; }
}