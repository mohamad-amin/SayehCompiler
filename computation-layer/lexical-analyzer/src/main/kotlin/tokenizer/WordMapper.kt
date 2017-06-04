package tokenizer

import base.Mapper
import entity.*
import tokenizer.extractor.*
import tokenizer.extractor.operator.OperatorExtractorImp

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */
class WordMapper: Mapper<Word, Token> {

    val keywordExtractor = KeywordExtractor()
    val operatorExtractor = OperatorExtractorImp()
    val punctuationExtractor = PunctuationExtractor()
    val identifierExtractor = IdentifierExtractor()
    val characterExtractor = CharacterExtractor()
    val numberExtractor = NumberExtractor()

    override fun interact(from: Word) =
            if (TokenConstants.contains(from.text)) {
                if (TokenConstants.Keyword.contains(from.text)) keywordExtractor.interact(from)
                else if (TokenConstants.Operator.contains(from.text)) operatorExtractor.interact(from)
                else if (TokenConstants.Punctuation.contains(from.text)) punctuationExtractor.interact(from)
                else Unknown(from.text, from.line)
            } else {
                if (IdentifierExtractor.isIdentifier(from)) identifierExtractor.interact(from)
                else if (CharacterExtractor.isCharacter(from)) characterExtractor.interact(from)
                else if (NumberExtractor.isNumber(from)) numberExtractor.interact(from)
                else Unknown(from.text, from.line)
            }

}