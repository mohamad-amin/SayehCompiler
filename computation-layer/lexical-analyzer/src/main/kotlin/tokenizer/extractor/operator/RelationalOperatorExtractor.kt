package tokenizer.extractor.operator

import entity.*
import entity.RelationalOperator
import entity.TokenConstants.Operator.RelationalOperator as R

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class RelationalOperatorExtractor : OperatorExtractor<RelationalOperator> {

    override fun interact(from: Word) = when(from.text) {
        R.Equal -> Equal(from.line)
        R.NotEqual -> NotEqual(from.line)
        R.Bigger -> Bigger(from.line)
        R.Smaller -> Smaller(from.line)
        R.BiggerEqual -> BiggerEqual(from.line)
        R.SmallerEqual -> SmallerEqual(from.line)
        else -> throw IllegalStateException(
                "Required token: RelationalOperator but it was: ${from.text} at line: ${from.line}")
    }

}