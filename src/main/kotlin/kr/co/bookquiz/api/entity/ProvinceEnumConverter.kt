package kr.co.bookquiz.api.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ProvinceEnumConverter : AttributeConverter<Province, String> {
    override fun convertToDatabaseColumn(attribute: Province?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): Province? {
        return dbData?.let { Province.valueOf(it) }
    }
}