package twoCars.view.agent

import twoCars.model.learn.Move
import twoCars.model.TwoCarsModelInterface
import kotlin.math.abs
import kotlin.math.pow

/**
 * Different types of Q learning Agents
 */
public class SimpleMDPLearningAgent : MDPLearningAgent {

    private var iterations = 10 // next steps would've been making values like these configurable in run config
    private var discountFactor = 0.8
    private var negDiscountFactor = 1.2
    private var weightPow = 50 //what weight we want to assign for scrollers that are closer to car; 50 is "happy medium"
    // for tests

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
                var weight = (1 + ((100 - abs(scroller.yPosn - model.getCarInfo().yPosn)) / 100)).pow(weightPow)
                var laneNum = scroller.lane
                var curUtil = utils[laneNum] ?: 0.0
                var scrollerVal = MDPLearningUtil.getScrollerVal(scroller.type)
                var finUtil = curUtil + weight.times(scrollerVal)
                utils[laneNum] = finUtil
                rewards[laneNum] = finUtil
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
                var bestUtil = bestUtil(j)
                if (bestUtil < 0.0) {
                    // should be making negative utilities more negative
                    discount = negDiscountFactor
                }
                var moveUtil = discount * bestUtil
                var newUtil = rewards[j] ?: -Double.MAX_VALUE + maxOf(moveUtil, utils[j] ?: -Double.MAX_VALUE)
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

    private fun bestUtil(laneNum :Int): Double {
        // if utility doesn't exist at this point, should definitely not select it
        var leftUtil = utils[laneNum - 1] ?: -Double.MAX_VALUE
        var rightUtil = utils[laneNum + 1] ?: -Double.MAX_VALUE
        var stayUtil = utils[laneNum] ?: -Double.MAX_VALUE

        // check for maximum
        return maxOf(leftUtil, rightUtil, stayUtil)
    }
}
