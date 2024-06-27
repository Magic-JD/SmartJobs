package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

class PageControllerIT extends IntegrationTest {

    @Test
    void testPageControllerReturnsTheLandingPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    void testPageControllerReturnsTheRolesPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/roles").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testPageControllerReturnsTheCandidatesPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/candidates").with(user(USER)))
                .andExpect(MockMvcResultMatchers.content().string("""
                        <div class="top-padding"></div>
                        <div class="flex-container">
                            <div id="info-box" class="flex-stack right-nudged text-only"
                             hx-get="/candidate/number/selected"
                             hx-swap="outerHTML"
                             hx-target="this"
                             hx-trigger="candidate-count-updated from:body">
                            <p class="text-only">Role: role</p>
                            <p class="text-only">Selected Candidates: 1</p>
                        </div>

                            <a class="button-dark button-enlarge right-pushed"
                               hx-get="/upload"
                               hx-swap="innerHtml"
                               hx-target="closest main"
                               hx-trigger="click"
                            >Upload Additional Candidates</a>
                            <a class="button-light button-enlarge right-pushed"
                               hx-get="/analyze"
                               hx-swap="innerHtml"
                               hx-target="closest main"
                               hx-trigger="click"
                            >Analyze Selected Candidates</a>
                        </div>
                        <div class="loading-container" hx-get="/candidate" hx-swap="outerHTML" hx-target="#table-placeholder"
                             hx-trigger="load"
                             id="table-placeholder">
                            <h1 class="text-large">Loading Candidates</h1>
                            <img alt="loading" class='htmx-indicator' src="/images/loading.gif"/>
                        </div>

                                        """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
