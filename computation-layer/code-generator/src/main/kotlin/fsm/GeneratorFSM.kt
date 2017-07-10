package fsm

import entity.*
import expression.solver.ExpressionCode
import expression.solver.ExpressionCompiler
import memory.MemoryManager
import org.statefulj.fsm.FSM
import org.statefulj.fsm.model.Action
import org.statefulj.fsm.model.State
import org.statefulj.fsm.model.Transition
import org.statefulj.fsm.model.impl.StateActionPairImpl
import org.statefulj.fsm.model.impl.StateImpl

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/21/17.
 * Todo: Save failure result for trap state moves
 * Todo: Change currentIndex after onEvent call
 */
class GeneratorFSM {

    private class StateActionPair(state: State<GeneratorStateful>, action: Action<GeneratorStateful>? = null) :
            StateActionPairImpl<GeneratorStateful>(state, action)

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


        inline fun <T> State<T>.addTransitions(events: List<String>, state: State<T>) =
                events.forEach { addTransition(it, state) }

        inline fun <T> State<T>.addTransitions(events: List<String>, state: State<T>, action: Action<T>) =
                events.forEach { addTransition(it, state, action) }

        inline fun <T, reified R : Any> State<T>.addTransition(event: R, transition: Transition<T>) =
                addTransition(event.className(), transition)

        inline fun <T, reified R : Any> State<T>.addTransition(event: R, state: State<T>) =
                addTransition(event.className(), state)

        inline fun <T, reified R : Any> State<T>.addTransition(event: R, state: State<T>, action: Action<T>) =
                addTransition(event.className(), state, action)

        inline fun <reified T : Any> T.className() = T::class.java.toString()

        val memory = MemoryManager.instance

        val identifierIndexSaver = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            identifierIndex = currentIndex
            (tokens[currentIndex] as Identifier).memoryAddress = memory.getNextEmptyRamSlot(currentIndex)
        }}

        val identifierAssignmentAction = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            identifierIndex = currentIndex
        }}

        val assignmentOperatorAction = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
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

        val aUnionExpressionAction = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            unionExpressionType = when (event) {
                PlusPlus().className() -> PlusPlus()
                else -> MinusMinus()
            }
            handleUnionExpression(stateful)
        }}
        
        val bUnionExpressionAction = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            unionExpressionType = when (event) {
                PlusPlus().className() -> PlusPlus()
                else -> MinusMinus()
            }
        }}

        val identifierUnionExpression = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            identifierIndex = currentIndex
            handleUnionExpression(stateful)
        }}

        val flowSaverAction = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            flowType = when (event) {
                WHILE().className() -> {
                    scopeStack.push(WhileG(code.lines().size - 1))
                    WHILE()
                }
                IF().className() -> {
                    scopeStack.push(IfG())
                    IF()
                }
                else -> {
                    println("WTF @ValidatorFSM::flowSaverAction")
                    flowType
                }
            }
        }}

        val braceYourselfActionIsComing = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            val topStack = scopeStack.pop()
            when (topStack) {
                is WhileG -> {
                    val endOfScope = code.lines().size
                    code += "BUN ${Integer.toBinaryString(endOfScope).takeLast(8)}" // Todo: Check this 8
                    val lines = code.lines().toMutableList()
                    lines[endOfScope] = "BUN ${Integer.toBinaryString(topStack.escapeIndex).takeLast(8)}" // Todo: Check
                    code = lines.joinToString("\n")
                }
                is IfG -> {
                    val endOfScope = code.lines().size
                    val lines = code.lines().toMutableList()
                    val afterShould = if (tokens[nextIndex] is ELSE) {
                        code += "BUN XXXXX"
                        scopeStack.push(ElseG(endOfScope))
                        endOfScope + 1
                    } else endOfScope
                    lines[topStack.afterIndex] = "BUN ${Integer.toBinaryString(afterShould).takeLast(8)}"
                    code = lines.joinToString("\n")
                }
                is ElseG -> {
                    val endOfScope = code.lines().size
                    val lines = code.lines().toMutableList()
                    lines[topStack.afterIndex] = "BUN ${Integer.toBinaryString(endOfScope).takeLast(8)}"
                    code = lines.joinToString("\n")
                }
            }
        }}

        val exp1Action = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            stateful.operatorType = Assign()
            expressionResult = handleExpression(stateful, listOf(Semicolon(), Comma()))
        }}

        val conditionalExpressionAction = Action<GeneratorStateful> { stateful, event, args -> with (stateful) {
            expressionResult = handleExpression(stateful, listOf(ParenthesisClose()), true)
            val topStack = scopeStack.peek()
            when (topStack) {
                is WhileG -> {
                    topStack.escapeIndex = code.lines().size
                    code += "\nBUN XXXXX"
                }
                is IfG -> {
                    val lines = code.lines().size
                    topStack.afterIndex = lines + 1
                    code += "\nBRZ ${lines + 2}\nBUN XXXXX"
                }
            }
        }}

        fun getFSM(): FSM<GeneratorStateful> {

            val trapState = StateImpl<GeneratorStateful>(States.trapState, false, true)
            val start = StateImpl<GeneratorStateful>(States.start, true)
            val declare = StateImpl<GeneratorStateful>(States.declare)
            val declared = StateImpl<GeneratorStateful>(States.declared)
            val exp1 = StateImpl<GeneratorStateful>(States.exp1)
            val assign = StateImpl<GeneratorStateful>(States.assign)
            val exp2 = StateImpl<GeneratorStateful>(States.exp2)
            val aUnionExp = StateImpl<GeneratorStateful>(States.aUnionExp)
            val bUnionExp = StateImpl<GeneratorStateful>(States.bUnionExp)
            val unionIdentifier = StateImpl<GeneratorStateful>(States.unionIdentifier)
            val conditionalFlow = StateImpl<GeneratorStateful>(States.conditionalFlow)
            val condition = StateImpl<GeneratorStateful>(States.condition)
            val afterCondition = StateImpl<GeneratorStateful>(States.afterCondition)
            val elseState = StateImpl<GeneratorStateful>(States.elseState)

            start.addTransitions(listOf(INT().className(), BOOL().className(), CHAR().className()), declared)

            declare.addTransition(Identifier(""), declared, identifierIndexSaver)

            declared.addTransition(Comma(), declare)
            declared.addTransition(Assign(), exp1, exp1Action)
            declared.addTransition(Semicolon(), start)

            exp1.addTransitions(listOf(Comma().className(), Semicolon().className()), Transition { stateful, event, a ->
                StateActionPair(if (event == Semicolon().className()) start else declare)
            })

            start.addTransition(Identifier(""), assign, identifierAssignmentAction)

            assign.addTransitions(listOf(Assign().className(), PlusAssign().className(),
                    MinusAssign().className(), DivideAssign().className(), MultiplyAssign().className()),
                    exp2, assignmentOperatorAction)

            assign.addTransitions(listOf(PlusPlus().className(), MinusMinus().className()),
                    aUnionExp, aUnionExpressionAction)

            exp2.addTransition(Semicolon(), start)

            aUnionExp.addTransition(Semicolon(), start)

            start.addTransitions(
                    listOf(PlusPlus().className(), MinusMinus().className()), bUnionExp, bUnionExpressionAction)

            bUnionExp.addTransition(Identifier(""), unionIdentifier, identifierUnionExpression)

            unionIdentifier.addTransition(Semicolon(), start)

            // Todo: here

            start.addTransitions(listOf(WHILE().className(), IF().className()), conditionalFlow, flowSaverAction)
            conditionalFlow.addTransition(ParenthesisOpen(), condition, conditionalExpressionAction)

            condition.addTransition(ParenthesisClose(), afterCondition)

            afterCondition.addTransition(BraceOpen(), start)

            start.addTransition(ELSE(), elseState)

            elseState.addTransition(BraceOpen(), start)

            start.addTransition(BraceClose(), start, braceYourselfActionIsComing)

            return FSM.FSMBuilder<GeneratorStateful>()
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

        fun handleUnionExpression(stateful: GeneratorStateful) {
            with (stateful) {
                val identifier = (tokens[identifierIndex] as Identifier).memoryAddress
                val reg1 = memory.getNextEmptyRegister(identifierIndex)
                val reg2 = memory.getNextEmptyRegister(identifierIndex + 1)
                code += "\n${moveMemoryToRegister(identifier, reg1)}\n" +
                        moveImmediateValueToRegister("0000000000000001", reg2) +
                        "\nR$reg1 <= R$reg1 ${if (unionExpressionType is PlusPlus) "+" else "-"} R$reg2" +
                        "\nMEM[$identifier] <= R$reg1"
                memory.freeRegisters(listOf(reg1, reg2))
            }
        }

        fun handleSingleExpression(stateful: GeneratorStateful, expResult: ExpressionCode): ExpressionCode {
            var code = expResult.code
            val reg1 = memory.getNextRegisterBeside(expResult.nextIndex, stateful.identifierIndex)
            with (stateful) {
                val identifier = (tokens[identifierIndex] as Identifier).memoryAddress
                code += "\n${moveMemoryToRegister(identifier, reg1)}\n" +
                        "\nR$reg1 <= R$reg1 $operatorType R${expResult.address}" +
                        "\nMEM[$identifier] <= R$reg1"
                memory.freeRegisters(expResult.disposables.plus(reg1))
            }
            return ExpressionCode(code, expResult.nextIndex)
        }

        fun handleConditionExpression(stateful: GeneratorStateful, expResult: ExpressionCode): ExpressionCode {
            var code = expResult.code
            val reg1 = memory.getNextRegisterBeside(expResult.nextIndex, stateful.identifierIndex)
            with (stateful) {
                code += "\n${moveImmediateValueToRegister("0000000000000001", reg1)}\n" +
                        "\nCMP R$reg1, R${expResult.address}"
                memory.freeRegisters(expResult.disposables.plus(reg1))
            }
            return ExpressionCode(code, expResult.nextIndex)

        }

        fun moveImmediateValueToRegister(value: String, register: Int) =
                "R$register(7:0) <= ${value.takeLast(8)}\n" +
                        "R$register(15:8) <= ${value.take(8)}"


        fun moveMemoryToRegister(memoryAddress: Int, register: Int) =
                "R$register <= MEM[$memoryAddress]"

        fun handleExpression(stateful: GeneratorStateful,
                             endTokens: List<Token>, condition: Boolean = false): ExpressionCode {

            var expressionResult = stateful.expressionResult

            with (stateful) {
                val expressionCompiler = ExpressionCompiler(tokens)
                val valueType = if (condition) ValueType.BOOL else (tokens[identifierIndex] as Identifier).type
                expressionResult = expressionCompiler.compile(currentIndex+1, valueType, endTokens)
            }

            val expRes = if (condition) handleConditionExpression(stateful, expressionResult)
            else
                if (stateful.operatorType is Assign) {
                    val address = (stateful.tokens[stateful.identifierIndex] as Identifier).memoryAddress
                    val code = expressionResult.code + "\nMEM[$address] <= ${expressionResult.address}"
                    memory.freeRegisters(expressionResult.disposables)
                    ExpressionCode(code, expressionResult.nextIndex)
                } else handleSingleExpression(stateful, expressionResult)

            stateful.code += "\n${expRes.code}"
            return expRes

        }

    }

}