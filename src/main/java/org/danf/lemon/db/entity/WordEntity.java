package org.danf.lemon.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This POJO represents a word in the database.
 * Each such word is a row with 2 columns:
 * <pre>word</pre>  - The table's primary key (handled by Hibernate with the {@link Id} annotation), represents a word.
 * <pre>count</pre> - Used for counting the appearances of the word in all input given to the app.
 *
 * Implementation note:
 * Persisting the counter on each appearance of the word is costly (db-wise) ideally I would choose an in-memory map
 * (i.e. {@see Map<String,Integer>})that gets flushed to the db on occasion (taking into account the compromise of
 * losing data on outage - but that depends on the specification for this service).
 *
 * Given the time constraint for this task and the requirement that the count will be persisted, I went with persisting
 * the words and counts immediately as they are processed.
 *
 * @author Dan Feldman
 */
@Data
@Builder
@AllArgsConstructor@NoArgsConstructor
@Entity
@Table(name = "words")
public class WordEntity {

    @Id
    private String word;
    private int count;
}
