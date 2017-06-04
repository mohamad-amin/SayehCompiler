package tokenizer.extractor

import entity.*
import entity.TokenConstants.Punctuation as P

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class PunctuationExtractor: TokenExtractor<Punctuation> {

    override fun interact(from: Word) = when(from.text) {
        P.Comma -> Comma(from.line)
        P.ParenthesisOpen -> ParenthesisOpen(from.line)
        P.ParenthesisClose -> ParenthesisClose(from.line)
        P.BraceOpen -> BraceOpen(from.line)
        P.BraceClose -> BraceClose(from.line)
        P.Colon -> Colon(from.line)
        P.Semicolon -> Semicolon(from.line)
        else -> throw IllegalStateException(
                "Required token: Punctuation but it was: ${from.text} at line: ${from.line}")
    }

}