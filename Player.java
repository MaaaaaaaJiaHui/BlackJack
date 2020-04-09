import java.util.ArrayList;
 
/**
 *
 */
public class Player {
    public int balance = 100;  //The money
    public boolean bust = false; // lost
    public boolean black = false; //blackjack
    public ArrayList<Integer> al = new ArrayList<Integer>(); //All cards
    public int currentPoint = 0; //TotalCount
    public String name;
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public Player() {
 
    }
 
    public int getBalance() {
        return balance;
    }
 
    public void setBalance(int balance) {
        this.balance = balance;
    }
 
    public boolean isBust() {
        return bust;
    }
 
    public void setBust(boolean bust) {
        this.bust = bust;
    }
 
    public boolean isBlack() {
        return black;
    }
 
    public void setBlack(boolean black) {
        this.black = black;
    }
    /*
    public ArrayList<Integer> getAl() {
        return al;
    }
 
    public void setAl(ArrayList<Integer> al) {
        this.al = al;
    }
*/
    public int getCurrentPoint() {
        return currentPoint;
    }
 
    public void setCurrentPoint(int currentPoint) {
        this.currentPoint = currentPoint;
    }
}