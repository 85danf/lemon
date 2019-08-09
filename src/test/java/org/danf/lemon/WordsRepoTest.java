package org.danf.lemon;

import org.danf.lemon.db.entity.WordEntity;
import org.danf.lemon.db.repo.WordsRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * @author Dan Feldman
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class WordsRepoTest {

    @Autowired
    private WordsRepo wordsRepo;

    private String word = "lemonade";
    private WordEntity testWord = WordEntity.builder().word(word).build();

    @Before
    @Transactional
    public void init() {
        wordsRepo.save(testWord);
    }

    @Test
    public void testWordEntity() {
        Optional<WordEntity> actualWord = wordsRepo.findById(word);
        assertThat(actualWord.isPresent()).isTrue();
        assertThat(actualWord.get().getWord()).isEqualTo(word);
        assertThat(actualWord.get().getCount()).isEqualTo(0);
    }
}
