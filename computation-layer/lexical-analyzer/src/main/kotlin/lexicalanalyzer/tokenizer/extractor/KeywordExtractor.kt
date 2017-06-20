package lexicalanalyzer.tokenizer.extractor

import entity.*
import entity.TokenConstants.Keyword as K

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class KeywordExtractor: TokenExtractor<Keyword> {

    override fun interact(from: Word) = when(from.text) {
        TokenConstants.Keyword.IF -> IF(from.line)
        TokenConstants.Keyword.ELSE -> ELSE(from.line)
        TokenConstants.Keyword.WHILE -> WHILE(from.line)
        TokenConstants.Keyword.INT -> INT(from.line)
        TokenConstants.Keyword.CHAR -> CHAR(from.line)
        TokenConstants.Keyword.BOOL -> BOOL(from.line)
        TokenConstants.Keyword.NULL -> NULL(from.line)
        TokenConstants.Keyword.TRUE -> TRUE(from.line)
        TokenConstants.Keyword.FALSE -> FALSE(from.line)
        else -> throw IllegalStateException("Required token: Keyword but it was: ${from.text} at line: ${from.line}")
    }

}