package expression.solver

import entity.*
import expression.converter.base.TokenConverter
import expression.resolver.NumericPostfixGenerator
import lexicalanalyzer.tokenizer.WordTokenizer
import memory.MemoryManager

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class ExpressionCompiler(var tokens: List<Token>) {

    val memory = MemoryManager()
    val converter = TokenConverter()
    var previousTokensCount = -1

    /**
     * @return index of the next token after expression
     */
    fun compile(address: Int, startIndex: Int, expressionType: ValueType, endTokens: List<Token>): ExpressionCode {

        val neededTokens = tokens.subList(startIndex, tokens.size)
        val expression = neededTokens.takeWhile { token -> endTokens.none { it.javaClass == token.javaClass } }
        expression.forEachIndexed { index, token -> token.index = startIndex + index }
        previousTokensCount = startIndex

        println("Expression: ${expression.joinToString(" ") { it.word }}") // Todo: Remove this line in final build

        return when (expressionType) {
            ValueType.INT -> interactIntExpr(expression, address)
            ValueType.CHAR -> TODO()
            ValueType.BOOL -> TODO()
            ValueType.UNKNOWN -> throw IllegalArgumentException("WTF :| Needed unknown value? token: ${expression[0]}")
        }

    }

    fun interactIntExpr(expression: List<Token>, memoryAddress: Int): ExpressionCode {
        val postfix = NumericPostfixGenerator(expression).getPostfixFromTokens()
        println("postfix: ${postfix.joinToString(" ") { it.word }}")
        val result = solveIntExpression(postfix, 0)
        val finalCode = "${result.code}\nMEM[$memoryAddress] <= R${result.address}"
        memory.freeRegisters(result.disposables)
        return ExpressionCode(finalCode, previousTokensCount + result.nextIndex)
    }

    fun solveIntExpression(postfix: List<Token>, startIndex: Int): ExpressionCode {
        val token = postfix[startIndex]
        return when (token) {
            is ArithmeticOperator -> {
                val first = solveIntExpression(postfix, startIndex+1)
                val second = solveIntExpression(postfix, first.nextIndex)
                val address = memory.getNextEmptyRegister(token.index)
                memory.freeRegisters(first.disposables.plus(second.disposables))
                memory.freeRegisters(listOf(first.address, second.address))
                val resultCode = "R$address <= R${first.address} ${token.word} R${second.address}"
                val finalCode = "${first.code}\n${second.code}\n$resultCode"
                ExpressionCode(finalCode, second.nextIndex, address)
            }
            else -> {
                val address = memory.getNextEmptyRegister(token.index)
                ExpressionCode("R$address <= ${converter.convert(token)}", startIndex+1, address)
            }
        }
    }

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

    val expression = "int a = 4 * ( 5 + 3 ) / 2 + ( 3 * ( 9 ) ) ;"
    val tokenizer = WordTokenizer()

    tokenizer.words = expression.split(" ").map { Word(it, 1) }
    val tokens = tokenizer.extractTokens()

    val expressionCompiler = ExpressionCompiler(tokens)

//    tokens.forEach { println("${it.word} -> ${it.javaClass}") }
    println("The result:\n${expressionCompiler.compile(1, 3, ValueType.INT, listOf(Semicolon())).code}")

}