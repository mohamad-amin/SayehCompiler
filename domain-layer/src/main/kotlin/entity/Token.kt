package entity

import java.io.File

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

inline fun <reified T : Any> T.className() = T::class.java.toString()

object TokenConstants {

    fun contains(text: String) = Keyword.contains(text) || Operator.contains(text) || Punctuation.contains(text)

    object Keyword {
        fun contains(text: String) =
                arrayOf(IF, ELSE, WHILE, INT, CHAR, BOOL, NULL, TRUE, FALSE).contains(text)
        val IF = "if"
        val ELSE = "else"
        val WHILE = "while"
        val INT = "int"
        val CHAR = "char"
        val BOOL = "bool"
        val NULL = "null"
        val TRUE = "true"
        val FALSE = "false"
    }

    object Operator {

        fun contains(text: String) =
            AssignmentOperator.contains(text) || LogicalOperator.contains(text) ||
            RelationalOperator.contains(text) || ArithmeticOperator.contains(text)

        object AssignmentOperator {
            fun contains(text: String) =
                    arrayOf(Assign, PlusAssign, MinusAssign, MultiplyAssign, DivideAssign, ModuloAssign).contains(text)
            val Assign = "="
            val PlusAssign = "+="
            val MinusAssign = "-="
            val MultiplyAssign = "*="
            val DivideAssign = "/="
            val ModuloAssign = "%="
        }

        object LogicalOperator {
            fun contains(text: String) = arrayOf(And, Or, Not).contains(text)
            val And = "&&"
            val Or = "||"
            val Not = "!"
        }

        object RelationalOperator {
            fun contains(text: String) =
                    arrayOf(Equal, NotEqual, Bigger, Smaller, BiggerEqual, SmallerEqual).contains(text)
            val Equal = "=="
            val NotEqual = "!="
            val Bigger = ">"
            val Smaller = "<"
            val BiggerEqual = ">="
            val SmallerEqual = "<="
        }

        object ArithmeticOperator {
            fun contains(text: String) =
                    arrayOf(Plus, Minus, Multiply, Divide, PlusPlus, MinusMinus).contains(text)
            val Plus = "+"
            val Minus = "-"
            val Multiply = "*"
            val Divide = "/"
            val PlusPlus = "++"
            val MinusMinus = "--"
        }

    }

    object Punctuation {
        fun contains(text: String) = arrayOf(
                Comma, ParenthesisOpen, ParenthesisClose, BraceOpen, BraceClose, Colon, Semicolon).contains(text)
        val Comma = ","
        val ParenthesisOpen = "("
        val ParenthesisClose = ")"
        val BraceOpen = "{"
        val BraceClose = "}"
        val Colon = ":"
        val Semicolon = ";"
    }

}

sealed class Token(val word: String, val line: Int = -1) {

    fun typeName() = when (this.word) {
        IF().word -> IF().className()
        ELSE().word -> ELSE().className()
        WHILE().word -> WHILE().className()
        CHAR().word -> CHAR().className()
        INT().word -> INT().className()
        BOOL().word -> BOOL().className()
        NULL().word -> NULL().className()
        TRUE().word -> TRUE().className()
        FALSE().word -> FALSE().className()
        Assign().word -> Assign().className()
        PlusAssign().word -> PlusAssign().className()
        MinusAssign().word -> MinusAssign().className()
        DivideAssign().word -> DivideAssign().className()
        MultiplyAssign().word -> MultiplyAssign().className()
        ModuloAssign().word -> ModuloAssign().className()
        AndOperator().word -> AndOperator().className()
        OrOperator().word -> OrOperator().className()
        NotOperator().word -> NotOperator().className()
        Equal().word -> Equal().className()
        NotEqual().word -> NotEqual().className()
        Bigger().word -> Bigger().className()
        Smaller().word -> Smaller().className()
        BiggerEqual().word -> BiggerEqual().className()
        SmallerEqual().word -> SmallerEqual().className()
        Plus().word -> Plus().className()
        Minus().word -> Minus().className()
        Multiply().word -> Multiply().className()
        Divide().word -> Divide().className()
        PlusPlus().word -> PlusPlus().className()
        MinusMinus().word -> MinusMinus().className()
        Comma().word -> Comma().className()
        ParenthesisOpen().word -> ParenthesisOpen().className()
        ParenthesisClose().word -> ParenthesisClose().className()
        BraceOpen().word -> BraceOpen().className()
        BraceClose().word -> BraceClose().className()
        Colon().word -> Colon().className()
        Semicolon().word -> Semicolon().className()
        else -> "WTF @Token::typeName"
    }

}

    sealed class Keyword(value: String, line: Int = -1) : Token(value, line)

        class IF(line: Int = -1, val singleLined: Boolean = false): Keyword(TokenConstants.Keyword.IF, line)
        class ELSE(line: Int = -1, val singleLined: Boolean = false): Keyword(TokenConstants.Keyword.ELSE, line)
        class WHILE(line: Int = -1, val singleLined: Boolean = false): Keyword(TokenConstants.Keyword.WHILE, line)
        class INT(line: Int = -1): Keyword(TokenConstants.Keyword.INT, line)
        class CHAR(line: Int = -1): Keyword(TokenConstants.Keyword.CHAR, line)
        class BOOL(line: Int = -1): Keyword(TokenConstants.Keyword.BOOL, line)
        class NULL(line: Int = -1): Keyword(TokenConstants.Keyword.NULL, line)
        class TRUE(line: Int = -1, var registerAddress: Int = -1, var memoryAddress: Int = -1):
                Keyword(TokenConstants.Keyword.TRUE, line)
        class FALSE(line: Int = -1, var registerAddress: Int = -1, var memoryAddress: Int = -1):
                Keyword(TokenConstants.Keyword.FALSE, line)

    sealed class Operator(value: String, line: Int = -1): Token(value, line)

        sealed class AssignmentOperator(value: String, line: Int = -1): Operator(value, line)

            class Assign(line: Int = -1): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.Assign, line)
            class PlusAssign(line: Int = -1): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.PlusAssign, line)
            class MinusAssign(line: Int = -1): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.MinusAssign, line)
            class MultiplyAssign(line: Int = -1): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.MultiplyAssign, line)
            class DivideAssign(line: Int = -1): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.DivideAssign, line)
            class ModuloAssign(line: Int = -1): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.ModuloAssign, line)

        sealed class LogicalOperator(value: String, line: Int = -1): Operator(value, line)

            class AndOperator(line: Int = -1): LogicalOperator(TokenConstants.Operator.LogicalOperator.And, line)
            class OrOperator(line: Int = -1): LogicalOperator(TokenConstants.Operator.LogicalOperator.Or, line)
            class NotOperator(line: Int = -1): LogicalOperator(TokenConstants.Operator.LogicalOperator.Not, line)

        sealed class RelationalOperator(value: String, line: Int = -1): Operator(value, line)

            class Equal(line: Int = -1): RelationalOperator(TokenConstants.Operator.RelationalOperator.Equal, line)
            class NotEqual(line: Int = -1): RelationalOperator(TokenConstants.Operator.RelationalOperator.NotEqual, line)
            class Bigger(line: Int = -1): RelationalOperator(TokenConstants.Operator.RelationalOperator.Bigger, line)
            class Smaller(line: Int = -1): RelationalOperator(TokenConstants.Operator.RelationalOperator.Smaller, line)
            class BiggerEqual(line: Int = -1): RelationalOperator(TokenConstants.Operator.RelationalOperator.BiggerEqual, line)
            class SmallerEqual(line: Int = -1): RelationalOperator(TokenConstants.Operator.RelationalOperator.SmallerEqual, line)

        sealed class ArithmeticOperator(value: String, line: Int = -1, val precedence: Int = -1): Operator(value, line)

            class Plus(line: Int = -1): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Plus, line, 1)
            class Minus(line: Int = -1): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Minus, line, 1)
            class Multiply(line: Int = -1): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Multiply, line, 2)
            class Divide(line: Int = -1): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Divide, line, 2)
            class PlusPlus(line: Int = -1): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.PlusPlus, line, 3)
            class MinusMinus(line: Int = -1): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.MinusMinus, line, 3)

    sealed class Punctuation(value: String, line: Int = -1): Token(value, line)

        class Comma(line: Int = -1): Punctuation(TokenConstants.Punctuation.Comma, line)
        class ParenthesisOpen(line: Int = -1): Punctuation(TokenConstants.Punctuation.ParenthesisOpen, line)
        class ParenthesisClose(line: Int = -1): Punctuation(TokenConstants.Punctuation.ParenthesisClose, line)
        class BraceOpen(line: Int = -1): Punctuation(TokenConstants.Punctuation.BraceOpen, line)
        class BraceClose(line: Int = -1): Punctuation(TokenConstants.Punctuation.BraceClose, line)
        class Colon(line: Int = -1): Punctuation(TokenConstants.Punctuation.Colon, line)
        class Semicolon(line: Int = -1): Punctuation(TokenConstants.Punctuation.Semicolon, line)

    class Number(val name: String, var number: String, line: Int = -1, var registerAddress: Int = -1,
                 var memoryAddress: Int = -1): Token(if (number[0].isDigit()) "+" + number else number, line)

    class Identifier(val name: String, line: Int = -1, var registerAddress: Int = -1, var memoryAddress: Int = -1,
                     var type: ValueType = ValueType.UNKNOWN, var value: String = ""): Token(name, line) {

        override fun equals(other: Any?) =
                other is Identifier && other.name == this.name

        override fun hashCode(): Int {
            return super.hashCode()
        }

    }

    class Character(val name: String, val char: String, line: Int = -1, var registerAddress: Int = -1,
                    var memoryAddress: Int = -1): Token(char, line)

    class Unknown(val text: String, line: Int = -1): Token(text, line)

enum class ValueType { INT, BOOL, CHAR, UNKNOWN }

fun main(args: Array<String>) {

    val text = File("/home/mohamadamin/Desktop/f.c").readLines().filter { it.isNotBlank() }.map { line ->
        val type = line.replace("  ", "").split(" ")[1]
        "\t\t$type().word -> $type().className()"
    }.joinToString("\n")
    println(text)

}