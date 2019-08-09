package org.danf.lemon.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;
import org.danf.lemon.db.entity.WordEntity;
import org.danf.lemon.db.repo.WordsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * This service provides word counting functionality for the REST endpoints of this application.
 *
 * Implementation note:
 * There's a compromise between async execution (and preventing client timeout for large resources) and returning
 * a correct status to the user (i.e. if the request to get the resource fails).
 * I chose to let the user have the option to decide  if they'd like to wait for the execution to finish or not by
 * waiting on the returned {@link Future}.
 * Error handling is made as if the requests are run in sync mode (i.e. attempting to return meaningful status codes
 * and errors to the user), they are also backed by logging each error so that errors originating async calls can also
 * be traced.
 *
 * @author Dan Feldman
 */
@Service
@Transactional
@Slf4j
public class WordOperationsService {

    private WordsRepo wordsRepo;

    @Autowired
    public WordOperationsService(WordsRepo wordsRepo) {
        this.wordsRepo = wordsRepo;
    }

    /**
     * Receives a valid {@param url} and sends the data returned from a GET request executed against it.
     * The content is then sent for processing by {@link #countFromStream}
     */
    @Async
    public Future<Void> countFromUrl(URI url) throws IOException {
        Request getRequest = Request.Get(url)
                .connectTimeout(3000)
                .socketTimeout(3000);
        try (InputStream in = getRequest.execute().returnContent().asStream()) {
            countFromStream(in);
        }
        return new AsyncResult<>(null);
    }

    /**
     * Receives a valid, existing {@param file} and creates a stream from its content.
     * The content is then sent for processing by {@link #countFromStream}
     */
    @Async
    public Future<Void> countFromFile(@NotNull File file) throws IOException {
        try (InputStream in = new FileInputStream(file)){
            countFromStream(in);
        }
        return new AsyncResult<>(null);
    }

    /**
     * Receives a stream of characters and reads it word-by-word.
     * Each word's counter
     */
    public void countFromStream(InputStream in) {
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\s+");
        while(scanner.hasNext()) {
            String word = scanner.next();
            log.info("Found word {} in stream.", word);
            createOrUpdate(word);
        }
    }

    private void createOrUpdate(String word) {
        if (wordsRepo.existsById(word)) {
            log.info("word '{}' exists, incrementing count", word);
            wordsRepo.incrementCount(word);
        } else {
            log.info("word '{}' doesn't exist, creating", word);
            wordsRepo.save(WordEntity.builder().word(word).count(1).build());
        }
    }
}
