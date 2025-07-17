package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Authority
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

import kr.co.bookquiz.api.TestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat

@DataJpaTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AuthorityRepositoryTest {

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    private lateinit var userAuthority: Authority
    private lateinit var adminAuthority: Authority

    @BeforeEach
    fun setUp() {
        userAuthority = Authority(
            name = "ROLE_USER",
            description = "Default user role",
            createdAt = LocalDateTime.now()
        )

        adminAuthority = Authority(
            name = "ROLE_ADMIN",
            description = "Administrator role",
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    fun `should find authority by name`() {
        // Given
        testEntityManager.persistAndFlush(userAuthority)

        // When
        val foundAuthority = authorityRepository.findByName("ROLE_USER")

        // Then
        assertThat(foundAuthority).isNotNull
        assertThat(foundAuthority?.name).isEqualTo("ROLE_USER")
        assertThat(foundAuthority?.description).isEqualTo("Default user role")
    }

    @Test
    fun `should return null when authority not found by name`() {
        // When
        val foundAuthority = authorityRepository.findByName("NONEXISTENT_ROLE")

        // Then
        assertThat(foundAuthority).isNull()
    }

    @Test
    fun `should check if authority exists by name`() {
        // Given
        testEntityManager.persistAndFlush(userAuthority)

        // When
        val exists = authorityRepository.existsByName("ROLE_USER")
        val notExists = authorityRepository.existsByName("NONEXISTENT_ROLE")

        // Then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun `should save new authority`() {
        // When
        val savedAuthority = authorityRepository.save(userAuthority)

        // Then
        assertThat(savedAuthority.id).isNotNull
        assertThat(savedAuthority.name).isEqualTo("ROLE_USER")
        assertThat(savedAuthority.description).isEqualTo("Default user role")
    }

    @Test
    fun `should find all authorities`() {
        // Given
        testEntityManager.persistAndFlush(userAuthority)
        testEntityManager.persistAndFlush(adminAuthority)

        // When
        val authorities = authorityRepository.findAll()

        // Then
        assertThat(authorities).hasSize(2)
        assertThat(authorities.map { it.name }).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN")
    }

    @Test
    fun `should find authorities by username using native query`() {
        // Given
        testEntityManager.persistAndFlush(userAuthority)
        testEntityManager.persistAndFlush(adminAuthority)

        // Execute native SQL to set up user_authorities relationship
        testEntityManager.entityManager.createNativeQuery(
            "INSERT INTO users (username, name, password, email, phone_number, date_of_birth, province, deleted, grade, enabled, gender, created_at, updated_at) " +
                "VALUES ('testuser', 'Test User', 'password', 'test@example.com', '+82101234567', '1990-01-01', 'SEOUL', false, 'COLLEGE_GENERAL', true, true, NOW(), NOW())"
        ).executeUpdate()

        testEntityManager.entityManager.createNativeQuery(
            "INSERT INTO user_authorities (username, authority_id) VALUES ('testuser', ?)"
        ).setParameter(1, userAuthority.id).executeUpdate()

        testEntityManager.entityManager.createNativeQuery(
            "INSERT INTO user_authorities (username, authority_id) VALUES ('testuser', ?)"
        ).setParameter(1, adminAuthority.id).executeUpdate()

        testEntityManager.flush()

        // When
        val userAuthorities = authorityRepository.findAuthoritiesByUsername("testuser")

        // Then
        assertThat(userAuthorities).hasSize(2)
        assertThat(userAuthorities.map { it.name }).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN")
    }

    @Test
    fun `should return empty list when finding authorities for non-existent user`() {
        // When
        val userAuthorities = authorityRepository.findAuthoritiesByUsername("nonexistent")

        // Then
        assertThat(userAuthorities).isEmpty()
    }
}
