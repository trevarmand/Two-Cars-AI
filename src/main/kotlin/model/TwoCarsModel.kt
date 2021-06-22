package twoCars.model

import tornadofx.getProperty
import twoCars.model.scroller.*

class TwoCarsModel : TwoCarsModelInterface {

    // The current score of the game.
    private var score : Int

    // How many times has the world ticked?
    private var currentTick : Int

    // The car!
    private val car : Car

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
    private val lanes: MutableList<MutableList<Scroller>>

    private var gameOver = false

    /**
     * A basic constructor.
     */
    constructor() {
        this.score = 0
        this.currentTick = 0
        this.tickRate = 1.0
        this.numLanes = 3
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(1, numLanes)
    }

    /**
     * Start with a custom tickrate
     */
    constructor(tickRate: Double) {
        this.score = 0
        this.currentTick = 0
        this.numLanes = 3
        this.tickRate = tickRate
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(1, numLanes)
    }

    /**
     * Start with a unique number of lanes
     */
    constructor(numLanes: Int) {
        this.score = 0
        this.currentTick = 0
        this.tickRate = 1.0
        this.numLanes = numLanes
        this.lanes = arrayListOf<MutableList<Scroller>>()
        this.car = Car(numLanes / 2, numLanes)
    }

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
        var objects = stringRepresentation.split(",")
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
        this.car = Car(this.numLanes / 2, this.numLanes)
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
        ++currentTick
//        TODO("remove obstacles that are below the screen")
    }

    /**
     * Determine if the car is currently collecting, colliding, or neither.
     * Also, determine if the car is missing a circle
     */
    fun handleCollisions() {

        // First, are we hitting a square?
        var squares = this.lanes[car.currentLane].filter { it.yPosn == car.yPosn && it.type == ScrollerType.SQUARE}
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
                        lane.remove(target)
                        this.score += target.getReward()
                    }
                }
            }
        }

        for(lane in this.lanes) {
            // At this point, it should only be stars or squares. Circles should be removed when
            for(item in lane.filter { it.yPosn <= 0.0 }) {
                lane.remove(item)
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