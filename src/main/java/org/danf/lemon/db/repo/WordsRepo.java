package org.danf.lemon.db.repo;

import org.danf.lemon.db.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * A Hibernate-backed {@link JpaRepository} for persistence operations of {@link WordEntity} on the <pre>words</pre> table.
 *
 * @author Dan Feldman
 */
@Repository
public interface WordsRepo extends JpaRepository<WordEntity, String> {

    @Query("UPDATE WordEntity w set w.count = w.count + 1 WHERE w.word = :word")
    @Modifying
    @Transactional
    void incrementCount(@Param("word") String word);

}
