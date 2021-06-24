package twoCars.view.agent

import twoCars.model.TwoCarsModel
import twoCars.model.TwoCarsModelInterface
import twoCars.model.learn.Move
import twoCars.model.scroller.ScrollerType
import java.util.*
import kotlin.random.Random

/**
 * This agent will learn provided, constant maps. It determines the utility of moves as if the model
 * is a 2D array. Each time step() is called on the model, it can be thought of as the car advancing up from the bottom.
 * This assumes a constant tick rate of one.
 */
class PositionBasedQLearningAgent() {

    /**
     * How many times to tick until we stop playing.
     */
    private val STEPS = 50

    /**
     * This is the epsilon-greedy probability of exploration.
     * Percentages expressed as whole numbers: 20% = 20
     */
    private val RANDOM_MOVE_CHANGE = 70

    /**
     * Influences how much new discoveries matter as opposed to existing information.
     */
    private val LEARNING_RATE = 0.2
    private val DISCOUNT_RATE = 0.8

    /**
     * Describes the utility of different scrollers, for use by the learner.
     */
    private val SCROLLER_UTILITIES = mapOf(ScrollerType.STAR to 10.0, ScrollerType.CIRCLE to 30.0, ScrollerType.SQUARE to -50.0)

    private var model : TwoCarsModelInterface
    // lane -> y position -> move (order: left, right, straight) -> q value
    //
    private var qValues : MutableMap<Int, MutableMap<Double, MutableMap<Move, Double>>>
    private var policy: MutableMap<Int, MutableMap<Double, Move>>

    init {
        this.model = TwoCarsModel("Circle 0 80, Circle 1 20, Circle 1 30, Square 1 40")
        this.qValues = hashMapOf<Int, MutableMap<Double, MutableMap<Move, Double>>>()
        this.policy = hashMapOf<Int, MutableMap<Double, Move>>()
        for(lane in 0..model.getNumLanes()) {
            this.qValues[lane] = hashMapOf<Double, MutableMap<Move, Double>>()
            this.policy[lane] = hashMapOf<Double, Move>()
            for(y in 0..STEPS) {
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
//        for(laneNo in scrollers.indices) {
//            for(scroller in scrollers[laneNo]) {
//                for(move in Move.values()) {
//                    when (scroller.type) {
//                        ScrollerType.CIRCLE -> this.qValues[laneNo]!![scroller.yPosn]!![move] = 100.0
//                        ScrollerType.SQUARE -> this.qValues[laneNo]!![scroller.yPosn]!![move] = -100.0
//                        ScrollerType.STAR -> this.qValues[laneNo]!![scroller.yPosn]!![move] = 20.0
//                    }
//                }
//            }
//        }
        print("DONE")

    }

    fun resetModel() {
        this.model = TwoCarsModel("Circle 0 80, Circle 1 20, Circle 1 30, Square 1 40")
    }

    /**
     * A simple helper, it's often easy to refer to moves by index.
     * 0 -> Left, 1 -> Right, 2 -> Stay
     */
    fun getMoveByIdx(idx: Int) :Move {
        if(idx == 0) {
            return Move.LEFT
        }

        if (idx == 1) {
            return Move.RIGHT
        }

        if (idx == 2) {
            return Move.STAY
        }

        error("getMoveByIdx called with invalid index: $idx")
    }


    /**
     * Returns a lookup table of Scroller values on the world.
     * Lookup table is indexed by [lane, double]
     */
    fun makeUtilityMap(model: TwoCarsModelInterface) :List<Map<Double, Double>> {
        var ret = arrayListOf<MutableMap<Double, Double>>()

        for(lane in 0 until model.getNumLanes()) {
            ret.add(lane, hashMapOf<Double, Double>())

        }

        for(lane in model.getScrollers()) {
            for(scroller in lane) {
                // Because we have to move, then step, the real utility of a scroller is placed one y-posn before it.
                ret[scroller.lane][scroller.yPosn - 1] = SCROLLER_UTILITIES[scroller.type]!!
            }
        }

        return ret
    }

    fun qSolve(iterations : Int) {

        val utilities = makeUtilityMap(model)

        for(i in 0 until iterations) {
            var curStep = 0
            resetModel()
            while (!model.isGameOver()) {


                val car = model.getCarInfo()
                val carLane = car.currentLane
                // We can pretend that as time progresses, the car is moving up the map.
                val carEffectiveY = car.yPosn + curStep

                // Returns values if we were to make a move, then step.
                val moveQValues = getAdjacentQValues(carLane, curStep)
                val bestMoveValue = maxOf(moveQValues[0], moveQValues[1], moveQValues[2])

                val eGreedyTrigger = Random.nextInt(0, 100)

//                // If e-greedy has decided we should make a random move:
//                if(eGreedyTrigger < RANDOM_MOVE_CHANGE) {
//                    // TODO -> Can 0..3 return 3? Or just 2? Remove error eventually.
//                    // Move follows the same indexes described in the state:
//                    // 0 left, 1 right, 2 stay
//                    var moveIdx = Random.nextInt(0, 3)
//                    if(moveIdx == 3) {
//                        error("FOUND MOVE OF 3 INDEX ISSUE")
//                    }
//                    val move = getMoveByIdx(moveIdx)
//
//                    // If on the left lane moving left, or on the right lane moving right, do nothing.
//                    if((carLane == 0 && move == Move.LEFT) ||
//                        (carLane == model.getNumLanes() - 1 && moveIdx == 1)) {
//                        model.step()
//                        curStep++
//                        continue
//                    }
//                    else {
//                        var newLane = carLane
//
//                        // Determine what lane we'll end up in based on this move.
//                        newLane = when(move) {
//                            Move.STAY -> carLane
//                            Move.RIGHT -> carLane + 1
//                            Move.LEFT -> carLane - 1
//                        }
//
//                        val bestFutureMoveValue = getAdjacentQValues(newLane, curStep + 1).maxOrNull()!!
//                        val existingVal = qValues[carLane]!![carEffectiveY]!![move] ?: 0.0
//                        val curUtility = utilities[carLane][car.yPosn] ?: 0.0
//                        var newDiscountedVal = LEARNING_RATE * (curUtility + (DISCOUNT_RATE * bestFutureMoveValue) - bestMoveValue)
//                        qValues[carLane]!![carEffectiveY]!![move] = existingVal + newDiscountedVal
//                        model.switchLane(move)
//                    }
//                }

//                else {
                    for (move in listOf(Move.STAY, Move.LEFT, Move.RIGHT)) {
                        if (qValues[carLane]!![carEffectiveY + 1]!![move] == bestMoveValue) {
                            var newLane = carLane
                            newLane = when(move) {
                                Move.STAY -> carLane
                                Move.RIGHT -> carLane + 1
                                Move.LEFT -> carLane - 1
                            }

                            // What is the best move after changing lanes and stepping once?
                            val bestFutureMoveValue = getAdjacentQValues(newLane, curStep + 1).maxOrNull()!!
                            val existingVal = qValues[carLane]!![carEffectiveY]!![move] ?: 0.0
                            val curUtility = utilities[carLane][carEffectiveY] ?: 0.0
                            // It seems like bestFutureMove value should actually be bestMoveValue
                            // Discount * q of where we end up) - q of where we just were
                            // Might need to do something special when the utility of the target square is non-zero
                            // EG if we are about to collect, the discounted value will be negative bc bestMoveValue reflects moving to collect

                            // Don't update q-values of scrollers
                            if (curUtility != 0.0 && qValues[carLane]!![carEffectiveY]!![move] != 0.0) {
                                break
                            }


                            var newDiscountedVal =
                                LEARNING_RATE * (curUtility + (DISCOUNT_RATE * bestFutureMoveValue) - existingVal)
                            qValues[carLane]!![carEffectiveY]!![move] = existingVal + newDiscountedVal
                            model.switchLane(move)

                            // We prefer to stay over doing anything else, if multiple moves yield the same y value.
                            if(move == Move.STAY) {
                                break
                            }
                        }
                    }
//                }


                model.step()
                curStep++
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
        if(curStep >= STEPS) {
            return arrayOf(0.0, 0.0, 0.0)
        }
        val vals = arrayOf(0.0, 0.0, 0.0)
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
                    val lane = qValues[carLane]!!
                    val moveSet = lane[curStep + 11.0]
                    vals[2] = moveSet!![Move.STAY] ?: 0.0
                }
                Move.RIGHT -> {
                    val lane = qValues[carLane + 1]
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