package lexicalanalyzer.tokenizer.extractor.operator

import entity.*
import entity.ArithmeticOperator
import entity.TokenConstants.Operator.ArithmeticOperator as A

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class ArithmeticOperatorExtractor : OperatorExtractor<ArithmeticOperator> {

    override fun interact(from: Word) = when(from.text) {
        TokenConstants.Operator.ArithmeticOperator.Plus -> Plus(from.line)
        TokenConstants.Operator.ArithmeticOperator.Minus -> Minus(from.line)
        TokenConstants.Operator.ArithmeticOperator.Multiply -> Multiply(from.line)
        TokenConstants.Operator.ArithmeticOperator.Divide -> Divide(from.line)
        TokenConstants.Operator.ArithmeticOperator.PlusPlus -> PlusPlus(from.line)
        TokenConstants.Operator.ArithmeticOperator.MinusMinus -> MinusMinus(from.line)
        else -> throw IllegalStateException(
                "Required token: ArithmeticOperator but it was: ${from.text} at line: ${from.line}")
    }

}