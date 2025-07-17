package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Illustrator
import org.springframework.data.jpa.repository.JpaRepository

interface IllustratorRepository : JpaRepository<Illustrator, Long>