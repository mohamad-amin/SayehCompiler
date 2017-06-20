package entity

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
class CompileError(val error: String) {
    // Todo: complete this constructor
    constructor(type: ErrorType, message: String, requiredClause: String, seenToken: Token): this("")
    override fun toString() = error
}

enum class ErrorType {
    Semantic, Syntax, Unknown
}