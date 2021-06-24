package twoCars.model

import twoCars.model.scroller.Scroller
import twoCars.model.learn.Move


interface TwoCarsModelInterface {

    /**
     * Resets the model to the state assigned to it via String constructor.
     */
    fun reset()

    /**
     * Switches the lane of the car towards the given side.
     *
     * @param direction Either the left, right, or stay enum value
     */
    fun switchLane(direction: Move)

    /**
     * Returns the current score.
     *
     * @return the current score of the game
     */
    fun getScore(): Int

    /**
     * Returns number of lanes in game.
     *
     * @return number of lanes
     */
    fun getNumLanes(): Int

    /**
     * Update scroller positions and handle collisions.
     */
    fun step()

    /**
     * Return a list of the active scrolling obstacles (squares, circles, etc) in the program.
     */
    fun getScrollers(): List<List<Scroller>>

    /**
     * Returns information about the car
     * Good design would be to pass back an immutable car, but that's not a priority for this project right now.
     */
    fun getCarInfo(): Car

    /**
     * Is the game over?
     */
    fun isGameOver() : Boolean
}