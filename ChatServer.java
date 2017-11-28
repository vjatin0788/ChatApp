import java.io.*;
import java.net.*;
import java.util.*;



public class ChatServer
{
static ServerSocket server;
static Socket client;
static int max=4,i=0;
static HashMap<String,Client> users;
static ObjectInputStream is;
static ObjectOutputStream os;
static boolean exist=false;
public static void main(String args[])
{
	try
	{
		server =new ServerSocket(5555);
		users = new HashMap<>();
		while(true)
		{
			client = server.accept();
			os = new ObjectOutputStream(client.getOutputStream());
			is = new ObjectInputStream(client.getInputStream());
			System.out.println("working");
			exist=false;
			String name=((Packet)is.readObject()).getSender();
			os.reset();
			os.writeObject(new Packet("Server",new String[]{""},"Connected to server! \nServer:Welcome "+name,"message",""));
			Set<String> existKey=users.keySet();
			Iterator<String> existKeyIterator = existKey.iterator();
			while(existKeyIterator.hasNext())
			{
				String str=existKeyIterator.next();
				if(str.equals(name))
				{
					exist = true;
					System.out.println("exist");
					os.reset();
					os.writeObject(new Packet("Server",new String[]{""},"User already Logged In","message",""));
					users.get(str).os.writeObject(new Packet("Server",new String[]{""},"Some other user tried to log in into your account.","message",""));
				}
			} 
			if(!exist)
			{
				Client cl=(new Client(client,users,name,os,is));
				cl.start();
				users.put(name,cl);
				if(i == max) 
				{
					System.out.println("Server is too busy");
					os.close();
					client.close();
					break; 
				}
				i++;
			}
		}
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
}	
}



class Client extends Thread
{
private Socket client;
private ObjectInputStream is;
public ObjectOutputStream os;
private HashMap<String,Client> users;
private String currentuser;
private int counter;
public Client(Socket client,HashMap<String,Client> users,String currentuser,ObjectOutputStream os,ObjectInputStream is)
{
this.client=client;
this.users=users;
this.currentuser=currentuser;
this.os=os;
this.is=is;
} 
public void run()
{
	try{
		counter=0;
		//updating other users and current user
		Packet user_online = new Packet(currentuser,new String[]{""},"","online","");
		Set<String> onlinekey=users.keySet();
		Iterator<String> onlinekeyIterator = onlinekey.iterator();
		while(onlinekeyIterator.hasNext())
		{
			String str=onlinekeyIterator.next();
			if(users.get(str)!=this)
			{ 
				users.get(str).os.writeObject(user_online);
				os.reset();
				os.writeObject(new Packet(str,new String[]{""},"","online",""));
			}
			counter++;
		}
        //No user is online
		if(counter==1)
		{
			os.reset();
			os.writeObject(new Packet("Server",new String[]{""},"NO User online","message",""));
		}
		Packet name;
		while((name=(Packet)is.readObject())!=null)
		{
			if((name.getType()).equals("logout"))
			{ 
				//notify the offline user
				Set<String> offlinekey=users.keySet();
				Iterator<String> offlinekeyIterator = offlinekey.iterator();
				while(offlinekeyIterator.hasNext())
				{
					String str=offlinekeyIterator.next();
					if(users.get(str)!=this)
					{ 
						os.reset();
						users.get(str).os.writeObject(new Packet(currentuser,new String[]{""},"","offline",""));
					}
				}
				if(name.getMessage().equals("logout"))
					break;
			}
			else if((name.getType()).equals("login"))
			{
				Set<String> online_key=users.keySet();
				Iterator<String> onlinekey_Iterator = online_key.iterator();
				while(onlinekey_Iterator.hasNext())
				{
					String str=onlinekey_Iterator.next();
					if(users.get(str)!=this)
					{ 	
						os.reset();
						users.get(str).os.writeObject(user_online);
					}
				}
			}
			
			else if((name.getType()).equals("message"))
			{
				Set<String> key=users.keySet();
				Iterator<String> keyIterator = key.iterator(); 
				if(name.getRecipient().length==1)
				{
					String recipient[] = name.getRecipient();
					if(recipient[0].equals("All"))
					{
						while(keyIterator.hasNext())
						{
							String str=keyIterator.next();
							if(users.get(str)!=this)
							{
								os.reset();
								users.get(str).os.writeObject(name);
							}
						}
					}
					else
					{
						while(keyIterator.hasNext())
						{
							String str=keyIterator.next();
							if((recipient[0]).equals(str))
							{
								os.reset();
								users.get(str).os.writeObject(name);
							}
						}
					}
				}
				else if(name.getRecipient().length>1)
				{
					int count=0;
					String recipient[] =name.getRecipient(); 
					for(count=0;count<recipient.length;count++)
					{ 
						while(keyIterator.hasNext())
						{
							String str=keyIterator.next();
							if(recipient[count].equals(str))
							{ 
								System.out.println(recipient[count]);
								os.reset();
								users.get(str).os.writeObject(name);
								break;
							}
						}
						keyIterator = key.iterator();
					}
				}
			} 
			else if((name.getType()).equals("group"))
			{
				Set<String> key=users.keySet();
				Iterator<String> keyIterator = key.iterator(); 
				int count=0;
				String recipient[] =name.getRecipient(); 
				for(count=0;count<recipient.length-1;count++)
				{ 
					while(keyIterator.hasNext())
					{
						String str=keyIterator.next();
						if(recipient[count].equals(str) )
						{ 
							os.reset();
							users.get(str).os.writeObject(name);
							break;
						}
					}
				}
			}
		}
		users.remove(currentuser,this);
		os.close();
		is.close();
		client.close();
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
}
}

