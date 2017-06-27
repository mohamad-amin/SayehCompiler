package memory

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class MemoryManager {

    companion object {
        val RAM_SIZE = 1024
        val RF_SIZE = 64
        val WP = -1
    }

    // Each item references to index of a token in the tokens list
    private val memory = Array(RAM_SIZE) { -1 }
    private val registerFile = Array(RF_SIZE) { -1 }

    fun getNextEmptyRamSlot(tokenIndex: Int): Int {
        val index = memory.indexOfFirst { it == -1 }
        memory[index] = tokenIndex
        return index
    }

    fun getNextEmptyRegister(tokenIndex: Int): Int {
        val index = registerFile.indexOfFirst { it == -1 }
        registerFile[index] = tokenIndex
        return index
    }

    fun freeRamSlot(slot: Int) { memory[slot] = -1 }
    fun freeRegister(slot: Int) { registerFile[slot] = -1 }

    fun freeRamSlots(slots: List<Int>) { slots.forEach { memory[it] = -1 } }
    fun freeRegisters(slots: List<Int>) { slots.forEach { registerFile[it] = -1 } }

}