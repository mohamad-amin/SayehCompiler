package expression.solver

import entity.*
import entity.Number
import expression.converter.base.TokenConverter
import expression.resolver.NumericPostfixGenerator
import lexicalanalyzer.tokenizer.WordTokenizer
import memory.MemoryManager

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class ExpressionCompiler(var tokens: List<Token>) {

    val memory = MemoryManager.instance
    val converter = TokenConverter()

    /**
     * @return index of the next token after expression
     */
    fun compile(startIndex: Int, expressionType: ValueType, endTokens: List<Token>): ExpressionCode {

        val expression = getNeededTokens(startIndex, endTokens)
        expression.forEachIndexed { index, token -> token.index = startIndex + index }

//        println("Expression: ${expression.joinToString(" ") { it.word }}") // Todo: Remove this line in final build

        return when (expressionType) {
            ValueType.INT -> interactIntExpr(expression, startIndex)
            ValueType.CHAR -> interactCharExpr(expression, startIndex)
            ValueType.BOOL -> interactBoolExpr(expression, startIndex)
            ValueType.UNKNOWN -> throw IllegalArgumentException("WTF :| Needed unknown value? token: ${expression[0]}")
        }

    }

    fun getNeededTokens(startIndex: Int, endTokens: List<Token>): List<Token> {
        val neededTokens = tokens.subList(startIndex, tokens.size)
        val op = endTokens.all { it.javaClass == ParenthesisClose().javaClass }
        return if (op) {
            var pars = 0
            neededTokens.takeWhile { token ->
                if (token is ParenthesisOpen) pars++
                else if (token is ParenthesisClose) pars--
                pars != -1
            }
        } else neededTokens.takeWhile { token -> endTokens.none { it.javaClass == token.javaClass } }
    }

    fun interactIntExpr(expression: List<Token>, startIndex: Int): ExpressionCode {
        val postfix = NumericPostfixGenerator(expression).getPostfixFromTokens()
//        println("Postfix: ${postfix.joinToString(" "){it.word}}")
        val result = solveIntExpression(postfix, 0)
        memory.freeRegisters(result.disposables)
        return ExpressionCode(result.code, startIndex + expression.size, result.address, listOf(result.address))
    }

    fun interactBoolExpr(expression: List<Token>, startIndex: Int) = when (expression.size) {
        1 -> getRegisterOfToken(expression[0], startIndex+1)
        2 -> {
            val registerExpression = getRegisterOfToken(expression[0])
            val code = "${registerExpression.code}\n" +
                    "R$ًً{registerExpression.address} <= NOT R${registerExpression.address}"
            ExpressionCode(code, startIndex+2, registerExpression.address, registerExpression.disposables)
        }
        else -> {

            val endTokens = listOf(Equal(-1), NotEqual(-1), Bigger(-1), BiggerEqual(-1), Smaller(-1),
                    SmallerEqual(-1), AndOperator(), OrOperator())

            val localCompiler = ExpressionCompiler(expression)
            val first = localCompiler.compile(0, getValueType(expression[0]), endTokens)
            val operator = expression[first.nextIndex]
            val second = localCompiler.compile(first.nextIndex+1, getValueType(expression[first.nextIndex+1]), listOf())

            val finalCode = if (!memory.sameBlockedRegisters(first.address, second.address)) {
                val movement = moveRegister(first.address, second.address, operator.index)
                val resultCode = "R${second.address} <= R${movement.address} ${operator.word} R${second.address}"
                memory.freeRegisters(movement.disposables)
                "${first.code}\n${second.code}\n${movement.code}\n$resultCode"
            } else "${first.code}\n${second.code}\n" +
                    "R${second.address} <= R${first.address} ${operator.word} R${second.address}"

            ExpressionCode(finalCode, second.nextIndex, second.address, listOf(second.address))

        }
    }

    fun getValueType(token: Token) = when (token) {
        is TRUE -> ValueType.BOOL
        is FALSE -> ValueType.BOOL
        is CHAR -> ValueType.CHAR
        is Number -> ValueType.INT
        is Identifier -> token.type
        is ParenthesisOpen -> ValueType.INT
        else -> {
            println("WTF token: $token")
            ValueType.UNKNOWN
        }
    }

    fun interactCharExpr(expression: List<Token>, startIndex: Int) = getRegisterOfToken(expression[0], startIndex+1)

    // Todo: maybe some improvements
    fun solveIntExpression(postfix: List<Token>, startIndex: Int): ExpressionCode {
        val token = postfix[startIndex]
        return when (token) {
            is ArithmeticOperator -> {

                val first = solveIntExpression(postfix, startIndex+1)
                val second = solveIntExpression(postfix, first.nextIndex)
                memory.freeRamSlots(first.disposables, second.disposables)

                if (!memory.sameBlockedRegisters(first.address, second.address)) {
                    val movement = moveRegister(first.address, second.address, token.index)
                    val resultCode = "R${second.address} <= R${second.address} ${token.word} R${movement.address}"
                    val finalCode = "${first.code}\n${second.code}\n${movement.code}\n$resultCode"
                    memory.freeRegisters(movement.disposables.plus(listOf(movement.address, first.address)))
                    ExpressionCode(finalCode, second.nextIndex, second.address)
                } else {
                    val resultCode = "R${second.address} <= R${second.address} ${token.word} R${first.address}"
                    val finalCode = "${first.code}\n${second.code}\n$resultCode"
                    memory.freeRegister(first.address)
                    ExpressionCode(finalCode, second.nextIndex, second.address)
                }

            }
            else -> getRegisterOfToken(token, startIndex+1)
        }
    }

    fun getRegisterOfToken(token: Token, nextAddress: Int = -1): ExpressionCode {
        val converted = converter.convert(token)
        return when (converted) {
            is ConvertedValue -> {
                val address = memory.getNextEmptyRegister(token.index)
                val code = moveImmediateValueToRegister(converted.value, address)
                ExpressionCode(code, nextAddress, address, listOf(address))
            }
            is ConvertedRegister -> {
                ExpressionCode("", nextAddress, converted.registerAddress)
            }
            is ConvertedMemory -> {
                val address = memory.getNextEmptyRegister(token.index)
                val code = moveMemoryToRegister(converted.memoryAddress, address)
                ExpressionCode(code, nextAddress, address, listOf(address))
            }
        }
    }

    fun moveRegister(from: Int, operand: Int, tokenIndex: Int): ExpressionCode {
        val memoryAddress = memory.getNextEmptyRamSlot(tokenIndex)
        val nextRegister = memory.getNextRegisterBeside(operand, tokenIndex)
        memory.freeRegister(from)
        memory.freeRamSlot(memoryAddress)
        val code = "MEM[$memoryAddress] <= R$from\n" + memory.moveWpTo(
                (converter.convert(Number("", nextRegister.toString())) as ConvertedValue).value.takeLast(8)) +
                "\nR$nextRegister <= MEM[$memoryAddress]"
        return ExpressionCode(code, -1, nextRegister, listOf(nextRegister))
    }

    fun moveImmediateValueToRegister(value: String, register: Int) =
            "R$register(7:0) <= ${value.takeLast(8)}\n" +
            "R$register(15:8) <= ${value.take(8)}"

    fun moveMemoryToRegister(memoryAddress: Int, register: Int) =
            "R$register <= MEM[$memoryAddress]"

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

//    val expression = "bool c = ( 1 * ( 2 ) == 4 * 5 / ( ( 2 ) ) )"
    val expression = "bool c = ( 1 * ( 2 ) == 4 * 5 / ( ( 2 + 3 ) ) * 2 )"
    val tokenizer = WordTokenizer()

    tokenizer.words = expression.split(" ").map { Word(it, 1) }
    val tokens = tokenizer.extractTokens()

    val expressionCompiler = ExpressionCompiler(tokens)

//    tokens.forEach { println("${it.word} -> ${it.javaClass}") }
    println("The result:\n${expressionCompiler.compile(4, ValueType.BOOL, listOf(ParenthesisClose())).code}")

}