package org.eclipse.digitaltwin.basyx.arena.workermanager.skills;

import java.util.Map;
import java.util.function.Function;

public record Skill(String skillId, Function<Map<String, Object>, Map<String, Object>> skillFunction) {
}