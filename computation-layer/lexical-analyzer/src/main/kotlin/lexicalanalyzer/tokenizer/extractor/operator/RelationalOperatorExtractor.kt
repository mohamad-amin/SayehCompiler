package lexicalanalyzer.tokenizer.extractor.operator

import entity.*
import entity.RelationalOperator
import entity.TokenConstants.Operator.RelationalOperator as R

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class RelationalOperatorExtractor : OperatorExtractor<RelationalOperator> {

    override fun interact(from: Word) = when(from.text) {
        TokenConstants.Operator.RelationalOperator.Equal -> Equal(from.line)
        TokenConstants.Operator.RelationalOperator.NotEqual -> NotEqual(from.line)
        TokenConstants.Operator.RelationalOperator.Bigger -> Bigger(from.line)
        TokenConstants.Operator.RelationalOperator.Smaller -> Smaller(from.line)
        TokenConstants.Operator.RelationalOperator.BiggerEqual -> BiggerEqual(from.line)
        TokenConstants.Operator.RelationalOperator.SmallerEqual -> SmallerEqual(from.line)
        else -> throw IllegalStateException(
                "Required token: RelationalOperator but it was: ${from.text} at line: ${from.line}")
    }

}