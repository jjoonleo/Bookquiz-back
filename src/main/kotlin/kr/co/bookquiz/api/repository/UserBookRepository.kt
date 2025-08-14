package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.User
import kr.co.bookquiz.api.entity.UserBook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserBookRepository : JpaRepository<UserBook, Long> {
  fun findByUser(user: User): List<UserBook>
  fun findByBook(book: Book): List<UserBook>
  fun findByUserAndBook(user: User, book: Book): UserBook?
}
