package tokenize

import entity.Token
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

class TokenExtractor: Tokenizer {

    val mapper = WordMapper()
    var sourceChanged = true
    var tokens = listOf<Token>()

    override var words: List<Word> = arrayListOf()
        set(value) {
            sourceChanged = true
        }

    override fun extractTokens(): List<Token> {
        if (sourceChanged) {
            tokens = words.map { mapper.interact(it) }
        }
        return tokens
    }

}
