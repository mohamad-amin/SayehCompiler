package reader

import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/5/17.
 */
interface Reader {

    var fileName: String

    fun extractWords(): List<Word>

}