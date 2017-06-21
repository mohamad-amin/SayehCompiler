package fsm

import entity.*
import expression.ExpressionResult
import expression.Failure
import org.statefulj.persistence.annotations.State
import java.util.*

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */

class ValidatorStateful(val tokens: MutableList<Token>) {

    @State
    var state: String? = ""

    var identifierType = ValueType.UNKNOWN
    var currentIndex = 0
    var nextIndex = 1
    var identifierIndex = -1
    var variableDeclarationMode = true
    var elsePossible = false
    var valuedIdentifier = false
    var operatorType: AssignmentOperator = ModuloAssign()
    var faultyExpression = false
    var expressionResult: ExpressionResult = Failure(CompileError(""))
    var unionExpressionType: ArithmeticOperator = Plus()

    val scopeStack: Stack<Scope> = Stack()

}

sealed class Scope
    sealed class Multiple: Scope()
        class MultipleIf: Multiple()
        class MultipleWhile: Multiple()
        class MultipleElse: Multiple()
    sealed class Single: Scope()
        class SingleIf: Single()
        class SingleWhile: Single()
        class SingleElse: Single()
