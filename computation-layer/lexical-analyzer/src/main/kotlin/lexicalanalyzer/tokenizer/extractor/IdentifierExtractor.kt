package lexicalanalyzer.tokenizer.extractor

import entity.Identifier
import entity.ValueType
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class IdentifierExtractor: TokenExtractor<Identifier> {

    companion object {
        val regex = Regex("[_a-zA-Z0-9]+")
        fun isIdentifier(from: Word) = from.text.matches(IdentifierExtractor.Companion.regex)
    }

    override fun interact(from: Word) = when {
        IdentifierExtractor.Companion.isIdentifier(from) -> Identifier(from.text, -1, -1, from.line, ValueType.UNKNOWN, "")
        else -> throw IllegalStateException(
                "Required token: Identifier but it was: ${from.text} at line: ${from.line}")
    }

}