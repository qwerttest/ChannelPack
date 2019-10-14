package com.jin.pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

public class PackWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MarginLeft = 10;
	
	JMenuBar menuBar;
	JMenu menuSettings;
	JMenuItem itemOutPathItem;
	JMenuItem itemPackForInputItem;
	private JTextArea consoleArea;
	
	private String baseFilePath;//基础包路径
	private String channelFilePath;//渠道文件路径
	private String outputPath;//文件输出路径
	
	public PackWindow(String title, int x, int y, int w, int h) {
		setBackground(Color.WHITE);
		setLayout(new GridLayout(6, 1));
		setTitle(title);
		initMenu();
		setLocation(x, y);
		setSize(w, h);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initTargetSelect();
		initChannelArea();
		initOutputPath();
		initPackButton();
		initConsole();
	}
	
	void initMenu() {
		menuBar = new JMenuBar();
		menuSettings = new JMenu("设置");
		itemOutPathItem = new JMenuItem("查看apk渠道");
		itemOutPathItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileSystemView fsvFileSystemView = FileSystemView.getFileSystemView();
				fileChooser.setCurrentDirectory(fsvFileSystemView.getHomeDirectory());
				fileChooser.setDialogTitle("选择文件输出目录");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setApproveButtonText("确定");
				int result = fileChooser.showOpenDialog(PackWindow.this);
				if(JFileChooser.APPROVE_OPTION == result) {
					String path = fileChooser.getSelectedFile().getPath();
					System.out.println("path=" + path);
				}
			}
		});
		
		menuSettings.add(itemOutPathItem);
		
		itemPackForInputItem = new JMenuItem("手动输入渠道");
		itemPackForInputItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		menuSettings.add(itemPackForInputItem);
		menuBar.add(menuSettings);
		setJMenuBar(menuBar);
	}
	
	/**
	 * 初始化基础包控件
	 * */
	void initTargetSelect() {
		final JButton targetPathTvArea = new JButton();
		targetPathTvArea.setText("将基础包拖动到此处");
		targetPathTvArea.setBounds(MarginLeft, 10, 400, 100);
		targetPathTvArea.setLayout(new BorderLayout());
		targetPathTvArea.setBackground(Color.LIGHT_GRAY);
		targetPathTvArea.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileSystemView fsvFileSystemView = FileSystemView.getFileSystemView();
				fileChooser.setCurrentDirectory(fsvFileSystemView.getHomeDirectory());
				fileChooser.setDialogTitle("选择基础包");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("确定");
				FileFilter fileterFileFilter = new FileFilter() {
					
					@Override
					public String getDescription() {
						return "apk文件";
					}
					
					@Override
					public boolean accept(File f) {
						return Utils.isApk(f);
					}
				};
				fileChooser.setFileFilter(fileterFileFilter);
				int result = fileChooser.showOpenDialog(PackWindow.this);
				if(JFileChooser.APPROVE_OPTION == result) {
					baseFilePath = fileChooser.getSelectedFile().getPath();
					targetPathTvArea.setText(baseFilePath);
				}
			}
		});
		
		add(targetPathTvArea);
		
		new DropTarget(targetPathTvArea, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
			
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						java.util.List<File> filesList = (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						File file = filesList.get(0);
						if(Utils.isApk(file)) {
							baseFilePath = file.getAbsolutePath();
							targetPathTvArea.setText(baseFilePath);
							dtde.dropComplete(true);
						} else {
							JOptionPane.showMessageDialog(PackWindow.this, "请选择apk文件");
						}
					} else {
						dtde.rejectDrop();
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 初始化渠道文件控件
	 * */
	void initChannelArea() {
		final JButton targetPathTvArea = new JButton();
		targetPathTvArea.setText("将渠道文件拖到此处");
		targetPathTvArea.setBounds(MarginLeft, 120, 400, 100);
		BorderLayout layout = new BorderLayout();
		targetPathTvArea.setLayout(layout);
		targetPathTvArea.setBackground(Color.LIGHT_GRAY);
		targetPathTvArea.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileSystemView fsvFileSystemView = FileSystemView.getFileSystemView();
				fileChooser.setCurrentDirectory(fsvFileSystemView.getHomeDirectory());
				fileChooser.setDialogTitle("选择渠道文件");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("确定");
				FileFilter fileterFileFilter = new FileFilter() {
					
					@Override
					public String getDescription() {
						return "txt文件";
					}
					
					@Override
					public boolean accept(File f) {
						return Utils.isTxt(f);
					}
				};
				fileChooser.setFileFilter(fileterFileFilter);
				int result = fileChooser.showOpenDialog(PackWindow.this);
				if(JFileChooser.APPROVE_OPTION == result) {
					channelFilePath = fileChooser.getSelectedFile().getPath();
					targetPathTvArea.setText(channelFilePath);
				}
			}
		});
		
		add(targetPathTvArea);
		
		new DropTarget(targetPathTvArea, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
			
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						java.util.List<File> filesList = (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						File file = filesList.get(0);
						if(Utils.isTxt(file)) {
							channelFilePath = file.getAbsolutePath();
							targetPathTvArea.setText(channelFilePath);
							dtde.dropComplete(true);
						} else {
							JOptionPane.showMessageDialog(PackWindow.this, "请选择txt文件");
						}
					} else {
						dtde.rejectDrop();
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 初始化渠道包输出目录
	 * */
	void initOutputPath() {
		final JButton targetPathTvArea = new JButton();
		targetPathTvArea.setText("选择渠道包存放目录");
		BorderLayout layout = new BorderLayout();
		targetPathTvArea.setBounds(MarginLeft, 240, 400, 50);
		targetPathTvArea.setLayout(layout);
		
		targetPathTvArea.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileSystemView fsvFileSystemView = FileSystemView.getFileSystemView();
				fileChooser.setCurrentDirectory(fsvFileSystemView.getHomeDirectory());
				fileChooser.setDialogTitle("选择渠道包存放路径");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setApproveButtonText("确定");
				FileFilter fileterFileFilter = new FileFilter() {
					
					@Override
					public String getDescription() {
						return "存放目录";
					}
					
					@Override
					public boolean accept(File f) {
						return f.isDirectory();
					}
				};
				fileChooser.setFileFilter(fileterFileFilter);
				int result = fileChooser.showOpenDialog(PackWindow.this);
				if(JFileChooser.APPROVE_OPTION == result) {
					outputPath = fileChooser.getSelectedFile().getPath();
					targetPathTvArea.setText(outputPath);
				}
			}
		});
		
		add(targetPathTvArea);
	}
	/**
	 * 初始化开始按钮
	 * */
	void initPackButton() {
		final JButton targetPathTvArea = new JButton();
		targetPathTvArea.setText("开始打包");
		targetPathTvArea.setBounds(MarginLeft, 300, 400, 50);
		BorderLayout layout = new BorderLayout();
		targetPathTvArea.setLayout(layout);
		targetPathTvArea.setBackground(Color.GREEN);
		targetPathTvArea.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Utils.isEmptyText(baseFilePath)) {
					JOptionPane.showMessageDialog(PackWindow.this, "请选择基础包");
					return;
				}
				
				if(Utils.isEmptyText(channelFilePath)) {
					JOptionPane.showMessageDialog(PackWindow.this, "请选择渠道文件");
					return;
				}
				
				if(Utils.isEmptyText(outputPath)) {
					outputPath = new File(baseFilePath).getParentFile().getAbsolutePath() + File.pathSeparator + "output";
				}
				append("打包开始.......");
				append("基础包目录为：" + baseFilePath);
				append("输出路径为：" + outputPath);
//				String wallString = "/Users/mac/Desktop/pack/pack2/walle-cli-all.jar";
//				String wallString = this.getClass().getResource("/walle-cli-all.jar").getPath();
				String runPathString = PackWindow.this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
				runPathString = new File(runPathString).getParentFile().getAbsolutePath();
				String wallString = runPathString + "/resources/walle-cli-all.jar";
				append("工具地址为：" + runPathString);
				// Java调用 dos命令
		        String cmd = "java -jar " + wallString + " batch -f " + channelFilePath + " " + baseFilePath + " " + outputPath;
		        try {
		        	targetPathTvArea.setEnabled(false);
		            Process process = Runtime.getRuntime().exec(cmd);
		            InputStream is = process.getInputStream();
		            InputStreamReader isr = new InputStreamReader(is);
		            BufferedReader br = new BufferedReader(isr);
		            String content = br.readLine();
		            while (content != null) {
		                System.out.println(content);
		                content = br.readLine();
		                append(content);
		            }
		            targetPathTvArea.setEnabled(true);
		            append("打包完成\n");
		            showOpenDialog();
		        } catch (IOException e1) {
		            e1.printStackTrace();
		            append(e1.getMessage());
		            targetPathTvArea.setEnabled(true);
		        }
			}
		});
		
		add(targetPathTvArea);
	}
	
	protected void showOpenDialog() {
		int result = JOptionPane.showConfirmDialog(PackWindow.this, "是否打开渠道包所在目录", "打包完成", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.YES_OPTION) {
			Utils.openDirectory(outputPath);
		}
	}

	/**
	 * 初始化日志打印
	 * */
	void initConsole() {
		consoleArea = new JTextArea();
		consoleArea.setEditable(false);
		consoleArea.setText("打包日志：\n");
		BorderLayout layout = new BorderLayout();
		consoleArea.setLayout(layout);
		JScrollPane pane = new JScrollPane(consoleArea);
		pane.setBounds(MarginLeft, 360, 400, 300);
		add(pane);
	}
	
	void append(String msg) {
		consoleArea.append(msg);
		consoleArea.append("\n");
	}
}
