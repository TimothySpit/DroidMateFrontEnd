package webFrontEnd;

import java.util.concurrent.atomic.AtomicLong;

public class FileContainer
{
	private long id;
	private String name;
	private String absoluteFilePath;
	private static final AtomicLong counter = new AtomicLong(1);

	public FileContainer(String name,String absoluteFilePath, long id)
        {
		this.name = name;
                this.absoluteFilePath = absoluteFilePath;
		this.id = id;
	}

	public FileContainer(String name, String absoluteFilePath)
        {
		this.name = name;
                this.absoluteFilePath = absoluteFilePath;
		this.id = counter.incrementAndGet();
	}

	public long getId()
        {return id;}

	public void setId(long id)
        {this.id = id;}

	public String getName()
        { return name;}

	public void setName(String name)
        {this.name = name;}

	public String getAbsoluteFilePath()
        {return absoluteFilePath;}

	public void setAbsoluteFilePath(String absoluteFilePath)
        {this.absoluteFilePath = absoluteFilePath;}

	@Override
	public String toString()
        {
		return "FileContainer{" + "id=" + id + ", name=" + name + 
                        ", absoluteFilePath=" + absoluteFilePath +'}';
	}

}