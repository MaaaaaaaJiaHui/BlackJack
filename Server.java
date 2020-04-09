
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
 
/*
 */
public class Server {
 
    List<ClientThread> clients = new ArrayList<ClientThread>();
    //private int[] pai = new int[52];
    //private String[] huase = {"black", "red", "a", "b"};
    //private String[] paiM = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static Deck decks;
    private static int deck_no = 0;
    private int currentPage = 0;
    private int playerNum = 1;
    private String totoalStr = "";
    private int totalmoney = 0;
    private int room_no = 2;
    private int stand = 0;
    //private int player1 = 0,player2 = 0,player3 = 0,player4 = 0;
    
    public static void main(String[] args) {
        new Server().start();
    }

	public static Map<String, PlayerCardHand> player_card = new HashMap<String, PlayerCardHand>();
    public List<String> players=new ArrayList<>();
	public static Map<String, HashMap<String, Player>> room=new HashMap<String,HashMap<String,Player>>(); 
	public static Map<String, String> roomnum = new HashMap<String, String>();
	public static Map<String, String> bets = new HashMap<String, String>();
    public boolean iConnect = false;
    public static ServerSocket ss;
    /**
     * running server
     */
    public void start() {
        try {
        	int port = 6666;
        	System.out.println("Input port...");
        	Scanner sc = new Scanner(System.in);
        	port = sc.nextInt();
        	ss = new ServerSocket(port);
            iConnect = true;
            totoalStr = packMsg(totoalStr, "system:notice#Server is online....");
            sendClientMsg(totoalStr);
            room_no = 0;
            while(room_no < 2){
            	System.out.println("Hello, how many players?");
                Scanner scc = new Scanner(System.in);
                room_no = scc.nextInt();
            }
            while(true){
	            connectPlayer();
	            playGame();
            }
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }
    public void connectPlayer() throws IOException{
    	if (clients.size() == room_no) {
            totoalStr = packMsg(totoalStr, "system:notice#The Players is reached limit. No more players.");
            sendClientMsg(totoalStr);
            return;//break;
        }
    	//players.clear();
    	
    	while (iConnect) {
    		System.out.println(clients.size()+";"+room_no);
            Socket s = ss.accept();
            playerNum = clients.size()+1;
            totoalStr = packMsg(totoalStr, "system:newplayer#" + playerNum); // new player join in.
            sendClientMsg(totoalStr);

        	ClientThread currentClient = new ClientThread(s, ""+playerNum);// Create thread...
        	totoalStr = packMsg("", "system:bindplayer#" + playerNum);
        	currentClient.sendMsg(totoalStr);	// send bind msg
        	
            clients.add(currentClient);//add current player into list
            player_card.put(playerNum+"", new PlayerCardHand());
        	//currentClient.s.close();
            //players.add("1");
            totoalStr = packMsg(totoalStr, "system:notice#Player " + playerNum + " joined...");
            sendClientMsg(totoalStr);
            
            broadcastPlayer();
            // message broadcast
            
            System.out.println(clients.size()+";"+room_no);
            if (clients.size() == room_no) {
                totoalStr = packMsg(totoalStr, "system:notice#The Players is reached limit. No more players.");
                currentClient.sendMsg(totoalStr);
                break;
            }
            /*
        	if(playerNum == 1){
        		currentClient.sendMsg("How many players?\r\n");
            	room_no = Integer.parseInt(currentClient.recMsg());
                if(room_no < 2){
                	currentClient.sendMsg(packMsg("","illegal input\r\n"));
                	room_no = 2;
                }else{
                	currentClient.sendMsg(packMsg("","enter game!\r\n"));
                }
        	}*/
            ++playerNum;
        }
		//System.out.println(clients.size()+";"+room_no);
    }
    public void broadcastPlayer(){
    	String msg = "system:allplayer#";
    	for (int i = 0; i < clients.size(); i++) {
    		msg += clients.get(i).player.getName();
    		if(i != clients.size()-1){
    			msg += ",";
    		}
    	}
    	System.out.println(msg);
    	sendClientMsg(msg);
    }
    public int getAll(){
    	//room_no
    	int j = 0;
    	for(int i=0;i<room_no;i++){
    		if(players.get(i).equals("1")) j++;
    	}
    	return j;
    }
    public boolean getAvailable(){
    	int j = 0;
    	for(int i=0;i<room_no;i++){
    		if(players.get(i).equals("1")) j++;
    	}
    	if(j==1) return true;
    	return false;
    }
    public void playGame(){
    	totoalStr = packMsg(totoalStr, "system:notice#Game is started,waiting for cards....");
        sendClientMsg(totoalStr);

        xipai();// shuffle cards

        totoalStr = packMsg(totoalStr, "system:notice#Cards assign....");
        sendClientMsg(totoalStr);
        //for (int i = 0; i < clients.size(); i++) {
        //}
        int round = 1,end=0;
        // send cards & ask for bet
        while(end==0){
        	stand = 0;
        	//first round, bet first
            String originalStr = new String(totoalStr);
        	if(round==1){

            	for(int j=0;j<clients.size();j++){
            		players.add("1");
            	}
            	
	            for (int i = 0; i < clients.size(); i++) {
	            	ClientThread c = clients.get(i);
                    originalStr = packMsg(originalStr, "system:notice#Players bet....");
                    c.sendMsg(originalStr);
	            	xiazhu(c.getPlayer(),c);
	            }
	            for (int i = 0; i < clients.size(); i++) {
	            	ClientThread c = clients.get(i);
	            	fapai(c.getPlayer(),c);
	            }
        	}
        	

            for (int i = 0; i < clients.size(); i++) {
            	getMsg(i);
            }
            for (int i = 0; i < clients.size(); i++) {
            	if(!players.get(i).equals("1")) continue;
                // fapai
                ClientThread c = clients.get(i);
                //String fapai_res = fapai(c.getPlayer());
                //originalStr = packMsg(originalStr, fapai_res);
                //c.sendMsg(originalStr);

                //String originalStr = new String(totoalStr);
                //ClientThread c = clients.get(i);
                /*if(round==1){
	                //String fapai_res = 
	                //originalStr = packMsg(originalStr, fapai_res);
	                //c.sendMsg(originalStr);
                }else{
                	//originalStr = packMsg(originalStr, getMsg(i));
                    //c.sendMsg(originalStr);
                }*/
                
                // Your turn
                originalStr = packMsg("","system:notice#It's your turn.");
                c.sendMsg(originalStr);
                if(player_card.get(c.getPlayer().getName()).getTotal()<15){
                    originalStr = packMsg("","system:showchoosemusthit");
                }else{
                    originalStr = packMsg("","system:showchoose"); 
                }
                c.sendMsg(originalStr);
                
                //
                String answer = c.recMsg().toLowerCase();
                if ("hit".equals(answer)) {
                    originalStr = packMsg(originalStr, "system:notice#You want one more card.");
                    c.sendMsg(originalStr);
                    String cards = "";
                    //while (true) {
                    Card cc = decks.pop();
                	player_card.get(c.getPlayer().getName()).add(cc);
                	cards += "system:notice#You get point" + cc + ", now your total point is "+player_card.get(c.getPlayer().getName()).getTotal()+"";
                    originalStr += cards;
                    c.sendMsg(originalStr);
                	//c.getPlayer().getName()
                	if(player_card.get(c.getPlayer().getName()).isBust()){
                		originalStr = packMsg(originalStr, "system:notice#You are bursted!");
                        c.sendMsg(originalStr);
                        c.getPlayer().setCurrentPoint(0);
                        //break;
                	}	
                    	/*
                        String cards = "";
                        for (Integer integer : c.getPlayer().getAl()) {
                            cards += xianshi(integer);
                        }
                        cards += " You get point:" + c.getPlayer().getCurrentPoint() + "\r\n";
                        originalStr += cards;
                        c.sendMsg(originalStr);
                        if (c.getPlayer().getCurrentPoint() >= 21) {
                            originalStr = packMsg(originalStr, "You are bursted!\r\n");
                            c.sendMsg(originalStr);
                            c.getPlayer().setCurrentPoint(0);
                            break;
                        }
                        originalStr = packMsg(originalStr, "Y or N ?\r\n");
                        c.sendMsg(originalStr);
                        String yesNo = c.recMsg();
                        if (yesNo.equalsIgnoreCase("N")) {
                            originalStr = packMsg(originalStr, "nO MORE CARD\r\n");
                            c.sendMsg(originalStr);
                            break;
                        } else {
                            c.getPlayer().getAl().add(pai[currentPage]);
                            c.getPlayer().setCurrentPoint(c.getPlayer().getCurrentPoint() + calPoint(pai[currentPage]));
                            currentPage++;
                        }*/
                    //} // while
                    
                    //c.sendMsg(originalStr);
 
 
                } else if ("double".equals(answer)) {
                    originalStr = packMsg(originalStr, "system:notice#You choose double bet!");
                    c.sendMsg(originalStr);
                    double_bet(c.getPlayer(),c);
                } else if ("stand".equals(answer)) {
                    originalStr = packMsg(originalStr, "system:notice#You choose stand.");
                    c.sendMsg(originalStr);
                    stand++;
                } else {
                    originalStr = packMsg(originalStr, "system:notice#Wrong choose!"+answer);
                    c.sendMsg(originalStr);
                }
                getMsg(i);
                // calculate points
	            String calRes = "";
	            System.out.println("calculate points....");
	            // int[] money = new int[room_no];   
	            int blackjack = -1,winner = -1,max=0;
	            int token = 0,doub=1;
	            for(int j=0;j<room_no;j++){
	            	if(players.get(j).equals("0")) continue;
	            	if(player_card.get(clients.get(j).getPlayer().getName()).isBust()){
	            		players.set(j, "0");
	            		token = 0;
	            		System.out.println("burst....");
	            		continue;
	            	}
	            	//calRes += result(clients.get(i).getPlayer()));
	            	//p2.setBalance(p2.getBalance() + totalmoney / 2);
	            	token = 0;
	            	if(player_card.get(clients.get(j).getPlayer().getName()).hasBlackjack()){
	                    if(round==1) continue; // round one do not calculate
	            		blackjack = j;
	            		token = 1;
	            		System.out.println("blackjack");break;
	            		//doub = 2;
	            	}else{
	            		if(player_card.get(clients.get(j).getPlayer().getName()).getTotal() == 21){
	            			winner = j;
	            			max = 21;
	            			token = 1;
	            			//blackjack= j;
		            		doub = 2;
	                		System.out.println("21");break;
	            		}else{
	            			if(max<=player_card.get(clients.get(j).getPlayer().getName()).getTotal()){
	            				if(max==player_card.get(clients.get(j).getPlayer().getName()).getTotal()){
	            					winner = -1;
	            				}else{
	            					winner = j;
	            				}
	            				max = player_card.get(clients.get(j).getPlayer().getName()).getTotal();
	            				
	                			token = token==1?1:0;
	            			}
	            		}
	            	}
	            	
	            }
	            /*for(int q=0;q<bets.size();q++){
	            	System.out.println("this is "+q+":"+bets.get(q+1+""));
	            }*/
	            if(getAvailable()){
	            	token = 1;
	            	/*winner = -1;
	            	for(int iw=0;i<players.size();i++){
	            		if(players.get(iw).equals("1")){
	            			winner = iw;
	            		}
	            	}*/
	            }else{
		            token = stand==getAll()?1:token;
	            }
	            if(token==1 || getAvailable()){// && token==1
	            	/*int[] score = new int[room_no];
	            	for(int j =0 ;j< room_no; j++){
	            		score[j] = clients.get(j).getPlayer().getBalance();
	                	//calRes += clients.get(j).getPlayer().getName() + " money left: " + String.valueOf(clients.get(j).getPlayer().getBalance()) + "\r\n";
	                }*/
	            	end = 1;
	            	if(blackjack>-1){
	            		//money[i] = 
	            		//score[blackjack] = score[blackjack]+totalmoney;
	            		//clients.get(blackjack).getPlayer().setBalance(clients.get(blackjack).getPlayer().getBalance() + totalmoney*doub);
	            		winner = blackjack;
	            		int mines;
            			for(int ij=0; ij<players.size();ij++){
            					//mines = Integer.parseInt(bets.get(winner+1+""))*2 - Integer.parseInt(bets.get(ij+1+""));
            					//mines = mines >= 0? mines :0;
        						mines = Integer.parseInt(bets.get(winner+1+""));
                				if(ij != winner){System.out.println("Now is player:"+ij+", mines is:"+mines+", now it has:"+(clients.get(ij).getPlayer().getBalance() - mines));
            					totalmoney = totalmoney+mines;
            					clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() - mines);//tm += Integer.parseInt(bets.get(ij+""));//clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() + (totalmoney/2)*doub);
            				}
            			}
            			clients.get(winner).getPlayer().setBalance(clients.get(winner).getPlayer().getBalance() + totalmoney);
	                }else{
	                	if(max == 21){//just 21, get all bets
	                		//score[blackjack] = score[blackjack]+totalmoney;
	                		//clients.get(blackjack).getPlayer().setBalance(clients.get(blackjack).getPlayer().getBalance() + totalmoney);
	                		/*int mines;
	            			for(int ij=0; ij<players.size();ij++){
	            					//mines = (int) (Integer.parseInt(bets.get(winner+1+""))*1.5) - Integer.parseInt(bets.get(ij+1+""));
	            					//mines = mines >= 0? mines :0;\
	            					mines = (int) (Integer.parseInt(bets.get(winner+1+""))*0.5);
	                				if(ij != winner){System.out.println("just 21, "+Integer.parseInt(bets.get(winner+1+""))*1.5+",Now is player:"+ij+", mines is:"+mines+", now it has:"+(clients.get(ij).getPlayer().getBalance() - mines));
	            					totalmoney = totalmoney+mines;
	            					clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() - mines);//tm += Integer.parseInt(bets.get(ij+""));//clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() + (totalmoney/2)*doub);
	            				}
	            			}
	            			clients.get(winner).getPlayer().setBalance(clients.get(winner).getPlayer().getBalance() + totalmoney);*/
	                		int mines;
	            			for(int ij=0; ij<players.size();ij++){
	            					//mines = Integer.parseInt(bets.get(winner+1+""))*2 - Integer.parseInt(bets.get(ij+1+""));
	            					//mines = mines >= 0? mines :0;
	        						mines = Integer.parseInt(bets.get(winner+1+""));
	                				if(ij != winner){System.out.println("Now is player:"+ij+", mines is:"+mines+", now it has:"+(clients.get(ij).getPlayer().getBalance() - mines));
	            					totalmoney = totalmoney+mines;
	            					clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() - mines);//tm += Integer.parseInt(bets.get(ij+""));//clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() + (totalmoney/2)*doub);
	            				}
	            			}
	            			clients.get(winner).getPlayer().setBalance(clients.get(winner).getPlayer().getBalance() + totalmoney);
	                	}else{// not 21
	                		if(winner == -1){// drawn, every get their bet back; if someone burst, others can get its bet.
                				int tm = 0;
                				for(int ij=0; ij<players.size();ij++){
	                				if(players.get(ij).equals("0")){
	                					tm += Integer.parseInt(bets.get(ij+1+""));//clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() + (totalmoney/2)*doub);
	                				}
	                			}
                				int nums = getAll(),avg = (int) Math.ceil(tm/nums);
                				for(int ij=0; ij<players.size();ij++){
	                				if(players.get(ij).equals("1")){
	                					clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() +avg+ Integer.parseInt(bets.get(ij+1+"")));//tm += Integer.parseInt(bets.get(ij+""));//clients.get(ij).getPlayer().setBalance(clients.get(ij).getPlayer().getBalance() + (totalmoney/2)*doub);
	                				}
	                			}
                			}else{// winner get all bets
                				clients.get(winner).getPlayer().setBalance(clients.get(winner).getPlayer().getBalance() + totalmoney);//(totalmoney/2)*doub
                			}
	                		/*if(blackjack == -1){ // not blackjack
	                			//score[winner] = score[winner]+totalmoney/2;
	                			
	                			
	                		}else{//in case error.
	                			//score[winner] = score[winner]+totalmoney;
	                			clients.get(winner).getPlayer().setBalance(clients.get(winner).getPlayer().getBalance() + totalmoney);
	                			
	                		}*/
	                	}
	                }
	                for(int j =0 ;j< room_no; j++){
	                	System.out.println("system:money#"+clients.get(j).getPlayer().getBalance());
	                	clients.get(j).sendMsg("system:money#"+clients.get(j).getPlayer().getBalance());
	                	//calRes += clients.get(j).getPlayer().getName() + " money left: " + String.valueOf(clients.get(j).getPlayer().getBalance()) + "\r\n";
	                }
	                if(winner==-1){
	                	calRes += "Drawn";
	                }else{

	                    calRes += "Last winner is "+clients.get(winner).getPlayer().getName();
	                }
	                //calRes += result(clients.get(0).getPlayer(), clients.get(1).getPlayer(), clients.get(2).getPlayer());
	                System.out.println("system:result#"+calRes);
	                sendClientMsg("system:result#"+calRes);
	                getMsg(i);
	                ///clients.get(0).sendMsg("Hi, how many players?\r\n");
	                System.out.println("Hi, how many players?");
	                Scanner sc = new Scanner(System.in);
	                int new_room = 0;
	                while(!(new_room>=2)){
	                	new_room = sc.nextInt();//Integer.parseInt(clients.get(0).recMsg());
	                	//break;
	                }
	                
	            	cleanTable(new_room);
	                room_no = new_room;
	            	sendClientMsg("system:notice#cleanscreen");
	            	sendClientMsg("system:notice#Game Started...");
	                return ;//break;
	            }
            }
            round++;
        }
    }
    public void cleanTable(int num){
    	//if(num>=room_no) return;// no need to push players out of table
    	bets.clear();
    	players.clear();
    	totalmoney = 0;
    	if(num<room_no){
    		System.out.println(num+"q:q"+room_no);
    		//int ii=0;
	    	for(int i = 0; i<room_no-num; i++){
	    		System.out.println(i+"was removed");
	    		//clients.get(i).sendMsg("cleanscreen");
	    		clients.get(room_no-1-i).sendMsg("system:notice#Hi, you are kicked out of room:)");
	    		try {
					clients.get(room_no-1-i).s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		clients.remove(room_no-1-i);// remove the player
	    		//System.out.println("name:"+clients.get(i).player.name);
	    		//playerNum;
	    	}
    	}
		player_card.clear();
		if(num<room_no){// less than now players.

	    	for(int i = 0;i<num;i++){
	    		
	    		clients.get(i).player = new Player();//setPlayer();
	    		clients.get(i).player.setName((i+1)+"");
	    		//player_card.put(i+"", new PlayerCardHand());
	        	//ClientThread currentClient = new ClientThread(s, ""+playerNum);//create more thread.
	            //clients.add(currentClient);//append list
	            player_card.put((i+1)+"", new PlayerCardHand());
	    	}
		}else{
			// more than now players
	    	for(int i = 0;i<room_no;i++){
	    		
	    		clients.get(i).player = new Player();//setPlayer();
	    		clients.get(i).player.setName((i+1)+"");
	    		//player_card.put(i+"", new PlayerCardHand());
	        	//ClientThread currentClient = new ClientThread(s, ""+playerNum);//
	            //clients.add(currentClient);//
	            player_card.put((i+1)+"", new PlayerCardHand());
	    	}
		}
    	//playerNum++;
    }
    public String getCard(Card c){
    	return c.getFace()+","+c.getSuit()+","+c.getCode();
    }
    public String getMsg(int j){
    	String cas;
    	int mines = 0;
    	String str;
    	for (int i = 0; i < clients.size(); i++) {
    		str = "system:getPlayerCard#"+clients.get(i).getPlayer().getName()+"#";
    		cas = "";
    		String[] ca = new String[player_card.get(clients.get(i).getPlayer().getName()).size()];
    		int is = 0;
    		for(Card c : player_card.get(clients.get(i).getPlayer().getName())){
    			/*if(j != i){
    				if(c == player_card.get(clients.get(i).getPlayer().getName()).get(player_card.get(clients.get(i).getPlayer().getName()).size()-1)){
    					cas += "@hidden";
    					mines = player_card.get(clients.get(i).getPlayer().getName()).get(player_card.get(clients.get(i).getPlayer().getName()).size()-1).getValue();
    				}else{
    					cas += ""+getCard(c);
    				}
    				cas += ""+getCard(c);
    			}else{
    				cas += getCard(c)+"@";
    			}*/
    			ca[is] = getCard(c);
    			is++;
    		}
			cas = ca[0];
			for(int ii = 1; ii < ca.length; ii++) {
				cas = cas + "@" + ca[ii];
			}
    		str += cas+"#"+(player_card.get(clients.get(i).getPlayer().getName()).getTotal() - mines);
			System.out.println("send cards to "+i+"::"+str);
			sendClientMsg(str);
			//clients.get(i).
            //if(i!=j){
            	/*str += "Player"+clients.get(i).getPlayer().getName() + ",\r\n\tnow total is:"+
            			(player_card.get(clients.get(i).getPlayer().getName()).getTotal() - mines)
            			+"\r\n\thas "+clients.get(i).getPlayer().getBalance()
            			+"\r\n\t"+cas+"\r\n";
            			*/
    			
            //}else{
            //	str += "";
            //}
    	}
    	return "";
    }
    
    /**
     * Client progress
     */
    class ClientThread implements Runnable {
 
        private Socket s;
        private DataInputStream dis;
        private DataOutputStream dos;
        private String str;
        private boolean iConnect = false;
        private Player player;
 
        ClientThread(Socket s, String name) {
            this.s = s;
            iConnect = true;
            this.player = new Player();
            this.player.setName(name);
        }
 
        public String recMsg() {
            try {
                dis = new DataInputStream(s.getInputStream());
                str = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
 
        public void run() {
//            System.out.println("run!");
//            try {
//
//                while (iConnect) {
//                    dis = new DataInputStream(s.getInputStream());
//                    str = dis.readUTF();
//                    System.out.println(str);
//                    for (int i = 0; i < clients.size(); i++) {
//                        System.out.println("trans msg..." + i);
//                        ClientThread c = clients.get(i);
//                        c.sendMsg(str);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
 
        }
 
        /**
         * trans msg to each client
         */
        public void sendMsg(String str) {
            try {
                dos = new DataOutputStream(this.s.getOutputStream());
                dos.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
 
        }
 
 
        public Socket getS() {
            return s;
        }
 
        public void setS(Socket s) {
            this.s = s;
        }
 
        public DataInputStream getDis() {
            return dis;
        }
 
        public void setDis(DataInputStream dis) {
            this.dis = dis;
        }
 
        public DataOutputStream getDos() {
            return dos;
        }
 
        public void setDos(DataOutputStream dos) {
            this.dos = dos;
        }
 
        public String getStr() {
            return str;
        }
 
        public void setStr(String str) {
            this.str = str;
        }
 
        public boolean isiConnect() {
            return iConnect;
        }
 
        public void setiConnect(boolean iConnect) {
            this.iConnect = iConnect;
        }
 
        public Player getPlayer() {
            return player;
        }
 
        public void setPlayer(Player player) {
            this.player = player;
        }
    }
 
    /**
     * send msg one by one
     *
     * @param str
     */
    private void sendClientMsg(String str) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread c = clients.get(i);
            c.sendMsg(str);
        }
    }
 
    private void xipai() {
 /*
        for (int i = 0; i < 52; i++) {
            pai[i] = i;
        }
        for (int j = 0; j < 26; j++) {
            Random r = new Random();
            int i1 = Math.abs(r.nextInt() % 52);
            int i2 = Math.abs(r.nextInt() % 52);
 
            int temp = pai[i2];
            pai[i2] = pai[i1];
            pai[i1] = temp;
        }*/
    	decks = new Deck(1);
    	
    }
 
 
    /**
     * send cards
     */
    private String fapai(Player player, ClientThread c) {
    	
    	Card cc = decks.pop();
    	player_card.get(player.getName()).add(cc);
    	cc = decks.pop();
    	player_card.get(player.getName()).add(cc);
    	
    	if(player_card.get(player.getName()).getTotal()==21){
    		player.setBlack(true);
    		player.setCurrentPoint(21);
    	}else{
    		player.setCurrentPoint(player_card.get(player.getName()).getTotal());
    	}
    	int a;
    	a = player_card.get(player.getName()).size();

		String name = player.getName(); //clients.get(i).getPlayer().getName();
    	for(int i = 0; i< clients.size(); i++){
    		String str = "";
    		int total=0,mines=0;
    		for(int j = 0; j<player_card.get(name).size(); j++){
    			Card cs = player_card.get(name).get(j);
    			if(j == player_card.get(name).size()-1){
    				if(player.getName() == name){
    					str += cs.getFace()+";"+cs.getSuit()+";"+cs.getCode();
    					total = player_card.get(player.getName()).getTotal();
    				}else{
    					str += "hidden";
    					mines = player_card.get(player.getName()).get(player_card.get(player.getName()).size()-1).getValue();
    					total = player_card.get(player.getName()).getTotal() - mines;
    				}
    			}else{
    				str += cs.getFace()+";"+cs.getSuit()+";"+cs.getCode()+"$";
    			}
    			//str += ?"":"";
    		}
    		//clients.get(i).sendMsg("system:deliever#"+name+"#$#"+str+"#$#"+total);
    	}
        // return "庄家发了：" + player_card.get(player.getName()).get(a-1) + " 和 " + player_card.get(player.getName()).get(a-2) + " \r\n" + "当前点数：" + String.valueOf(player_card.get(player.getName()).getTotal()) + "\r\n";
        /*int first = currentPage;
        int second = currentPage + 1;
        player.getAl().add(pai[first]);
        player.getAl().add(pai[second]);
        if ((calPoint(pai[first]) == 1 && calPoint(pai[second]) == 10) || (calPoint(pai[first])) == 10 && calPoint(pai[second]) == 1) {
            player.setBlack(true);
            player.setCurrentPoint(21);
        } else {
            player.setCurrentPoint(player.getCurrentPoint() + calPoint(pai[first]));
            player.setCurrentPoint(player.getCurrentPoint() + calPoint(pai[second]));
        }
        currentPage += 2;
        return "" + xianshi(pai[first]) + " & " + xianshi(pai[second]) + " \r\n" + "current points：" + String.valueOf(player.getCurrentPoint()) + "\r\n";
        */
    	return "";
    }
 /*
    private int calPoint(int i) {
        int temp = 0;
        if (i % 13 + 1 > 10) {
            temp = 10;
        } else {
            temp = (i + 1) % 13;
        }
        return temp;
    }*/
 
    /**
     * show point
     *
     * @param i
     * @return
     */
    /*
    private String xianshi(int i) {
        //return huase[i / 13] + " " + paiM[i % 13];
    	Card c = decks.get(deck_no);
    	return c.getFace()+ " " + c.getSuit();
    }
 */
    /**
     * bet
     *
     * @param player
     */
    private void xiazhu(Player player,ClientThread c) {
    	c.sendMsg("system:bet#Place Your bet.");
    	//String 
    	int bet = Integer.parseInt(c.recMsg());
    	if(player.getBalance()>=bet){
    		player.setBalance(player.getBalance() - bet);
    		bets.put(player.name,bet+"");
    		totalmoney += bet;
    	}
    	c.sendMsg("system:changeMoney#$"+player.getBalance());
    	/*if(player.getBalance()>20){
    		player.setBalance(player.getBalance() - 20);
    		totalmoney += 20;
    	}else{
    		totalmoney += player.getBalance();
    		player.setBalance(0);
    	}*/
    }
    
    /**
     * double bet
     *
     * @param p
     */
    private void double_bet(Player p, ClientThread c) {
        //p.setBalance(p.getBalance() - 40);
    	if(p.getBalance()>=Integer.parseInt(bets.get(p.name))){

            p.setBalance(p.getBalance() - Integer.parseInt(bets.get(p.name)));
            totalmoney = totalmoney + Integer.parseInt(bets.get(p.name));
        	c.sendMsg("system:changeMoney#$"+p.getBalance());
    	}else{
    		c.sendMsg("system:notice#Balence isn't enough!");
    	}
    }
 
    /**
     * package messsage
     * @param originStr
     * @param str
     * @return
     */
    private String packMsg(String originStr, String str) {
        System.out.println(str);
        //originStr += str;originStr
        return str;
    }
 
}