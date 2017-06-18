package tokenizer

import entity.Token
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

class WordTokenizer : Tokenizer {

    private val mapper = WordMapper()
    private var sourceChanged = true
    private var tokens = listOf<Token>()

    override var words: List<Word> = arrayListOf()
        set(value) {
            sourceChanged = true
            field = value
        }

    override fun extractTokens(): List<Token> {
        if (sourceChanged) {
            tokens = words.map { mapper.interact(it) }
            sourceChanged = false
        }
        return tokens
    }

}
