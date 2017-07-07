package expression.converter.base

import entity.*
import entity.Number
import expression.converter.BoolConverter
import expression.converter.CharConverter
import expression.converter.IntConverter
import expression.solver.ConvertedMemory
import expression.solver.ConvertedRegister
import expression.solver.ConvertionResult

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class TokenConverter: Converter<Token> {

    private val intConverter = IntConverter()
    private val boolConverter = BoolConverter()
    private val charConverter = CharConverter()

    override fun convert(t: Token): ConvertionResult = when (t) {
        is Number -> intConverter.convert(t.number.toInt())
        is TRUE -> boolConverter.convert(true)
        is FALSE -> boolConverter.convert(false)
        is Character -> charConverter.convert(t.char[1])
        is Identifier ->
            if (t.registerAddress != -1) ConvertedRegister(t.registerAddress)
            else ConvertedMemory(t.memoryAddress)
        else -> throw IllegalStateException("Converting non-allowed token? $t")
    }

}