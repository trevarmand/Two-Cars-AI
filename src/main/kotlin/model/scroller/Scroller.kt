package twoCars.model.scroller

/**
 * An object that scrolls vertically from top to bottom.
 */
abstract class Scroller {

    /**
     * The current lane of this scroller.
     */
    private var lane = 0
        get() = lane


    /**
     * The Y position of the scroller, as a % of the vertical length of the screen.
     * Using a % allows us to not bind model specifics to the details of the UI.
     */
    private var yPosn = 0.0
        get() = yPosn

    /**
     * Updates this Mover's y position to simulate vertical movement.
     */
    fun move(distance: Float) {
        yPosn += distance
    }

    /**
     * Return the type of this mover
     * MUST BE OVERRIDDEN
     */
    abstract val type: ScrollerType?
}