package org.smartjobs.adaptors.view.web.controller.roles.display;

import java.util.List;

public record Role(long id, String position, List<Category> categories) {
}
