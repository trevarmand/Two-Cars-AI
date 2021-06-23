package twoCars.view.agent

import twoCars.model.TwoCarsModel
import twoCars.model.TwoCarsModelInterface
import twoCars.model.learn.Move
import twoCars.model.scroller.ScrollerType
import java.util.*
import java.util.Collections.max

/**
 * This agent will learn provided, constant maps. It determines the utility of moves as if the model
 * is a 2D array. Each time step() is called on the model, it can be thought of as the car advancing up from the bottom.
 * This assumes a constant tick rate of one.
 */
class PositionBasedQLearningAgent() {

    /**
     * How many times to tick until we stop playing.
     */
    private val steps = 50

    private var model : TwoCarsModelInterface
    // lane -> y position -> move -> q value
    private var qValues : MutableMap<Int, MutableMap<Double, MutableMap<Move, Double>>>
    private var policy: MutableMap<Int, MutableMap<Double, Move>>

    init {
        this.model = TwoCarsModel("Circle 0 20, Square 1 30, Circle 1 40, Square 1 50")
        this.qValues = hashMapOf<Int, MutableMap<Double, MutableMap<Move, Double>>>()
        this.policy = hashMapOf<Int, MutableMap<Double, Move>>()
        for(lane in 0..model.getNumLanes()) {
            this.qValues[lane] = hashMapOf<Double, MutableMap<Move, Double>>()
            this.policy[lane] = hashMapOf<Double, Move>()
            for(y in 0..steps) {
                this.qValues[lane]!![y.toDouble()] = EnumMap(twoCars.model.learn.Move::class.java)
                for(move in Move.values()) {
                    this.qValues[lane]!![y.toDouble()]!![move] = 0.0
                }
            }
        }

        val scrollers = model.getScrollers()

        // Initializes Q values for all moves at the position where collected
        // This may be wrong - might need to initialize q values not in-place:
        //      for example staying when you are below a reward, not for when you are actively positioned on the reward
        for(laneNo in scrollers.indices) {
            for(scroller in scrollers[laneNo]) {
                for(move in Move.values()) {
                    when (scroller.type) {
                        ScrollerType.CIRCLE -> this.qValues[laneNo]!![scroller.yPosn]!![move] = 100.0
                        ScrollerType.SQUARE -> this.qValues[laneNo]!![scroller.yPosn]!![move] = -100.0
                        ScrollerType.STAR -> this.qValues[laneNo]!![scroller.yPosn]!![move] = 20.0
                    }
                }
            }
        }
        print("DONE")

    }

    fun qSolve(iterations : Int) {

        for(i in 0 until iterations) {

            var step = 0
            print(model.isGameOver())
            while (!model.isGameOver()) {
                var moveQValues = getAdjacentQValues(model.getCarInfo().currentLane, step)

                model.step()
                step++
                print("AYE!")
            }
        }
        print("DONE")
    }

    /**
     * Returns the q values of spaces one move away.
     * If the car is on the bottom of the screen, in the left corner,
     * it will return a negative value for moving up one to the left, and some value for up one and up one to the right
     * Returns an array of values in order:  Left, Right, Stay
     */
    private fun getAdjacentQValues(carLane: Int, curStep: Int) : Array<Double> {
        if(curStep >= steps) {
            return arrayOf(0.0, 0.0, 0.0)
        }
        var vals = arrayOf(0.0, 0.0, 0.0)
        for(move in Move.values()) {
            when (move) {
                Move.LEFT -> {
                    var lane = qValues[carLane - 1]
                    // if not possible to move there, return a very low q value for that move.
                    if(lane == null) {
                        vals[0] = -100.0
                    } else {
                        var moveSet = lane[curStep + 11.0]
                        // Move set should never be null
                        // we should not be calling getAdjacentQValues more often than 'steps' val above
                        vals[0] = moveSet!![Move.LEFT] ?: 0.0
                    }
                }
                Move.STAY -> {
                    var lane = qValues[carLane]!!
                    var moveSet = lane[curStep + 11.0]
                    vals[2] = moveSet!![Move.STAY] ?: 0.0
                }
                Move.RIGHT -> {
                    var lane = qValues[carLane + 1]
                    if(lane == null) {
                        vals[1] = -100.0
                    } else {
                        var moveSet = lane[curStep + 11.0]
                        vals[1] = moveSet!![Move.RIGHT] ?: 0.0
                    }
                }
            }
        }
        return vals
    }

}