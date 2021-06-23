package twoCars.view.agent

import twoCars.model.learn.Move
import twoCars.model.TwoCarsModelInterface
import kotlin.math.abs
import kotlin.math.pow

/**
 * Different types of Q learning Agents
 */
public class SimpleMDPLearningAgent : MDPLearningAgent {

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
                var weight = (1 + ((100 - abs(scroller.yPosn - model.getCarInfo().yPosn)) / 100)).pow(1000)
                var laneNum = scroller.lane
                var curUtil = utils[laneNum] ?: 0.0
                var scrollerVal = MDPLearningUtil.getScrollerVal(scroller.type)
                //weight.times()
                var finUtil = curUtil + weight.times(scrollerVal)
                //var finUtil = curUtil + weight * scrollerVal
                utils[laneNum] = finUtil
                //utils.put(laneNum, curUtil + weight * MDPLearningUtil.getScrollerVal(scroller.type))
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
            for (j in 0..model.getNumLanes() - 1) {
                var moveUtil = discountFactor * MDPLearningUtil.bestUtil(j, utils)
                var newUtil = maxOf(moveUtil, utils[j] ?: 0.0)
                this.utils.put(j, newUtil)
            }
        }
    }

    override fun getBestMove(laneNum :Int) : Move {
        var leftUtil = utils[laneNum - 1] ?: 0.0
        var rightUtil = utils[laneNum + 1] ?: 0.0
        var stayUtil = utils[laneNum] ?: 0.0

        // check for maximum
        var maxUtil = maxOf(leftUtil, rightUtil, stayUtil)

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
