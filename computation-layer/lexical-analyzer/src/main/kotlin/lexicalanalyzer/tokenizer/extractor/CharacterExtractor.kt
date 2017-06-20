package lexicalanalyzer.tokenizer.extractor

import entity.Character
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class CharacterExtractor: TokenExtractor<Character> {

    companion object {
        fun isCharacter(from: Word) =
                from.text.length == 3 && from.text[1].isLetter() &&
                        from.text[0] == '\'' && from.text[2] == '\''
    }

    // Todo: Name of the number object?
    override fun interact(from: Word) = when {
        CharacterExtractor.Companion.isCharacter(from) -> Character("", from.text, from.line)
        else -> throw IllegalStateException(
                "Required token: Identifier but it was: ${from.text} at line: ${from.line}")
    }

}