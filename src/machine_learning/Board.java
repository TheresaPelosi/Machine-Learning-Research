package machine_learning;

import java.util.Random;
import java.awt.Color;
import javalib.impworld.*;
import javalib.worldimages.*;

public class Board extends World {
  public int width = 500; //sure would be helpful to have global variables
  public int height = 400;
  private Population pop;
  private Posn goalLocation = new Posn(width / 2, 10); //middle top of board
  private Random random;

  int generation = 0;

  //world constructor, creates initial population
  public Board(int width, int height, Random rand) {
    this.random = rand;
    this.width = width;
    this.height = height;
    this.pop = new Population(100, width, height, this.random, this.goalLocation);
  }

  //draws scene at every tick, moves population at every tick
  public WorldScene makeScene() {
    pop.moveAll();

    WorldScene window = new WorldScene(width, height);
    window.placeImageXY(new CircleImage(5, OutlineMode.SOLID, Color.RED),
            goalLocation.x, goalLocation.y); //places goal
    window = pop.drawAll(window); //draws all of population

    //if population extinct, create next generation with mutation
    if (pop.extinct()) {
      pop.calculateFitnessSum();
      pop.naturalSelection();
      pop.mutate();
      this.generation += 1;
    }

    return window;
  }
}
