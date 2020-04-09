import java.util.Random;


public class Util {

    public static int randroom(int start, int end) {
        // TODO Auto-generated method stub
         
        //Random
        Random random = new Random();              
         
        // make random num
        int number = random.nextInt(end - start + 1) + start;
         
        //
        return number;
         
    }
}
