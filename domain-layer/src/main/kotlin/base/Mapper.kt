package base

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/3/17.
 */
interface Mapper<F, T> {

    fun interact(from: F): T

}