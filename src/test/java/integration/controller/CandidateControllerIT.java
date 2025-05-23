package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static constants.HtmlConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CandidateControllerIT extends IntegrationTest {

    @Test
    void testCandidateReturnsTheGivenCandidates() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("HX-Request", "true");
        getMockMvc().perform(get("/candidate")
                        .with(user(USER))
                        .headers(httpHeaders)
                )
                .andExpect(status().isOk())
                .andExpect(matchesHtml(CANDIDATES_TABLE_DEFAULT));
    }

    @Test
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
                .andExpect(matchesHtml(CANDIDATES_TABLE_ALL_UNSELECTED));
        //TODO this is currently not working
//        getMockMvc().perform(put("/candidate/select/all?select=true")
//                        .with(user(USER))
//                        .headers(HTTP_HEADERS))
//                .andExpect(status().isOk())
//                .andExpect(matchesHtml(CANDIDATES_TABLE_ALL_SELECTED));
    }

    @Test
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
                .andExpect(matchesHtml(CANDIDATES_TABLE_DELETED));
    }
}
