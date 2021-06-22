package twoCars.model

import java.lang.IllegalArgumentException
import twoCars.model.learn.Move

class Car {

    constructor(currentLane: Int, totalLanes: Int) {
        this.currentLane = currentLane
        this.totalLanes = totalLanes
        this.yPosn = 10.0
    }

    /**
     * What lane does the car currently occupy?
     */
    var currentLane = 0
        private set

    /**
     * The total number of lanes in the world.
     * Influences movement legality.
     */
    private val totalLanes: Int

    /**
     * The vertical position of the car.
     * For now, this won't change; in the future, that may be controllable by the agent.
     */
    val yPosn: Double

    /**
     * Move the car left or right.
     */
    fun switchLane(direction : Move) {
        if(direction == Move.LEFT && this.currentLane != 0){
            this.currentLane -= 1
        }
        else if(direction == Move.RIGHT && this.currentLane != totalLanes - 1){
            this.currentLane += 1
        } else {
            // lane remains the same: just return
            return
        }
    }
}