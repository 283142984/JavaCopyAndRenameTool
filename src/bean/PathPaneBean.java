package bean;

import java.io.Serializable;

import javax.swing.JTextArea;
/**
 * 保存要新旧路径  序列化类 用于恢复现场
 * 
 * */
public class PathPaneBean implements Serializable {

	 private JTextArea oldPathNametextArea=new JTextArea();//原文件目录
	    private JTextArea newPathNametextArea=new JTextArea();//新文件目录
	    public PathPaneBean() {
		}
	    
		public PathPaneBean(JTextArea oldPathNametextArea,
				JTextArea newPathNametextArea) {
			super();
			this.oldPathNametextArea = oldPathNametextArea;
			this.newPathNametextArea = newPathNametextArea;
		}

		public JTextArea getOldPathNametextArea() {
			return oldPathNametextArea;
		}
		public void setOldPathNametextArea(JTextArea oldPathNametextArea) {
			this.oldPathNametextArea = oldPathNametextArea;
		}
		public JTextArea getNewPathNametextArea() {
			return newPathNametextArea;
		}
		public void setNewPathNametextArea(JTextArea newPathNametextArea) {
			this.newPathNametextArea = newPathNametextArea;
		}

}
