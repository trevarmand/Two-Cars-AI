package twoCars.model.scroller

class Star (lane: Int,
            override val type: ScrollerType = ScrollerType.STAR): Scroller(), Objective {

    /**
     * The reward of collecting a circle is 1 point.
     */
    override fun getReward() : Int {
        return 3
    }

    override fun isMandatory(): Boolean {
        return false
    }
}