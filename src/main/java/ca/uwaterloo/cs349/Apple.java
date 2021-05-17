package ca.uwaterloo.cs349;
import java.util.ArrayList;

public class Apple  {
    public ArrayList<Coordinate> placements = new ArrayList<>(15);
    Apple() {
        placements.add(0,new Coordinate(0,0));
        placements.add(0,new Coordinate(1,7));
        placements.add(0,new Coordinate(2,2));
        placements.add(0,new Coordinate(9,3));
        placements.add(0,new Coordinate(14,3));
        placements.add(0,new Coordinate(13,9));
        placements.add(0,new Coordinate(12,4));
        placements.add(0,new Coordinate(5,12));
        placements.add(0,new Coordinate(12,2));
        placements.add(0,new Coordinate(6,11));
        placements.add(0,new Coordinate(6,6));
        placements.add(0,new Coordinate(13,7));
        placements.add(0,new Coordinate(2,3));
        placements.add(0,new Coordinate(0,0));
        placements.add(0,new Coordinate(4,2));
    }
}