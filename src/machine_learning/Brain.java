package machine_learning;

import java.util.ArrayList;
import java.util.Random;
import javalib.worldimages.*;

public class Brain {
  private ArrayList<Posn> directions;
  private Random random;

  public Brain(int directions, Random rand) {
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

