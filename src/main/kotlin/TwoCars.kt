
package twoCars

import twoCars.model.TwoCarsModel


fun main(args : Array<String>) {
    println("Hello, World!")
    // Describes a basic world: Shape lane yPosn, Shape lane yPosn....
    val simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
    println(simpleModel.getScrollers()[0][0].yPosn)
    simpleModel.step()
    simpleModel.step()
    println(simpleModel.getScrollers()[0][0].yPosn)

    println(simpleModel.getCarInfo().currentLane)
    simpleModel.switchLane("right")
    println(simpleModel.getCarInfo().currentLane)
}