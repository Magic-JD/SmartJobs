package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static constants.HtmlConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

class PageControllerIT extends IntegrationTest {

    @Test
    void testPageControllerReturnsTheUnloggedLandingPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(matchesHtml(UNLOGGED_LANDING_PAGE));
    }

    @Test
    void testPageControllerReturnsTheLoggedInLandingPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(matchesHtml(LOGGED_LANDING_PAGE));
    }

    @Test
    void testPageControllerReturnsTheRolesPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/roles").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testPageControllerReturnsTheCandidatesPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/candidates").with(user(USER)))
                .andExpect(matchesHtml(CANDIDATE_PAGE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
