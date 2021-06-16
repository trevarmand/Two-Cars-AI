package twoCars.model.scroller

/**
 * Squares must be avoided and will result in game over if the car collides with one.
 */
class Square(lane: Int,
             yPosn: Double,
             override val type: ScrollerType = ScrollerType.SQUARE): Scroller(lane, yPosn) {
}