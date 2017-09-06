package bean;

import java.io.Serializable;

import javax.swing.JTextArea;

import pathTree.CheckBoxTreeNode;
/**
 * 保存要新旧路径  序列化类 用于恢复现场
 * 
 * */
public class PathPaneBean implements Serializable {

	 private JTextArea oldPathNametextArea=new JTextArea();//原文件目录
	    private JTextArea newPathNametextArea=new JTextArea();//新文件目录
	    private CheckBoxTreeNode checkBoxTreeNode;
	    public PathPaneBean() {
		}
	    
		public PathPaneBean(JTextArea oldPathNametextArea,
				JTextArea newPathNametextArea,CheckBoxTreeNode checkBoxTreeNode) {
			super();
			this.oldPathNametextArea = oldPathNametextArea;
			this.newPathNametextArea = newPathNametextArea;
			this.checkBoxTreeNode=checkBoxTreeNode;
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

		public CheckBoxTreeNode getCheckBoxTreeNode() {
			return checkBoxTreeNode;
		}

		public void setCheckBoxTreeNode(CheckBoxTreeNode checkBoxTreeNode) {
			this.checkBoxTreeNode = checkBoxTreeNode;
		}

}
