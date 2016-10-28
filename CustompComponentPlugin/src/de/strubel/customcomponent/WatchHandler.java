package de.strubel.customcomponent;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Taken from: http://www.straub.as/java/history/watchservice.html
 * 
 * @author Herbert Max Straub
 * @author strubelz
 * 
 */

public class WatchHandler extends Thread
{
   WatchService watchService;
   Path dirToWatch;
   ArrayList<WatchListener> listeners;

   public WatchHandler(WatchService watchService, Path dirToWatch)
   {
      this.watchService = watchService;
      this.dirToWatch = dirToWatch;
      listeners = new ArrayList<WatchListener>();
   }

   /**
    */
   @Override
   public void run()
   {
//      System.out.println("run");
      for(;;)
      {
         try
         {
            WatchKey key = watchService.take(); // blockiert
            
            List<WatchEvent<?>> eventList = key.pollEvents();
//            System.out.println("size = " + eventList.size());
            	for (WatchListener listener : listeners)
            	{
                    for(WatchEvent<?> e : eventList)
                    {
                    	Path path = dirToWatch.resolve((Path) e.context());
                    	FileHandle handle = Gdx.files.absolute(path.toString());
                    	if (e.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
                    	{
                    		listener.fileCreated(handle);
                    	}
                    	if (e.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))
                    	{
                    		listener.fileDeleted(handle);
                    	}
                    	if (e.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
                    	{
                    		listener.fileChanged(handle);
                    	}
                    }
            	}
            	
//               System.out.print(e.kind() + " -> ");
//               Path name = (Path)e.context();
//               //System.out.print(name.getParent());
//               // context liefert nur den Dateinamen, parent ist null !
//               Path path = dirToWatch.resolve(name);
//               System.out.print(path);
//               if (Files.isDirectory(path))
//                  System.out.println(" <dir>");
//               else
//                  System.out.println(" <file>");
            boolean valid = key.reset();
            if (!valid)
            {
               break;
            }
         }
         catch(InterruptedException ex)
         {
            // TODO Auto-generated catch block
            ex.printStackTrace();
         }
      } // end for

   } // end run
   
   public ArrayList<WatchListener> getListeners() {
	   return listeners;
   }

} // end class