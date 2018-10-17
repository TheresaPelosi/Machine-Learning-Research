package machine_learning;

import java.util.Random;
import java.awt.Color;
import javalib.impworld.*;
import javalib.worldimages.*;

public class Dot {
  public boolean dead = false;
  private Posn position;
  private boolean reachedGoal = false;
  public Brain brain;
  public boolean valedictorian = false;
  public double fitness = 0.0;
  private Posn goal;
  private Random random;

  int width;
  int height;

  int steps = 0;

  public Dot(int width, int height, Random rand, Posn goal) {
    this.random = rand;
    this.position = new Posn(width / 2, height - 20); //middle bottom of screen
    this.brain = new Brain(100, random);

    this.width = width;
    this.height = height;

    this.goal = goal;
  }

  //draws a dot
  public WorldScene draw(WorldScene base) {
    if (valedictorian) {
      //draw green
      base.placeImageXY(new CircleImage(3, OutlineMode.SOLID, Color.GREEN), position.x, position.y);
    }
    else {
      //draw black
      base.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.BLACK), position.x, position.y);
    }

    return base;
  }

  //moves a dot based on the next direction given from the brain
  void move() {
    if (this.brain.length() > this.steps + 1 && !this.reachedGoal) { //check
      steps++;
      this.position.x += brain.updatePosition(steps).x;
      this.position.y += brain.updatePosition(steps).y;

      if (this.position.x < 0 || this.position.x > this.width || this.position.y < 0) {
        this.dead = true;
      }

      if (this.position.x == goal.x && this.position.y == goal.y) {
        this.reachedGoal = true;
      }
    }
    else {
      this.dead = true;
    }
  }

  //calculates how well a dot did to reaching goal
  void calculateFitness() {
    if (reachedGoal) {
      this.fitness = 1.0 / 16.0 + 10000 / (double)(this.steps * this.steps); //calculation pulled from video, should change later
    }
    else {
      double distanceToGoal = Math.sqrt(Math.pow(this.position.x - goal.x, 2) + Math.pow(this.position.y - goal.y, 2));
      this.fitness = 1.0 / (distanceToGoal * distanceToGoal);
    }
  }

  //generates a new member of the next generation
  Dot newGeneration() {
    Dot child = new Dot(width, height, random, goal);
    child.brain = this.brain;

    return child;
  }
}
