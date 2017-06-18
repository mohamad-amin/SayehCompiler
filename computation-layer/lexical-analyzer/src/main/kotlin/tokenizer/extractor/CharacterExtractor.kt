package tokenizer.extractor

import entity.Identifier
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class CharacterExtractor: TokenExtractor<Identifier> {

    companion object {
        fun isCharacter(from: Word) =
                from.text.length == 3 && from.text[1].isLetter() &&
                        from.text[0] == '\'' && from.text[2] == '\''
    }

    override fun interact(from: Word) = when {
        isCharacter(from) -> Identifier(from.text, -1, -1, from.line)
        else -> throw IllegalStateException(
                "Required token: Identifier but it was: ${from.text} at line: ${from.line}")
    }

}