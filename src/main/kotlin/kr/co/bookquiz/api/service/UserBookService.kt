package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Order
import kr.co.bookquiz.api.entity.User
import kr.co.bookquiz.api.entity.UserBook
import kr.co.bookquiz.api.repository.UserBookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserBookService(private val userBookRepository: UserBookRepository) {
  fun getUserBooksByUser(user: User): List<UserBook> = userBookRepository.findByUser(user)

  fun getUserBooksByBook(book: Book): List<UserBook> = userBookRepository.findByBook(book)

  fun getUserBookByUserAndBook(user: User, book: Book): UserBook? =
          userBookRepository.findByUserAndBook(user, book)

  @Transactional
  fun createUserBook(user: User, book: Book, order: Order, maxAttempts: Int = 3): UserBook {
    val userBook = UserBook(user = user, book = book, order = order, maxAttempts = maxAttempts)
    return userBookRepository.save(userBook)
  }
}
