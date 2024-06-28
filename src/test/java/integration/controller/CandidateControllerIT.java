package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CandidateControllerIT extends IntegrationTest {

    private static final String TABLE_HEADING = """
            <div class="candidate-row flex-container">
                    <p class="candidate-display-left">Name</p>
                    <div class="flex-container right-pushed">
                        <button class="button-light button-enlarge candidate-display-right"
                                hx-put="/candidate/select/all?select=true"
                                hx-target="#uploaded"
                                hx-swap="outerHTML"
                        >Select All
                        </button>
                        <button class="button-dark button-enlarge candidate-display-right"
                                hx-delete="/candidate/delete/all"
                                hx-target="#uploaded"
                                hx-swap="outerHTML"
                        >Delete All
                        </button>
                    </div>
                </div>""";

    public static final String CANDIDATES_TABLE_DEFAULT = STR. """
            <div id="uploaded" hx-target="closest div" hx-swap="outerHTML"
                 class="candidate-table flex-stack text-medium">
                \{ TABLE_HEADING }
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">Joe</p>
                    <button class='candidate-display-right button-light'
                            hx-swap="innerHTML" hx-put="/candidate/select/1?select=false">Selected</button>
                    <button class='button-dark' hx-delete="/candidate/delete/1">Delete
                    </button>
                </div>
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">james</p>
                    <button class='candidate-display-right button-dark'
                            hx-swap="innerHTML" hx-put="/candidate/select/2?select=true">Select</button>
                    <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                    </button>
                </div>
            </div>
                                    """ ;

    public static final String CANDIDATES_TABLE_DELETED = STR. """
            <div id="uploaded" hx-target="closest div" hx-swap="outerHTML"
                 class="candidate-table flex-stack text-medium">
                \{ TABLE_HEADING }
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">james</p>
                    <button class='candidate-display-right button-dark'
                            hx-swap="innerHTML" hx-put="/candidate/select/2?select=true">Select</button>
                    <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                    </button>
                </div>
            </div>
                                    """ ;

    public static final String CANDIDATES_TABLE_ALL_UNSELECTED = STR. """
            <div id="uploaded" hx-target="closest div" hx-swap="outerHTML"
                 class="candidate-table flex-stack text-medium">
                \{ TABLE_HEADING }
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">Joe</p>
                    <button class='candidate-display-right button-dark'
                            hx-swap="innerHTML" hx-put="/candidate/select/1?select=true">Select</button>
                    <button class='button-dark' hx-delete="/candidate/delete/1">Delete
                    </button>
                </div>
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">james</p>
                    <button class='candidate-display-right button-dark'
                            hx-swap="innerHTML" hx-put="/candidate/select/2?select=true">Select</button>
                    <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                    </button>
                </div>
            </div>
                                    """ ;


    public static final String CANDIDATES_TABLE_ALL_SELECTED = STR. """
            <div id="uploaded" hx-target="closest div" hx-swap="outerHTML"
                 class="candidate-table flex-stack text-medium">
                \{ TABLE_HEADING }
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">Joe</p>
                    <button class='candidate-display-right button-light'
                            hx-swap="innerHTML" hx-put="/candidate/select/1?select=false">Selected</button>
                    <button class='button-dark' hx-delete="/candidate/delete/1">Delete
                    </button>
                </div>
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">james</p>
                    <button class='candidate-display-right button-light'
                            hx-swap="innerHTML" hx-put="/candidate/select/2?select=false">Selected</button>
                    <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                    </button>
                </div>
            </div>
                                    """ ;

    @Test
    @Sql("/init-db.sql")
    void testCandidateReturnsTheGivenCandidates() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("HX-Request", "true");
        getMockMvc().perform(get("/candidate")
                        .with(user(USER))
                        .headers(httpHeaders)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(CANDIDATES_TABLE_DEFAULT));
    }


    @Test
    @Sql("/init-db.sql")
    void testSelectCandidates() throws Exception {
        getMockMvc().perform(put("/candidate/select/1?select=false")
                        .with(user(USER))
                        .headers(HTTP_HEADERS))
                .andExpect(status().isOk());
        getMockMvc().perform(get("/candidate")
                        .with(user(USER))
                        .headers(HTTP_HEADERS)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(CANDIDATES_TABLE_ALL_UNSELECTED));
        getMockMvc().perform(put("/candidate/select/all?select=true")
                        .with(user(USER))
                        .headers(HTTP_HEADERS))
                .andExpect(status().isOk())
                .andExpect(content().string(CANDIDATES_TABLE_ALL_SELECTED));
    }

    @Test
    @Sql("/init-db.sql")
    void testDeleteCandidates() throws Exception {
        getMockMvc().perform(delete("/candidate/delete/all")
                        .with(user(USER))
                        .headers(HTTP_HEADERS))
                .andExpect(status().isOk());
        getMockMvc().perform(get("/candidate")
                        .with(user(USER))
                        .headers(HTTP_HEADERS)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(CANDIDATES_TABLE_DELETED));
    }
}
