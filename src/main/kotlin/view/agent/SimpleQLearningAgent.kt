package twoCars.view.agent

import twoCars.model.learn.Move
import twoCars.model.TwoCarsModelInterface
import kotlin.math.abs

/**
 * Different types of Q learning Agents
 */
public class SimpleQLearningAgent : QLearningAgent {

    private var iterations = 10 // TODO: make this configurable
    private var discountFactor = 0.8

    private val model : TwoCarsModelInterface

    private val utils : MutableMap<Int, Double>

    /**
     * Constructor that takes in model
     */
    constructor(model: TwoCarsModelInterface) {
        this.model = model
        this.utils = HashMap()
        initUtils()
    }

    /**
     * Will calculate utility for each lane based on objects in that lane. Will use distance from agent
     * to weight each object
     */
    // this can probably be private since should only be called upon initialization
    override fun initUtils() {
        // clear utility values so they can be re-calculated after each tick
        for (i in 0..model.getNumLanes() - 1) {
            utils.put(i, 0.0)
        }

        var lanes = model.getScrollers()
        for (lane in lanes) {
            for (scroller in lane) {
                // objects closer to car are weighted more
                // theoretically object should always be above car here
                var weight = (100 - abs(scroller.getPosn() - model.getCarInfo().yPosn)) / 100
                var laneNum = scroller.getLaneNum()
                var curUtil = utils[laneNum] ?: 0.0
                utils.put(laneNum, curUtil + weight * QLearningUtil.getScrollerVal(scroller.type))
                //utils[scroller.getLaneNum()] = utils[scroller.getLaneNum()] + QLearningUtil.getScrollerVal(scroller.type)
            }
        }
    }

    /**
     * Will run value iteration to calculate utilities for each of the lanes
     */
    // this will need to be called in the entry point
    override fun solve() {
        // clear utilities from previous tick
        initUtils()
        for (i in 0..iterations) {
            // believe the .. is inclusive
            for (j in 0..model.getNumLanes() - 1) {
                var moveUtil = discountFactor * QLearningUtil.bestUtil(j, utils)
                //utils[j] = newUtil
                var newUtil = maxOf(moveUtil, utils[j] ?: 0.0)
                this.utils.put(j, newUtil)
            }
        }
    }

    override fun getBestMove(laneNum :Int) : Move {
        var leftUtil = utils[laneNum - 1] ?: 0.0
        var rightUtil = utils[laneNum + 1] ?: 0.0
        var stayUtil = utils[laneNum] ?: 0.0
        //edge case: can't move left
        /*
        if (laneNum == 0) {
            leftUtil = stayUtil
        } else {
            leftUtil = utils[laneNum - 1]
        }

        // edge case: can't move right
        if (laneNum == utils.size - 1) {
            rightUtil = stayUtil
        } else {
            rightUtil = utils[laneNum + 1]
        }
         */

        // check for maximum
        var maxUtil = maxOf(leftUtil, rightUtil, stayUtil)
        var maxMove = Move.LEFT

        // there's probably a cleaner and more efficient way to do this
        // this is tying highest utility value to corresponding move
        if (maxUtil == leftUtil) {
            return Move.LEFT
        } else if (maxUtil == rightUtil) {
            return Move.RIGHT
        } else {
            return Move.STAY
        }

    }
}
