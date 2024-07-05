package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

class AdminControllerIT extends IntegrationTest {

    @Test
    @WithMockUser(username = "username")
    void testAdminControllerEndpointsCanNotBeAccessedByUsersWithUserRole() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/admin/console/landing").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
        getMockMvc().perform(MockMvcRequestBuilders.post("/admin/coupon/issue?emails=email").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "username", roles = {"ADMIN"})
    void testAdminControllerEndpointsCanOnlyBeAccessedByUsersWithAdminRole() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/admin/console/landing"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        getMockMvc().perform(MockMvcRequestBuilders.post("/admin/coupon/issue?emails=email").headers(HTTP_HEADERS))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}
