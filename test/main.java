import java.awt.*;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import machine_learning.Board;
import machine_learning.Dot;
import tester.Tester;

public class main {
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
