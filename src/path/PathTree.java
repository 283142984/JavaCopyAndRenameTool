package path;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PathTree extends JPanel {
    private static final long serialVersionUID = 1L;

    private JButton stopButton = new JButton("停止扫描");
    private JButton browseButton = new JButton("选择文件夹");
    private JTextArea pathsTextArea = new JTextArea();
    private JCheckBox showHiddenFilesCheckbox = new JCheckBox("显示隐藏文件", false);

    private Map<Integer, String> pathIndexes = new HashMap<Integer, String>();
    private FileFilter docFilter = new DocFilter(); // 文档过滤器
    private FileFilter dirFilter = new DirFilter(); // 文件夹过滤器

    private boolean stopped = false; // 是否停止扫描的标志

    public PathTree() {
        initGui();
    }

    // 初始化界面
    private void initGui() {
        this.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        buttonsPanel.add(showHiddenFilesCheckbox);
        buttonsPanel.add(browseButton);
        buttonsPanel.add(stopButton);
        this.add(buttonsPanel, BorderLayout.NORTH);

        JScrollPane scroller = new JScrollPane(pathsTextArea);
        scroller.setBorder(null);
        this.add(scroller, BorderLayout.CENTER);

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
                            stopped = false;
                            walkTree(dir, 0);
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
    private void walkTree(File dir, int level) {
        // 1. current dir path
        // 2. docs path that located in this dir
        // 3. sub dirs path

        if (stopped) { return; }

        // 如果不显示隐藏文件，则返回
        if (dir.isHidden() && !showHiddenFilesCheckbox.isSelected()) { return; }

        final StringBuilder pathBuffer = new StringBuilder(1024);

        // 访问当前目录
        pathBuffer.append(createPath(dir, level));

        // 访问文档
        for (File doc : dir.listFiles(docFilter)) {
            if (doc.isHidden() && !showHiddenFilesCheckbox.isSelected()) {
                continue;
            }
            pathBuffer.append(createPath(doc, level + 1));
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
            walkTree(subDir, level + 1);
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
    private static void createGUIAndShow() {
        JFrame frame = new JFrame("目录结构树");
        frame.setContentPane(new PathTree());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 600;
        int h = 700;
        int x = (ss.width - w) / 2;
        int y = (ss.height - h) / 2 - 40;
        x = x > 0 ? x : 0;
        y = y > 0 ? y : 0;
        frame.setBounds(x, y, w, h);
        frame.setVisible(true);
    }

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
                createGUIAndShow();
            }
        });
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