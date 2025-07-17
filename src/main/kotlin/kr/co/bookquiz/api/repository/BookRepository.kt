package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, String>
