package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Author
import org.springframework.data.jpa.repository.JpaRepository

interface AuthorRepository : JpaRepository<Author, Long>