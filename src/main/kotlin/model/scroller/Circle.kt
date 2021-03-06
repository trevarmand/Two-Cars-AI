package twoCars.model.scroller

class Circle(lane: Int,
             yPosn: Double,
            override val type: ScrollerType = ScrollerType.CIRCLE): Objective(lane, yPosn) {

    /**
     * The reward of collecting a circle is 1 point.
     */
    override fun getReward() : Int {
        return 1
    }

    override fun isMandatory(): Boolean {
        return false
    }
}