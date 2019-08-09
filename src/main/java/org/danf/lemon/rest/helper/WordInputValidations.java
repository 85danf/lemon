package org.danf.lemon.rest.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Validations for user input incoming from REST endpoints.
 *
 * @author Dan Feldman
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WordInputValidations {

    static File validateFile(String filePath) {
        if (isBlank(filePath)) {
            throw new BadRequestException("Got filePath query param but no value");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            String err = "File '" + filePath + "' doesn't exist.";
            log.error(err);
            throw new NotFoundException(err);
        }
        return file;
    }

    static URI validateUrl(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException | NullPointerException e) {
            log.error("Got malformed url '{}' : {}", url, e.getMessage());
            log.debug("", e);
            throw new BadRequestException("Malformed URL: " + url);
        }
    }

    static void validateRemoteAddress(URI uri) {
        String err = "Unable to reach remote address '" + uri.toString() + "' : ";
        try {
            HttpResponse response = Request.Head(uri).execute().returnResponse();
            StatusLine statusLine = response.getStatusLine();
            int status = statusLine.getStatusCode();
            if (!isRequestSuccessful(status)) {
                log.error(err + statusLine.getReasonPhrase());
                throw new NotFoundException(err + statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            log.error(err + e.getMessage());
            log.debug("", e);
            throw new IllegalArgumentException(err, e);
        }
    }

    private static boolean isRequestSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }
}
