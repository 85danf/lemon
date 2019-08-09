package org.danf.lemon.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.danf.lemon.rest.helper.WordService;
import org.danf.lemon.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Dan Feldman
 */
@Slf4j
@RestController
@RequestMapping("api/v1/words")
@Api(value = "Word Endpoints: providing count and statistics operations on words.")
public class WordResource {

    @Autowired
    private HttpServletRequest servletRequest;

    private StatisticsService statisticsService;
    private WordService wordService;

    @Autowired
    public WordResource(StatisticsService statisticsService, WordService wordService) {
        this.statisticsService = statisticsService;
        this.wordService = wordService;
    }

    /**
     * This endpoint receives a stream of characters or a location to retrieve such a stream (url / file) and reads
     * the entire available input to count appearances of words.
     *
     * @param url       Optional: retrieve the word list from the specified url
     * @param filePath  Optional: retrieve the word list from a path on the local filesystem.
     * @param async     Optional: allow the user to choose whether they'd like to defer the request.
     *
     * Assumption: read from url / file are sync operations, but since we don't want the user to time out on requests
     * for such resources in case they take a long time to process, we also allow async execution
     * Read from incoming stream must always be sync regardless of this flag (since we must consume the incoming stream).
     */
    @PostMapping(path = "count", produces = MediaType.TEXT_PLAIN, consumes = MediaType.WILDCARD)
    @ApiOperation(value = "Counts word occurrences in a given resource")
    @ResponseBody
    public ResponseEntity count(
            @ApiParam(value = "URL to read data from") @RequestParam(required = false) String url,
            @ApiParam(value = "File path to read data from") @RequestParam(required = false) String filePath,
            @ApiParam(value = "Whether to perform the processing of data async. Passing a stream implies sync execution " +
                    "regardless of this flag's value.", defaultValue = "false") @RequestParam(required = false, defaultValue = "false") boolean async) {
        ResponseEntity response;
        if (isNotBlank(url) && isNotBlank(filePath)) {
            response = ResponseEntity
                    .status(HttpStatus.SC_BAD_REQUEST)
                    .body("You can either specify a url or a file path, not both.");
        } else if (isNotBlank(url)) {
            response = wordService.verifyAndCountFromUrl(url, async);
        } else if (isNotBlank(filePath)) {
            response = wordService.verifyAndCountFromFile(filePath, async);
        } else {
            try (InputStream in = servletRequest.getInputStream()) {
                response = wordService.countFromStream(in);
            } catch (Exception e) {
                String err = "Failed to read incoming stream: " + e.getMessage();
                log.error(err, e);
                response = ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(err);
            }
        }
        return response;
    }

    /**
     * Gets statistics for the requested {@param word} from the database.
     */
    @GetMapping(path = "statistics/{word:.+}")
    @ApiOperation(value = "Gets statistics for a word")
    @ResponseBody
    public int getWordStatistics(@PathVariable String word) {
        return statisticsService.getWordCount(word);
    }

    /**
     * Resets statistics for the requested {@param word} from the database.
     *
     * Although not required by the original spec, it made sense to me that this is basic functionality that should be
     * provided as well (if only for testing purposes, although in a production scenario it would obviously be required).
     */
    @DeleteMapping(path = "statistics/{word:.+}")
    @ApiOperation(value = "Resets statistics for a word")
    @ResponseBody
    public ResponseEntity clearWordStatistics(@PathVariable String word) {
        statisticsService.clearWordStatistic(word);
        return ResponseEntity.ok()
                .body("Statistics for word '" + word + "' were cleared.");
    }
}
