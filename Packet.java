import java.io.*;

class Packet implements Serializable
{
private static final long serialVersionUID = 5950169519310163575L;
private String sender,message,type,gname;
private String[] recipient;

public Packet(){}

public Packet(String sender,String reci[],String message,String type,String gname)
{
	this.sender=sender;
	this.message=message;
	this.type=type;
	recipient = new String[reci.length];
	for(int i=0;i<reci.length;i++)
	{
		recipient[i] = reci[i];
	}
	this.gname=gname;
}
public void setSender(String sender)
{
	this.sender=sender;
}
public String getSender()
{
	return sender;
}

public void setRecipient(String reci[])
{
	recipient = new String[reci.length];
	for(int i=0;i<reci.length;i++)
	{
		recipient[i] = reci[i];
	}
}
public String[] getRecipient()
{
	return recipient;
}

public void setMessage(String message)
{
	this.message=message;
}
public String getMessage()
{
	return message;
}

public void setType(String type)
{
	this.type=type;
}
public String getType()
{
	return type;
}
public void setGname(String gname)
{
	this.gname=gname;
}
public String getGname()
{
	return gname;
}
}