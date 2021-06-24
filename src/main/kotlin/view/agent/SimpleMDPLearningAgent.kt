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

    private var utils : MutableMap<Int, Double>
    private var rewards : MutableMap<Int, Double>

    /**
     * Constructor that takes in model
     */
    constructor(model: TwoCarsModelInterface) {
        this.model = model
        this.utils = HashMap()
        this.rewards = HashMap()
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
                //var weight = (1 + ((100 - abs(scroller.yPosn - model.getCarInfo().yPosn)) / 100)).pow(1000)
                var weight = (1 + ((100 - abs(scroller.yPosn - model.getCarInfo().yPosn)) / 100)).pow(100)
                var laneNum = scroller.lane
                var curUtil = utils[laneNum] ?: 0.0
                var scrollerVal = MDPLearningUtil.getScrollerVal(scroller.type)
                //weight.times()
                var finUtil = curUtil + weight.times(scrollerVal)
                //var finUtil = curUtil + weight * scrollerVal
                utils[laneNum] = finUtil
                rewards[laneNum] = finUtil
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
            val newUtils = HashMap<Int, Double>()
            for (j in 0..model.getNumLanes() - 1) {
                var discount = discountFactor
                var bestUtil = MDPLearningUtil.bestUtil(j, utils)
                if (bestUtil < 0.0) {
                    discount = 1.2
                }
                //var moveUtil = discountFactor * MDPLearningUtil.bestUtil(j, utils)
                var moveUtil = discount * bestUtil
                var newUtil = rewards[j] ?: -Double.MAX_VALUE + maxOf(moveUtil, utils[j] ?: -Double.MAX_VALUE)
                //var newUtil = utils[j] ?: -Double.MAX_VALUE + moveUtil
                newUtils.put(j, newUtil)
            }

            this.utils = newUtils
        }
    }

    override fun getBestMove(laneNum :Int) : Move {
        var leftUtil = utils[laneNum - 1] ?: -Double.MAX_VALUE
        var rightUtil = utils[laneNum + 1] ?: -Double.MAX_VALUE
        var stayUtil = utils[laneNum] ?: -Double.MAX_VALUE

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
