//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;
 

public class ChatApplication implements Runnable
{
static	JTextField hostname,hostport,user,message,file;
static  JPasswordField pass;
static	JLabel l_hname,l_hport,l_user,l_pass,l_message,l_file;
static	JButton cg,logout,connect,send_message,b_file,ok,online_offline;
static	JTextArea chat;
static  DefaultCaret caret;
static	JList friends;
static	JSeparator sep1,sep2;
static  JScrollPane scroll,scrolllist; 
static  DefaultListModel model;
static  ListSelectionModel lsm;

static HashMap<String,StringBuilder> msg;
static HashMap<String,List<String>> group;
static Socket client;
static ObjectInputStream is;
static ObjectOutputStream os;
static String line="";
static boolean closed=false;
static Thread thread;
public static void main(String args[])
{
	JFrame frame = new JFrame();	
	frame.setSize(750,550);
	frame.setVisible(true);
	frame.setTitle("Mi Chat");
	frame.setLayout(null);
	l_hname   = new JLabel("Host Address");
	l_hport   = new JLabel("Host Port");
	l_user    = new JLabel("Username");
	l_pass    = new JLabel("Password");
	l_message = new JLabel("Message :");
	l_file    = new JLabel("File :");
	connect   = new JButton("Connect");
	logout    = new JButton("Logout");
	cg    	  = new JButton("create group");
	send_message = new JButton("Send Message");
	b_file    = new JButton("Send");
	ok    	  = new JButton("OK");
 	online_offline = new JButton("Off");
	hostname  = new JTextField();
	hostport  = new JTextField();
	user      = new JTextField();
	pass      = new JPasswordField();
	message   = new JTextField();
	file      = new JTextField();
	sep1 	  = new JSeparator();  
	chat 	  = new JTextArea();
	model     = new DefaultListModel();
	friends   = new JList();
	sep2      = new JSeparator();
	scroll    = new JScrollPane(chat);
	scrolllist= new JScrollPane(friends);
	caret     = (DefaultCaret)chat.getCaret();
	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	lsm = friends.getSelectionModel();
	friends.setModel(model);
	model.addElement("All");
	friends.setSelectedIndex(0);
	
	l_hname.setBounds(10,10,100,20);
	hostname.setBounds(110,10,100,20);
	connect.setBounds(450,10,190,20);
	l_hport.setBounds(220,10,100,20);
	hostport.setBounds(320,10,100,20);
	l_user.setBounds(10,40,100,20);
	user.setBounds(110,40,100,20);
	l_pass.setBounds(220,40,100,20);
	pass.setBounds(320,40,100,20);
	cg.setBounds(450,40,90,20);
	logout.setBounds(550,40,90,20);
	sep1.setBounds(10,80,600,20);
	scroll.setBounds(10,100,420,300);
	scrolllist.setBounds(450,100,190,300);
	sep2.setBounds(10,420,600,20);
	online_offline.setBounds(450,440,190,20);
	l_message.setBounds(10,480,100,20);
	message.setBounds(120,480,300,20);
	send_message.setBounds(450,480,190,20);
	l_file.setBounds(10,520,100,20);
	file.setBounds(120,520,300,20);
	ok.setBounds(450,520,190,20);
	ok.setEnabled(false);
	chat.setEditable(false);
	connect.setEnabled(true);
	logout.setEnabled(false);
	send_message.setEnabled(false);
	cg.setEnabled(false);
	//socket
	connect.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent ae){  
		try
		{
			if(!(hostname.getText()).equals("") && !(hostport.getText().equals("")) && !(user.getText()).equals(""))
			{
				//System.out.println("working");
				client = new Socket(hostname.getText(),Integer.parseInt(hostport.getText()));
				os = new ObjectOutputStream(client.getOutputStream());
				is = new ObjectInputStream(client.getInputStream());
				msg= new HashMap<>();
				group = new HashMap<String,List<String>>();
				msg.put("All",new StringBuilder(""));
				connect.setEnabled(false);
				logout.setEnabled(true);
				send_message.setEnabled(true);
				cg.setEnabled(true);
				frame.setTitle("Logged In");
			}
		
		if(client!=null && is!=null && os!=null)
		{
			(thread = new Thread(new ChatApplication())).start();
			Packet packet = new Packet(user.getText(),new String[]{""},"","login","");
			os.reset();	  
			os.writeObject(packet);
		}  
		}
		catch(SocketException e)
		{
			chat.append("Server is Currently down! please try again later \n");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	  }  
    });
	
	cg.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent ae1){  
				logout.setEnabled(false);
				send_message.setEnabled(false);
				ok.setEnabled(true);
				friends.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				l_message.setText("Name");
			}  
    });
	ok.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent ae1){  
		String gname = "group "+message.getText();
		List<String> list = friends.getSelectedValuesList();
		if(list.size()>2){
		String recipientGroup[] = new String[list.size()+1];
		Iterator<String> iterator = list.iterator();
		int i=0;
		while(iterator.hasNext())
		{
				String temp = iterator.next();
				recipientGroup[i]=temp;
				System.out.println(recipientGroup[i]);
				i++;		
		}
		recipientGroup[list.size()]=user.getText();
		try{
			Packet packet = new Packet(user.getText(),recipientGroup,"","group",gname);
			os.reset();
			os.writeObject(packet);
		}
		catch(Exception e){
			System.out.println(e);
		}
		model.addElement(gname);
		msg.put(gname,new StringBuilder(""));
		group.put(gname,list);
		logout.setEnabled(true);
		send_message.setEnabled(true);
		ok.setEnabled(false);
		friends.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l_message.setText("Message");
		}  
		else{
   			JOptionPane.showMessageDialog(null,"Please select more than 2 recipient!");
		}
	}
    });
	send_message.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent ae2){  
              try
              {
              	if(friends.getSelectedValue()!=null)
              	{
              		String str = "Me: "+message.getText()+"\n";
              		if(!group.containsKey(friends.getSelectedValue().toString()))
              		{
              			String recipient = friends.getSelectedValue().toString();
              			Packet packet = new Packet(user.getText(),new String[]{recipient},message.getText(),"message","");
              			chat.append(str);
						if(!msg.containsKey(recipient))
						{
							msg.put(recipient,new StringBuilder(str));
						}
						else
						{
							boolean bool = msg.replace(recipient,msg.get(recipient),msg.get(recipient).append(str));
							if(!bool)
								throw new Exception();
						}
					os.reset();
              		os.writeObject(packet);
              		}
              		else
              		{
              			String value = friends.getSelectedValue().toString();
              			String recipient[] = (group.get(value).toArray(new String[0]));
              			Packet packet = new Packet(user.getText(),recipient,message.getText(),"message",value);
              			chat.append(str);
						if(!msg.containsKey(value))
						{
							msg.put(value,new StringBuilder(str));
						}
						else
						{
							boolean bool = msg.replace(value,msg.get(value),msg.get(value).append(str));
							if(!bool)
								throw new Exception();
						}
						os.reset();
              			os.writeObject(packet);
              		}
              		message.setText("");
              	}
              	else
              		JOptionPane.showMessageDialog(null,"Please select the recipient!");
              }
              catch(Exception e1)
              {
              	System.out.println(e1);
              }
    }  
    });
    logout.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent ae3){  
             try{
			 Packet packet = new Packet(user.getText(),new String[]{""},"logout","logout","");
			 os.reset();
             os.writeObject(packet);
             os.close();
             is.close(); 
			 connect.setEnabled(true);
			 logout.setEnabled(false);
			 send_message.setEnabled(false);
			 thread.stop();	
    		 }
    		 catch(Exception e2)
    		 {
    		 	System.out.println(e2);
    		 }
    		 frame.setTitle("Logged  out");
    }  
    });
    lsm.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg) {
             	StringBuilder val = msg.get(friends.getSelectedValue().toString());
             	chat.setText(val.toString());
            }
        });
	online_offline.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent ae3){  
			
			if(online_offline.getText().equals("Off"))
			{
				Packet packet = new Packet(user.getText(),new String[]{""},"idle","logout","");
				try{
					os.reset();
                    os.writeObject(packet);
				}
				catch(Exception e)
				{
						System.out.println(e);
				}	
				online_offline.setText("On");
			}
			else if(online_offline.getText().equals("On"))
			{
				Packet packet = new Packet(user.getText(),new String[]{""},"","login","");
				try{
					os.reset();
                    os.writeObject(packet);
				}
				catch(Exception e)
				{
						System.out.println(e);
				}
				online_offline.setText("Off");
			}
    }  
    });	
    
	frame.add(l_hname);
	frame.add(l_hport);
	frame.add(l_user);
	frame.add(l_pass);
	frame.add(l_file);
	frame.add(connect);
	frame.add(hostname);
	frame.add(hostport);
	frame.add(user);
	frame.add(pass);
	frame.add(sep1);
	frame.add(cg);
	frame.add(logout);
	frame.add(scroll);
	frame.add(scrolllist);
	frame.add(l_message);
	frame.add(message);
	frame.add(send_message);	
	frame.add(file);
	frame.add(ok);
	frame.add(online_offline);
}
public void run()
{
	try
	{
		Packet packet;	
		while((packet=(Packet)is.readObject())!=null)
		{
			String type = packet.getType();
			
			if(type.equals("online"))
			{
				model.addElement(packet.getSender());
				msg.put(packet.getSender(),new StringBuilder(""));
			}
			if(type.equals("offline"))
			{
				model.removeElement(packet.getSender());
				//msg.put(packet.getSender(),new StringBuilder(""));
			}
			if(type.equals("group"))
			{
					String recieved[] = packet.getRecipient();
					ArrayList<String> list = new ArrayList<>();
					for(int i=0;i<recieved.length;i++)
						if(!recieved[i].equals(user.getText()))
							 list.add(recieved[i]);
					group.put(packet.getGname(),list);
					msg.put(packet.getGname(),new StringBuilder(""));
					model.addElement(packet.getGname());		 
					
			}
			if(type.equals("message"))
			{
				String str = "From "+packet.getSender()+": "+packet.getMessage()+"\n";
				String val = friends.getSelectedValue().toString();
				int recipientCount= packet.getRecipient().length;
				if(recipientCount==1)
				{
					if(!msg.containsKey(packet.getSender()))
					{
						msg.put(packet.getSender(),new StringBuilder(str));
					}
					else
					{
						StringBuilder data = msg.get(packet.getSender());
						boolean bool = msg.replace(packet.getSender(),data,data.append(str));
						if(!bool)
							throw new Exception();
					}
					if(packet.getSender().equals("Server"))
						chat.append(str);
					else if(val.equals(packet.getSender()))
						chat.append(str);

				}
				else if(recipientCount>1)
				{
					String gname = packet.getGname();
					if(!msg.containsKey(gname))
					{
						msg.put(gname,new StringBuilder(str));
					}
					else
					{
						StringBuilder data = msg.get(gname);
						boolean bool = msg.replace(gname,data,data.append(str));
						if(!bool)
							throw new Exception();
					}
					if(packet.getSender().equals("Server"))
						chat.append(str);
					else if(val.equals(packet.getGname()))
						chat.append(str);
				}
			}
		}	
	closed=true;
	}
	catch(Exception e)
	{
		System.out.println(e);
	}

}
}

