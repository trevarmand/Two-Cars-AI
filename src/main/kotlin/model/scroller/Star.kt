package twoCars.model.scroller

class Star (lane: Int,
            yPosn : Double,
            override val type: ScrollerType = ScrollerType.STAR): Objective(lane, yPosn) {

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