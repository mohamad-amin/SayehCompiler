package entity

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
class CompileError(val error: String) {
    constructor(type: ErrorType, message: String, requiredClause: String, seenToken: Token)
            : this("$type: $message. $requiredClause, but saw: \"${seenToken.word}\" at line ${seenToken.line}")
    override fun toString() = error
}

enum class ErrorType {
    Semantic, Syntax, Unknown
}