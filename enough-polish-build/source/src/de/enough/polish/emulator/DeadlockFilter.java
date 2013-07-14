package de.enough.polish.emulator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.enough.polish.BuildException;
import de.enough.polish.ant.emulator.FilterSetting;

public class DeadlockFilter
extends EmulatorOutputFilter
implements Runnable
{
	private Hashtable<String,Vector<String>> methodTracesByThread = new Hashtable<String, Vector<String>>();
	private char methodTraceMessageChar = '|';
	private ArrayList<String> observePackages;
	
	public DeadlockFilter()
	{
		Thread t = new Thread(this);
		t.start();
	}
	
	protected void configure(FilterSetting filterSetting) 
	{
		if (filterSetting.getFilterMandatoryMessageChar() != null)
		{
			methodTraceMessageChar = filterSetting.getFilterMandatoryMessageChar().charValue();
		}
		if (filterSetting.getFilterPackageNames() == null)
		{
			throw new BuildException("Invalid filter setting for deadlock filter: FilterPackageNames need to be configured.");
			
		}
		observePackages = new ArrayList<String>(filterSetting.getFilterPackageNames().size());
		for (String packageName : filterSetting.getFilterPackageNames())
		{
			observePackages.add(packageName.replace('.', '/'));
		}
	}

	
	public FilterResult filter(String message) 
	{
		if (message.indexOf(this.methodTraceMessageChar) == -1)
		{
			return FilterResult.NotProcessed;
		}
		for (String packageName : this.observePackages)
		{
			if (message.indexOf(packageName) != -1)
			{
				addMethodTrace(message);
				break;
			}
		}
		return FilterResult.DoNotPrint;
	}

	
	public void addMethodTrace(String message)
	{
		int threadNameIndex = message.lastIndexOf("(Thread ");
		if (threadNameIndex == -1)
		{
			return;
		}
		String threadName = message.substring(threadNameIndex + "(Thread ".length(), message.lastIndexOf(')'));
		Vector<String> methodTraces = methodTracesByThread.get(threadName);
		if (methodTraces == null)
		{
			methodTraces = new Vector<String>();
			methodTracesByThread.put(threadName, methodTraces);
			System.out.println("NEW THREAD: [" + threadName + "]");
		}
		if (message.indexOf(" => ") != -1)
		{
			methodTraces.add(message);
		}
		else
		{
			int index = message.indexOf("<=");
			if (index == -1)
			{
				System.out.println("weird message: " + message);
				return;
			}
			String enterMethodTrace = message.substring(0, index) + "=>" + message.substring(index + "=>".length());
			boolean removed = methodTraces.remove(enterMethodTrace);
			if (!removed)
			{
				System.out.println("unable to remove " + enterMethodTrace);
			}
//			if (methodTraces.size() == 0)
//			{
//				methodTracesByThread.remove(threadName);
//				System.out.println("LEAVING THREAD [" + threadName + "]");
//			}
		}
	}

	public void run() {
		while (true)
		{
			try { Thread.sleep(10 * 1000); } catch (InterruptedException ex) {}
			Enumeration<String> keyEnum = methodTracesByThread.keys();
			boolean firstFound = true;
			while (keyEnum.hasMoreElements())
			{
				String threadName = keyEnum.nextElement();
				Vector<String> methodTraces = methodTracesByThread.get(threadName);
				if (methodTraces == null)
				{
					continue;
				}
				if (methodTraces.size() > 3)
				{
					if (firstFound)
					{
						System.out.println("______method_traces____________at_" + new Date());
						firstFound = false;
					}
					System.out.println("___Thread: " + threadName);
					for (int i=0; i<methodTraces.size(); i++)
					{
						try {
							System.out.println(methodTraces.get(i));
						} 
						catch (Exception ex)
						{
							break;
						}
					}
				}
			}
		}
	}



}
