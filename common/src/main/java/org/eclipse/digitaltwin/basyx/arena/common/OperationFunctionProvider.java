package org.eclipse.digitaltwin.basyx.arena.common;

import java.util.Map;

@FunctionalInterface
public interface OperationFunctionProvider {
    Map<String, Object> apply(Map<String, Object> ins);
}
