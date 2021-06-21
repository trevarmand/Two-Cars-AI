package view.agent;

/**
 * Different types of Q learning Agents
 */
public class SimpleQLearningAgent {

    private var iterations = 100 // TODO: make this configurable

    private val model : TwoCarsModelInterface

    private val utils : Map<Int, Double>
    /**
     * Constructor that takes in model
     */
    constructor(model: TwoCarsModelInterface) {
        this.model = model
        for (i in 0...model.getNumLanes()) {
            utils.put(0, 0)
        }

        initUtils()
    }

    /**
     * Will calculate utility for each lane based on objects in that lane. Will use distance from agent
     * to weight each object
     */
    // this can probably be private since should only be called upon initialization
    override fun initUtils() {
        lanes = model.getScrollers()
        for (lane in lanes) {
            for (scroller in lane) {
                // closer objects are weighted more
                weight = 100 - scroller.getPosn()
                utils[scroller.getLaneNum()] = utils[scroller.getLaneNum()] + QLearningUtil.getScrollerVal(scroller.type)
            }
        }
    }

    /**
     * Will run value iteration to calculate utilities for each of the lanes
     */
    // this will need to be called in the entry point
    override fun solve() {
        for (i in 0...iterations) {
            for (j in 0...model.getNumLanes()) {
                var newUtil = discountFactor * QLearningUtil.bestUtil(j, utils)
                utils[j] = newUtil
            }
        }
    }
}
