package tokenizer.extractor

import entity.Identifier
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class IdentifierExtractor: TokenExtractor<Identifier> {

    companion object {
        val regex = Regex("[_a-zA-Z0-9]+")
        fun isIdentifier(from: Word) = from.text.matches(regex)
    }

    override fun interact(from: Word) = when {
        isIdentifier(from) -> Identifier(from.text, -1, -1, from.line)
        else -> throw IllegalStateException(
                "Required token: Identifier but it was: ${from.text} at line: ${from.line}")
    }

}