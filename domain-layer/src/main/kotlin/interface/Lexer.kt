package `interface`

import entity.Token

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/3/17.
 */
interface Lexer {

    var filePath: String

    fun getTokens(): List<Token>

}