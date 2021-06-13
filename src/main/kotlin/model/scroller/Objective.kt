package twoCars.model.scroller

abstract class Objective : Scroller() {

    /**
     * Return the reward given for collecting this objective.
     */
    abstract fun getReward() : Int

    /**
     * Is it mandatory to collect this mover?
     */
    abstract fun isMandatory() : Boolean
}