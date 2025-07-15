package kr.co.bookquiz.api.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class GradeEnumConverter : AttributeConverter<Grade, String> {
    override fun convertToDatabaseColumn(attribute: Grade?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): Grade? {
        return dbData?.let { Grade.valueOf(it) }
    }
}