package kr.co.bookquiz.api.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class RoleEnumConverter : AttributeConverter<Role, String> {
    override fun convertToDatabaseColumn(attribute: Role?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): Role? {
        return dbData?.let { Role.valueOf(it) }
    }
}
