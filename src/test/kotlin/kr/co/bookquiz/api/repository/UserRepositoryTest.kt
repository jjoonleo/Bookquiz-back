package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.TestcontainersConfiguration
import kr.co.bookquiz.api.entity.Authority
import kr.co.bookquiz.api.entity.Grade
import kr.co.bookquiz.api.entity.Province
import kr.co.bookquiz.api.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    private lateinit var testUser: User
    private lateinit var userAuthority: Authority

    @BeforeEach
    fun setUp() {
        // Create test authority
        userAuthority = Authority(
            name = "ROLE_USER",
            description = "Default user role",
            createdAt = LocalDateTime.now()
        )
        testEntityManager.persistAndFlush(userAuthority)

        // Create test user
        testUser = User(
            username = "testuser",
            name = "Test User",
            password = "encodedPassword123",
            email = "test@example.com",
            phoneNumber = "+82101234567",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            deleted = false,
            grade = Grade.COLLEGE_GENERAL,
            enabled = true,
            gender = true,
            refreshToken = null,
            lastLogin = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            authorities = setOf(userAuthority)
        )
    }

    @Test
    fun `should find user by username when not deleted`() {
        // Given
        testEntityManager.persistAndFlush(testUser)

        // When
        val foundUser = userRepository.findByUsernameAndDeletedFalse("testuser")

        // Then
        assertThat(foundUser).isNotNull
        assertThat(foundUser?.username).isEqualTo("testuser")
        assertThat(foundUser?.email).isEqualTo("test@example.com")
        assertThat(foundUser?.authorities).hasSize(1)
        assertThat(foundUser?.authorities?.first()?.name).isEqualTo("ROLE_USER")
    }

    @Test
    fun `should not find user by username when deleted`() {
        // Given
        val deletedUser = testUser.copy(deleted = true)
        testEntityManager.persistAndFlush(deletedUser)

        // When
        val foundUser = userRepository.findByUsernameAndDeletedFalse("testuser")

        // Then
        assertThat(foundUser).isNull()
    }

    @Test
    fun `should find user by email when not deleted`() {
        // Given
        testEntityManager.persistAndFlush(testUser)

        // When
        val foundUser = userRepository.findByEmailAndDeletedFalse("test@example.com")

        // Then
        assertThat(foundUser).isNotNull
        assertThat(foundUser?.username).isEqualTo("testuser")
        assertThat(foundUser?.email).isEqualTo("test@example.com")
    }

    @Test
    fun `should find user by phone number when not deleted`() {
        // Given
        testEntityManager.persistAndFlush(testUser)

        // When
        val foundUser = userRepository.findByPhoneNumberAndDeletedFalse("+82101234567")

        // Then
        assertThat(foundUser).isNotNull
        assertThat(foundUser?.username).isEqualTo("testuser")
        assertThat(foundUser?.phoneNumber).isEqualTo("+82101234567")
    }

    @Test
    fun `should check username exists when not deleted`() {
        // Given
        testEntityManager.persistAndFlush(testUser)

        // When
        val exists = userRepository.existsByUsernameAndDeletedFalse("testuser")
        val notExists = userRepository.existsByUsernameAndDeletedFalse("nonexistent")

        // Then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun `should check email exists when not deleted`() {
        // Given
        testEntityManager.persistAndFlush(testUser)

        // When
        val exists = userRepository.existsByEmailAndDeletedFalse("test@example.com")
        val notExists = userRepository.existsByEmailAndDeletedFalse("nonexistent@example.com")

        // Then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun `should check phone number exists when not deleted`() {
        // Given
        testEntityManager.persistAndFlush(testUser)

        // When
        val exists = userRepository.existsByPhoneNumberAndDeletedFalse("+82101234567")
        val notExists = userRepository.existsByPhoneNumberAndDeletedFalse("+82109876543")

        // Then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun `should not find deleted user in exists checks`() {
        // Given
        val deletedUser = testUser.copy(deleted = true)
        testEntityManager.persistAndFlush(deletedUser)

        // When
        val usernameExists = userRepository.existsByUsernameAndDeletedFalse("testuser")
        val emailExists = userRepository.existsByEmailAndDeletedFalse("test@example.com")
        val phoneExists = userRepository.existsByPhoneNumberAndDeletedFalse("+82101234567")

        // Then
        assertThat(usernameExists).isFalse()
        assertThat(emailExists).isFalse()
        assertThat(phoneExists).isFalse()
    }

    @Test
    fun `should save user with authorities`() {
        // Given
        val adminAuthority = Authority(
            name = "ROLE_ADMIN",
            description = "Admin role",
            createdAt = LocalDateTime.now()
        )
        testEntityManager.persistAndFlush(adminAuthority)

        val userWithMultipleAuthorities = testUser.copy(
            username = "adminuser",
            email = "admin@example.com",
            phoneNumber = "+82101234568",
            authorities = setOf(userAuthority, adminAuthority)
        )

        // When
        userRepository.save(userWithMultipleAuthorities)
        testEntityManager.flush()
        testEntityManager.clear()

        // Then
        val foundUser = userRepository.findByUsernameAndDeletedFalse("adminuser")
        assertThat(foundUser).isNotNull
        assertThat(foundUser?.authorities).hasSize(2)
        assertThat(foundUser?.authorities?.map { it.name }).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN")
    }

    @Test
    fun `should update user refresh token`() {
        // Given
        testEntityManager.persistAndFlush(testUser)
        testEntityManager.clear()
        val refreshToken = "new-refresh-token"

        // When - Use a simpler approach to avoid collection merge issues
        var foundUser = userRepository.findByUsernameAndDeletedFalse("testuser")
        assertThat(foundUser).isNotNull
        foundUser!!.refreshToken = refreshToken

        // Detach and reattach to avoid merge issues with collections
        testEntityManager.detach(foundUser)
        userRepository.save(foundUser)
        testEntityManager.flush()
        testEntityManager.clear()

        // Then
        val updatedUser = userRepository.findByUsernameAndDeletedFalse("testuser")
        assertThat(updatedUser?.refreshToken).isEqualTo(refreshToken)
    }
}