package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Translator
import org.springframework.data.jpa.repository.JpaRepository

interface TranslatorRepository : JpaRepository<Translator, Long>