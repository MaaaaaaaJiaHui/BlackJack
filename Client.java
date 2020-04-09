import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Application window.
 * Holds the menu-bar etc.
 *
 * @author David Winter
 */
//jar -cfvm Blackjack.jar Blackjack.mf *
//jar -cfvm Blackjack.jar Blackjack.mf *.class card_images Cards/*.class Players/*.class

import javax.swing.UIManager;

// import SendThread;

public class Client
{
 public static void main(String[] args)
 {
     try
     {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         // automatic set theme
     }
     catch (Exception e)
     {
         System.out.println(e);
     }
     
     /**
      * I think that an application should adopt the same look and feel as 
      * the system it's running on. This is what a user expects from
      * an application. It's great that Java is cross platform and all,
      * but the end-user doesn't care if a program is written in Java
      * or not.
      * So, I force the application to use the system look and feel. If
      * the look and feel can't be found, it'll use metal anyway as a last
      * resort.
      */
     
     System.setProperty("apple.laf.useScreenMenuBar", "true");
     
     AppWindow window = new AppWindow();      
 }
}
class AppWindow extends JFrame implements ActionListener, ComponentListener
{
    private GamePanel gamePanel;
    private Color defaultTableColour = new Color(6, 120, 0);
    public static int money = 100;
    // private JMenuItem savePlayer = new JMenuItem("Save Current Player");
    // private JMenuItem openPlayer = new JMenuItem("Open Existing Player");
    
    final int WIDTH = 900;
    final int HEIGHT = 600;
    public static int port;
    private Socket server = null;
    /*private void connect() {
        try {
            server = new Socket("127.0.0.1", port);
            System.out.println("Connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    private Socket s;
    private static DataOutputStream dos;
    private DataInputStream dis;
    private JScrollPane scroll;
    public static int result = 0;
    public static void sendMsg(String msg){
    	try {
			dos.writeUTF(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void connect() {
        try {
            int port = 1320;
            String ip = "127.0.0.1";
            String[] a = JOptionPane.showInputDialog(this, "What's your server&Port?(like 127.0.0.1:6666)","127.0.0.1:6666").split(":");
            port = Integer.parseInt(a[1]);
            ip = a[0];
            s = new Socket(ip,port);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            new Thread(new SendThread()).start();
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }finally{
            //Close
        }
 
    }

    /**
     * Client Receive thread
     *
     */
    class SendThread implements Runnable{
        private String str;
        private boolean iConnect = false;
 
        public void run(){
            iConnect = true;
            recMsg();
 
        }

        public void sendMsg(String str) {
            try {
                //dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
 
        }
        public void recMsg() {
            try {//127.0.0.1:5888
                while(iConnect){
                    str = dis.readUTF();//System.out.println(str);
                    String command[] = str.split(":");
                    String commands[] = command[1].split("#");
                    switch(commands[0]){
                    	case "notice":
                    		GamePanel.dealerSays.setText(commands[1]);
                    		break;
                    	case "result":
                    		result = 1;
                    		GamePanel.dealerSays.setText(commands[1]);
                    		//gamePanel.table.players.clear();
                    		gamePanel.table.player_card.clear();
                    		gamePanel.updateValues();
                    		//result = 0;
                    		break;
                    	case "newplayer"://new message, notice player
                    		// newplayer
                    		GamePanel.dealerSays.setText(commands[1]);
                    		//
                    		break;
                    	case "allplayer":
                    		gamePanel.table.players.clear();
                    		String[] allplayers = commands[1].split(",");
                			for(String a:allplayers){
                    			gamePanel.table.addPlayer(a);
                    		}
                    		gamePanel.updateValues();
                    		
                    		break;
                    	case "bindplayer"://bind player number with the machine
                    		gamePanel.table.setName(commands[1]);
                    		break;
                    	case "bet":
                    		result = 0;
                    		GamePanel.dealerSays.setText(commands[1]);
                    		gamePanel.showchoose(false);
                    		break;//changeMoney
                    	case "money":
                    		gamePanel.playerWallet.setText(commands[1]);
                    		gamePanel.setBet(Integer.parseInt(commands[1]));
                    		break;
                    	case "changeMoney":
                    		gamePanel.playerWallet.setText(commands[1]);
                    		break;
                    	case "showchoose":
                    		gamePanel.showchoose(true);
                    		break;
                    	case "showchoosemusthit":
                    		gamePanel.showchoose(true,true);
                    		break;
                    	case "getPlayerCard":
                    		String player = commands[1];
                    		PlayerCardHand player_cards = new PlayerCardHand();
                    		gamePanel.table.player_card.put(player, player_cards);
                    		String[] cards  = commands[2].split("@");
                    		Card card;
                    		Face face;Suit suit;
                    		String[] cs = new String[3];
                    		for(String a:cards){
                    			if(a=="hidden"){
                    				face = new Face(1);
                    				suit = new Suit("hidden");
	                    			card = new Card(face, suit, 1);
                    			}else{
	                    			cs = a.split(",");
	                    			if(cs.length!=3){
	                    				
	                    			}else{
		                    			face = new Face(cs[0]);
		                    			suit = new Suit(cs[1]);
		                    			card = new Card(face, suit, Integer.parseInt(cs[2]));
		                        		player_cards.add(card);
	                    			}
                    			}
                    		}
                    		gamePanel.table.player_card.put(player, player_cards);
                    		gamePanel.updateValues();
                    		break;
                    }
                    /*if(str.equals("cleanscreen")){
                    	//taContent.setText("");
                    }else{
                    	//taContent.append(str);
                    }
                    //taContent.setCaretPosition(taContent.getText().length());*/
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
 
        }
 
    }
	public AppWindow()
    {
        super("Blackjack");
        
        addComponentListener(this);
        
        //String name = JOptionPane.showInputDialog(this, "What's your name?","");
        //port = Integer.parseInt(JOptionPane.showInputDialog(this, "What's your Port?","8888"));
        
        
        /*connect();
        try {
            outputStream = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReadWorker rw = new ReadWorker(server, this);
        rw.execute();*/
        System.out.println("connected!");
        
        
        
        Dimension windowSize = new Dimension(WIDTH, HEIGHT);
        setSize(windowSize);
        setLocationRelativeTo(null); // put game in centre of screen
        
        this.setBackground(defaultTableColour);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu playerMenu = new JMenu("Player");
        JMenuItem updatePlayerDetails = new JMenuItem("Update Player Details");
        playerMenu.add(updatePlayerDetails);
        playerMenu.addSeparator();
        // playerMenu.add(savePlayer);
        // playerMenu.add(openPlayer);
        menuBar.add(playerMenu);
        
        JMenu actionMenu = new JMenu("Actions");
        //JMenuItem dealAction = new JMenuItem("Deal");
        JMenuItem hitAction = new JMenuItem("Hit");
        JMenuItem doubleAction = new JMenuItem("Double");
        JMenuItem standAction = new JMenuItem("Stand");
        //actionMenu.add(dealAction);
        actionMenu.add(hitAction);
        actionMenu.add(doubleAction);
        actionMenu.add(standAction);
        menuBar.add(actionMenu);
        
        JMenu betMenu = new JMenu("Bet");
        JMenuItem oneChip = new JMenuItem("$1");
        JMenuItem fiveChip = new JMenuItem("$5");
        JMenuItem tenChip = new JMenuItem("$10");
        JMenuItem twentyFiveChip = new JMenuItem("$25");
        JMenuItem hundredChip = new JMenuItem("$100");
        betMenu.add(oneChip);
        betMenu.add(fiveChip);
        betMenu.add(tenChip);
        betMenu.add(twentyFiveChip);
        betMenu.add(hundredChip);
        menuBar.add(betMenu);
        
        JMenu windowMenu = new JMenu("Window");
        JMenuItem windowTableColourMenu = new JMenuItem("Change Table Colour");
        windowMenu.add(windowTableColourMenu);
        menuBar.add(windowMenu);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpBlackjackRulesMenu = new JMenuItem("Blackjack Rules");
        JMenuItem helpAboutMenu = new JMenuItem("About Blackjack");
        helpMenu.add(helpBlackjackRulesMenu);
        helpMenu.addSeparator();
        helpMenu.add(helpAboutMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        
        // keyboard shortcuts
        
        updatePlayerDetails.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_U,                                            
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        /*
         * savePlayer.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openPlayer.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));   
        */
        //dealAction.setAccelerator(
        //    KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_N,
        //        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        hitAction.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_C,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        doubleAction.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_D,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        standAction.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        oneChip.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_1,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fiveChip.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_2,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        tenChip.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_3,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        twentyFiveChip.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_4,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        hundredChip.setAccelerator(
            KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_5,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        // set key
        
		// action listeners
		//dealAction.addActionListener(this);
        hitAction.addActionListener(this);
        doubleAction.addActionListener(this);
        standAction.addActionListener(this);
		updatePlayerDetails.addActionListener(this);
		//savePlayer.addActionListener(this);
		//openPlayer.addActionListener(this);
		windowTableColourMenu.addActionListener(this);
		helpAboutMenu.addActionListener(this);
		oneChip.addActionListener(this);
        fiveChip.addActionListener(this);
        tenChip.addActionListener(this);
        twentyFiveChip.addActionListener(this);
        hundredChip.addActionListener(this);
        		
        gamePanel = new GamePanel("Game");

        connect();
        gamePanel.setBackground(defaultTableColour);
		add(gamePanel);
        //add main game panel
        
        setVisible(true);
    }

	public void actionPerformed(ActionEvent evt)
    {
        String act = evt.getActionCommand();
        
        if (act.equals("$1"))
        {
            gamePanel.increaseBet(1);
        }
        else if (act.equals("$5"))
        {
            gamePanel.increaseBet(5);
        }
        else if (act.equals("$10"))
        {
            gamePanel.increaseBet(10);
        }
        else if (act.equals("$25"))
        {
            gamePanel.increaseBet(25);
        }
        else if (act.equals("$100"))
        {
            gamePanel.increaseBet(100);
        }
        else if (act.equals("Deal"))
        {
            gamePanel.newGame();
        }
        else if (act.equals("Hit"))
        {
            gamePanel.hit();
        }
        else if (act.equals("Double"))
        {
            gamePanel.playDouble();
        }
        else if (act.equals("Stand"))
        {
            gamePanel.stand();
        }
        else if (act.equals("Update Player Details"))
        {
            gamePanel.updatePlayer();
        }
        /*else if (act.equals("Save Current Player"))
        {
            gamePanel.savePlayer();
        }
        else if (act.equals("Open Existing Player"))
        {
            gamePanel.openPlayer();
        }*/
		else if (act.equals("Change Table Colour"))
		{
		    Color tableColour = JColorChooser.showDialog(this, "Select Table Colour", defaultTableColour);
		    // show color choose.
		    this.setBackground(tableColour);//set background color
		    gamePanel.setBackground(tableColour);
		    gamePanel.repaint();
		    this.repaint();
		}
		else if (act.equals("About Blackjack"))
		{
		    String aboutText = "<html><p align=\"center\" style=\"padding-bottom: 10px;\">Version 1.2</p></html>";
		    JOptionPane.showMessageDialog(this, aboutText, "About Blackjack", JOptionPane.PLAIN_MESSAGE);
		}
		
		gamePanel.updateValues();
	}
    
    // top menu listener ends.
	
	public void componentResized(ComponentEvent e)
	{
	    int currentWidth = getWidth();
	    int currentHeight = getHeight();
	    
	    boolean resize = false;
	    
	    if (currentWidth < WIDTH)
	    {
	        resize = true;
	        currentWidth = WIDTH;
	    }
	    
	    if (currentHeight < HEIGHT)
	    {
	        resize = true;
	        currentHeight = HEIGHT;
	    }
	    
	    if (resize)
	    {
	        setSize(currentWidth, currentHeight);
	    }
	}
	
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
}