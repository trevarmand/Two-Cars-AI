package view.agent;

/**
 * Different types of Q learning Agents
 */
public class SimpleQLearningAgent {

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
    }

    /**
     * Will calculate utility for each lane based on objects in that lane. Will use distance from agent
     * to weight each object
     */
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
}
