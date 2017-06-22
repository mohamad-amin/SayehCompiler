package fsm

import entity.*
import entity.Number
import expression.Success
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

    companion object {

        inline fun <T, reified R : Any> State<T>.addTransitions(events: List<R>, transition: Transition<T>) =
                events.forEach { addTransition(it, transition) }

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
            (args[0] as Identifier).type = identifierType

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
            val identifier = args[0] as Identifier
            identifierType = identifier.type
            operatorType = when (event) {
                Assign().className() -> Assign()
                PlusAssign().className() -> PlusAssign()
                MinusAssign().className() -> MinusAssign()
                DivideAssign().className() -> DivideAssign()
                MultiplyAssign().className() -> MultiplyAssign()
                else -> operatorType
            }
        }}
        
        val aUnionExpressionAction = Action<ValidatorStateful> { stateful, event, args -> with (stateful) {
            val engine = ScriptEngineManager().getEngineByName("js")
            val identifier = tokens[identifierIndex] as Identifier
            val number =
                    if (event == PlusPlus().className()) engine.eval(identifier.value + " + 1") as String
                    else engine.eval(identifier.value + " - 1") as String
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
                    if (unionExpressionType is PlusPlus) engine.eval(identifier.value + " + 1") as String
                    else engine.eval(identifier.value + " - 1") as String
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
            when (flowType) {
                is IF -> MultipleIf()
                is WHILE -> MultipleWhile()
                else -> {
                    println("WTF @ValidatorFSM::flowAdderAction")
                    UnknownScope()
                }
            }
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

        fun getFSM(): FSM<ValidatorStateful> {

            val trapState = StateImpl<ValidatorStateful>("TRAP", false, true)
            val start = StateImpl<ValidatorStateful>("Start", true)
            val declare = StateImpl<ValidatorStateful>("Declare")
            val declared = StateImpl<ValidatorStateful>("Declared")
            val exp1 = StateImpl<ValidatorStateful>("Expression Type 1")
            val assign = StateImpl<ValidatorStateful>("Assign")
            val exp2 = StateImpl<ValidatorStateful>("Expression Type 2")
            val aUnionExp = StateImpl<ValidatorStateful>("After Union Expression")
            val bUnionExp = StateImpl<ValidatorStateful>("Before Union Expression")
            val unionIdentifier = StateImpl<ValidatorStateful>("Union Identifier")
            val conditionalFlow = StateImpl<ValidatorStateful>("Conditional Flow")
            val condition = StateImpl<ValidatorStateful>("Condition")
            val afterCondition = StateImpl<ValidatorStateful>("After Condition")
            val elseState = StateImpl<ValidatorStateful>("Else")

            start.addTransition(listOf(INT(), BOOL(), CHAR()), Transition { stateful, event, args ->
                if (stateful.variableDeclarationMode) {
                    StateActionPair(declare, identifierDeclarationAction)
                } else StateActionPair(trapState) // not variable declaration mode
            })

            // Todo: Pass identifier as vararg in the transition
            declare.addTransition(Identifier(""), Transition { stateful, event, args ->
                if (isVariableDeclared(stateful.tokens, stateful.currentIndex, (args[0] as Identifier).name))
                    StateActionPair(trapState) // already declared
                else StateActionPair(declared, identifierIndexSaver)
            })

            declared.addTransition(Comma(), declare)
            // Todo: Expression Action
            declare.addTransition(Assign(), exp1)
            declare.addTransition(Semicolon(), start)

            exp1.addTransition(listOf(Comma(), Semicolon()), Transition { stateful, event, args ->
                if (stateful.faultyExpression) StateActionPair(trapState)
                else StateActionPair(if (event == Semicolon().className()) start else declare, identifierValueSaver)
            })

            // Todo: Pass identifier as vararg in the transition
            start.addTransition(Identifier(""), Transition { stateful, event, args ->
                if (isVariableDeclared(stateful.tokens, stateful.currentIndex, (args[0] as Identifier).name))
                     StateActionPair(assign, identifierAssignmentAction)
                else StateActionPair(trapState) // not declared
            })

            // Todo: Expression Action
            // Todo: Pass identifier as vararg in the transition
            assign.addTransition(listOf(Assign(), PlusAssign(), MinusAssign(), DivideAssign(), MultiplyAssign()),
                    Transition { stateful, event, args ->
                        if (event != Assign().className())
                            if (stateful.valuedIdentifier) StateActionPair(exp2, assignmentOperatorAction)
                            else StateActionPair(trapState) // Needed value for identifier but nothing's there
                        else StateActionPair(exp2, assignmentOperatorAction)
                    })

            assign.addTransition(listOf(PlusPlus(), MinusMinus()), Transition { stateful, event, args ->
                if (stateful.identifierType == ValueType.INT)
                    if (stateful.valuedIdentifier) StateActionPair(aUnionExp, aUnionExpressionAction)
                    else StateActionPair(trapState) // Needed valued identifier
                else StateActionPair(trapState) // Needed int to perform these operations on
            })

            exp2.addTransition(Semicolon(), Transition { stateful, event, args ->
                if (stateful.faultyExpression) StateActionPair(trapState)
                else StateActionPair(start, identifierValueSaver)
            })

            aUnionExp.addTransition(Semicolon(), start)

            start.addTransition(listOf(PlusPlus(), MinusMinus()), bUnionExp, bUnionExpressionAction)

            bUnionExp.addTransition(Identifier(""), Transition { stateful, event, args ->
                val token = stateful.tokens[stateful.currentIndex]
                if (token is Identifier)
                    if (token.type == ValueType.INT)
                        if (token.value.isNotBlank()) StateActionPair(unionIdentifier, identifierUnionExpression)
                        else StateActionPair(trapState) // It should have value
                    else StateActionPair(trapState) // It should be an int
                else StateActionPair(trapState) // It should be identifier
            })

            unionIdentifier.addTransition(Semicolon(), start)

            start.addTransition(listOf(WHILE(), IF()), conditionalFlow, flowSaverAction)
            // Todo: Expression Action
            conditionalFlow.addTransition(ParenthesisOpen(), condition)

            condition.addTransition(ParenthesisClose(), Transition { stateful, event, args ->
                if (stateful.faultyExpression) StateActionPair(trapState)
                else StateActionPair(afterCondition)
            })

            afterCondition.addTransition(BraceOpen(), start, flowAdderAction)

            start.addTransition(ELSE(), Transition { stateful, event, args ->
                if (stateful.elsePossible) StateActionPair(elseState, elseAction)
                else StateActionPair(trapState) // Unexpected token: Else
            })

            elseState.addTransition(BraceOpen(), start, elseAdderAction)

            start.addTransition(BraceClose(), Transition { stateful, event, args ->
                if (stateful.scopeStack.peek() is Multiple) StateActionPair(start, braceYourselfActionIsComing)
                else StateActionPair(trapState) // Closed Brace wasn't allowed
            })

//            return FSM.FSMBuilder<ValidatorStateful>()
//                    .addState(stateS, true)
//                    .addState(stateN)
//                    .addState(stateO)
//                    .build()

            TODO()

        }

        fun isVariableDeclared(tokens: List<Token>, currentIndex: Int, id: String) =
            tokens.subList(0, currentIndex).any { it is Identifier && it.name == id }

    }

}