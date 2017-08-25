package treedemo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import path.PathTree;  
  
public class CheckBoxTreeNodeSelectionListener extends MouseAdapter  
{  PathTree pathTree;
    public CheckBoxTreeNodeSelectionListener(PathTree pathTree) {
		super();
		this.pathTree=pathTree;
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
                System.out.println(node);
                if(node.getChildCount()==0){//说明是最后的
                	pathTree.readFileToTextArea(node.toString());
                }
                ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(node);  
            }  
        }  
    }  
}  