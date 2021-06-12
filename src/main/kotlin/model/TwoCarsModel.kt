package twoCars.model

import twoCars.model.scroller.Scroller

class TwoCarsModel : TwoCarsModelInterface {

    // The current score of the game.
    private var score = 0

    // How many times has the world ticked?
    private var currentTick = 0

    // The car!
    private val car : Car

    /**
     * The tick rate specifies how fast the obstacles are moving down the screen.
     * This allows us to increase the rate obstacles scroll at.
     * A tick rate of 0.5 means the obstacles move 0.5% of the vertical distance of the world each tick.
     */
    private var tickRate = 0.5

    /**
     * The number of lanes in the world.
     */
    private val numLanes: Int


    /**
     * A set of lists representing lanes, each occupied by a set of Scrollers.
     */
    private val lanes: List<MutableList<Scroller>>

    /**
     * A basic constructor.
     */
    constructor() {
        this.numLanes = 3
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(1, numLanes)
    }

    /**
     * Start with a custom tickrate
     */
    constructor(tickRate: Double) {
        this.numLanes = 3
        this.tickRate = tickRate
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(1, numLanes)
    }

    /**
     * Start with a unique number of lanes
     */
    constructor(numLanes: Int) {
        this.numLanes = numLanes
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(numLanes / 2, numLanes)
    }

    override fun switchLane(direction: String) {
        this.car.switchLane(direction)
    }

    override fun getScore(): Int {
        return this.score
    }

    /**
     * Step forward one tick. Update scroller positions and check for collisions.
     */
    override fun step() {
        TODO("Not yet implemented")
        handleCollisions()
    }

    /**
     * Determine if the car is currently collecting, collinding, or neither.
     */
    fun handleCollisions() {
        TODO("Not yet implemented")
    }

    override fun getScrollers(): List<List<Scroller>> {
        return this.lanes
    }


}