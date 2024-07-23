package org.smartjobs.adaptors.view.web.controller.roles.display;

import java.util.List;

public record Category(String name, List<ScoringCriteria> criteria) {
}
