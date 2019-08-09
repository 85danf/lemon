package org.danf.lemon.rest.helper;

import lombok.extern.slf4j.Slf4j;
import org.danf.lemon.rest.resource.WordResource;
import org.danf.lemon.service.WordOperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Future;

import static org.danf.lemon.rest.helper.WordInputValidations.*;

/**
 * Provides word-related backend services to the {@link WordResource} endpoints.
 *
 * @author Dan Feldman
 */
@Service
@Slf4j
public class WordService {

    private WordOperationsService wordOperationsService;

    @Autowired
    public WordService(WordOperationsService wordOperationsService) {
        this.wordOperationsService = wordOperationsService;
    }

    /**
     * Counts words in the incoming {@param stream}
     */
    public ResponseEntity countFromStream(InputStream in) {
        try {
            wordOperationsService.countFromStream(in);
        } catch (Exception e) {
            return handleError("incoming stream", e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * There's an obvious security risk in letting {@param filePath} be any path on the local filesystem since a user
     * could potentially read any file (depending on OS filesystem permissions) which we don't want.
     * Due to lack of time I'm allowing it, but on a production-grade app I would limit the path to be relative to some
     * agreed upon path in the app's running dir (and would also prevent crawling the filesystem with '../' tricks).
     */
    public ResponseEntity verifyAndCountFromFile(String filePath, boolean async) {
        log.debug("Got count request with file path: '{}'", filePath);
        File file = validateFile(filePath);
        try {
            final Future<Void> execution = wordOperationsService.countFromFile(file);
            return waitForExecutionIfNeeded(async, execution, "count from file");
        } catch (Exception e) {
            return handleError(filePath, e);
        }
    }

    /**
     * A small sanity check is performed on the incoming parameter:
     * 1. A check if its a valid URL
     * 2. A check if the endpoint it refers to is available (Using HTTP HEAD) - an assumption is made here that if this
     * endpoint is available to GET requests then it must also be available to HEAD requests, which is not always true
     * in real-world situations.
     */
    public ResponseEntity verifyAndCountFromUrl(String url, boolean async) {
        log.debug("Got count request with url: '{}'", url);
        URI uri = validateUrl(url);
        validateRemoteAddress(uri);
        try {
            Future<Void> execution = wordOperationsService.countFromUrl(uri);
            return waitForExecutionIfNeeded(async, execution, "count from url");
        } catch (Exception e) {
            return handleError(url, e);
        }
    }

    /**
     * If the execution is requested to run as {@param async} this method will not wait on the {@param execution}
     * until it is completed (default sync execution waits).
     */
    private ResponseEntity waitForExecutionIfNeeded(boolean async, Future<Void> execution, String methodName) {
        if (!async) {
            try {
                execution.get();
            } catch (Exception e) {
                log.warn("Interrupted waiting for execution of {}", methodName);
                log.debug("", e);
                return handleError(methodName,e);
            }
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity handleError(String resource, Exception e) {
        String err = "Failed to count words from '" + resource + "' : " + e.getMessage();
        log.error(err, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
