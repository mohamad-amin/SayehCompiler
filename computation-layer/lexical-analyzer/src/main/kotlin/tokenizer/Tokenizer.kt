package tokenizer

import entity.Token
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/3/17.
 */
interface Tokenizer {

    var words: List<Word>

    fun extractTokens(): List<Token>

}