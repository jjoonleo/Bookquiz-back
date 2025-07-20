package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.config.TestSecurityConfig
import kr.co.bookquiz.api.security.JwtUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TestController::class)
@Import(TestSecurityConfig::class)
class TestControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @MockitoBean private lateinit var jwtUtil: JwtUtil

    @Test
    fun `should access public endpoint without authentication`() {
        mockMvc.perform(get("/api/test/public"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Public endpoint accessed successfully"))
            .andExpect(jsonPath("$.data.message").value("This is a public endpoint"))
    }

    @Test
    @WithMockUser(username = "testuser", authorities = ["ROLE_USER"])
    fun `should access protected endpoint with authentication`() {
        mockMvc.perform(get("/api/test/protected"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Access granted to protected endpoint"))
            .andExpect(jsonPath("$.data.username").value("testuser"))
            .andExpect(jsonPath("$.data.authorities[0]").value("ROLE_USER"))
    }

    @Test
    @WithMockUser(username = "adminuser", authorities = ["ROLE_ADMIN", "ROLE_USER"])
    fun `should access protected endpoint with multiple authorities`() {
        mockMvc.perform(get("/api/test/protected"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Access granted to protected endpoint"))
            .andExpect(jsonPath("$.data.username").value("adminuser"))
            .andExpect(jsonPath("$.data.authorities").isArray)
            .andExpect(
                jsonPath("$.data.authorities")
                    .value(org.hamcrest.Matchers.hasItems("ROLE_ADMIN", "ROLE_USER"))
            )
    }
}