package tokenizer.extractor.operator

import entity.*
import entity.TokenConstants.Operator.AssignmentOperator as A

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class AssignmentOperatorExtractor: OperatorExtractor<AssignmentOperator> {

    override fun interact(from: Word) = when(from.text) {
        A.Assign -> Assign(from.line)
        A.PlusAssign -> PlusAssign(from.line)
        A.MinusAssign -> MinusAssign(from.line)
        A.MultiplyAssign -> MultiplyAssign(from.line)
        A.DivideAssign -> DivideAssign(from.line)
        A.ModuloAssign -> ModuloAssign(from.line)
        else -> throw IllegalStateException(
                "Required token: AssignmentOperator but it was: ${from.text} at line: ${from.line}")
    }

}