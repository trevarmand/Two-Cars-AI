package twoCars.view.agent

import twoCars.model.learn.Move
import twoCars.model.TwoCarsModelInterface
import kotlin.math.abs
import kotlin.math.pow

public class SimpleMDPLearningAgent : MDPLearningAgent {

    private var iterations = 10 // next steps would've been making values like these configurable in run config
    private var discountFactor = 0.8
    private var negDiscountFactor = 1.2
    private var weightPow = 50 //what weight we want to assign for scrollers that are closer to car; 50 is "happy medium"
    // for tests

    private val model : TwoCarsModelInterface

    private var utils : MutableMap<Int, Double>

    /**
     * Constructor that takes in model
     */
    constructor(model: TwoCarsModelInterface) {
        this.model = model
        this.utils = HashMap()
        initUtils()
    }

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
            }
        }
    }

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
                var newUtil = utils[j] ?: -Double.MAX_VALUE + maxOf(moveUtil, utils[j] ?: -Double.MAX_VALUE)
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

        if (maxUtil == stayUtil) {
            return Move.STAY
        } else if (maxUtil == rightUtil) {
            return Move.RIGHT
        } else {
            return Move.LEFT
        }
    }

    /**
     * Returns best utility of either going left, right, or staying
     *
     * @param laneNum what lane number car's currently in
     *
     * @return maximum utility for all possible moves
     */
    private fun bestUtil(laneNum :Int): Double {
        // if utility doesn't exist at this point, should definitely not select it
        var leftUtil = utils[laneNum - 1] ?: -Double.MAX_VALUE
        var rightUtil = utils[laneNum + 1] ?: -Double.MAX_VALUE
        var stayUtil = utils[laneNum] ?: -Double.MAX_VALUE

        // check for maximum
        return maxOf(leftUtil, rightUtil, stayUtil)
    }
}
