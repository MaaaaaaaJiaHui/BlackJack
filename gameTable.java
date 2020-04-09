import javax.swing.*;
import java.awt.*;
import java.net.URL;
//import Cards.*;
//import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class gameTable extends JPanel
{
    //private DealerCardHand dealer;
    private PlayerCardHand player;
    
    private boolean showAllDealerCards;
    
    // drawing position vars
    private final int CARD_INCREMENT = 20;
    private final int CARD_START = 100;
    private final int CARD_START2 = 550;
    private final int DEALER_POSITION = 50;
    private final int PLAYER_POSITION = 200;
    private final int PLAYER_POSITION2 = 50;
    private final int CARD_IMAGE_WIDTH = 71;
    private final int CARD_IMAGE_HEIGHT = 96;
    public int result = 0;
    private final int NAME_SPACE = 10;
    
    private Font handTotalFont;
    private Font playerNameFont;
    
    private String dealerName;
    private String playerName = "undefined";

	public Map<String, PlayerCardHand> player_card = new HashMap<String, PlayerCardHand>();
	
    private Image[] cardImages = new Image[CardPack.CARDS_IN_PACK + 1];
    
    //private HashMap<String,String> players = new HashMap<String,String>();
    
    public List<String> players=new ArrayList<>();
    
    // take game model as parameter so that it can get cards and draw them
    public gameTable()
    {
        super();
        
        this.setBackground(Color.BLUE);
        this.setOpaque(false);
        
        handTotalFont = new Font("Serif", Font.PLAIN, 96);
        playerNameFont = new Font("Serif", Font.ITALIC, 20);
        
        showAllDealerCards = true;
        
        for (int i = 0; i < CardPack.CARDS_IN_PACK; i++)
        {
            String cardName = "card_images/" + (i+1) + ".png";
            
            URL urlImg = getClass().getResource(cardName);
            Image cardImage = Toolkit.getDefaultToolkit().getImage(urlImg);
            cardImages[i] = cardImage;
        }
        
        String backCard = "card_images/red_back.png";
        
        URL backCardURL = getClass().getResource(backCard);
        Image backCardImage = Toolkit.getDefaultToolkit().getImage(backCardURL);
        
        cardImages[CardPack.CARDS_IN_PACK] = backCardImage;
        
        MediaTracker imageTracker = new MediaTracker(this);
        
        for (int i = 0; i < CardPack.CARDS_IN_PACK + 1; i++)
        {
            imageTracker.addImage(cardImages[i], i + 1); 
        }
        
        try
        {
            imageTracker.waitForAll();
        }
        catch (InterruptedException excep)
        {
            System.out.println("Interrupted while loading card images.");
        }
    }
    public void setRes(int res){
    	result = res;
    }
    public void setName(String playerName)
    {//String dealerName, 
        //this.dealerName = dealerName;
        this.playerName = playerName;
    }
    
    public void update(PlayerCardHand player, boolean showDealer)
    {        
        //DealerCardHand dealer,  this.dealer = dealer;
        this.player = player;
        this.showAllDealerCards = showDealer;
    }
    
    // draw images from jar archive or dir: http://www.particle.kth.se/~fmi/kurs/PhysicsSimulation/Lectures/10B/jar.html
    public void addPlayer(String name){
    	players.add(name);
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        g.setColor(Color.WHITE);
        
        g.setFont(playerNameFont);
        
        int i = 1;
        g.drawString(playerName, CARD_START, PLAYER_POSITION - NAME_SPACE);
        for(String a:players){
        	if(a.equals(playerName)){
        		//i++;
        		continue;
        	}
        	if( i < 2){
        		g.drawString(a, CARD_START, DEALER_POSITION - NAME_SPACE);
        	}else{
        		if(i==2){
            		g.drawString(a, CARD_START2, PLAYER_POSITION2 - NAME_SPACE);
        		}else{
            		g.drawString(a, CARD_START2, PLAYER_POSITION - NAME_SPACE);
        			
        		}
        	}
        	i++;
        }
        
        // qaq
        
        g.setFont(handTotalFont);
        
        String cardName;
    
        // draw dealer cards
    
        i = CARD_START;
    /*
        if (showAllDealerCards)
        {
            for (Card aCard : dealer)
            {
                g.drawImage(cardImages[aCard.getCode() - 1], i, DEALER_POSITION, this);

                i += CARD_INCREMENT;
            }
        
            g.drawString(Integer.toString(dealer.getTotal()), i 
                + CARD_IMAGE_WIDTH + CARD_INCREMENT, DEALER_POSITION 
                + CARD_IMAGE_HEIGHT);
        }
        else
        {
            for (Card aCard : dealer)
            {
                g.drawImage(cardImages[CardPack.CARDS_IN_PACK], i, DEALER_POSITION, this);

                i += CARD_INCREMENT;
            }
        
            try
            {
                Card topCard = dealer.lastElement();
            
                i -= CARD_INCREMENT;
            
                g.drawImage(cardImages[topCard.getCode() - 1], i, DEALER_POSITION, this);
            
                
                    
                //
                
            }
            catch (Exception e)
            {
                // caused when trying to draw cards from empty vector
                // can't use NoSuchElementException above...?
                System.out.println("No cards have been dealt yet.");
            }
            
            g.drawString("?", i + CARD_IMAGE_WIDTH + CARD_INCREMENT, 
                DEALER_POSITION + CARD_IMAGE_HEIGHT);
            
        }*/
    
        // draw player cards
        int aa = PLAYER_POSITION,j=1;
        i = CARD_START;
        
        ///player = new PlayerCardHand();
        PlayerCardHand the_player_card;
        
        the_player_card = player_card.get(playerName);
    	try{
    		if(the_player_card.size()==0){
        		the_player_card = new PlayerCardHand();
        	}
        	//System.out.println("Num:"+a+"::"+the_player_card.size());
    	}catch(Exception e){
    		the_player_card = new PlayerCardHand();
    		//e.printStackTrace();
    		//System.out.println("Error:"+a);
    	}
        for (Card aCard : the_player_card)
        {
        	//if(AppWindow.result==0 && aCard.getCode() == the_player_card.get(the_player_card.size()-1).getCode()){//.getSuit().getName()=="hidden"
            //    g.drawImage(cardImages[CardPack.CARDS_IN_PACK], i, aa, this);
            //   the_player_card.get(the_player_card.size()-1).setSuit(new Suit("hidden"));
        	//}else{
                g.drawImage(cardImages[aCard.getCode() - 1], i, aa, this);
        	//}
            i += CARD_INCREMENT;
        }
        
        g.drawString(Integer.toString(the_player_card.getTotal()), i + CARD_IMAGE_WIDTH + CARD_INCREMENT, aa + CARD_IMAGE_HEIGHT);
        
        for(String a:players){
        	if(a.equals(playerName)){
        		i = CARD_START;
        		aa = PLAYER_POSITION;
        		continue;
        	}else{
        		if(j<2){
        			i = CARD_START;
            		aa = PLAYER_POSITION2;
        		}else{
        			if(j==2){
            			i = CARD_START2;
                		aa = PLAYER_POSITION2;
                		
        			}else{
            			i = CARD_START2;
                		aa = PLAYER_POSITION;
        				
        			}
        		}
        		
        	}
        	the_player_card = player_card.get(a);
        	try{
        		if(the_player_card.size()==0){
            		the_player_card = new PlayerCardHand();
            	}
            	//System.out.println("Card num:"+a+"::"+the_player_card.size());
        	}catch(Exception e){
        		the_player_card = new PlayerCardHand();
        		//e.printStackTrace();
        		//System.out.println("Wrong Card:"+a);
        	}
        	try{
            for (Card aCard : the_player_card)
            {
            	System.out.println("res:"+AppWindow.result);
            	if(AppWindow.result==0 && aCard.getCode() == the_player_card.get(the_player_card.size()-1).getCode() && !a.equals(playerName)){//.getSuit().getName()=="hidden"
                    g.drawImage(cardImages[CardPack.CARDS_IN_PACK], i, aa, this);
                    the_player_card.get(the_player_card.size()-1).setSuit(new Suit("hidden"));
            	}else{
                    g.drawImage(cardImages[aCard.getCode() - 1], i, aa, this);
            	}
                i += CARD_INCREMENT;
            }
        	}catch(Exception e){
        		
        	}
            g.drawString(Integer.toString(the_player_card.getTotal()), i + CARD_IMAGE_WIDTH + CARD_INCREMENT, aa + CARD_IMAGE_HEIGHT);
            
            j++;
        }
    }
}