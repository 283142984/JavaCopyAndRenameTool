package test;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class MyTree extends JFrame {
private Container con;
private JTree tree;
private DefaultTreeModel treeModel;
private Map<String,ArrayList<String>>map=new HashMap<String,ArrayList<String>>();

public MyTree(String s,String root){
super(s);
con=getContentPane();
DefaultMutableTreeNode rt=new DefaultMutableTreeNode(root);
treeModel = new DefaultTreeModel(rt);
//DefaultMutableTreeNode r1=new DefaultMutableTreeNode("book");
//treeModel.insertNodeInto(r1, rt, rt.getChildCount());


}

private void ShowTree(){
JTree tree = new JTree(treeModel); 
JScrollPane scrollpane=new JScrollPane(tree);
con.add(scrollpane);
setSize(400,400);
setVisible(true);
validate();
setVisible(true); //设置可见 
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}
private void add(){
DefaultMutableTreeNode root=(DefaultMutableTreeNode) treeModel.getRoot();
String sf=root.toString();
for(String Key:map.keySet()){
if(sf.equals(Key)){
continue; 
}
else
{
addnode(root,Key);
}
}
System.out.println("ok!");
}
private void init(){
ArrayList<String>li=new ArrayList<String>();
li.add("book");
li.add("hotel");
li.add("ride");

map.put("travel", li);

ArrayList<String>li1=new ArrayList<String>();
li1.add("search");
li1.add("social");
map.put("book", li1);

ArrayList<String>li2=new ArrayList<String>();
li2.add("X");
li2.add("Y");
map.put("hotel", li2);
System.out.println("mapsize"+map.size());


ArrayList<String>li3=new ArrayList<String>();
li3.add("X1");
li3.add("Y1");
map.put("X", li3);
}
private void addnode(DefaultMutableTreeNode n,String key){
ArrayList<String>list=map.get(key);
// DefaultMutableTreeNode root=(DefaultMutableTreeNode) treeModel.getRoot();
Enumeration<?> enumeration=n.children();
while(enumeration.hasMoreElements()){
DefaultMutableTreeNode node1=(DefaultMutableTreeNode) enumeration.nextElement();
String str=node1.toString();
if(str.equals(key)){
for(String s:list){
DefaultMutableTreeNode r1=new DefaultMutableTreeNode(s); 
treeModel.insertNodeInto(r1,node1,node1.getChildCount());
}
}
addnode(node1,key);
}
}


private void addroot(){
DefaultMutableTreeNode root=(DefaultMutableTreeNode) treeModel.getRoot();
String sroot=root.toString();
ArrayList<String>list=map.get(sroot);
for(String sr:list){
DefaultMutableTreeNode r1=new DefaultMutableTreeNode(sr); 
treeModel.insertNodeInto(r1,root,root.getChildCount());
}

}
public static void main(String[] args) {
MyTree tree=new MyTree("目录展示","travel");
tree.init();
tree.addroot();
tree.add();
tree.ShowTree();

}

}