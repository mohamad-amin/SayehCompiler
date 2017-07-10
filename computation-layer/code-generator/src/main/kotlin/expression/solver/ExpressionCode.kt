package expression.solver

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class ExpressionCode(val code: String, val nextIndex: Int, val address: Int = -1, val disposables: List<Int> = listOf())

sealed class ConvertionResult
    class ConvertedValue(val value: String): ConvertionResult()
    class ConvertedMemory(val memoryAddress: Int): ConvertionResult()
    class ConvertedRegister(val registerAddress: Int): ConvertionResult()
