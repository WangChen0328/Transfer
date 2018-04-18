package frame;

import io.netty.channel.nio.NioEventLoopGroup;
import netty.NettyConstant;
import netty.client.NettyClient;
import netty.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.HttpFileDownloadServer;
import web.HttpFileUploadServer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchen
 * @date 2018/4/3 9:10
 */
public class JProgressBarPanel extends JFrame {

    Thread threadNetty = null;
    Thread threadWebUpload = null;
    Thread threadWebDownload = null;
    JProgressBarPanel frame = null;
    NioEventLoopGroup bossGroup = null;
    NioEventLoopGroup workGroup = null;

    Map<String, JProgressBar> progressBars = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(JProgressBarPanel.class);


    public NioEventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public NioEventLoopGroup getWorkGroup() {
        return workGroup;
    }

    public void  buildPanel() {
        /**
         * 面板
         */
        frame = new JProgressBarPanel();
        frame.setSize(750, 850);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        /**
         * 面板关闭事件
         */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                }
                if (workGroup != null) {
                    workGroup.shutdownGracefully();
                }
                if (threadNetty != null) {
                    threadNetty.interrupt();
                }
                if (threadWebUpload != null) {
                    threadWebUpload.interrupt();
                }
                if (threadWebDownload != null) {
                    threadWebDownload.interrupt();
                }
            }
        });
        /**
         * 按钮
         */
        JButton accept = new JButton("接收文件");
        JButton transfer = new JButton("发送文件");

        Container contentPane = frame.getContentPane();

        /**
         * 接收文件
         */
        accept.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setTitle("正在接收");
                accept.setVisible(false);
                transfer.setVisible(false);

                JPanel panel = new JPanel();
                final String[] savePath = {""};
                panel.setLayout(new GridLayout(7,1,20,0));
                frame.add(panel);

                JTextField jTextField = null;
                JTextField jTextFieldWebUpLoad = null;
                JTextField jTextFieldWebDownLoad = null;
                JTextField localFilePath = new JTextField("默认1保存地址：" + System.getProperty("user.home") + File.separator + "FileServer" + File.separator + ": ...");
                JButton filePath = new JButton(" 选择文件保存地址");

                try {
                    jTextField = new JTextField("Netty  监听地址：" + InetAddress.getLocalHost().getHostAddress() + ":" + NettyConstant.LOCAL_NETTY_PORT );
                    panel.add(jTextField);
                    jTextFieldWebUpLoad = new JTextField("Web监听上传地址：" + InetAddress.getLocalHost().getHostAddress() + ":" + NettyConstant.LOCAL_WEB_UPLOAD_PORT);
                    panel.add(jTextFieldWebUpLoad);
                    jTextFieldWebDownLoad = new JTextField("Web监听下载地址：" + InetAddress.getLocalHost().getHostAddress() + ":" + NettyConstant.LOCAL_WEB_DOWNLOAD_PORT);
                    panel.add(jTextFieldWebDownLoad);
                    panel.add(filePath);
                    panel.add(localFilePath);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                filePath.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setMultiSelectionEnabled(false);
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        /**
                         * 只选文件夹
                         */
                        if (chooser.showOpenDialog(filePath) == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = chooser.getSelectedFile();
                            savePath[0] = selectedFile.getPath();
                            localFilePath.setText("文件保存地址：" + selectedFile.getPath()  + File.separator + " : ...");
                            frame.invalidate();
                            frame.repaint();
                        }
                    }
                });

                /**
                 * 白名单地址
                 */
                JTextArea area = new JTextArea("谁可以访问你? 输入IP,逗号隔开！ 默认 127.0.0.1", 5, 5);
                panel.add(area);

                area.setTabSize(4);
                area.setFont(new Font("标楷体", Font.BOLD, 16));
                area.setLineWrap(true);// 激活自动换行功能
                area.setWrapStyleWord(true);// 激活断行不断字功能
                area.setBackground(Color.LIGHT_GRAY);


                jTextField.setBorder(new EmptyBorder(0,0,0,0));
                jTextField.setBackground(new Color(238, 238, 238));
                jTextField.setFont(new Font("宋体",Font.BOLD,20));

                jTextFieldWebUpLoad.setBorder(new EmptyBorder(0,0,0,0));
                jTextFieldWebUpLoad.setBackground(new Color(238, 238, 238));
                jTextFieldWebUpLoad.setFont(new Font("宋体",Font.BOLD,20));

                jTextFieldWebDownLoad.setBorder(new EmptyBorder(0,0,0,0));
                jTextFieldWebDownLoad.setBackground(new Color(238, 238, 238));
                jTextFieldWebDownLoad.setFont(new Font("宋体",Font.BOLD,20));

                localFilePath.setBorder(new EmptyBorder(0,0,0,0));
                localFilePath.setBackground(new Color(238, 238, 238));
                localFilePath.setFont(new Font("宋体",Font.BOLD,20));

                JButton run = new JButton("启动");
                run.setFont(new Font("宋体",Font.BOLD,20));

                run.setSize(10,10);
                panel.add(run);

                /**
                 * 开启Netty监听
                 */
                run.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        boolean matches = true;

                        String text = area.getText();

                        if (text.length() == 0) {
                            matches = false;
                        } else {
                            String[] regexs = text.split(",");
                            for (String regex : regexs) {
                                boolean matches1 = regex.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
                                if (!matches1) {
                                    matches = false;
                                    break;
                                }
                            }
                        }

                        if (matches) {
                            /**
                             * 接收Netty客户端
                             */
                            threadNetty = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        bossGroup = new NioEventLoopGroup();
                                        workGroup = new NioEventLoopGroup();
                                        new NettyServer().run(InetAddress.getLocalHost().getHostAddress(), NettyConstant.LOCAL_NETTY_PORT, bossGroup, workGroup, area.getText().split(","), savePath[0]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            threadNetty.start();
                            /**
                             * 接收Web上传
                             */
                            threadWebUpload = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        new HttpFileUploadServer().run(NettyConstant.LOCAL_WEB_UPLOAD_PORT, savePath[0]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            threadWebUpload.start();
                            /**
                             * 接收Web下载
                             */
                            threadWebDownload = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        new HttpFileDownloadServer().run(NettyConstant.LOCAL_WEB_DOWNLOAD_PORT);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            threadWebDownload.start();
                            run.setEnabled(false);
                            filePath.setEnabled(false);
                            run.setText("已启动！");
                        } else {
                            run.setVisible(true);
                            area.setText("ip地址格式不合法！");
                        }
                    }
                });
            }
        });
        /**
         * 发送文件
         */
        transfer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setTitle("发送文件");
                accept.setVisible(false);
                transfer.setVisible(false);

                JPanel panel = new JPanel();

                contentPane.add(panel);
                /**
                 * 上传按钮
                 */
                JButton developer = new JButton("上传文件");
                JTextField field = new JTextField("输入,Netty监听地址");
                field.setSize(220, 20);
                field.setFont(new Font("宋体",Font.BOLD,20));

                panel.add(field);
                panel.add(developer);

                developer.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        field.setEditable(false);

                        /**
                         * 选择文件
                         */
                        JFileChooser chooser = new JFileChooser();
                        /**
                         * 多选
                         */
                        chooser.setMultiSelectionEnabled(true);

                        if (chooser.showOpenDialog(developer) == JFileChooser.APPROVE_OPTION) {
                            File[] files = chooser.getSelectedFiles();
                            if (files != null && files.length > 0) {

                                panel.setLayout(new GridLayout(files.length + 1, 2,20,10));

                                if (panel.getLayout() != null) {

                                    GridLayout layout = (GridLayout) panel.getLayout();
                                    layout.setRows(layout.getRows() + files.length);

                                    frame.invalidate();
                                    frame.repaint();
                                    frame.setVisible(true);
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        workGroup = new NioEventLoopGroup();
                                        try {

                                            if ("输入Netty远程连接地址".equals(field.getText())) {
                                                field.setText("");
                                            }

                                            String host = isEmpty(field.getText().split(":")[0]) ? NettyConstant.REMOTE_IP : field.getText().split(":")[0];
                                            int port = isEmpty(field.getText().split(":")[1]) ? NettyConstant.LOCAL_NETTY_PORT : Integer.parseInt(field.getText().split(":")[1]);

                                            new NettyClient().connect(
                                                    host,
                                                    port,
                                                    files,
                                                    workGroup,
                                                    frame);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                });
            }
        });
        /**
         * 添加按钮
         */
        frame.add(accept);
        frame.add(transfer);
        /**
         * 显示面板
         */
        frame.setVisible(true);
    }

    /**
     * 加入进度条
     * @param file
     * @param progress
     */
    public void progress(JFrame frame, File file, String progress) {
        JProgressBar progressBar = null;
        if (!progressBars.containsKey(file.getPath())) {
            progressBar = new JProgressBar();

            progressBar.setStringPainted(true);
            progressBars.put(file.getPath(), progressBar);

            Component[] components = frame.getContentPane().getComponents();

            JPanel jPanels = (JPanel) components[2];

            jPanels.add(progressBar);
            jPanels.add(new JLabel(file.getName()));

            frame.invalidate();
            frame.repaint();
            frame.setVisible(true);
        } else {
            progressBar = progressBars.get(file.getPath());
        }

        if(progress == null) {
            progressBar.setBackground(Color.red);
            progressBar.setString("文件异常!");
        } else if (Integer.parseInt(progress) == 100) {
            progressBar.setValue(Integer.parseInt(progress));
            progressBar.setString("已完成!");
        } else {
            progressBar.setValue(Integer.parseInt(progress));
        }
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static void main(String[] args){
        new JProgressBarPanel().buildPanel();
    }
}
