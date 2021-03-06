package lexicalanalyzer.tokenizer.extractor

import entity.Number
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class NumberExtractor: TokenExtractor<Number> {

    companion object {
        val regex = Regex("^[+-]\\d+|\\d+")
        fun isNumber(from: Word) = from.text.matches(NumberExtractor.Companion.regex)
    }

    // Todo: Name of the number object?
    override fun interact(from: Word) = when {
        NumberExtractor.Companion.isNumber(from) -> Number("", memoryAddress = -1, registerAddress = -1, line = from.line,
                number = if (from.text.toInt() >= 0 && from.text[0] != '+') "+" + from.text else from.text)
        else -> throw IllegalStateException(
                "Required token: Identifier but it was: ${from.text} at line: ${from.line}")
    }

}