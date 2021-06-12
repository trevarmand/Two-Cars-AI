package twoCars.model

interface TwoCarsModelInterface {
    /**
     * Switches the lane of the car on the given side.
     *
     * @param side A string, either "left" or "right"
     */
    fun switchLane(side: String)

    /**
     * Returns the current score.
     *
     * @return the current score of the game
     */
    fun getScore(): Int

    /**
     * Update scroller positions and handle collisions.
     */
    fun step()

    /**
     * Return a list of the active scrolling obstacles (squares, circles, etc) in the program.
     */
    fun getScrollers(): List<Scroller>?
}