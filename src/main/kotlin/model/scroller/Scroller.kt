package twoCars.model.scroller

/**
 * An object that scrolls vertically from top to bottom.
 */
abstract class Scroller {

    constructor(lane: Int, yPosn: Double){
        this.lane = lane
        this.yPosn = yPosn
    }

    /**
     * The current lane of this scroller.
     */
    var lane = 0
        private set

    /**
     * The Y position of the scroller, as a % of the vertical length of the screen.
     * Using a % allows us to not bind model specifics to the details of the UI.
     */
    var yPosn = 0.0
        private set

    /**
     * Updates this Mover's y position to simulate vertical movement.
     */
    fun move(distance: Double) {
        yPosn -= distance
    }

    /**
     * Return the type of this mover
     * MUST BE OVERRIDDEN
     */
    abstract val type: ScrollerType
}