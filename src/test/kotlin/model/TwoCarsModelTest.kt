package model

import org.junit.jupiter.api.Test
import twoCars.model.TwoCarsModel
import twoCars.model.TwoCarsModelInterface
import twoCars.model.scroller.ScrollerType

class TwoCarsModelTest {

    var simpleModel : TwoCarsModelInterface

    constructor() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
    }

    private fun resetModel() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
    }


    @Test
    fun switchLane() {
        resetModel()
        assert(simpleModel.getCarInfo().currentLane == 1)
        simpleModel.switchLane("left")
        assert(simpleModel.getCarInfo().currentLane == 0)
        simpleModel.switchLane("right")
        assert(simpleModel.getCarInfo().currentLane == 1)
    }

    @Test
    fun getScore() {
        resetModel()
        assert(simpleModel.getScore() == 0)
    }

    @Test
    fun step() {
        resetModel()
        assert(simpleModel.getScrollers()[0][0].yPosn == 50.0)
        simpleModel.step()
        simpleModel.step()
        assert(simpleModel.getScrollers()[0][0].yPosn == 48.0)
    }

    @org.junit.jupiter.api.Test
    fun handleCollisions() {
        resetModel()
        assert(simpleModel.getScrollers()[0][0].yPosn == 50.0)
        assert(simpleModel.getScrollers()[0][0].type == ScrollerType.CIRCLE)
        assert(simpleModel.getScore() == 0)
        simpleModel.switchLane("left")
        for(i in 1..40) {
            simpleModel.step()
        }
        assert(simpleModel.getScrollers()[0][0].yPosn == 10.0)
        assert(simpleModel.getScore() == 1)
    }


    @org.junit.jupiter.api.Test
    fun getCarInfo() {
        resetModel()
        val car = simpleModel.getCarInfo()
        assert(car.currentLane == 1)
        assert(car.yPosn == 10.0)
    }

    @org.junit.jupiter.api.Test
    fun isGameOver() {
        resetModel()
        assert(simpleModel.getScrollers()[1][0].yPosn == 25.0)
        assert(simpleModel.getScrollers()[1][0].type == ScrollerType.SQUARE)
        for(i in 1..15) {
            simpleModel.step()
        }
        assert(simpleModel.getScrollers()[1][0].yPosn == 10.0)
        assert(simpleModel.isGameOver())

    }
}