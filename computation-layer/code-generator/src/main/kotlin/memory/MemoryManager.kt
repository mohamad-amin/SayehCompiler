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

    fun getNextEmptyRamSlot() = memory.first { it == -1 }
    fun getNextEmptyRegister() = registerFile.first { it == -1 }

    fun freeRamSlot(slot: Int) { memory[slot] = -1 }
    fun freeRegister(slot: Int) { registerFile[slot] = -1 }

}