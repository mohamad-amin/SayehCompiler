package expression.resolver

import entity.*
import entity.Number
import expression.ExpressionResult
import expression.Failure
import expression.Success
import expression.validator.ExpressionValidator
import javax.script.ScriptEngineManager

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 * Todo: Remember to update tokens every time
 */
class BaseExpressionResolver(var tokens: List<Token>) {

    var identifiers = listOf<Token>()
    val expressionValidator = ExpressionValidator()

    fun interact(startIndex: Int, expressionType: ValueType, endTokens: List<Token>) {

        identifiers = tokens.subList(0, startIndex).filter { it is Identifier }

        val neededTokens = tokens.subList(startIndex, tokens.size)
        val expression = neededTokens.takeWhile { token -> endTokens.none { it.javaClass == token.javaClass } }
        val nextToken = neededTokens[expression.size + startIndex]

        println("Expression: $expression") // Todo: Remove this line in final build

        // Todo: Evaluate expression based on

    }

    fun interactCharExpr(exp: Token, nextToken: Token, startIndex: Int, expType: ValueType) = when (exp) {
        is Identifier -> {
            if (exp.type == expType)
                if (exp.value.isNotEmpty())
                    Success(exp.value, startIndex+1)
                else Failure(CompileError(ErrorType.Semantic, "Undefined variable", "required type: $expType", exp))
            else Failure(CompileError(ErrorType.Semantic, "Types do not match", "required type: $expType", exp))
        }
        is Character -> Success(exp.char, startIndex+1)
        else -> Failure(CompileError(ErrorType.Syntax, "Unexpected token", "required a character expression", exp))
    }

    fun interactBoolExpr(tokens: List<Token>, nextToken: Token): ExpressionResult {
        TODO()
    }

    fun interactIntExpr(exp: List<Token>, nextToken: Token, startIndex: Int, expType: ValueType): ExpressionResult {

        var faultyExpression = false
        var failureResult: Failure = Failure(CompileError(ErrorType.Unknown, "", "", nextToken))

        val expression = exp.joinToString(" ") { token ->
            if (faultyExpression) token.word else when(token) {
                is Identifier -> {
                    if (token.type == expType)
                        if (token.value.isNotEmpty()) token.value
                        else {
                            faultyExpression = true
                            failureResult = Failure(CompileError(
                                    ErrorType.Semantic, "Undefined variable", "required type: $expType", token))
                            token.word
                        }
                    else {
                        faultyExpression = true
                        failureResult = Failure(CompileError(
                                ErrorType.Semantic, "Types do not match", "required type: $expType", token))
                        token.word
                    }
                }
                is Number -> token.number
                is Operator -> token.word
                is ParenthesisOpen -> token.word
                is ParenthesisClose -> token.word
                else -> {
                    faultyExpression = true
                    failureResult = Failure(CompileError(ErrorType.Syntax,
                            "Unexpected token", "required a number expression", token))
                    token.word
                }
            }
        }

        return if (faultyExpression) failureResult
        else {
            val validationResult = expressionValidator.validate(exp)
            when (validationResult) {
                is Success -> processIntExpression(expression, startIndex + exp.size, exp[0].line)
                is Failure -> validationResult
            }
        }

    }

    // Todo: Possible Improvements
    fun processIntExpression(expression: String, endToken: Int, line: Int): ExpressionResult {
        val engine = ScriptEngineManager().getEngineByName("js")
        val result = engine.eval(expression).toString()
        return when (result) {
            "Infinity" ->
                Failure(CompileError(ErrorType.Semantic, "Division by zero!", "zero wasn't allowed", Divide(line)))
            else -> Success(result, endToken)
        }
    }

}