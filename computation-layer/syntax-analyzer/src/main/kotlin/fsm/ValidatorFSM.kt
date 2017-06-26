package fsm

import entity.*
import entity.Number
import expression.ExpressionResult
import expression.Failure
import expression.Success
import expression.resolver.BaseExpressionResolver
import org.statefulj.fsm.FSM
import org.statefulj.fsm.model.Action
import org.statefulj.fsm.model.State
import org.statefulj.fsm.model.Transition
import org.statefulj.fsm.model.impl.StateActionPairImpl
import org.statefulj.fsm.model.impl.StateImpl
import javax.script.ScriptEngineManager

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/21/17.
 * Todo: Save failure result for trap state moves
 * Todo: Change currentIndex after onEvent call
 */
class ValidatorFSM {

    private class StateActionPair(state: State<ValidatorStateful>, action: Action<ValidatorStateful>? = null) :
            StateActionPairImpl<ValidatorStateful>(state, action)

    object States {
        val trapState = "TRAP"
        val start = "Start"
        val declare = "Declare"
        val declared = "Declared"
        val exp1 = "Expression Type 1"
        val assign = "Assign"
        val exp2 = "Expression Type 2"
        val aUnionExp = "After Union Expression"
        val bUnionExp = "Before Union Expression"
        val unionIdentifier = "Union Identifier"
        val conditionalFlow = "Conditional Flow"
        val condition = "Condition"
        val afterCondition = "After Condition"
        val elseState = "Else"
    }

    companion object {

        inline fun <T> State<T>.addTransitions(events: List<String>, transition: Transition<T>) =
                events.forEach { addTransition(it, transition) }

        inline fun <T> State<T>.addTransitions(events: List<String>, state: State<T>, action: Action<T>) =
                events.forEach { addTransition(it, state, action) }

        inline fun <T, reified R : Any> State<T>.addTransition(event: R, transition: Transition<T>) =
                addTransition(event.className(), transition)

        inline fun <T, reified R : Any> State<T>.addTransition(event: R, state: State<T>) =
                addTransition(event.className(), state)

        inline fun <T, reified R : Any> State<T>.addTransition(event: R, state: State<T>, action: Action<T>) =
                addTransition(event.className(), state, action)

        inline fun <reified T : Any> T.className() = T::class.java.toString()

        val identifierDeclarationAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            identifierType = when (event) {
                INT().className() -> ValueType.INT
                BOOL().className() -> ValueType.BOOL
                CHAR().className() -> ValueType.CHAR
                else -> ValueType.UNKNOWN
            }
        }}

        val identifierIndexSaver = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            identifierIndex = currentIndex
            (tokens[currentIndex] as Identifier).type = identifierType

        }}

        val identifierValueSaver = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            (tokens[stateful.identifierIndex] as Identifier).value = (expressionResult as Success).result
        }}

        val identifierAssignmentAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            variableDeclarationMode = false
            valuedIdentifier = (tokens[currentIndex] as Identifier).value.isNotBlank()
            identifierIndex = currentIndex
            // Todo: Single if and after it else?
            elsePossible = false
        }}

        val assignmentOperatorAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            val identifier = tokens[identifierIndex] as Identifier
            identifierType = identifier.type
            operatorType = when (event) {
                Assign().className() -> Assign()
                PlusAssign().className() -> PlusAssign()
                MinusAssign().className() -> MinusAssign()
                DivideAssign().className() -> DivideAssign()
                MultiplyAssign().className() -> MultiplyAssign()
                else -> operatorType
            }
            expressionResult = handleExpression(stateful, listOf(Semicolon()))
        }}

        val aUnionExpressionAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            val engine = ScriptEngineManager().getEngineByName("js")
            val identifier = tokens[identifierIndex] as Identifier
            val number =
                    if (event == PlusPlus().className()) engine.eval(identifier.value + " + 1").toString()
                    else engine.eval(identifier.value + " - 1").toString()
            identifier.value = Number("?", number).number
        }}
        
        val bUnionExpressionAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            unionExpressionType = when (event) {
                PlusPlus().className() -> PlusPlus()
                else -> MinusMinus()
            }
            variableDeclarationMode = false
            // Todo: Single if and after it else?
            elsePossible = false
        }}

        val identifierUnionExpression = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            val engine = ScriptEngineManager().getEngineByName("js")
            val identifier = tokens[currentIndex] as Identifier
            val number =
                    if (unionExpressionType is PlusPlus) engine.eval(identifier.value + " + 1").toString()
                    else engine.eval(identifier.value + " - 1").toString()
            identifier.value = Number("?", number).number
        }}

        val flowSaverAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            variableDeclarationMode = false
            flowType = when (event) {
                WHILE().className() -> WHILE()
                IF().className() -> IF()
                else -> {
                    println("WTF @ValidatorFSM::flowSaverAction")
                    flowType
                }
            }
            elsePossible = false
        }}

        val flowAdderAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            val scope = when (flowType) {
                is IF -> MultipleIf()
                is WHILE -> MultipleWhile()
                else -> {
                    println("WTF @ValidatorFSM::flowAdderAction")
                    UnknownScope()
                }
            }
            scopeStack.push(scope)
        }}

        // Todo: Save if for code generation
        val elseAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            elsePossible = false
        }}

        val elseAdderAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            scopeStack.push(MultipleElse())
        }}

        val braceYourselfActionIsComing = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            if (scopeStack.pop() is MultipleIf) elsePossible = true
        }}

        val exp1Action = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            stateful.operatorType = Assign()
            expressionResult = handleExpression(stateful, listOf(Semicolon(), Comma()))
        }}

        val conditionalExpressionAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            expressionResult = handleExpression(stateful, listOf(ParenthesisClose()), true)
        }}

        fun getErrorAction(message: String, errorType: ErrorType = ErrorType.Syntax) =
                Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
                    error = CompileError(errorType, message, tokens[currentIndex])
                }}

        fun getFSM(): FSM<ValidatorStateful> {

            val trapState = StateImpl<ValidatorStateful>(States.trapState, false, true)
            val start = StateImpl<ValidatorStateful>(States.start, true)
            val declare = StateImpl<ValidatorStateful>(States.declare)
            val declared = StateImpl<ValidatorStateful>(States.declared)
            val exp1 = StateImpl<ValidatorStateful>(States.exp1)
            val assign = StateImpl<ValidatorStateful>(States.assign)
            val exp2 = StateImpl<ValidatorStateful>(States.exp2)
            val aUnionExp = StateImpl<ValidatorStateful>(States.aUnionExp)
            val bUnionExp = StateImpl<ValidatorStateful>(States.bUnionExp)
            val unionIdentifier = StateImpl<ValidatorStateful>(States.unionIdentifier)
            val conditionalFlow = StateImpl<ValidatorStateful>(States.conditionalFlow)
            val condition = StateImpl<ValidatorStateful>(States.condition)
            val afterCondition = StateImpl<ValidatorStateful>(States.afterCondition)
            val elseState = StateImpl<ValidatorStateful>(States.elseState)

            start.addTransitions(listOf(INT().className(), BOOL().className(), CHAR().className()),
                    Transition { stateful, event, args ->
                        if (stateful.variableDeclarationMode) StateActionPair(declare, identifierDeclarationAction)
                        else StateActionPair(
                                trapState, getErrorAction("Not variable declaration mode, required an expression"))
                    })

            declare.addTransition(Identifier(""), Transition { stateful, event, args ->
                val identifier = stateful.tokens[stateful.currentIndex] as Identifier
                if (isVariableDeclared(stateful.tokens, stateful.currentIndex, identifier.name))
                    StateActionPair(trapState, getErrorAction("Variable is already declared"))
                else StateActionPair(declared, identifierIndexSaver)
            })

            declared.addTransition(Comma(), declare)
            declared.addTransition(Assign(), exp1, exp1Action)
            declared.addTransition(Semicolon(), start)

            exp1.addTransitions(listOf(Comma().className(), Semicolon().className()), Transition { stateful, event, a ->
                if (stateful.faultyExpression) StateActionPair(trapState)
                else StateActionPair(if (event == Semicolon().className()) start else declare, identifierValueSaver)
            })

            start.addTransition(Identifier(""), Transition { stateful, event, args ->
                val identifier = stateful.tokens[stateful.currentIndex] as Identifier
                if (isVariableDeclared(stateful.tokens, stateful.currentIndex, identifier.name))
                     StateActionPair(assign, identifierAssignmentAction)
                else StateActionPair(trapState, getErrorAction("Variable is not declared"))
            })

            assign.addTransitions(listOf(Assign().className(), PlusAssign().className(),
                    MinusAssign().className(), DivideAssign().className(), MultiplyAssign().className()),
                    Transition { stateful, event, args ->
                        if (event != Assign().className())
                            if (stateful.valuedIdentifier) StateActionPair(exp2, assignmentOperatorAction)
                            else StateActionPair(
                                    trapState, getErrorAction("Variable doesn't have any value", ErrorType.Semantic))
                        else StateActionPair(exp2, assignmentOperatorAction)
                    })

            assign.addTransitions(listOf(PlusPlus().className(), MinusMinus().className()),
                    Transition { stateful, event, args ->
                        if (stateful.identifierType == ValueType.INT)
                            if (stateful.valuedIdentifier) StateActionPair(aUnionExp, aUnionExpressionAction)
                            else StateActionPair(
                                    trapState, getErrorAction("Variable doesn't have any value", ErrorType.Semantic))
                        else StateActionPair(trapState, getErrorAction("Variable should be of Int type", ErrorType.Semantic))
                    })

            exp2.addTransition(Semicolon(), Transition { stateful, event, args ->
                if (stateful.faultyExpression) StateActionPair(trapState)
                else StateActionPair(start, identifierValueSaver)
            })

            aUnionExp.addTransition(Semicolon(), start)

            start.addTransitions(
                    listOf(PlusPlus().className(), MinusMinus().className()), bUnionExp, bUnionExpressionAction)

            bUnionExp.addTransition(Identifier(""), Transition { stateful, event, args ->
                val token = stateful.tokens[stateful.currentIndex]
                if (token is Identifier)
                    if (token.type == ValueType.INT)
                        if (token.value.isNotBlank()) StateActionPair(unionIdentifier, identifierUnionExpression)
                        else StateActionPair(
                                trapState, getErrorAction("Variable doesn't have any value", ErrorType.Semantic))
                    else StateActionPair(
                            trapState, getErrorAction("Variable should be of Int type", ErrorType.Semantic))
                else StateActionPair(trapState, getErrorAction("Expected an identifier"))
            })

            unionIdentifier.addTransition(Semicolon(), start)

            start.addTransitions(listOf(WHILE().className(), IF().className()), conditionalFlow, flowSaverAction)
            conditionalFlow.addTransition(ParenthesisOpen(), condition, conditionalExpressionAction)

            condition.addTransition(ParenthesisClose(), Transition { stateful, event, args ->
                if (stateful.faultyExpression) StateActionPair(trapState)
                else StateActionPair(afterCondition)
            })

            afterCondition.addTransition(BraceOpen(), start, flowAdderAction)

            start.addTransition(ELSE(), Transition { stateful, event, args ->
                if (stateful.elsePossible) StateActionPair(elseState, elseAction)
                else StateActionPair(trapState, getErrorAction("Else wasn't allowed here"))
            })

            elseState.addTransition(BraceOpen(), start, elseAdderAction)

            start.addTransition(BraceClose(), Transition { stateful, event, args ->
                if (stateful.scopeStack.isNotEmpty() && stateful.scopeStack.peek() is Multiple)
                    StateActionPair(start, braceYourselfActionIsComing)
                else StateActionPair(trapState, getErrorAction("\"}\" wasn't allowed here"))
            })

            return FSM.FSMBuilder<ValidatorStateful>()
                    .addState(start, true)
                    .addState(declare)
                    .addState(declared)
                    .addState(exp1)
                    .addState(assign)
                    .addState(exp2)
                    .addState(aUnionExp)
                    .addState(bUnionExp)
                    .addState(unionIdentifier)
                    .addState(conditionalFlow)
                    .addState(condition)
                    .addState(afterCondition)
                    .addState(elseState)
                    .addState(trapState)
                    .build()

        }

        fun handleExpression(stateful: ValidatorStateful,
                             endTokens: List<Token>, condition: Boolean = false): ExpressionResult {

            var expressionResult: ExpressionResult = Failure(CompileError(""))

            with (stateful) {
                val expressionResolver = BaseExpressionResolver(tokens)
                val valueType = if (condition) ValueType.BOOL else identifierType
                expressionResult = expressionResolver.interact(currentIndex+1, valueType, endTokens)
            }

            return when (expressionResult) {
                is Failure -> expressionResult
                is Success -> {
                    val identifier = stateful.tokens[stateful.identifierIndex] as Identifier
                    if (condition || stateful.operatorType is Assign || identifier.type != ValueType.INT)
                        expressionResult
                    else processAssignment(stateful.operatorType,
                            expressionResult as Success, identifier.value, identifier.line)
                }
            }

        }

        fun evaluate(expression: String) = ScriptEngineManager().getEngineByName("js").eval(expression).toString()

        fun processAssignment(type: AssignmentOperator, result: Success, value: String, line: Int) = when (type) {
            is PlusAssign -> Success(Number("", evaluate(value + "+" + result.result)).number, result.endToken)
            is MinusAssign -> Success(Number("", evaluate(value + "-" + result.result)).number, result.endToken)
            is MultiplyAssign -> Success(Number("", evaluate(value + "*" + result.result)).number, result.endToken)
            is DivideAssign ->
                    if (evaluate(result.result).toInt() == 0)
                        Failure(CompileError("SEMANTIC ERROR: Division by zero at line $line"))
                    else Success(Number("", evaluate(value + "/" + result.result)).number, result.endToken)
            else -> Failure(CompileError("WTF @ValidatorFSM::processComplexAssignment opeartor type: $type"))
        }

        fun isVariableDeclared(tokens: List<Token>, currentIndex: Int, id: String) =
            tokens.subList(0, currentIndex).any { it is Identifier && it.name == id }

    }

}