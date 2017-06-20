package lexicalanalyzer.tokenizer.extractor

import entity.*
import entity.TokenConstants.Punctuation as P

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class PunctuationExtractor: TokenExtractor<Punctuation> {

    override fun interact(from: Word) = when(from.text) {
        TokenConstants.Punctuation.Comma -> Comma(from.line)
        TokenConstants.Punctuation.ParenthesisOpen -> ParenthesisOpen(from.line)
        TokenConstants.Punctuation.ParenthesisClose -> ParenthesisClose(from.line)
        TokenConstants.Punctuation.BraceOpen -> BraceOpen(from.line)
        TokenConstants.Punctuation.BraceClose -> BraceClose(from.line)
        TokenConstants.Punctuation.Colon -> Colon(from.line)
        TokenConstants.Punctuation.Semicolon -> Semicolon(from.line)
        else -> throw IllegalStateException(
                "Required token: Punctuation but it was: ${from.text} at line: ${from.line}")
    }

}