package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

class AdminControllerIT extends IntegrationTest {

    @Test
    @WithMockUser(username = "username")
    void testAdminControllerEndpointsCanNotBeAccessedByUsersWithUserRole() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/admin/console/landing").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        getMockMvc().perform(MockMvcRequestBuilders.post("/admin/coupon/issue?emails=email").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "username", roles = {"ADMIN"})
    void testAdminControllerEndpointsCanOnlyBeAccessedByUsersWithAdminRole() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/admin/console/landing"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        getMockMvc().perform(MockMvcRequestBuilders.post("/admin/coupon/issue?emails=email"))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}
