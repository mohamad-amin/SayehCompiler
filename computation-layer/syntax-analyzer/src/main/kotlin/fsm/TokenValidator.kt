package fsm

import entity.*
import entity.Number
import expression.Failure
import lexicalanalyzer.LexicalAnalyzer
import org.statefulj.fsm.model.impl.StateImpl
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/21/17.
 */
class TokenValidator {

    val fsm = ValidatorFSM.getFSM()

    fun isValid(tokens: List<Token>): Boolean {

        val state = ValidatorStateful(tokens)

        tokens.forEachIndexed { index, token ->
            if (index < state.currentIndex) return@forEachIndexed
            if (inTrapState(state)) {
                println(state.error)
                return false
            } else {

                val events = (fsm.getCurrentState(state) as StateImpl).transitions.keys

                val type = when (token) {
                    is Number -> Number("", "1").className()
                    is Character -> Character("", "").className()
                    is Identifier -> Identifier("").className()
                    is Unknown -> Unknown("").className()
                    else -> token.typeName()
                }

                if (hasEvent(state, type)) {
                    state.nextIndex = state.currentIndex + 1
                    fsm.onEvent(state, type)
                    state.currentIndex = state.nextIndex
                } else {
                    printStateEvent(fsm.getCurrentState(state) as StateImpl, token.javaClass.toString())
                    println("Doesn't have $type")
                    showFailure(events, token)
                    return false
                }

            }
        }

        return if (inTrapState(state)) { println(state.error); false }
            else if (fsm.getCurrentState(state).isEndState)
            
            if (state.scopeStack.isEmpty()) true
            else {
                Failure(CompileError("SYNTAX ERROR, extra open scope was found, required a" +
                        "\"}\"but reached the end of the file at line ${tokens.last().line}"))
                false
            }
        else {
            showFailure((fsm.getCurrentState(state) as StateImpl).transitions.keys, Unknown("EOF"))
            false
        }

    }

    fun inTrapState(stateful: ValidatorStateful) =
            fsm.getCurrentState(stateful).isBlocking || stateful.faultyExpression

    fun printStateEvent(state: StateImpl<ValidatorStateful>, event: String) {
        println("state: ${state.name} and event: $event and possible events: ${state.transitions.keys.count()} -> ")
//        state.transitions.keys.forEach { print("$it | ")}
//        println()
    }

    fun hasEvent(state: ValidatorStateful, event: String) = fsm.getCurrentState(state).getTransition(event) != null

    fun showFailure(events: MutableSet<String>, token: Token) =
            println(Failure(CompileError(ErrorType.Syntax, "Unexpected Token", "required $events", token)))

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

    val syntaxValidator = TokenValidator()
    val lexicalAnalyzer = LexicalAnalyzer()

    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory.path + "/Desktop")
    fileChooser.dialogTitle = "Choose source code file:"
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY

    val returnValue = fileChooser.showOpenDialog(null)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        val tokens = lexicalAnalyzer(fileChooser.selectedFile.path)
        println(tokens.joinToString("\n") { "${it.word} -> ${it::class.java}" })
        println(syntaxValidator.isValid(tokens))
    }

}