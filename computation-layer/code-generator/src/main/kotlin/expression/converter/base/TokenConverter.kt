package expression.converter.base

import entity.*
import entity.Number
import expression.converter.BoolConverter
import expression.converter.CharConverter
import expression.converter.IntConverter

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class TokenConverter: Converter<Token> {

    val intConverter = IntConverter()
    val boolConverter = BoolConverter()
    val charConverter = CharConverter()

    override fun convert(t: Token) = when (t) {
        is Number -> intConverter.convert(t.number.toInt())
        is TRUE -> boolConverter.convert(true)
        is FALSE -> boolConverter.convert(false)
        is Character -> charConverter.convert(t.char[1])
        is Identifier -> when (t.type) {
            ValueType.INT -> intConverter.convert(t.value.toInt())
            ValueType.BOOL -> boolConverter.convert(t.value.toBoolean())
            ValueType.CHAR -> charConverter.convert(t.value[1])
            ValueType.UNKNOWN -> throw IllegalStateException("Converting Unknown identifier token? $t")
        }
        else -> throw IllegalStateException("Converting non-allowed token? $t")
    }

}