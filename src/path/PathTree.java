package path;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;

import Utils.TextAreaUtils;
import treedemo.CheckBoxTreeCellRenderer;
import treedemo.CheckBoxTreeNode;
import treedemo.CheckBoxTreeNodeSelectionListener;

public class PathTree extends JPanel {
    private static final long serialVersionUID = 1L;

    private JButton stopButton = new JButton("停止扫描");
    private JButton browseButton = new JButton("选择文件夹");
    private JButton addReNameButton = new JButton("添加替换");
    private JTextArea pathsTextArea = new JTextArea();
    private JCheckBox showHiddenFilesCheckbox = new JCheckBox("显示隐藏文件", false);

    private Map<Integer, String> pathIndexes = new HashMap<Integer, String>();
    private FileFilter docFilter = new DocFilter(); // 文档过滤器
    private FileFilter dirFilter = new DirFilter(); // 文件夹过滤器
    private boolean stopped = false; // 是否停止扫描的标志
    public JScrollPane JTreescroll;//树形区域
    private JTextArea textArea;//输入区域
    private JTextArea oldfileNametextArea=new JTextArea();//旧文件名Po
    private JTextArea newfileNametextArea=new JTextArea();//新文件名Po
    private  JLabel oldfileNameLabel =new JLabel("旧文件Po名:");  
    private  JLabel newfileNameLabel =new JLabel("新文件Po名:");  
    public PathTree() {
        initGui();
    }

    // 初始化界面
    private void initGui() {
        this.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        northPanel.add(showHiddenFilesCheckbox);
        northPanel.add(browseButton);
        northPanel.add(stopButton);
        northPanel.add(addReNameButton);
        northPanel.add(new JLabel());
        northPanel.setLayout(new GridLayout(2,5));  
        northPanel.add(oldfileNameLabel);
        northPanel.add(oldfileNametextArea);
        oldfileNametextArea.setLineWrap(true);
        northPanel.add(newfileNameLabel);
        northPanel.add(newfileNametextArea);
        newfileNametextArea.setLineWrap(true);
        this.add(northPanel, BorderLayout.NORTH);

        JScrollPane scroller = new JScrollPane(pathsTextArea);
        scroller.setBorder(null);
        this.add(scroller, BorderLayout.CENTER);

        
        
        JTree tree = new JTree();  
        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener(this));  
       JTreescroll = new JScrollPane(tree);  
      this.add(JTreescroll, BorderLayout.WEST);
      
      textArea = new JTextArea();
      textArea.setFont(new Font("细明体", Font.PLAIN, 16));
      textArea.setLineWrap(true);
      JScrollPane panel = new JScrollPane(textArea,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      panel.setPreferredSize(new Dimension(0, 300));
      this.add(panel, BorderLayout.SOUTH);
        
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 选择文件夹
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(PathTree.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File dir = chooser.getSelectedFile();
                            pathsTextArea.setText("");
                          CheckBoxTreeNode rootNode = new CheckBoxTreeNode(dir.toString());  
                          DefaultTreeModel model = new DefaultTreeModel(rootNode);  
                          tree.setModel(model);  
                          tree.setCellRenderer(new CheckBoxTreeCellRenderer());  
                            stopped = false;
                            walkTree(dir, 0,rootNode);
                        }
                    });
                    t.start();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopped = true;
            }
        });
    }

    // 递归遍历目录树
    private void walkTree(File dir, int level,CheckBoxTreeNode parentNode) {
        // 1. current dir path
        // 2. docs path that located in this dir
        // 3. sub dirs path
    	if(parentNode==null){return;}
    	CheckBoxTreeNode childrenNode=null;
        if (stopped) { return; }

        // 如果不显示隐藏文件，则返回
        if (dir.isHidden() && !showHiddenFilesCheckbox.isSelected()) { return; }

        final StringBuilder pathBuffer = new StringBuilder(1024);

        // 访问当前目录
        pathBuffer.append(createPath(dir, level));
//         childrenNode = new CheckBoxTreeNode(); 

        // 访问文档
        for (File doc : dir.listFiles(docFilter)) {
            if (doc.isHidden() && !showHiddenFilesCheckbox.isSelected()) {
                continue;
            }
            pathBuffer.append(createPath(doc, level + 1));
            
            childrenNode = new CheckBoxTreeNode(doc); 
            parentNode.add(childrenNode);
        }

        // 把当前目录下的文件更新到text area中
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pathsTextArea.append(pathBuffer.toString());
            }
        });

        // 递归遍历子目录
        for (File subDir : dir.listFiles(dirFilter)) {
        	childrenNode = new CheckBoxTreeNode(subDir); 
            parentNode.add(childrenNode);
            walkTree(subDir, level + 1,childrenNode);
        }
    }

    // 创建文件的路径
    public String createPath(File file, int level) {
        StringBuilder pathBuffer = new StringBuilder(128);
        pathBuffer.append(getPathIndex(level)).append(file.getName()).append("\n");

        return pathBuffer.toString();
    }

    // 创建目录的缩进
    private String getPathIndex(int level) {
        // 如果不存在，则创建
        if (pathIndexes.get(level) == null) {
            StringBuilder indexBuffer = new StringBuilder(128);
            for (int i = 0; i < level; ++i) {
                indexBuffer.append("|        ");
            }

            indexBuffer.append("|----");
            pathIndexes.put(Integer.valueOf(level), indexBuffer.toString());
            return indexBuffer.toString();
        } else {
            return pathIndexes.get(level);
        }
    }

    // 创建主窗口
    static void createGUIAndShow() {
        JFrame frame = new JFrame("目录结构树");
      
        

        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 600;
        int h = 700;
        int x = (ss.width - w) / 2;
        int y = (ss.height - h) / 2 - 40;
        x = x > 0 ? x : 0;
        y = y > 0 ? y : 0;
        frame.setBounds(x, y, w, h);
      
        
//        JFrame frame = new JFrame("CheckBoxTreeDemo");  
//        frame.setBounds(200, 200, 600, 700);  
//        JTree tree = new JTree();  
//        CheckBoxTreeNode rootNode = new CheckBoxTreeNode("root");  
//        CheckBoxTreeNode node1 = new CheckBoxTreeNode("node_1");  
//        CheckBoxTreeNode node1_1 = new CheckBoxTreeNode("node_1_1");  
//        CheckBoxTreeNode node1_2 = new CheckBoxTreeNode("node_1_2");  
//        CheckBoxTreeNode node1_3 = new CheckBoxTreeNode("node_1_3");  
//        node1.add(node1_1);  
//        node1.add(node1_2);  
//        node1.add(node1_3);  
//        CheckBoxTreeNode node2 = new CheckBoxTreeNode("node_2");  
//        CheckBoxTreeNode node2_1 = new CheckBoxTreeNode("node_2_1");  
//        CheckBoxTreeNode node2_2 = new CheckBoxTreeNode("node_2_2");  
//        node2.add(node2_1);  
//        node2.add(node2_2);  
//        rootNode.add(node1);  
//        rootNode.add(node2);  
//        DefaultTreeModel model = new DefaultTreeModel(rootNode);  
//        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener());  
//        tree.setModel(model);  
//        tree.setCellRenderer(new CheckBoxTreeCellRenderer());  
//        JScrollPane scroll = new JScrollPane(tree);  
//        scroll.setBounds(0, 0, 300, 320);  
//        frame.getContentPane().add(scroll);  
          
        frame.setContentPane(new PathTree());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        
        frame.setVisible(true);
        
        
    }

    public void readFileToTextArea(String file){
    	textArea.setText(TextAreaUtils.read(file, "GBK", "gb2312"));
    }
 
}

class DocFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.isFile();
    }
}

class DirFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.isDirectory();
    }
}