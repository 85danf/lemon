package org.danf.lemon.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * @author Dan Feldman
 */
@Slf4j
@RestController
@RequestMapping("api/v1/system")
@Api(value = "System Operations")
public class SystemResource {

    @Autowired
    private HttpServletRequest request;

    /**
     * Ping endpoint to test health of this web service.
     */
    @GetMapping(path = "ping", produces = MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Ping endpoint")
    public String ping() {
        log.info("Got ping request from {}", request.getRemoteAddr());
        return "PONG\n";
    }

    /**
     * A small easter egg, because why not
     */
    @GetMapping(path = "coffee", produces = MediaType.TEXT_PLAIN)
    @ResponseBody
    public ResponseEntity brewCoffee() {
        return ResponseEntity.status(418)
                .body("I'm sorry, I can't brew you coffee, I'm a teapot! \n" +
                        "                       (        \n" +
                        "            _           ) )     \n" +
                        "         _,(_)._        ((      \n" +
                        "    ___,(_______).        )     \n" +
                        "  ,'__.   /       \\    /\\_    \n" +
                        " /,' /  |\"\"|       \\  /  /   \n" +
                        "| | |   |__|       |,'  /       \n" +
                        " \\`.|                  /       \n" +
                        "  `. :           :    /         \n" +
                        "    `.            :.,'          \n" +
                        "      `-.________,-'            \n" );
    }
}
