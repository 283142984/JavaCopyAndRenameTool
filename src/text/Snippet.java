package text;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Snippet {
	 //测试函数
	    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
	        //将建立的JNotePad实例与SetVisible()的动作排入事件队列
	        //这玩意应该是一个Runnable接口的实现
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	        SwingUtilities.invokeLater( ()->{
	            new JNotePad_demo2(new FileTextDAO()).setVisible(true);//true显示,false隐藏
	        });
	    }
}

