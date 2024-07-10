package org.eclipse.digitaltwin.basyx.arena.mockedcc.controllers;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides test endpoints working with operations
 * 
 * @author mateusmolina
 */
@RestController
public class MockedOperationController {
    final static Logger logger = LoggerFactory.getLogger(MockedOperationController.class);

    @PostMapping("/invoke/square")
    public ResponseEntity<OperationVariable[]> invokeOperation(@RequestBody OperationVariable[] requestData) {
        return new ResponseEntity<OperationVariable[]>(
                OperationChain.from(requestData)
                        .mapValues(MockedOperationController::square)
                        .map(MockedOperationController::turnAllInToOut)
                        .map(MockedOperationController::sleep)
                        .log(logger)
                        .end(),
                HttpStatus.OK);
    }

    private static Object[] square(Object[] objs) {
        Object[] result = Arrays.stream(objs)
                .map(String::valueOf)
                .map(Double::parseDouble)
                .map(num -> Math.pow(num, 2))
                .toArray();

        logger.info("Applying square: (" + Arrays.toString(objs) + ") -> (" + Arrays.toString(result) + ")");

        return result;
    }

    private static Map<String, Object> turnAllInToOut(Map<String, Object> in) {
        return in.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().endsWith("_in")
                                ? entry.getKey().substring(0, entry.getKey().length() - 3) + "_out"
                                : entry.getKey(),
                        Map.Entry::getValue));
    }

    private static <T> T sleep(T in) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(null, e);
        }
        return in;
    }
}
