package expression

import entity.CompileError

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
sealed class ExpressionResult
    data class Success(val result: String, val endToken: Int): ExpressionResult()
    data class Failure(val error: CompileError): ExpressionResult()