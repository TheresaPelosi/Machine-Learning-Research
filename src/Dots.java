import javalib.utils.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javalib.impworld.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;
import tester.Tester;


class Board extends World {
  int width = 500; //sure would be helpful to have global variables
  int height = 400;
  Population pop;
  Posn goalLocation = new Posn(width / 2, 10); //middle top of board
  Random random;
  
  int generation = 0;
  
  //world constructor, creates initial population
  Board(int width, int height, Random rand) {
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

class Dot {
  boolean dead = false;
  Posn position;
  boolean reachedGoal = false;
  Brain brain;
  boolean valedictorian = false;
  double fitness = 0.0;
  Posn goal;
  Random random;
  
  int width;
  int height;
  
  int steps = 0;
  
  Dot(int width, int height, Random rand, Posn goal) {
    this.random = rand;
    this.position = new Posn(width / 2, height - 20); //middle bottom of screen
    this.brain = new Brain(100, random);
    
    this.width = width;
    this.height = height;
    
    this.goal = goal;
  }
  
  //draws a dot
  WorldScene draw(WorldScene base) {
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

class Brain {
  ArrayList<Posn> directions;
  Random random;
  
  Brain(int directions, Random rand) {
    this.directions = new ArrayList<Posn>();
    this.random = rand;
    for (int i = 0; i < directions; i++) {
      this.directions.add(new Posn(random.nextInt(10) - 5, -5)); //constant movement in y direction, random x value between -5 and 5
    }
  }
  
  //returns the next direction in the brain
  Posn updatePosition(int steps) { 
    return this.directions.get(steps);
  }
  
  //returns the amount of directions in a brain
  int length() {
    int length = 0;
    
    for (@SuppressWarnings("unused") Posn p: this.directions) {
      length++;
    }
    
    return length;
  }
  
  //mutates the directions in a brain by a random amount
  void mutate() {
    double mutationRate = 0.01;
    
    for (int i = 0; i < this.length(); i++) {
      double rand = this.random.nextFloat();
      
      if(rand < mutationRate) {
        this.directions.set(i, new Posn(this.random.nextInt(10) - 5, -5));
      }
    }
  }
}

class Population {
  ArrayList<Dot> pop;
  double fitnessSum;
  Posn goal;
  Dot bestDot;
  Random random;
  int size;
  
  //creates a population
  Population(int size, int width, int height, Random rand, Posn goal) {
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

class ExampleAI {
  Random rand1;
  Board game1;
  Posn goal1;
  Dot d1;
  WorldScene background1;
  
  void initValues() {
    rand1 = new Random(5);
    game1 = new Board(500, 400, rand1);
    goal1 = new Posn(50, 50);
    d1 = new Dot(500, 400, rand1, goal1);
    d1.valedictorian = true;
    background1 = new WorldScene(500, 500);
  }
  
  void testBigBang(Tester t) {
    initValues();
    game1.bigBang(game1.width, game1.height);
  }
  
  void testDrawDot(Tester t) {
    initValues();
    
    background1.placeImageXY(new CircleImage(3, OutlineMode.SOLID, Color.GREEN), 2080, 6);
    t.checkExpect(d1.draw(background1), background1);
  }
}