package fsm

import entity.*
import expression.ExpressionResult
import expression.Failure
import expression.Success
import org.statefulj.persistence.annotations.State
import java.util.*

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */

class ValidatorStateful(val tokens: List<Token>) {

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
        set(value) {
            if (value is Failure) {
                faultyExpression = true
                error = value.error
            }
            nextIndex = if (value is Success) value.endToken else nextIndex
            field = value
        }

    var unionExpressionType: ArithmeticOperator = Plus()
    var flowType: Keyword = INT()
    var error = CompileError("")

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
    class UnknownScope: Scope()
