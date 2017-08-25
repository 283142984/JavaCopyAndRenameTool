package path;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Snippet {
	   public static void main(String[] args) {
	        try {
	            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	            //UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                PathTree.createGUIAndShow();
	            }
	        });
	    }
}

