package pathTree;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import main.MainJPanel;
  
public class CheckBoxTreeNodeSelectionListener extends MouseAdapter  
{  MainJPanel mainJPanel;
    public CheckBoxTreeNodeSelectionListener(MainJPanel mainJPanel) {
		super();
		this.mainJPanel=mainJPanel;
	}

	@Override  
    public void mouseClicked(MouseEvent event)  
    {  
        JTree tree = (JTree)event.getSource();  
        int x = event.getX();  
        int y = event.getY();  
        int row = tree.getRowForLocation(x, y);  
        TreePath path = tree.getPathForRow(row);  
        if(path != null)  
        {  
            CheckBoxTreeNode node = (CheckBoxTreeNode)path.getLastPathComponent();  
            if(node != null)  
            {  
                boolean isSelected = !node.isSelected();  
                node.setSelected(isSelected);  
//                System.out.println(node);
                if(node.getChildCount()==0){//说明是最后的
                	mainJPanel.readFileToTextArea(node.toString());
                	
                	if(isSelected){//同时选中
                		node.newPathArea(node);
                	   }else{
                		   node.deletePathArea(node);
                	   }
                	
                }
                ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(node);  
            }  
        }  
    }  
}  