package twoCars.model

import tornadofx.getProperty
import twoCars.model.scroller.Objective
import twoCars.model.scroller.Scroller
import twoCars.model.scroller.ScrollerType

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
     * The yPson of scroller increments by tickRate amount every tick.
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

    private var gameOver = false

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
        for (lane in this.lanes) {
            for (scroller in lane) {
                scroller.move(tickRate)
            }
        }
        handleCollisions()
    }

    /**
     * Determine if the car is currently collecting, colliding, or neither.
     * Also, determine if the car is missing a circle
     */
    fun handleCollisions() {

        // First, are we hitting a square?
        var squares = this.lanes[car.currentLane].filter { it.yPosn <= car.yPosn && it.type == ScrollerType.SQUARE}
        if (squares.isNotEmpty()) {
            gameOver = true
            return
        }

        // Are we missing a circle? Are we collecting a star or circle?
        for (lane in this.lanes) {
            var targets = lane.filter { it.yPosn <= car.yPosn && it is Objective}
            for (target in targets) {
                // have to add this check - Kotlin isn't smart enough to smart cast based on filter
                if(target is Objective) {
                    if (target.lane != car.currentLane && target.isMandatory()) {
                        this.gameOver = true
                        return
                    }
                    if (target.lane == car.currentLane){
                        this.score += target.getReward()
                    }
                }
            }
        }
    }

    override fun getScrollers(): List<List<Scroller>> {
        return this.lanes
    }

    override fun isGameOver(): Boolean {
        return this.gameOver
    }


}