package machine_learning;

import java.util.ArrayList;
import java.util.Random;
import javalib.impworld.*;
import javalib.worldimages.*;

public class Population {
  private ArrayList<Dot> pop;
  private double fitnessSum;
  private Posn goal;
  private Dot bestDot;
  private Random random;
  private int size;

  //creates a population
  public Population(int size, int width, int height, Random rand, Posn goal) {
    this.fitnessSum = 0.0;
    this.random = rand;
    this.goal = goal;
    pop = new ArrayList<Dot>();
    this.size = size;
    this.bestDot = new Dot(width, height, random, goal); //placeholder

    for (int i = 0; i < size; i++) {
      pop.add(new Dot(width, height, this.random, this.goal));
    }
  }

  //draws all dots onto the background
  WorldScene drawAll(WorldScene base) {
    for (Dot d: pop) {
      base = d.draw(base); //overlays dot onto base
    }

    return base;
  }

  //moves all dots in a population
  void moveAll() {
    for (Dot d: this.pop) {
      d.move();
    }
  }

  //determines whether any dots are still alive
  boolean extinct() {
    for (Dot d: pop) {
      if (!d.dead) {
        return false;
      }
    }

    return true;
  }

  //
  void naturalSelection() {
    ArrayList<Dot> nextGen = new ArrayList<Dot>();
    findValedictorian();
    calculateFitnessSum();

    nextGen.add(bestDot.newGeneration());
    nextGen.get(0).valedictorian = true;

    for (int i = 1; i < this.size; i++) {
      System.out.println("New generation is " + i + " long.");
      Dot parent = this.findParent();

      nextGen.add(parent.newGeneration());
    }

    this.pop = nextGen;
  }

  //calculates the total fitness of the population, to be used in parent selection
  void calculateFitnessSum() {
    this.fitnessSum = 0.0; //resets fitness sum

    for (Dot d: this.pop) {
      this.fitnessSum +=  d.fitness;
    }
  }

  //chooses the parent to be cloned in the next generation
  Dot findParent() {
    double rand = this.random.nextDouble() * this.fitnessSum;
    System.out.println("Rand: " + this.fitnessSum);
    double runningSum = 0;

    for (Dot d: this.pop) {
      d.calculateFitness();
      runningSum += d.fitness;

      if (runningSum > rand) {
        return d;
      }
    }

    System.out.println("Fatal error: did not find parent.");
    return null;
  }

  //adds a random amount of mutation to the total population
  void mutate() {
    for (Dot d: this.pop) {
      d.brain.mutate();
    }
  }

  //finds the most fit dot and saves it to bestDot
  void findValedictorian() {
    this.pop.get(0).calculateFitness();
    double max = this.pop.get(0).fitness;
    Dot maxDot = this.pop.get(0);

    for (Dot d: this.pop) {
      d.calculateFitness();
      if (d.fitness > max) {
        max = d.fitness;
        maxDot = d;
      }
    }

    maxDot.valedictorian = true;
    bestDot = maxDot;
  }
}
