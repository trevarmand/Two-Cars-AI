package twoCars.model.scroller

abstract class Objective(lane: Int, yPosn: Double) : Scroller(lane, yPosn) {

    /**
     * Return the reward given for collecting this objective.
     */
    abstract fun getReward() : Int

    /**
     * Is it mandatory to collect this mover?
     */
    abstract fun isMandatory() : Boolean
}