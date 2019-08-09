package org.danf.lemon.service;

import lombok.extern.slf4j.Slf4j;
import org.danf.lemon.db.entity.WordEntity;
import org.danf.lemon.db.repo.WordsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Statistics are served on a best-effort basis, if an operation is actively updating the database with count information
 * this service's methods will return partial information (which is up-to-date with the point in time the api was called).
 *
 * @author Dan Feldman
 */
@Service
@Slf4j
public class StatisticsService {

    private WordsRepo wordsRepo;

    @Autowired
    public StatisticsService(WordsRepo wordsRepo) {
        this.wordsRepo = wordsRepo;
    }

    /**
     * @return The number of times {@param word} has appeared in any of the given user inputs until now.
     */
    public int getWordCount(String word) {
        return wordsRepo.findById(word).orElse(WordEntity.builder().word(word).build()).getCount();
    }

    /**
     * Clears the statistics data for {@param word}.
     */
    @Transactional
    public void clearWordStatistic(String word) {
        log.info("Clearing counter for word {}", word);
        wordsRepo.deleteById(word);
    }
}
