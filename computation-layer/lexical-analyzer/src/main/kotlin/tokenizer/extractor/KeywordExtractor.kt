package tokenizer.extractor

import entity.*
import entity.TokenConstants.Keyword as K

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class KeywordExtractor: TokenExtractor<Keyword> {

    override fun interact(from: Word) = when(from.text) {
        K.IF -> IF(from.line)
        K.ELSE -> ELSE(from.line)
        K.WHILE -> WHILE(from.line)
        K.INT -> INT(from.line)
        K.CHAR -> CHAR(from.line)
        K.BOOL -> BOOL(from.line)
        K.NULL -> NULL(from.line)
        K.TRUE -> TRUE(from.line)
        K.FALSE -> FALSE(from.line)
        else -> throw IllegalStateException("Required token: Keyword but it was: ${from.text} at line: ${from.line}")
    }

}