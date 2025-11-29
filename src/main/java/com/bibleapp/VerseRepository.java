package com.bibleapp;

import com.bibleapp.model.BibleVerse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VerseRepository {
    private List<BibleVerse> verses;
    private Random random;

    public VerseRepository() {
        this.verses = new ArrayList<>();
        this.random = new Random();
        loadVerses();
    }

    private void loadVerses() {
        verses.add(new BibleVerse("John 3:16",
                "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life."));

        verses.add(new BibleVerse("Philippians 4:13",
                "I can do all things through Christ who strengthens me."));

        verses.add(new BibleVerse("Psalm 23:1",
                "The Lord is my shepherd, I lack nothing."));

        verses.add(new BibleVerse("Proverbs 3:5-6",
                "Trust in the Lord with all your heart and lean not on your own understanding; in all your ways submit to him, and he will make your paths straight."));

        verses.add(new BibleVerse("Romans 8:28",
                "And we know that in all things God works for the good of those who love him, who have been called according to his purpose."));
    }

    public BibleVerse getRandomVerse() {
        int index = random.nextInt(verses.size());
        return verses.get(index);
    }
}