package bean;

import java.io.Serializable;

import javax.swing.JTextArea;
/**
 * 保存要替换的字段和新字段  序列化类 用于恢复现场
 * 
 * */
public class ReNamePaneBean implements Serializable {

	 private JTextArea oldNametextArea=new JTextArea();//旧文件名Po
	    private JTextArea newNametextArea=new JTextArea();//新文件名Po
		public JTextArea getOldNametextArea() {
			return oldNametextArea;
		}
		public void setOldNametextArea(JTextArea oldNametextArea) {
			this.oldNametextArea = oldNametextArea;
		}
		public JTextArea getNewNametextArea() {
			return newNametextArea;
		}
		public void setNewNametextArea(JTextArea newNametextArea) {
			this.newNametextArea = newNametextArea;
		}

}
