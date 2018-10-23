/*
 * iSignal - Digital circuit simulator
 * Copyright (C) 2018-present Iercosan-Lucaci Alexandru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *    ||=============================================||
 *    ||     _  _____  _          _            _     ||
 *    ||    (_)/  ___|(_)       =)_)-         | |    ||
 *    ||     _ \ `--.  _   __ _  _ __    __ _ | |    ||
 *    ||    | | `--. \| | / _` || '_ \  / _` || |    ||
 *    ||    | |/\__/ /| || (_| || | | || (_| || |    ||
 *    ||    |_|\____/ |_| \__, ||_| |_| \__,_||_|    ||
 *    ||                   __/ |                     ||
 *    ||                  |___/  Digital Simulator   ||
 *    ||=============================================||
 */

package com.lucaci32u4.UI.Windows;

import com.lucaci32u4.UI.Windows.PageTree.Page;
import com.lucaci32u4.UI.Windows.PageTree.PagedTreeWindow;
import com.lucaci32u4.main.LanguagePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MainWindow {
	public interface ExitDialogResult {
		int EXIT_SAVE = 0b11;
		int EXIT_DISCARD = 0b10;
		int EXIT_CANCEL = 0b00;
	}
	
	public interface UserInputListener {
		enum Type {
			NEW, OPEN, SAVE, EXPORT, SETTINGS, EXIT, UNDO,
			REDO, COPY, PASTE, DELETE, SELECTALL, EDITSEL,
			NEWCIRCUIT, DELETECIRCUIT, LIBRARIES, STARTSTOP,
			RESET, ACTIVESIMULATION,
		}
		void onUserEvent(Type subject, int param1, String param2);
	}

	private LanguagePack lang;

	// Swing objects
	private JFrame frame;
	private JPanel contentPanel;
	private JToolBar topToolBar, bottomToolBar;
	private JSplitPane splitPaneVertical;
	private JPanel circuitPanel;
	private JSplitPane splitPaneHorizontal;
	private JTabbedPane tabbedPane;
	private JTree selectTree, simulationTree;
	private HashMap<Object, Icon> treeIcon;
	private DefaultMutableTreeNode selectTreeRoot, simulationTreeRoot;
	private DefaultMutableTreeNode circuitsRoot;
	private DefaultMutableTreeNode componentsRoot;
	private JPanel propertiesPanel;
	private MenuEventHandler menuEventHandler;
	private WindowEventHandler windowEventHandler;

	// Swing menus
	private JMenuBar menuBar;
	private JMenu menuFile, menuEdit, menuProject, menuSimulation, menuWindow, menuHelp;
	private JMenuItem miNew, miOpen, miSave, miSaveAs, miExport, miSettings, miExit;
	private JMenu miOpenRecent;
	private JMenuItem[] miOpenRecentFiles;
	private JMenuItem miUndo, miRedo, miRedoAll, miCopy, miPaste, miCut, miDelete, miSelectAll, miEditSelection;
	private JMenuItem miNewCircuit, miDeleteCircuit, miLibraries;
	private JMenuItem miReset;
	private JCheckBoxMenuItem miStartStop;
	private JMenu miActiveSimulations;
	private JMenuItem[] miActiveSimulationsOptions;
	private JMenuItem miMinimise, miMaxmise;
	private JMenu miToolbarLocation;
	private JMenuItem[] miToolbarLocationOptions;
	private JMenuItem miTutorial, miDocumentation, miAbout;

	// Outbound event queue
	private UserInputListener listener;

	// Simulation variables
	private boolean activeSimulation;
	
	// Strings
	private String aboutText;
	
	public MainWindow(@Nullable UserInputListener uil, @NotNull String aboutText) {
		this.listener = uil;
		this.aboutText = aboutText;
		activeSimulation = false;
		lang = LanguagePack.getInstance();
		try {
			SwingUtilities.invokeAndWait(() -> {
				frame = new JFrame();
				menuBar = new JMenuBar();
				menuFile = new JMenu();
				menuEdit = new JMenu();
				menuProject = new JMenu();
				menuSimulation = new JMenu();
				menuWindow = new JMenu();
				menuHelp = new JMenu();
				miNew = new JMenuItem();
				miOpen = new JMenuItem();
				miSave = new JMenuItem();
				miSaveAs = new JMenuItem();
				miExport = new JMenuItem();
				miSettings = new JMenuItem();
				miExit = new JMenuItem();
				miOpenRecent = new JMenu();
				miOpenRecentFiles = new JMenuItem[0];
				miUndo = new JMenuItem();
				miRedo = new JMenuItem();
				miRedoAll = new JMenuItem();
				miCopy = new JMenuItem();
				miPaste = new JMenuItem();
				miCut = new JMenuItem();
				miDelete = new JMenuItem();
				miSelectAll = new JMenuItem();
				miEditSelection = new JMenuItem();
				miNewCircuit = new JMenuItem();
				miDeleteCircuit = new JMenuItem();
				miLibraries = new JMenuItem();
				miStartStop = new JCheckBoxMenuItem();
				miReset = new JMenuItem();
				miActiveSimulations = new JMenu();
				miActiveSimulationsOptions = new JMenuItem[0];
				miMinimise = new JMenuItem();
				miMaxmise = new JMenuItem();
				miToolbarLocation = new JMenu();
				miToolbarLocationOptions = new JMenuItem[2];
				miToolbarLocationOptions[0] = new JMenuItem();
				miToolbarLocationOptions[1] = new JMenuItem();
				miTutorial = new JMenuItem();
				miDocumentation = new JMenuItem();
				miAbout = new JMenuItem();
				menuFile.add(miNew);
				menuFile.add(miOpen);
				menuFile.add(miOpenRecent);
				menuFile.addSeparator();
				menuFile.add(miSave);
				menuFile.add(miSaveAs);
				menuFile.addSeparator();
				menuFile.add(miExport);
				menuFile.addSeparator();
				menuFile.add(miSettings);
				menuFile.addSeparator();
				menuFile.add(miExit);
				menuEdit.add(miUndo);
				menuEdit.add(miRedo);
				menuEdit.add(miRedoAll);
				menuEdit.addSeparator();
				menuEdit.add(miCopy);
				menuEdit.add(miPaste);
				menuEdit.add(miCut);
				menuEdit.addSeparator();
				menuEdit.add(miDelete);
				menuEdit.add(miSelectAll);
				menuEdit.add(miEditSelection);
				menuProject.add(miNewCircuit);
				menuProject.add(miDeleteCircuit);
				menuProject.addSeparator();
				menuProject.add(miLibraries);
				menuSimulation.add(miStartStop);
				menuSimulation.add(miReset);
				menuSimulation.addSeparator();
				menuSimulation.add(miActiveSimulations);
				menuWindow.add(miMinimise);
				menuWindow.add(miMaxmise);
				menuWindow.addSeparator();
				menuWindow.add(miToolbarLocation);
				for (JMenuItem item : miToolbarLocationOptions) miToolbarLocation.add(item);
				menuHelp.add(miTutorial);
				menuHelp.add(miDocumentation);
				menuHelp.addSeparator();
				menuHelp.add(miAbout);
				menuBar.add(menuFile);
				menuBar.add(menuEdit);
				menuBar.add(menuProject);
				menuBar.add(menuSimulation);
				menuBar.add(menuWindow);
				menuBar.add(menuHelp);
				menuEventHandler = new MenuEventHandler();
				miNew.addActionListener(menuEventHandler);
				miOpen.addActionListener(menuEventHandler);
				miSave.addActionListener(menuEventHandler);
				miSaveAs.addActionListener(menuEventHandler);
				miExport.addActionListener(menuEventHandler);
				miSettings.addActionListener(menuEventHandler);
				miExit.addActionListener(menuEventHandler);
				miUndo.addActionListener(menuEventHandler);
				miRedo.addActionListener(menuEventHandler);
				miRedoAll.addActionListener(menuEventHandler);
				miCopy.addActionListener(menuEventHandler);
				miCut.addActionListener(menuEventHandler);
				miPaste.addActionListener(menuEventHandler);
				miDelete.addActionListener(menuEventHandler);
				miSelectAll.addActionListener(menuEventHandler);
				miEditSelection.addActionListener(menuEventHandler);
				miNewCircuit.addActionListener(menuEventHandler);
				miDeleteCircuit.addActionListener(menuEventHandler);
				miLibraries.addActionListener(menuEventHandler);
				miStartStop.addActionListener(menuEventHandler);
				miReset.addActionListener(menuEventHandler);
				miMinimise.addActionListener(menuEventHandler);
				miMaxmise.addActionListener(menuEventHandler);
				miDocumentation.addActionListener(menuEventHandler);
				miTutorial.addActionListener(menuEventHandler);
				miAbout.addActionListener(menuEventHandler);
				for (JMenuItem item : miToolbarLocationOptions) item.addActionListener(menuEventHandler);
				miStartStop.setState(activeSimulation);
				windowEventHandler = new WindowEventHandler();
				frame.addWindowListener(windowEventHandler);
				frame.setJMenuBar(menuBar);
				frame.setSize(1200, 600);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				treeIcon = new HashMap<>();
				contentPanel = new JPanel();
				topToolBar = new JToolBar();
				topToolBar.add(new JButton("cba"));
				bottomToolBar = new JToolBar();
				bottomToolBar.add(new JButton("abc"));
				splitPaneVertical = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				circuitPanel = new JPanel();
				splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				tabbedPane = new JTabbedPane();
				propertiesPanel = new JPanel();
				selectTreeRoot = new DefaultMutableTreeNode("");
				simulationTreeRoot = new DefaultMutableTreeNode("");
				selectTree = new JTree(selectTreeRoot);
				simulationTree = new JTree(simulationTreeRoot);
				circuitsRoot = new DefaultMutableTreeNode("");
				componentsRoot = new DefaultMutableTreeNode("");
				selectTreeRoot.add(circuitsRoot);
				selectTreeRoot.add(componentsRoot);
				selectTree.expandRow(0);
				simulationTree.expandRow(0);
				selectTree.setRootVisible(false);
				selectTree.setShowsRootHandles(true);
				selectTree.addTreeSelectionListener(new SelectTreeSelectionListener());
				selectTree.setCellRenderer(new MyTreeCellRenderer());
				simulationTree.setRootVisible(false);
				simulationTree.setShowsRootHandles(true);
				simulationTree.addTreeSelectionListener(new SimulationTreeSelectionListener());
				simulationTree.setCellRenderer(selectTree.getCellRenderer());
				contentPanel.setLayout(new BorderLayout());
				topToolBar.setRollover(false);
				bottomToolBar.setRollover(false);
				contentPanel.add(topToolBar, BorderLayout.NORTH);
				contentPanel.add(bottomToolBar, BorderLayout.SOUTH);
				contentPanel.add(splitPaneVertical, BorderLayout.CENTER);
				splitPaneVertical.setLeftComponent(splitPaneHorizontal);
				splitPaneVertical.setRightComponent(circuitPanel);
				splitPaneHorizontal.setTopComponent(tabbedPane);
				splitPaneHorizontal.setBottomComponent(new JScrollPane(propertiesPanel));
				tabbedPane.addTab(null, new JScrollPane(selectTree));
				tabbedPane.addTab(null, new JScrollPane(simulationTree));
				tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				splitPaneVertical.setDividerLocation(200);
				splitPaneVertical.setDividerSize(8);
				splitPaneHorizontal.setDividerSize(5);
				frame.setContentPane(contentPanel);
				updateText();
			});
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void updateText() {
		SwingUtilities.invokeLater(() -> {
			menuFile.setText(lang.get("file"));
			menuEdit.setText(lang.get("edit"));
			menuProject.setText(lang.get("project"));
			menuSimulation.setText(lang.get("simulation"));
			menuWindow.setText(lang.get("window"));
			menuHelp.setText(lang.get("help"));
			miNew.setText(lang.get("new"));
			miOpen.setText(lang.get("open"));
			miSave.setText(lang.get("save"));
			miSaveAs.setText(lang.get("saveas"));
			miExport.setText(lang.get("export"));
			miSettings.setText(lang.get("settings"));
			miExit.setText(lang.get("exit"));
			miOpenRecent.setText(lang.get("openrecent"));
			miUndo.setText(lang.get("undo"));
			miRedo.setText(lang.get("redo"));
			miRedoAll.setText(lang.get("redoall"));
			miCopy.setText(lang.get("copy"));
			miPaste.setText(lang.get("paste"));
			miCut.setText(lang.get("cut"));
			miDelete.setText(lang.get("delete"));
			miSelectAll.setText(lang.get("selectall"));
			miEditSelection.setText(lang.get("editselection"));
			miNewCircuit.setText(lang.get("newcircuit"));
			miDeleteCircuit.setText(lang.get("deletecircuit"));
			miLibraries.setText(lang.get("libraries"));
			miStartStop.setText(lang.get("simulationrunning"));
			miReset.setText(lang.get("reset"));
			miActiveSimulations.setText(lang.get("activesimulations"));
			miMaxmise.setText(lang.get("maximise"));
			miMinimise.setText(lang.get("minimise"));
			miToolbarLocation.setText(lang.get("toolbarlocation"));
			miToolbarLocationOptions[0].setText(lang.get("left"));
			miToolbarLocationOptions[1].setText(lang.get("right"));
			miTutorial.setText(lang.get("tutorial"));
			miDocumentation.setText(lang.get("documentation"));
			miAbout.setText(lang.get("about"));
			tabbedPane.setTitleAt(0, lang.get("componentstree"));
			tabbedPane.setTitleAt(1, lang.get("simulationtree"));
			componentsRoot.setUserObject(" " + lang.get("components") + " ");
			circuitsRoot.setUserObject(" " + lang.get("circuits") + " ");
		});
	}

	public void setVisible(boolean val) {
		SwingUtilities.invokeLater(() -> frame.setVisible(val));
	}

	public int showExitPopup() {
		String[] options = { lang.get("save"), lang.get("discard"), lang.get("cancel") };
		AtomicInteger nOption = new AtomicInteger(JOptionPane.CLOSED_OPTION);
		try {
			SwingUtilities.invokeAndWait(() ->
					nOption.set(JOptionPane.showOptionDialog(frame, lang.get("exitquestion"), lang.get("confirm"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]))
			);
		} catch (Exception e) { e.printStackTrace(); };
		int n = nOption.get();
		return (n == 2 || n == JOptionPane.CLOSED_OPTION ? ExitDialogResult.EXIT_CANCEL : (n == 0 ? ExitDialogResult.EXIT_SAVE : ExitDialogResult.EXIT_DISCARD));
	}

	public void close() {
		SwingUtilities.invokeLater(() -> {
			frame.setVisible(false);
			circuitPanel.removeAll();
			frame.dispose();
		});
	}

	public void setActiveSimulation(boolean active) {
		activeSimulation = active;
		SwingUtilities.invokeLater(() -> miStartStop.setState(activeSimulation));
	}

	public JPanel getCircuitPanel() {
		return circuitPanel;
	}
	
	// Handle all JMenuItem clicks
	private class MenuEventHandler implements ActionListener {
		@Override public void actionPerformed(ActionEvent e) {
			if (e.getSource() == miNew) listener.onUserEvent(UserInputListener.Type.NEW, 0, null);
			if (e.getSource() == miOpen) listener.onUserEvent(UserInputListener.Type.OPEN, 0, null);
			for (JMenuItem item : miOpenRecentFiles) if (e.getSource() == item) {
				listener.onUserEvent(UserInputListener.Type.OPEN, 0, e.getActionCommand());
				break;
			}
			if (e.getSource() == miSave) listener.onUserEvent(UserInputListener.Type.SAVE, 0, null);
			if (e.getSource() == miSaveAs) listener.onUserEvent(UserInputListener.Type.SAVE, 1, null);
			if (e.getSource() == miExport) listener.onUserEvent(UserInputListener.Type.EXPORT, 0, null);
			if (e.getSource() == miSettings) listener.onUserEvent(UserInputListener.Type.SETTINGS, 0, null);
			if (e.getSource() == miExit) windowEventHandler.windowClosing(null);
			if (e.getSource() == miUndo) listener.onUserEvent(UserInputListener.Type.UNDO, 0, null);
			if (e.getSource() == miRedo) listener.onUserEvent(UserInputListener.Type.REDO, 0, null);
			if (e.getSource() == miRedoAll) listener.onUserEvent(UserInputListener.Type.REDO, 1, null);
			if (e.getSource() == miCopy) listener.onUserEvent(UserInputListener.Type.COPY, 0, null);
			if (e.getSource() == miPaste) listener.onUserEvent(UserInputListener.Type.PASTE, 0, null);
			if (e.getSource() == miCut) {
				listener.onUserEvent(UserInputListener.Type.COPY, 0, null);
				listener.onUserEvent(UserInputListener.Type.DELETE, 0, null);
			}
			if (e.getSource() == miDelete) listener.onUserEvent(UserInputListener.Type.DELETE, 0, null);
			if (e.getSource() == miSelectAll) listener.onUserEvent(UserInputListener.Type.SELECTALL, 0, null);
			if (e.getSource() == miEditSelection) listener.onUserEvent(UserInputListener.Type.EDITSEL, 0, null);
			if (e.getSource() == miNewCircuit) listener.onUserEvent(UserInputListener.Type.NEWCIRCUIT, 0, null);
			if (e.getSource() == miDeleteCircuit) listener.onUserEvent(UserInputListener.Type.DELETECIRCUIT, 0, null);
			if (e.getSource() == miLibraries) listener.onUserEvent(UserInputListener.Type.LIBRARIES, 0, null);
			if (e.getSource() == miReset) listener.onUserEvent(UserInputListener.Type.RESET, 0, null);
			if (e.getSource() == miStartStop) listener.onUserEvent(UserInputListener.Type.STARTSTOP, activeSimulation ? 1 : 0, null);
			for (JMenuItem item : miActiveSimulationsOptions) if (e.getSource() == item) {
				listener.onUserEvent(UserInputListener.Type.ACTIVESIMULATION, 0, e.getActionCommand());
				break;
			}
			if (e.getSource() == miMinimise) {
				SwingUtilities.invokeLater(() -> frame.setState(JFrame.ICONIFIED));
			}
			if (e.getSource() == miMaxmise) {
				SwingUtilities.invokeLater(() -> {
					frame.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
					frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				});
			}
			if (e.getSource() == miTutorial) {
				Page root = new Page();
				Page exroot = new Page();
				Page ch1 = new Page(), ch2 = new Page(), ch3 = new Page();
				ch1.setName("ch1");
				ch2.setName("ch2");
				ch3.setName("ch3");
				root.setName("root");
				exroot.setName("exroot");
				root.add(ch1).add(ch2);
				exroot.add(ch3);
				PagedTreeWindow p = new PagedTreeWindow( new Page[] {root, exroot}, "tutorial");
				p.setVisible(true);
				
			}
			if (e.getSource() == miDocumentation) {
			
			}
			if (e.getSource() == miAbout) {
				AboutWindow about = new AboutWindow();
				about.setContent(aboutText);
				about.setVisible(true);
			}
		}
	}
	
	
	// Handle window closing button
	private class WindowEventHandler implements WindowListener {
		@Override public void windowClosing(WindowEvent e) {
			listener.onUserEvent(UserInputListener.Type.EXIT, 0, null);
		}
		@Override public void windowClosed(WindowEvent e) {
		
		}
		@Override public void windowOpened(WindowEvent e) {
		
		}
		@Override public void windowIconified(WindowEvent e) {
		
		}
		@Override public void windowDeiconified(WindowEvent e) {

		}
		@Override public void windowActivated(WindowEvent e) {

		}
		@Override public void windowDeactivated(WindowEvent e) {

		}
	}
	
	// Handle tree cell drawing
	private class MyTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
		private Icon closed = UIManager.getIcon("Tree.closedIcon");
		private Icon open = UIManager.getIcon("Tree.openIcon");
		private Icon leaf = UIManager.getIcon("Tree.leafIcon");
		@Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean bSelected, boolean bExpanded, boolean bLeaf, int iRow, boolean bHasFocus) {
			Icon nodeIcon = treeIcon.get(value);
			nodeIcon = (nodeIcon != null ? nodeIcon : (bLeaf ? leaf : (bExpanded ? open : closed)));
			setIcon(nodeIcon);
			setBackgroundSelectionColor(bLeaf ? Color.LIGHT_GRAY : Color.WHITE);
			setBorderSelectionColor(Color.BLACK);
			setTextSelectionColor(Color.BLACK);
			return super.getTreeCellRendererComponent(tree, value, bSelected, bExpanded, bLeaf, iRow, bHasFocus);
		}
	}
	
	// Handle component tree events
	private class SelectTreeSelectionListener implements TreeSelectionListener {
		@Override public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectTree.getLastSelectedPathComponent();
			if (node != null) {
			
			}
		}
	}
	
	private class SimulationTreeSelectionListener implements TreeSelectionListener {
		@Override public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)simulationTree.getLastSelectedPathComponent();
			if (node != null) {
			
			}
		}
	}
}
