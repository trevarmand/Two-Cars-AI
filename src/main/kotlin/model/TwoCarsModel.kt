package twoCars.model

import tornadofx.getProperty
import twoCars.model.scroller.*
import twoCars.model.learn.Move

class TwoCarsModel : TwoCarsModelInterface {

    // The current score of the game.
    private var score : Int

    // How many times has the world ticked?
    private var currentTick : Int

    // The car!
    private var car : Car

    /**
     * The tick rate specifies how fast the obstacles are moving down the screen.
     * This allows us to increase the rate obstacles scroll at.
     * A tick rate of 0.5 means the obstacles move 0.5% of the vertical distance of the world each tick.
     * The yPson of scroller increments by tickRate amount every tick.
     */
    private var tickRate : Double

    /**
     * The number of lanes in the world.
     */
    private var numLanes: Int


    /**
     * A set of lists representing lanes, each occupied by a set of Scrollers.
     */
    private var lanes: MutableList<MutableList<Scroller>>

    private val stringRepresentation : String

    private var gameOver = false

    /**
     * Allows for loading a static model representation.
     * Strings should be formatted as such:
     * Type lane yPosn, type lane yPosn, type lane yPosn, ...
     * Circle 0 50, Square 0 100, Square 1 25
     * This constructor is not robust against poorly-formatted strings.
     */
    constructor(stringRepresentation: String) {
        this.score = 0
        this.currentTick = 0
        this.tickRate = 1.0
        this.numLanes = 0
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(this.numLanes / 2, this.numLanes)
        this.stringRepresentation = stringRepresentation
        reset()
    }

    override fun reset() {
        this.gameOver = false
        this.score = 0
        this.currentTick = 0
        this.tickRate = 1.0
        this.numLanes = 0
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(this.numLanes / 2, this.numLanes)
        var objects = this.stringRepresentation.split(",")
        for(obj in objects) {
            var details = obj.trim().split(" ")
            var type = details[0]
            var lane = details[1].toInt()
            var yPosn = details[2].toDouble()

            if (this.lanes.isEmpty() || this.lanes.size <= lane) {
                this.numLanes += 1
                this.lanes.add(lane, ArrayList<Scroller>())
            }

            if(type == "Circle") {
                var newCircle = Circle(lane, yPosn)
                this.lanes[lane].add(newCircle)
            }
            if(type == "Square") {
                var newSquare = Square(lane, yPosn)
                this.lanes[lane].add(newSquare)
            }
            if(type == "Star") {
                var newStar = Star(lane, yPosn)
                this.lanes[lane].add(newStar)
            }
        }
    }

    override fun switchLane(direction: Move) {
        this.car.switchLane(direction)
    }

    override fun getScore(): Int {
        return this.score
    }

    override fun getNumLanes(): Int {
        return this.numLanes
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
        ++currentTick
//        TODO("remove obstacles that are below the screen")
    }

    /**
     * Determine if the car is currently collecting, colliding, or neither.
     * Also, determine if the car is missing a circle
     */
    fun handleCollisions() {

        // First, are we hitting a square?
        // changing <= to == for testing purposes
        var squares = this.lanes[car.currentLane].filter { it.yPosn == car.yPosn && it.type == ScrollerType.SQUARE}
        if (squares.isNotEmpty()) {
            gameOver = true
            return
        }

        // Are we missing a circle? Are we collecting a star or circle?
        for (lane in this.lanes) {
            // changing <= to == for testing purposes
            var targets = lane.filter { it.yPosn == car.yPosn && it is Objective}
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

    override fun getCarInfo(): Car {
        return this.car
    }

    override fun isGameOver(): Boolean {
        return this.gameOver
    }


}