package ca.uwaterloo.cs349;
import java.util.ArrayList;

public class Snake  {
    private char direction = 'E';
    private final int boardXWidth = 20;
    private final int boardYHeight = 15;
    private ArrayList<Coordinate> coords = new ArrayList<>(3);
    Snake() {
        Coordinate first = new Coordinate(0,7);
        Coordinate second = new Coordinate(1,7);
        Coordinate third = new Coordinate(2,7);
        coords.add(third);
        coords.add(second);
        coords.add(first);
    }
   public char getDirection() {
        return direction;
    }
    public void changeDirection(char arrow) {
        if (direction == 'N' && arrow == 'R') {
            direction = 'E';
        } else if (direction == 'N') {
            direction = 'W';
        } else if (direction == 'W' && arrow == 'R') {
            direction = 'N';
        } else if (direction == 'W') {
            direction = 'S';
        } else if (direction == 'S' && arrow == 'R') {
            direction = 'W';
        } else if (direction == 'S') {
            direction = 'E';
        } else if (direction == 'E' && arrow == 'R') {
            direction = 'S';
        } else {
            direction = 'N';
        }

    }
    public Coordinate getTail() {
        return coords.get(coords.size()-1);
    }
    public Coordinate getHead() {
        return coords.get(0);
    }
    public int getSnakeSize() {
        return coords.size();
    }
    public void move() {
        Coordinate head = new Coordinate(getHead().x, getHead().y);
        // whatever is in pos 1 equals stuff in pos 0, pos 2 equals pos 1, pos n-1 equals n-2

        if (direction == 'N') {
            head.y = head.y - 1;
        } else if (direction == 'S') {
            head.y = head.y + 1;
        } else if (direction == 'W') {
            head.x = head.x - 1;
        } else {
            head.x = head.x + 1;
        }
        coords.remove(coords.size() - 1);
        coords.add(0, head);
    }


    public ArrayList<Coordinate> getCoordinates() {
        return coords;
    }



    public boolean checkDead() {
        Coordinate head = getHead();
        if (head.x < 0 || head.x >= boardXWidth) return true;
        if (head.y < 0 || head.y >= boardYHeight) return true;
        for (int i = 1; i < coords.size(); i++) {
            if (head.x == coords.get(i).x && head.y == coords.get(i).y) {
                return true;
            }
        }
        return false;
    }
    public boolean checkApplesCaptured(ArrayList<Coordinate> apples) {
        Coordinate head = new Coordinate(getHead().x, getHead().y);
        for (int i = 0; i < apples.size(); i++) {
            Coordinate apple = new Coordinate(apples.get(i).x, apples.get(i).y);
            if (apple.x == head.x && apple.y == head.y) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Coordinate c) {
        for (int i = 0; i < coords.size(); i++) {
            if (coords.get(i).x == c.x && coords.get(i).y == c.y) {
                return true;
            }
        }
        return false;
    }

    public boolean appleBoardContainsGrowth(ArrayList<Coordinate> applesBoard, Coordinate c) {
        for (int i = 0; i < applesBoard.size(); i++) {
            Coordinate curr = new Coordinate(applesBoard.get(i).x, applesBoard.get(i).y);
            if (curr.x == c.x && curr.y == c.y) return true;
        }
        return false;
    }
    // moveCoordinates, grow, getTail, checkDead,getHead, initializeSnake, checkAppleCaptured
    public void grow(ArrayList<Coordinate> applesBoard) {
        Coordinate tail = new Coordinate(getTail().x, getTail().y);
        if (tail.x - 1 >= 0 && !contains(new Coordinate(tail.x-1, tail.y))
                && !appleBoardContainsGrowth(applesBoard, new Coordinate(tail.x-1, tail.y))) {
            coords.add(new Coordinate(tail.x-1, tail.y));
        } else if (tail.x + 1 < boardXWidth && !contains(new Coordinate(tail.x+1, tail.y))
        && !appleBoardContainsGrowth(applesBoard, new Coordinate(tail.x+1, tail.y))) {
            coords.add(new Coordinate(tail.x+1, tail.y));
        } else if (tail.y - 1 >= 0 && !contains(new Coordinate(tail.x, tail.y-1))
                && !appleBoardContainsGrowth(applesBoard, new Coordinate(tail.x, tail.y-1))) {
            coords.add(new Coordinate(tail.x, tail.y-1));
        } else if (tail.y + 1 < boardYHeight && !contains(new Coordinate(tail.x, tail.y + 1))
                && !appleBoardContainsGrowth(applesBoard, new Coordinate(tail.x, tail.y+1))) {
            coords.add(new Coordinate(tail.x, tail.y + 1));
        }
    }
    public static void main(String args[])
    {
    }
}