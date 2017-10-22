// Assignment 8
// McLean Colin
// mcleancolin
// Bui Kristi
// kristibui

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// An iterator for IList
class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  IListIterator(IList<T> items) {
    this.items = items;
  }

  public boolean hasNext() {
    return this.items.isCons();
  }

  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }

  public void remove() {
    throw new UnsupportedOperationException("Don't do this!");
  }
}

// to represent a general predicate interface
interface IPred<T, U> {

  // to apply a method
  boolean apply(T t, U u);

}

// to check if a player has hit a target
class PickUpTarget implements IPred<Target, Player> {

  // determines if a player has hit a target
  public boolean apply(Target target, Player player) {
    return (target.x == player.x) && (target.y == player.y);
  }
}

// to check if a player is on a flooded cell
class FloodedCell implements IPred<Cell, Player> {

  // determines if a player is on a flooded cell
  public boolean apply(Cell cell, Player player) {
    return (cell.x == player.x) && (cell.y == player.y)
        && cell.isFlooded;
  }
}

// represents a List
interface IList<T> extends Iterable<T> {

  // adds an element to a list
  IList<Cell> add(Cell cell, IList<Cell> list, 
      Cell left, Cell top, Cell right, Cell bottom);

  // determines whether this list is a ConsList
  boolean isCons();

  // creates a ConsList of the given List
  ConsList<T> asCons();

  // determines if it is an empty list
  boolean empty();

  // determines the length of a list
  int length();

  // updates the target list when a player acquires a target
  IList<Target> getTarget(IPred<Target, Player> p, Player player, IList<Target> t);

  // helps determine if a player is on a flooded cell
  boolean playerOnFloodedCellHelp(Player player, IPred<Cell,Player> p);

}

// represents an empty list
class MtList<T> implements IList<T> {

  // determines whether this empty list is a ConsList
  public boolean isCons() {
    return false;
  }

  // creates a ConsList of the given List
  public ConsList<T> asCons() {
    return null;
  }

  // makes this IList iterable
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // adds the given Cell to the list
  public IList<Cell> add(Cell cell, IList<Cell> list,
      Cell left, Cell top, Cell right, Cell bottom) {

    cell.updateCellRight(right);
    cell.updateCellLeft(left);
    cell.updateCellTop(top);
    cell.updateCellBottom(bottom);
    return new ConsList<Cell>(cell, list);
  }

  // determines if it is an empty list
  public boolean empty() {
    return true;
  }

  // determines the length of an empty list
  public int length() {
    return 0;
  }

  // updates the target list when a player acquires a target
  public IList<Target> getTarget(IPred<Target, Player> p, Player player, IList<Target> t) {
    return new MtList<Target>();
  }

  // helps determine if a player is on a flooded cell
  public boolean playerOnFloodedCellHelp(Player player, IPred<Cell,Player> p) {
    return false;
  }

}

// represents a non empty list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // adds the given Cell to this list
  public void update(T cell, IList<T> list) {
    list = new ConsList<T>(cell, list);
  }

  // determines whether this list is a ConsList
  public boolean isCons() {
    return true;
  }

  // returns this list as a ConsList
  public ConsList<T> asCons() {
    return this;
  }

  // makes this IList iterable
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // adds the given cell onto the list
  public IList<Cell> add(Cell cell, IList<Cell> list, 
      Cell left, Cell top, Cell right, Cell bottom) {

    cell.right = right;
    cell.left = left;
    cell.top = top;
    cell.bottom = bottom;
    return new ConsList<Cell>(cell, list);
  }

  // determines if it is an empty list
  public boolean empty() {
    return false;
  }

  // determines the length of this list
  public int length() {
    return 1 + this.rest.length();
  }

  // updates the target list when a player acquires a target
  public IList<Target> getTarget(IPred<Target, Player> p, Player player, IList<Target> t) {
    if (p.apply((Target) this.first, player)) {
      t = (IList<Target>) this.rest;
      return this.rest.getTarget(p,  player, t);
    }
    else {
      return new ConsList<Target>((Target) this.first, this.rest.getTarget(p, player, t));
    }
  }

  // helps determine if a player is on a flooded cell
  public boolean playerOnFloodedCellHelp(Player player, IPred<Cell,Player> p) {
    return p.apply((Cell) this.first, player) || this.rest.playerOnFloodedCellHelp(player, p);
  }

}

// To represent the player
class Player {

  // coordinates of the Player
  int x;
  int y;

  Player(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // moves the player
  public Player movePlayer(String ke, ArrayList<ArrayList<Cell>> boardCell) {
    if(ke.equals("right") && boardCell.get(x + 1).get(y).height 
        <= ForbiddenIslandWorld.ISLAND_MIDDLE) {
      return new Player(this.x, this.y);
    }
    else if(ke.equals("right")) {
      return new Player(this.x + 1, this.y);
    }
    else if(ke.equals("left") && boardCell.get(x - 1).get(y).height 
        <= ForbiddenIslandWorld.ISLAND_MIDDLE) {
      return new Player(this.x, this.y);
    }
    else if (ke.equals("left")) {
      return new Player(this.x - 1, this.y);
    }
    else if(ke.equals("up") && boardCell.get(x).get(y - 1).height 
        <= ForbiddenIslandWorld.ISLAND_MIDDLE) {
      return new Player(this.x, this.y);
    }
    else if (ke.equals("up")) {
      return new Player(this.x, this.y - 1);
    }
    else if(ke.equals("down") && boardCell.get(x).get(y + 1).height 
        <= ForbiddenIslandWorld.ISLAND_MIDDLE) {
      return new Player(this.x, this.y);
    }
    else if (ke.equals("down")) {
      return new Player(this.x, this.y + 1);
    }
    else {
      return this;
    }
  }

  // draws the player
  public WorldImage drawPlayer() {
    return new CircleImage(5, "solid", new Color(100, 0, 100));
  }
}

// To represent the targets
class Target {

  // coordinates of the Target
  int x;
  int y;

  Target(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // to draw the Targets
  public WorldImage drawTarget() {
    return new CircleImage(5, "solid", new Color(150, 150, 0));
  }

}

// To represent the helicopter target
class HelicopterTarget extends Target {

  HelicopterTarget(int x, int y) {
    super(x, y);
  }

  // to draw the helicopter target
  public WorldImage drawTarget() {
    return new CircleImage(5, "outline", new Color(100, 40, 140));
  }

  // determines if a Player can pick up the helicopter
  boolean pickUp(Player p) {
    return p.x == this.x && p.y == this.y;
  }

}

// Represents a single square of the game area
class Cell {

  // represents absolute height of this cell, in feet
  double height;

  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;

  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // reports whether this cell is flooded or not
  boolean isFlooded;

  Cell(double height, int x, int y, boolean isFlooded) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = null;
    this.right = null;
    this.top = null;
    this.bottom = null;
    this.isFlooded =  isFlooded;
  }

  // links the given left cell with this cell
  void updateCellLeft(Cell c) {
    if (this.x == 0) {
      this.left = this;
    }
    else {
      c.right = this;
    }
  }

  // links the given top cell with this cell
  void updateCellTop(Cell c) {
    if (this.y == 0) {
      this.top = this;
    }
    else {
      c.bottom = this;
    }
  }

  // links the given right cell with this cell
  void updateCellRight(Cell c) {
    if (this.x == ForbiddenIslandWorld.ISLAND_SIZE - 1) {
      this.right = this;
    }
    c.left = this;
  }

  // links the given bottom cell with this cell
  void updateCellBottom(Cell c) {
    if (this.y == ForbiddenIslandWorld.ISLAND_SIZE - 1) {
      this.bottom = this;
    }
    c.top = this;
  }

  // draws a cell
  public WorldImage drawCell(int waterHeight) {
    Double h = this.height;
    if (this.isFlooded && this.height > ForbiddenIslandWorld.ISLAND_MIDDLE) {
      int height = h.intValue();
      int value = 240 - (40 * ((waterHeight + ForbiddenIslandWorld.ISLAND_MIDDLE) - height));
      return new RectangleImage(10, 10, "solid", 
          new Color(0 , 0, Math.max(0, value)));
    }
    else if (this.isFlooded) {
      int height = h.intValue() * 4;
      int value = 240 - (60 * (waterHeight - height));
      value = Math.max(0, value);
      return new RectangleImage(10, 10, "solid", 
          new Color(0 , 0, 255));
    }
    else if (this.height <= waterHeight + ForbiddenIslandWorld.ISLAND_MIDDLE && !this.checkAdj()) {
      int height = h.intValue();
      int value = 240 - (40 * ((waterHeight + ForbiddenIslandWorld.ISLAND_MIDDLE) - height));
      return new RectangleImage(10, 10, "solid", 
          new Color(Math.max(0, value), 150, 150));
    }
    else {
      int height = h.intValue() * 4;
      int value = Math.min(height, 255);
      return new RectangleImage(10, 10, "solid", 
          new Color(Math.min(255, value), 255, Math.min(255, value)));
    }
  }

  // checks to see if this Cell's adjacent Cells are flooded
  boolean checkAdj() {
    return this.left.isFlooded || this.right.isFlooded 
        || this.top.isFlooded  || this.bottom.isFlooded;
  }

}

// represents a single ocean cell
class OceanCell extends Cell {

  // constructor to initialize the data, initializes the Cells to null
  OceanCell(double height, int x, int y, boolean isFlooded) {
    super(height, x, y, isFlooded);
  }

  // draws an ocean cell
  public WorldImage drawCell() {
    return new RectangleImage(10, 10, "solid", new Color(0, 0, 255));
  }

}

// to represent the Forbidden Island world
class ForbiddenIslandWorld extends World {

  // representing the heights of every cell in the game
  ArrayList<ArrayList<Double>> boardDouble;

  // each Cellâ€™s height is determined by the corresponding item in the previous
  // list of lists of doubles
  ArrayList<ArrayList<Cell>> boardCell;

  // All the cells of the game, including the ocean
  IList<Cell> board;

  // the current height of the ocean
  int waterHeight;

  // random nummber generator
  Random rand = new Random();

  // counter for the on tick function 
  int counter = 0;

  // Defines an int constant
  static final int ISLAND_SIZE = 64;

  // Defines an int constant for the middle
  static final int ISLAND_MIDDLE = ISLAND_SIZE / 2;

  double tl = 0.0;
  double tr = 0.0;
  double bl = 0.0;
  double br = 0.0;
  double m = ForbiddenIslandWorld.ISLAND_MIDDLE;
  double t = 1.0;
  double b = 1.0;
  double l = 1.0;
  double r = 1.0;

  // Defines the player
  Player player;

  // Defines the Target class
  IList<Target> target;

  // Defines the Helicopter Target class
  HelicopterTarget helicopter;

  public ForbiddenIslandWorld(int waterHeight) {
    super();
    this.waterHeight = waterHeight;
    this.boardDouble = this.initBoardDoubleRan();
    this.initBoardCell(this.boardDouble);
    this.board = this.initBoard(this.boardCell);
    this.target = this.targetList(new MtList<Target>());
    this.player = this.initPlayer();
    this.helicopter = this.initHelicopter();
  }

  // Determines the heights of every cell in the game for mountain mode
  ArrayList<ArrayList<Double>> initBoardDouble() {
    ArrayList<ArrayList<Double>> boardHeights2 = 
        new ArrayList<ArrayList<Double>>(ForbiddenIslandWorld.ISLAND_SIZE + 1);

    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
      ArrayList<Double> boardHeights1 = 
          new ArrayList<Double>(ForbiddenIslandWorld.ISLAND_SIZE + 1);
      boardHeights2.add(i, initBoardArrayHelper(boardHeights1, i));
    }
    return boardHeights2;
  }

  // helps determine the heights of every cell in the game for mountain mode
  ArrayList<Double> initBoardArrayHelper(ArrayList<Double> boardHeights1, int i) {
    for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
      boardHeights1.add(j, this.initBoardDoubleHelper(i, j));
    }
    return boardHeights1;
  }

  // helps determine the heights of every cell in the game for mountain mode
  double initBoardDoubleHelper(int i, int j) {
    double manDist = Math.abs(ISLAND_MIDDLE - i) + Math.abs(ISLAND_MIDDLE - j);
    double item = ISLAND_SIZE - manDist;
    return item;
  }

  // Determine the random heights of every cell in the game for mountain mode
  ArrayList<ArrayList<Double>> initBoardDoubleRan() {
    ArrayList<ArrayList<Double>> boardHeights2 = 
        new ArrayList<ArrayList<Double>>(ForbiddenIslandWorld.ISLAND_SIZE + 1);

    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
      ArrayList<Double> boardHeights1 = 
          new ArrayList<Double>(ForbiddenIslandWorld.ISLAND_SIZE + 1);
      boardHeights2.add(i, initBoardDoubleRan(boardHeights1, i));
    }
    return boardHeights2;
  }

  // helps determine the random heights of every cell in the game for mountain mode
  ArrayList<Double> initBoardDoubleRan(ArrayList<Double> boardHeights1, int i) {
    for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
      boardHeights1.add(j, this.initBoardDoubleRanHelper(i, j));
    }
    return boardHeights1;
  }

  // helps determine the random heights of every cell in the game for mountain mode
  double initBoardDoubleRanHelper(int i, int j) {
    double manDist = Math.abs(ISLAND_MIDDLE - i) + Math.abs(ISLAND_MIDDLE - j);
    double item = ISLAND_SIZE - manDist;
    if (item > (this.waterHeight + ISLAND_MIDDLE)) {
      int randomNum = 
          rand.nextInt(ISLAND_SIZE - ISLAND_MIDDLE) + (ISLAND_MIDDLE + 1);
      item = randomNum;
    }
    return item;
  }

  // determines the heights of each cell in the random terrain mode
  ArrayList<ArrayList<Double>> initDoubleRanTer() {
    ArrayList<ArrayList<Double>> boardHeights2 = 
        new ArrayList<ArrayList<Double>>(ForbiddenIslandWorld.ISLAND_SIZE + 1);

    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
      ArrayList<Double> boardHeights1 = 
          new ArrayList<Double>(ForbiddenIslandWorld.ISLAND_SIZE + 1);
      boardHeights2.add(i, initDoubleRanZero(boardHeights1, i));
    }

    boardHeights2.get(0).set(ISLAND_MIDDLE, 1.0);
    boardHeights2.get(ISLAND_SIZE).set(ISLAND_MIDDLE, 1.0);
    boardHeights2.get(ISLAND_MIDDLE).set(0, 1.0);
    boardHeights2.get(ISLAND_MIDDLE).set(ISLAND_SIZE, 1.0);
    boardHeights2.get(ISLAND_MIDDLE).set(ISLAND_MIDDLE, 32.0);

    initRanTerHelper(ForbiddenIslandWorld.ISLAND_SIZE - ForbiddenIslandWorld.ISLAND_SIZE,
        ForbiddenIslandWorld.ISLAND_SIZE - ForbiddenIslandWorld.ISLAND_SIZE,
        ForbiddenIslandWorld.ISLAND_MIDDLE,ForbiddenIslandWorld.ISLAND_MIDDLE, boardHeights2);
    initRanTerHelper(ForbiddenIslandWorld.ISLAND_MIDDLE, ForbiddenIslandWorld.ISLAND_SIZE
        - ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE,
        ForbiddenIslandWorld.ISLAND_MIDDLE, boardHeights2);
    initRanTerHelper(ForbiddenIslandWorld.ISLAND_MIDDLE, 
        ForbiddenIslandWorld.ISLAND_MIDDLE, ForbiddenIslandWorld.ISLAND_SIZE, 
        ForbiddenIslandWorld.ISLAND_SIZE, boardHeights2);
    initRanTerHelper(ForbiddenIslandWorld.ISLAND_SIZE - ForbiddenIslandWorld.ISLAND_SIZE,
        ForbiddenIslandWorld.ISLAND_MIDDLE, 
        ForbiddenIslandWorld.ISLAND_MIDDLE, ForbiddenIslandWorld.ISLAND_SIZE,
        boardHeights2);

    return boardHeights2;
  }

  // helps to initialize the random terrain heights
  void initRanTerHelper(int x1, int y1, int x2, int y2, 
      ArrayList<ArrayList<Double>> boardHeights2) {
    tl = boardHeights2.get(x1).get(y1);
    tr = boardHeights2.get(x2).get(y1);
    bl = boardHeights2.get(x1).get(y2);
    br = boardHeights2.get(x2).get(y2);
    if (boardHeights2.get(y1).get((x1 + x2) / 2) == 0.0) {
      boardHeights2.get(y1).set((x1 + x2) / 2, 
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 4) + (tl + tr) / 2);
    }
    if (boardHeights2.get(y2).get((x1 + x2) / 2) == 0.0) {
      boardHeights2.get(y2).set((x1 + x2) / 2,
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 4) + (tl + bl) / 2);
    }     
    if (boardHeights2.get((y1 + y2) / 2).get(x1) == 0.0) {
      boardHeights2.get((y1 + y2) / 2).set(x1, 
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 4) + (tr + br) / 2);
    }
    if (boardHeights2.get((y1 + y2) / 2).get(x2) == 0.0) {
      boardHeights2.get((y1 + y2) / 2).set(x2, 
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 4) + (bl + br) / 2);      
    }
    if (boardHeights2.get((y1 + y2) / 2).get((x1 + x2) / 2) == 0.0) {
      boardHeights2.get((y1 + y2) / 2).set((x1 + x2) / 2, 
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 4) + 
          (bl + br + tl + tr) / 4);
    }
    if (x1 + 1 != x2 && y1 + 1 != y2) {
      initRanTerHelper(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2, boardHeights2);
      initRanTerHelper((x1 + x2) / 2, y1, x2, (y1 + y2) / 2, boardHeights2);
      initRanTerHelper((x1 + x2) / 2, (y1 + y2) / 2, x2, y2, boardHeights2);
      initRanTerHelper(x1, (y1 + y2) / 2, (x1 + x2) / 2, y2, boardHeights2);
    }
  }

  // helps determine the heights of each cell in the random terrain mode
  ArrayList<Double> initDoubleRanZero(ArrayList<Double> boardHeights1, int i) {
    for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
      boardHeights1.add(j, 0.0);
    }
    return boardHeights1;
  }

  // turns the array of heights into an array of cells with the specified heights
  void initBoardCell(ArrayList<ArrayList<Double>> boardHeights) {
    ArrayList<ArrayList<Cell>> boardCells2 = 
        new ArrayList<ArrayList<Cell>>(ForbiddenIslandWorld.ISLAND_SIZE + 1);

    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
      ArrayList<Cell> boardCells1 = 
          new ArrayList<Cell>(ForbiddenIslandWorld.ISLAND_SIZE + 1);
      boardCells2.add(i, 
          initBoardCellAdder(boardCells1, i, boardHeights.get(i)));
    }
    this.cellLink(boardCells2);
    this.boardCell = boardCells2;
  }

  // creates each array of cells for the array of array of cells
  ArrayList<Cell> initBoardCellAdder(ArrayList<Cell> boardCells1, int i, 
      ArrayList<Double> boardHeights) {

    for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
      boardCells1.add(j, this.initBoardCellCreator(i, j, boardHeights.get(j)));
    }
    return boardCells1;

  }

  // creates each individual cell for the array of cells and determines if it is an OceanCell
  Cell initBoardCellCreator(int i, int j, double height) {
    if (height <= ForbiddenIslandWorld.ISLAND_MIDDLE) {
      return new OceanCell(height, i, j, true);
    } 
    else if (height <= (ForbiddenIslandWorld.ISLAND_MIDDLE + this.waterHeight)) {
      return new Cell(height, i, j, true);
    } 
    else {
      return new Cell(height, i, j, false);
    }
  }

  // links the cells together
  void cellLink(ArrayList<ArrayList<Cell>> boardCell) {

    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
        if (i == 0) {
          boardCell.get(i).get(j).left = boardCell.get(i).get(j);
        }
        else {
          boardCell.get(i).get(j).left = boardCell.get(i - 1).get(j);
        }
        if (i == ForbiddenIslandWorld.ISLAND_SIZE) {
          boardCell.get(i).get(j).right = boardCell.get(i).get(j);
        }
        else {
          boardCell.get(i).get(j).right = boardCell.get(i + 1).get(j);
        }
        if (j == 0) {
          boardCell.get(i).get(j).top = boardCell.get(i).get(j);
        }
        else {
          boardCell.get(i).get(j).top = boardCell.get(i).get(j - 1);
        }
        if (j == ForbiddenIslandWorld.ISLAND_SIZE) {
          boardCell.get(i).get(j).bottom = boardCell.get(i).get(j);
        }
        else {
          boardCell.get(i).get(j).bottom = boardCell.get(i).get(j + 1);
        }
      }
    }
  }

  // initializes the IList of Cells and links them all together
  IList<Cell> initBoard(ArrayList<ArrayList<Cell>> boardCells) {
    IList<Cell> boardFinal = new MtList<Cell>();

    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
        boardFinal = new ConsList<Cell>(boardCells.get(i).get(j), boardFinal);
      }
    }
    return boardFinal;
  }

  // extracts the coast Cells from the board IList<Cell>
  void coastCells() {   
    for(Cell elt : this.board) {
      if (elt.checkAdj() && !elt.isFlooded
          && elt.height <= this.waterHeight + ISLAND_MIDDLE) {
        elt.isFlooded = true;
      }
    }
  }

  // generates the list of targets
  IList<Target> targetList(IList<Target> targets) {
    int randomNum1 = rand.nextInt(ISLAND_SIZE);
    int randomNum2 = rand.nextInt(ISLAND_SIZE);

    if (targets.length() == 5) {
      return targets;
    }
    if (this.boardCell.get(randomNum1).get(randomNum2).isFlooded) {
      return targetList(targets);
    }
    else {
      return targetList(new ConsList<Target>(
          new Target(randomNum1, randomNum2), targets));
    }
  }

  // generates the player onto the board
  Player initPlayer() {
    int randomNum1 = rand.nextInt(ISLAND_SIZE);
    int randomNum2 = rand.nextInt(ISLAND_SIZE);

    if (this.boardCell.get(randomNum1).get(randomNum2).isFlooded) {
      return this.initPlayer();
    }
    else {
      return new Player(randomNum1, randomNum2);
    }
  }

  // generates the helicopter target onto the highest cell on the board
  HelicopterTarget initHelicopter() {
    int x = this.initHelicopterHelp(0.0).x;
    int y = this.initHelicopterHelp(0.0).y;

    return new HelicopterTarget(x, y);
  }

  // helps generates the helicopter target onto the highest cell on the board
  Cell initHelicopterHelp(double currentHighest) {
    Cell currentHighestCell = new Cell(currentHighest, 0, 0, false);

    for (Cell elt : this.board) {
      if (elt.height > currentHighest) {
        currentHighestCell = elt;
        currentHighest = elt.height;
      }
    }
    return currentHighestCell;
  }

  // determines if a player has picked up a target
  public boolean pickUpTarget() {
    if (this.target.empty()) {
      return true;
    }
    else {
      IList<Target> newList = this.target.getTarget(new PickUpTarget(), this.player, this.target);
      this.target = newList;
      return false;
    }
  }

  // determines if a player has picked up the helicopter target
  public boolean pickUpHelicopter() {
    return this.pickUpTarget() && this.helicopter.pickUp(this.player);
  }

  // determines if a player is on a flooded cell
  public boolean playerOnFloodedCell() {
    if (this.board.playerOnFloodedCellHelp(this.player, new FloodedCell())) {
      return true;
    }
    else {
      return false;
    }
  }

  // produces the image of the world
  public javalib.impworld.WorldScene makeScene() {
    javalib.impworld.WorldScene s = this.getEmptyScene();    
    for (Cell cell : this.board) {
      s.placeImageXY(cell.drawCell(this.waterHeight), (cell.x * 10) + 5, (cell.y * 10) + 5);
    }
    for (Target target : this.target) {
      s.placeImageXY(target.drawTarget(), (target.x * 10) + 5, (target.y * 10) + 5);
    }
    s.placeImageXY(helicopter.drawTarget(), (helicopter.x * 10) + 5, (helicopter.y * 10) + 5);
    s.placeImageXY(player.drawPlayer(), (player.x * 10) + 5, (player.y * 10) + 5);

    return s;
  } 

  // raises the water height of the island 1 foot every 10 ticks
  public void onTick() {
    if (this.counter % 10 == 0) {
      this.waterHeight = this.waterHeight + 1;
      this.coastCells();
      this.counter++;
    }
    else {
      this.coastCells();
      this.counter++;
    }
  }

  // resets the game, chooses the game mode and moves the player
  public void onKeyEvent(String ke) {
    if (ke.equals("m")) {
      this.waterHeight = 0;
      this.boardDouble = this.initBoardDouble();
      this.initBoardCell(this.boardDouble);
      this.board = this.initBoard(this.boardCell);
      this.target = this.targetList(new MtList<Target>());
      this.player = this.initPlayer();
      this.helicopter = this.initHelicopter();
    }
    else if (ke.equals("r")) {
      this.waterHeight = 0;
      this.boardDouble = this.initBoardDoubleRan();
      this.initBoardCell(this.boardDouble);
      this.board = this.initBoard(this.boardCell);
      this.target = this.targetList(new MtList<Target>());
      this.player = this.initPlayer();
      this.helicopter = this.initHelicopter();
    }
    else if (ke.equals("t")) {
      this.waterHeight = 0;
      this.boardDouble = this.initDoubleRanTer();
      this.initBoardCell(this.boardDouble);
      this.board = this.initBoard(this.boardCell);
      this.target = this.targetList(new MtList<Target>());
      this.player = this.initPlayer();
      this.helicopter = this.initHelicopter();
    }
    else {
      this.player = this.player.movePlayer(ke, this.boardCell);
    }
  }

  // displays end game message
  public javalib.impworld.WorldScene makeFinalScene(String end, WorldScene s) {
    if (end.equals("You win!")) {
      s.placeImageXY(new TextImage(end, 60, new Color(0, 0, 0)), 
          (ISLAND_MIDDLE * 10) + 5, (ISLAND_MIDDLE * 10) + 5);
    }
    else if (end.equals("You lose!")) {
      s.placeImageXY(new TextImage(end, 60, new Color(0, 0, 0)), 
          (ISLAND_MIDDLE * 10) + 5, (ISLAND_MIDDLE * 10) + 5);
    }
    return s;
  }

  // to end the game, with different scenarios that can determine the game ending
  public WorldEnd worldEnds() {
    // if the player picks up the helicopter: player WINS
    if (this.pickUpHelicopter()) {
      return new WorldEnd(true, this.makeFinalScene("You win!", this.makeScene()));
    }

    // if the player is on a flooded cell: player LOSES
    if (this.playerOnFloodedCell()) {
      return new WorldEnd(true, this.makeFinalScene("You lose!", this.makeScene()));
    }

    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

// to represent the Forbidden Island examples
class ExamplesIsland {

  // cell examples
  Cell terrain1 = new Cell(6.0, 3, 4, false);
  Cell ocean1 = new OceanCell(0.0, 2, 2, true);
  Cell cell1 = new Cell(3.0 ,0, 0, false);
  Cell cell2 = new Cell(4.0 ,0, 1, false);
  Cell cell3 = new Cell(5.0 ,0, 2, false);
  Cell cell4 = new Cell(6.0 ,0, 3, false);
  Cell cell5 = new Cell(33.0, 15, 24, true);
  Cell cell6 = new Cell(16.0, 13, 40, true);
  Cell cell7 = new Cell(25.0, 10, 17, false);
  OceanCell oceanCell1 = new OceanCell(1.0, 1, 0, true);
  OceanCell oceanCell2 = new OceanCell(2.0, 1, 1, true);
  OceanCell oceanCell3 = new OceanCell(1.0, 1, 2, true);

  // list of cell examples
  IList<Cell> emptyBoard = new MtList<Cell>();
  IList<Cell> board1 = new ConsList<Cell>(this.cell1, new ConsList<Cell>(this.oceanCell1,
      new ConsList<Cell>(this.cell2, new ConsList<Cell>(this.cell3,
          new ConsList<Cell>(this.oceanCell1, new ConsList<Cell>(this.cell1, this.emptyBoard))))));

  // Forbidden Island world examples
  ForbiddenIslandWorld world = new ForbiddenIslandWorld(8);
  ForbiddenIslandWorld world2 = new ForbiddenIslandWorld(2);

  // Target examples
  Target t1 = new Target(5, 4);
  Target t2 = new Target(14, 27);
  Target t3 = new Target(40, 35);
  HelicopterTarget t4 = new HelicopterTarget(36, 36);

  // List of Target examples
  IList<Target> targetList1 = new ConsList<Target>(this.t1,
      new ConsList<Target>(this.t2, new MtList<Target>()));
  IList<Target> targetList2 = new ConsList<Target>(this.t2,
      new ConsList<Target>(this.t2, new MtList<Target>()));
  IList<Target> targetList3 = new ConsList<Target>(this.t1,
      new ConsList<Target>(this.t2, new ConsList<Target>(this.t3,
          new ConsList<Target>(this.t4, new MtList<Target>()))));
  IList<Target> targetList4 = new MtList<Target>();

  // Player examples
  Player p1 = new Player(14, 27);
  Player p2 = new Player(3, 25);
  Player p3 = new Player(36, 36);

  IPred<Target, Player> pickTarget = new PickUpTarget();

  // to test method empty
  boolean testEmpty(Tester t) {
    return t.checkExpect(this.board1.empty(), false)
        && t.checkExpect(this.targetList4.empty(), true);
  }

  // to test method length
  boolean testLength(Tester t) {
    return t.checkExpect(this.targetList4.length(), 0)
        && t.checkExpect(this.targetList3.length(), 4);
  }

  // to test method getTarget
  boolean testGetTarget(Tester t) {
    return t.checkExpect(this.targetList4.getTarget(this.pickTarget, this.p1, this.targetList4), this.targetList4)
        && t.checkExpect(this.targetList1.getTarget(this.pickTarget, this.p1, this.targetList4),
            new ConsList<Target>(this.t1, this.targetList4))
        && t.checkExpect(this.targetList2.getTarget(this.pickTarget, this.p1, this.targetList4),
            this.targetList4);
  }

  // to test method movePlayer
  boolean testMovePlayer(Tester t) {
    return t.checkExpect(this.p1.movePlayer("right", this.world.boardCell), new Player(15, 27))
        && t.checkExpect(this.p1.movePlayer("left", this.world.boardCell), new Player(13, 27))
        && t.checkExpect(this.p2.movePlayer("up", this.world.boardCell), new Player(3, 25))
        && t.checkExpect(this.p2.movePlayer("down", this.world.boardCell), new Player(3, 25));    
  }

  // to test method drawPlayer
  boolean testDrawPlayer(Tester t) {
    return t.checkExpect(this.p1.drawPlayer(),
        new CircleImage(5, "solid", new Color(100, 0, 100)))
        && t.checkExpect(this.p2.drawPlayer(),
            new CircleImage(5, "solid", new Color(100, 0, 100)));
  }

  // to test method drawTarget
  boolean testTarget(Tester t) {
    return t.checkExpect(this.t1.drawTarget(),
        new CircleImage(5, "solid", new Color(150, 150, 0)))
        && t.checkExpect(this.t2.drawTarget(),
            new CircleImage(5, "solid", new Color(150, 150, 0)))
        && t.checkExpect(this.t4.drawTarget(),
            new CircleImage(5, "outline", new Color(100, 40, 140)));
  }

  // to test method pickUp
  boolean testPickUp(Tester t) {
    return t.checkExpect(this.t4.pickUp(this.p1), false)
        && t.checkExpect(this.t4.pickUp(this.p3), true);
  }

  // to test method drawCell
  boolean testDrawCell(Tester t) {
    this.terrain1.updateCellTop(this.cell7);
    this.terrain1.updateCellLeft(this.cell7);
    this.terrain1.updateCellRight(this.cell7);
    this.terrain1.updateCellBottom(this.cell7);

    this.ocean1.updateCellTop(this.cell2);
    this.ocean1.updateCellLeft(this.cell2);
    this.ocean1.updateCellRight(this.cell2);
    this.ocean1.updateCellBottom(this.cell2);

    return t.checkExpect(this.cell5.drawCell(20), new RectangleImage(10, 10, "solid", new Color(0, 0, 0)))
        && t.checkExpect(this.cell6.drawCell(10), new RectangleImage(10, 10, "solid", new Color(0, 0, 255)))
        && t.checkExpect(this.cell7.drawCell(0), new RectangleImage(10, 10, "solid", new Color(0, 150, 150)))
        && t.checkExpect(this.cell2.drawCell(0), new RectangleImage(10, 10, "solid", new Color(16, 255, 16)));
  }

  // to test method checkAdj
  boolean testCheckAdj(Tester t) {
    this.terrain1.updateCellBottom(this.ocean1);
    this.terrain1.updateCellLeft(this.ocean1);
    this.terrain1.updateCellRight(this.ocean1);
    this.terrain1.updateCellTop(this.ocean1);

    this.ocean1.updateCellBottom(this.terrain1);
    this.ocean1.updateCellLeft(this.terrain1);
    this.ocean1.updateCellRight(this.terrain1);
    this.ocean1.updateCellTop(this.terrain1);

    this.ocean1.updateCellTop(this.cell7);
    this.ocean1.updateCellLeft(this.cell7);
    this.ocean1.updateCellRight(this.cell7);
    this.ocean1.updateCellBottom(this.cell7);

    return t.checkExpect(this.ocean1.checkAdj(), false)
        && t.checkExpect(this.terrain1.checkAdj(), true)
        && t.checkExpect(this.cell7.checkAdj(), true);
  }

  // to test method initBoardDouble
  // test by checking size of generated arrayList
  boolean testInitBoardDouble(Tester t) {
    return t.checkExpect(this.world.initBoardDouble().size(), 65)
        && t.checkExpect(this.world2.initBoardDouble().size(), 65);
  }

  // to test method initBoardCellCreator
  boolean testInitBoardCellCreator(Tester t) {
    return t.checkExpect(this.world.initBoardCellCreator(3, 4, 4), new OceanCell(4, 3, 4, true))
        && t.checkExpect(this.world.initBoardCellCreator(1, 2, 0), new OceanCell(0, 1, 2, true));
  }

  // to test method initBoardDoubleRan
  // test by checking size of generated arrayList
  boolean testInitBoardDoubleRan(Tester t) {
    return t.checkExpect(this.world.initBoardDoubleRan().size(), 65)
        && t.checkExpect(this.world2.initBoardDoubleRan().size(), 65);
  }

  // to test method initDoubleRanTer
  // test by checking size of generated arrayList
  boolean testInitDoubleRanTer(Tester t) {
    return t.checkExpect(this.world.initDoubleRanTer().size(), 65)
        && t.checkExpect(this.world2.initDoubleRanTer().size(), 65);
  }

  // to test method targetList
  boolean testTargetList(Tester t) {
    return t.checkExpect(this.world.target.length(), 5);
  }

  // to test method initPlayer
  // test by checking the range of where the player is initially generated
  boolean testInitPlayer(Tester t) {
    return t.checkRange(this.world.player.x, 0, 64)
        && t.checkRange(this.world.player.y, 0, 64);
  }

  // to test method initHelicopter
  // test by checking the range of where the helicopter is initially generated
  boolean testInitHelicopter(Tester t) {
    return t.checkRange(this.world.helicopter.x, 0, 64)
        && t.checkRange(this.world.helicopter.y, 0, 64);
  }

  // to test method pickUpTarget
  boolean testPickUpTarget(Tester t) {
    this.world2.target = this.targetList4;

    return t.checkExpect(this.world.pickUpTarget(), false)
        && t.checkExpect(this.world2.pickUpTarget(), true);
  }

  // to test method pickUpHelicopter
  boolean testPickUpHelicopter(Tester t) {
    this.world2.target = this.targetList4;
    this.world2.helicopter = new HelicopterTarget(14, 27);
    this.world2.player = this.p1;

    return t.checkExpect(this.world.pickUpHelicopter(), false)
        && t.checkExpect(this.world2.pickUpHelicopter(), true);
  }

  // to test method onTick
  boolean testOnTick(Tester t) {
    this.world.onTick();

    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();
    this.world2.onTick();

    return t.checkExpect(this.world.counter, 1)
        && t.checkExpect(this.world.waterHeight, 9)
        && t.checkExpect(this.world2.counter, 10)
        && t.checkExpect(this.world2.waterHeight, 3);
  }

  // to run the game
  public static void main(String[] argv) {

    // water height for random terrain is 32
    // run the game
    ForbiddenIslandWorld w = new ForbiddenIslandWorld(0);
    w.bigBang((ForbiddenIslandWorld.ISLAND_SIZE + 1) * 10, ((ForbiddenIslandWorld.ISLAND_SIZE + 1) * 10), .1);
  }
}