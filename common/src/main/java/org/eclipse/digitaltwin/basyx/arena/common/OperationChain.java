package org.eclipse.digitaltwin.basyx.arena.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.slf4j.Logger;

/**
 * Helper class to build a set of OperationVariable[] from an input
 * OperationVariable[], enabling mapping to common Java Objects in the process.
 * 
 * @author mateusmolina
 */
public class OperationChain {

    private final OperationVariable[] inputVars;

    private Deque<OperationFunctionProvider> funs = new ArrayDeque<>();

    private Optional<Logger> logger;

    private OperationChain(OperationVariable[] inputVars) {
        this.inputVars = inputVars;
    }

    public static OperationChain from(OperationVariable[] requestData) {
        return new OperationChain(requestData);
    }

    public static OperationChain from(Map<String, Object> inputMap) {
        return new OperationChain(buildOperationVariablesFromMap(inputMap));
    }

    public OperationChain map(OperationFunctionProvider fun) {
        funs.add(fun);
        return this;
    }

    public OperationChain mapOperation(Function<OperationVariable[], OperationVariable[]> fun) {
        funs.add((ops) -> {
            return buildMapFromOperationVariables(fun.apply(buildOperationVariablesFromMap(ops)));
        });
        return this;
    }

    public OperationChain mapValues(Function<Object[], Object[]> fun) {
        return map(map -> {
            Object[] inputArray = map.values().toArray();
            Object[] outputArray = fun.apply(inputArray);

            List<String> keys = new ArrayList<>(map.keySet());
            return IntStream.range(0, keys.size())
                    .boxed()
                    .collect(Collectors.toMap(keys::get, i -> outputArray[i]));
        });
    }

    public OperationVariable[] end() {
        final Map<String, Object> inputs = buildMapFromOperationVariables(inputVars);
        Map<String, Object> outputs = inputs;

        logger.ifPresent(l -> l.info("Received following input map: " + inputs));

        for (OperationFunctionProvider fun : funs) {
            outputs = fun.apply(outputs);
        }

        final Map<String, Object> finalOutputs = outputs;
        logger.ifPresent(l -> l.info("Sending following output map: " + finalOutputs));

        return buildOperationVariablesFromMap(outputs);
    }

    public Map<String, Object> endMap() {
        return buildMapFromOperationVariables(end());
    }

    public OperationChain log(Logger logger) {
        this.logger = Optional.of(logger);
        return this;
    }

    static private OperationVariable[] buildOperationVariablesFromMap(
            Map<String, Object> inputs) {
        return inputs.entrySet().stream().map(entry -> buildOperationVariable(entry.getKey(), entry.getValue()))
                .toArray(OperationVariable[]::new);
    }

    static private OperationVariable buildOperationVariable(String idShort, Object value) {
        return new DefaultOperationVariable.Builder()
                .value(new DefaultProperty.Builder()
                        .idShort(idShort)
                        .valueType(DataTypeDefXsd.STRING)
                        .value(String.valueOf(value)).build())
                .build();
    }

    static private Map<String, Object> buildMapFromOperationVariables(OperationVariable[] outputs) {
        return List.of(outputs).stream()
                .map(OperationVariable::getValue)
                .map(Property.class::cast)
                .collect(Collectors.toMap(Property::getIdShort, Property::getValue));
    }
}
