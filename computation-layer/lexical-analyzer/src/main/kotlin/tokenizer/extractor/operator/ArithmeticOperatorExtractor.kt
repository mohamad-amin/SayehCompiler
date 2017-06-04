package tokenizer.extractor.operator

import entity.*
import entity.ArithmeticOperator
import entity.TokenConstants.Operator.ArithmeticOperator as A

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class ArithmeticOperatorExtractor : OperatorExtractor<ArithmeticOperator> {

    override fun interact(from: Word) = when(from.text) {
        A.Plus -> Plus(from.line)
        A.Minus -> Minus(from.line)
        A.Multiply -> Multiply(from.line)
        A.Divide -> Divide(from.line)
        A.PlusPlus -> PlusPlus(from.line)
        A.MinusMinus -> MinusMinus(from.line)
        else -> throw IllegalStateException(
                "Required token: ArithmeticOperator but it was: ${from.text} at line: ${from.line}")
    }

}