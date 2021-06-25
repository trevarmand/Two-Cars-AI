package twoCars.view.agent

import twoCars.model.TwoCarsModelInterface
import twoCars.model.learn.Move
import twoCars.model.scroller.ScrollerType
import java.util.*
import kotlin.random.Random

/**
 * This agent will learn provided, constant maps. It determines the utility of moves as if the model
 * is a 2D array. Each time step() is called on the model, it can be thought of as the car advancing up from the bottom.
 * This model assumes a constant tick rate of one.
 */
class PositionBasedQLearningAgent {

    /**
     * How many times to tick until we stop playing.
     */
    private val HEIGHT: Double

    /**
     * This is the epsilon-greedy probability of exploration.
     * Percentages expressed as whole numbers: 20% = 20
     */
    private val RANDOM_MOVE_CHANGE = 30

    /**
     * Influences how much new discoveries matter as opposed to existing information.
     */
    private val LEARNING_RATE = 0.01
    private val DISCOUNT_RATE = 0.8

    /**
     * Describes the utility of different scrollers, for use by the learner.
     */
    private val SCROLLER_UTILITIES =
        mapOf(ScrollerType.STAR to 30.0, ScrollerType.CIRCLE to 20.0, ScrollerType.SQUARE to -50.0)

    private var model: TwoCarsModelInterface

    // lane -> y position -> move (order: left, right, straight) -> q value
    private var qValues: MutableMap<Int, MutableMap<Double, MutableMap<Move, Double>>>

    constructor(model: TwoCarsModelInterface, highestScoller: Double) {
        this.HEIGHT = highestScoller
        this.model = model
        this.qValues = hashMapOf<Int, MutableMap<Double, MutableMap<Move, Double>>>()
        for (lane in 0 until this.model.getNumLanes()) {
            this.qValues[lane] = hashMapOf<Double, MutableMap<Move, Double>>()
            for (y in 0..HEIGHT.toInt() + 11) {
                this.qValues[lane]!![y.toDouble()] = EnumMap(twoCars.model.learn.Move::class.java)
                for (move in Move.values()) {
                    this.qValues[lane]!![y.toDouble()]!![move] = 0.0
                }
            }
        }
    }

    /**
     * Displays the world to the console.
     * This only works for tick rates that are whole numbers.
     */
    fun printPolicy() {
        println()
        this.model.reset()
        var utilities = this.makeUtilityMap(model)
        for (y in this.HEIGHT.toInt() downTo this.model.getCarInfo().yPosn.toInt()) {
            var scrollerLane = -1
            print("$y: ")
            for (lane in 0 until this.model.getNumLanes()) {
                var curBest = -100000.0
                var bestMove = Move.STAY
                for (move in qValues[lane]!![y.toDouble()]!!) {
                    if (move.value >= curBest) {
                        curBest = move.value
                        bestMove = move.key
                    }
                }
                print(" $bestMove ")
                var utilHere = utilities[lane][y.toDouble()] ?: 0.0
                if (utilHere != 0.0) {
                    scrollerLane = lane
                }
            }

            if (scrollerLane != -1) {
                var utilHere = utilities[scrollerLane][y.toDouble()] ?: 0.0
                print(" scroller worth $utilHere at $y in lane $scrollerLane")
            }
            println()
        }
    }

    /**
     * A simple helper, it's often easy to refer to moves by index.
     * 0 -> Left, 1 -> Right, 2 -> Stay
     */
    fun getMoveByIdx(idx: Int): Move {
        if (idx == 0) {
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
     * Lookup table is indexed by [lane, yPosn]
     */
    fun makeUtilityMap(model: TwoCarsModelInterface): List<Map<Double, Double>> {
        var ret = arrayListOf<MutableMap<Double, Double>>()

        for (lane in 0 until model.getNumLanes()) {
            ret.add(lane, hashMapOf<Double, Double>())
        }

        for (lane in model.getScrollers()) {
            for (scroller in lane) {
                // Because we have to move, then step, the real utility of a scroller is placed one y-posn before it.
                ret[scroller.lane][scroller.yPosn - 1] = SCROLLER_UTILITIES[scroller.type]!!
            }
        }

        return ret
    }


    /**
     * The heart of q learning.
     */
    fun qSolve(iterations: Int) {

        val utilities = makeUtilityMap(model)

        for (i in 0 until iterations) {
            var curStep = 0
            this.model.reset()
            while (!model.isGameOver() && curStep <= HEIGHT) {


                val car = model.getCarInfo()
                val carLane = car.currentLane
                // We can pretend that as time progresses, the car is moving up the map.
                val carEffectiveY = car.yPosn + curStep

                // Returns values if we were to make a move, then step.
                val moveQValues = getAdjacentQValues(carLane, curStep)
                val bestMoveValue = maxOf(moveQValues[0], moveQValues[1], moveQValues[2])

                val eGreedyTrigger = Random.nextInt(0, 100)
                val curUtility = utilities[carLane][carEffectiveY] ?: 0.0


                // If e-greedy has decided we should make a random move:
                if (eGreedyTrigger < RANDOM_MOVE_CHANGE) {
                    val moveIdx = Random.nextInt(0, 3)
                    val move = getMoveByIdx(moveIdx)

                    // If on the left lane moving left, or on the right lane moving right, do nothing.
                    if ((carLane == 0 && move == Move.LEFT) ||
                        (carLane == model.getNumLanes() - 1 && moveIdx == 1)
                    ) {
                        model.step()
                        curStep++
                        continue
                    } else {
                        var newLane: Int

                        // Determine what lane we'll end up in based on this move.
                        newLane = when (move) {
                            Move.STAY -> carLane
                            Move.RIGHT -> carLane + 1
                            Move.LEFT -> carLane - 1
                        }

                        // Don't update q-values of scrollers
                        if (curUtility != 0.0 && qValues[carLane]!![carEffectiveY]!![Move.STAY] != curUtility) {
                            qValues[carLane]!![carEffectiveY]!![Move.STAY] = curUtility
                            break
                        } else if (curUtility != 0.0) {
                            break
                        }

                        val nextStepQValues = getAdjacentQValues(newLane, curStep + 1)
                        val bestFutureMoveValue = nextStepQValues.maxOrNull()!!
                        val existingVal = qValues[carLane]!![carEffectiveY]!![move] ?: 0.0
                        var newDiscountedVal =
                            LEARNING_RATE * (curUtility + (DISCOUNT_RATE * bestFutureMoveValue) - bestMoveValue)
                        qValues[carLane]!![carEffectiveY]!![move] = existingVal + newDiscountedVal
                        model.switchLane(move)
                    }
                } else {
                    for (move in listOf(Move.STAY, Move.LEFT, Move.RIGHT)) {
                        if (qValues[carLane]!![carEffectiveY]!![move] == bestMoveValue) {
                            var newLane = carLane
                            newLane = when (move) {
                                Move.STAY -> carLane
                                Move.RIGHT -> carLane + 1
                                Move.LEFT -> carLane - 1
                            }

                            // What is the best move after changing lanes and stepping once?
                            val bestFutureMoveValue = getAdjacentQValues(newLane, curStep + 1).maxOrNull()!!
                            val existingVal = qValues[carLane]!![carEffectiveY]!![move] ?: 0.0

                            // Don't update q-values of scrollers
                            if (curUtility != 0.0 && qValues[carLane]!![carEffectiveY]!![Move.STAY] != curUtility) {
                                qValues[carLane]!![carEffectiveY]!![Move.STAY] = curUtility
                                break
                            } else if (curUtility != 0.0) {
                                break
                            }


                            val newDiscountedVal =
                                LEARNING_RATE * (curUtility + (DISCOUNT_RATE * bestFutureMoveValue) - bestMoveValue)
                            qValues[carLane]!![carEffectiveY]!![move] = existingVal + newDiscountedVal
                            model.switchLane(move)

                            // We prefer to stay over doing anything else, if multiple moves yield the same y value.
                            if (move == Move.STAY) {
                                break
                            }
                        }
                    }
                }
                model.step()
                curStep++
            }
        }
        this.printPolicy()
    }

    /**
     * Returns the q values of spaces one move away.
     * If the car is on the bottom of the screen, in the left corner,
     * it will return a negative value for moving up one to the left, and some value for up one and up one to the right
     * Returns an array of values in order:  Left, Right, Stay
     */
    private fun getAdjacentQValues(carLane: Int, curStep: Int): Array<Double> {
        if (curStep >= HEIGHT - 10.0) {
            return arrayOf(0.0, 0.0, 0.0)
        }
        var offset = this.model.getCarInfo().yPosn
        val vals = arrayOf(0.0, 0.0, 0.0)
        for (move in Move.values()) {
            when (move) {
                Move.LEFT -> {
                    var lane = qValues[carLane - 1]
                    // if not possible to move there, return a very low q value for that move.
                    if (lane == null) {
                        vals[0] = -100.0
                    } else {
                        var moveSet = lane[curStep + offset]
                        // Move set should never be null
                        // we should not be calling getAdjacentQValues more often than 'steps' val above
                        vals[0] = moveSet!![Move.LEFT] ?: 0.0
                    }
                }
                Move.STAY -> {
                    val lane = qValues[carLane]!!
                    val moveSet = lane[curStep + offset]
                    vals[2] = moveSet!![Move.STAY] ?: 0.0
                }
                Move.RIGHT -> {
                    val lane = qValues[carLane + 1]
                    if (lane == null) {
                        vals[1] = -100.0
                    } else {
                        var moveSet = lane[curStep + offset]
                        vals[1] = moveSet!![Move.RIGHT] ?: 0.0
                    }
                }
            }
        }
        return vals
    }
}
