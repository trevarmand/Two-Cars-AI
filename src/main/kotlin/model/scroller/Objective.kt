package twoCars.model.scroller

interface Objective {

    /**
     * Return the reward given for collecting this objective.
     */
    fun getReward() : Int

    /**
     * Is it mandatory to collect this mover?
     */
    fun isMandatory() : Boolean
}