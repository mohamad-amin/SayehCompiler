package memory

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class MemoryManager private constructor() {

    companion object {

        val RAM_SIZE = 1024
        val RF_SIZE = 64
        val WP = 0

        var instance: MemoryManager
        init {
            instance = MemoryManager()
        }

    }

    // Each item references to index of a token in the tokens list
    private val memory = Array(RAM_SIZE) { -1 }
    private val registerFile = Array(RF_SIZE) { -1 }

    fun getNextEmptyRamSlot(tokenIndex: Int): Int {
        val index = memory.slice(600..RAM_SIZE).indexOfFirst { it == -1 } + 600
        memory[index] = tokenIndex
        return index
    }

    fun getNextEmptyRegister(tokenIndex: Int): Int {
        val index = registerFile.indexOfFirst { it == -1 }
        registerFile[index] = if (tokenIndex == -1) Integer.MAX_VALUE else tokenIndex
        return index
    }

    fun freeRamSlot(slot: Int) { memory[slot] = -1 }
    fun freeRegister(slot: Int) { registerFile[slot] = -1 }

    fun freeRamSlots(slots: List<Int>) { slots.forEach { memory[it] = -1 } }
    fun freeRamSlots(vararg slots: List<Int>) { slots.forEach { list -> list.forEach{ memory[it] = -1 } } }
    fun freeRegisters(slots: List<Int>) { slots.forEach { registerFile[it] = -1 } }
    fun freeRegisters(vararg slots: List<Int>) { slots.forEach { list -> list.forEach{ registerFile[it] = -1 } } }

    fun sameBlockedRegisters(a: Int, b: Int) =
            maxOf(a, b) - ((minOf(a, b) / 4) * 4) < 4

    fun getNextRegisterBeside(register: Int, tokenIndex: Int): Int {
        val startWp = register / 4
        var newIndex = -1
        for (i in 4*startWp..registerFile.size) {
            if (registerFile[i] == -1 && i != register) {
                newIndex = i
                break
            }
        }
        registerFile[newIndex] = if (tokenIndex == -1) Integer.MAX_VALUE else tokenIndex
        return newIndex
    }

    fun moveWpTo(binaryValue: String) = "WP <= 000\nWP <= Wp + $binaryValue"

}