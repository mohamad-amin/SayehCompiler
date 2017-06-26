package lexicalanalyzer.tokenizer.extractor

import entity.Identifier
import entity.ValueType
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class IdentifierExtractor: TokenExtractor<Identifier> {

    companion object {
        val regex = Regex("[_a-zA-Z][_a-zA-Z0-9]*")
        fun isIdentifier(from: Word) = from.text.matches(IdentifierExtractor.Companion.regex)
        val identifiers = arrayListOf<Identifier>()
    }

    override fun interact(from: Word): Identifier {
        val result = when {
            IdentifierExtractor.Companion.isIdentifier(from) ->
                Identifier(from.text, from.line, -1, -1, ValueType.UNKNOWN, "")
            else -> throw IllegalStateException(
                    "Required token: Identifier but it was: ${from.text} at line: ${from.line}")
        }
        val index = identifiers.indexOf(result)
        if (index == -1) {
            identifiers.add(result)
            return result
        } else return identifiers[index]
    }

}