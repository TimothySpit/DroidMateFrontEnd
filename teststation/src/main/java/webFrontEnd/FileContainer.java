package webFrontEnd;

import java.util.concurrent.atomic.AtomicLong;

public class FileContainer
{
	private long id;
	private String name;
	private String absoluteFilePath;
	private double progress=0.0;
	private long length=0;
	private static final AtomicLong counter = new AtomicLong();



	public FileContainer(String name, String absoluteFilePath,long length)
        {
		this.length=length;
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
	
	public synchronized double getProgress() {
		return progress;
	}

	public synchronized void setProgress(double progress) {
		this.progress = progress;
	}

	public static void resetCount(){
		counter.set(0);
	}

	@Override
	public String toString()
        {
		return "FileContainer{" + "id=" + id + ", name=" + name + 
                        ", absoluteFilePath=" + absoluteFilePath +", progress=" + progress +'}';
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

}