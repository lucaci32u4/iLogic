package com.lucaci32u4.UI;

import com.lucaci32u4.main.LanguagePack;
import com.lucaci32u4.util.SimpleEventQueue;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.atomic.AtomicInteger;

public class MainWindow {
	public final static int EXIT_SAVE = 0b11;
	public final static int EXIT_DISCARD = 0b10;
	public final static int EXIT_CANCEL = 0b00;
	
	public static class Event {
		public enum Type {
			NEW, OPEN, SAVE, EXPORT, SETTINGS, EXIT, UNDO, REDO, COPY, PASTE, DELETE, SELECTALL, EDITSEL, NEWCIRCUIT, DELETECIRCUIT, LIBRARIES, STARTSTOP, RESET, ACTIVESIMULATION, TUTORIAL, DOCUMENTATION,
		}
		public Type subject;
		public int param1;
		public String param2;
		public Event(Type subject, int param1, String param2) {
			this.subject = subject;
			this.param1 = param1;
			this.param2 = param2;
		}
		public Event(Type subject) {
			this(subject, 0, null);
		}
		public Event(Type subject, int param1) {
			this(subject, param1, null);
		}
		public Event(Type subject, String param2) {
			this(subject, 0, param2);
		}
	}
	private LanguagePack lang;
	private JFrame frame;
	private JPanel contentPanel;
	private JToolBar topToolBar, bottomToolBar;
	private JSplitPane splitPaneVertical;
	private JPanel circuitPanel;
	private JSplitPane splitPaneHorizontal;
	private JTabbedPane tabbedPane;
	private JTree selectTree, simulationTree;
	private DefaultMutableTreeNode selectTreeRoot, simulationTreeRoot;
	private DefaultMutableTreeNode circuitsRoot;
	private DefaultMutableTreeNode componentsRoot;
	private JPanel propertiesPanel;
	private MenuEventHandler menuEventHandler;
	private WindowEvenHandler windowEvenHandler;
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
	private SimpleEventQueue<Event> listener;
	
	private boolean activeSimulation;
	
	public MainWindow(@NotNull SimpleEventQueue<MainWindow.Event> listener) {
		this.listener = listener;
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
				for (JMenuItem item : miToolbarLocationOptions) item.addActionListener(menuEventHandler);
				miStartStop.setState(activeSimulation);
				windowEvenHandler = new WindowEvenHandler();
				frame.addWindowListener(windowEvenHandler);
				frame.setJMenuBar(menuBar);
				frame.setSize(1200, 600);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
				simulationTree.setRootVisible(false);
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
			componentsRoot.setUserObject(lang.get("components"));
			circuitsRoot.setUserObject(lang.get("circuits"));
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
		return (n == 2 || n == JOptionPane.CLOSED_OPTION ? EXIT_CANCEL : (n == 0 ? EXIT_SAVE : EXIT_DISCARD));
	}
	public void close() {
		SwingUtilities.invokeLater(() -> { frame.setVisible(false); frame.dispose(); });
	}
	public void setActiveSimulation(boolean active) {
		SwingUtilities.invokeLater(() -> { activeSimulation = active; miStartStop.setState(activeSimulation); });
	}
	
	// Handle all JMenuItem clicks
	private class MenuEventHandler implements ActionListener {
		@Override public void actionPerformed(ActionEvent e) {
			if (e.getSource() == miNew) listener.produce(new Event(Event.Type.NEW));
			if (e.getSource() == miOpen) listener.produce(new Event(Event.Type.OPEN));
			for (JMenuItem item : miOpenRecentFiles) if (e.getSource() == item) {
				listener.produce(new Event(Event.Type.OPEN, item.getActionCommand()));
				break;
			}
			if (e.getSource() == miSave) listener.produce(new Event(Event.Type.SAVE));
			if (e.getSource() == miSaveAs) listener.produce(new Event(Event.Type.SAVE, e.getActionCommand()));
			if (e.getSource() == miExport) listener.produce(new Event(Event.Type.EXPORT));
			if (e.getSource() == miSettings) listener.produce(new Event(Event.Type.SETTINGS));
			if (e.getSource() == miExit) windowEvenHandler.windowClosing(null);
			if (e.getSource() == miUndo) listener.produce(new Event(Event.Type.UNDO));
			if (e.getSource() == miRedo) listener.produce(new Event(Event.Type.REDO, 0));
			if (e.getSource() == miRedoAll) listener.produce(new Event(Event.Type.REDO, 1));
			if (e.getSource() == miCopy) listener.produce(new Event(Event.Type.COPY));
			if (e.getSource() == miPaste) listener.produce(new Event(Event.Type.PASTE));
			if (e.getSource() == miCut) {
				listener.produce(new Event(Event.Type.COPY));
				listener.produce(new Event(Event.Type.DELETE));
			}
			if (e.getSource() == miDelete) listener.produce(new Event(Event.Type.DELETE));
			if (e.getSource() == miSelectAll) listener.produce(new Event(Event.Type.SELECTALL));
			if (e.getSource() == miEditSelection) listener.produce(new Event(Event.Type.EDITSEL));
			if (e.getSource() == miNewCircuit) listener.produce(new Event(Event.Type.NEWCIRCUIT));
			if (e.getSource() == miDeleteCircuit) listener.produce(new Event(Event.Type.DELETECIRCUIT));
			if (e.getSource() == miLibraries) listener.produce(new Event(Event.Type.LIBRARIES));
			if (e.getSource() == miReset) listener.produce(new Event(Event.Type.RESET));
			if (e.getSource() == miStartStop) listener.produce(new Event(Event.Type.STARTSTOP, activeSimulation ? 1 : 0));
			for (JMenuItem item : miActiveSimulationsOptions) if (e.getSource() == item) {
				listener.produce(new Event(Event.Type.ACTIVESIMULATION, item.getActionCommand()));
				break;
			}
			if (e.getSource() == miMinimise) {
				SwingUtilities.invokeLater(() -> frame.setState(JFrame.ICONIFIED));
			}
			if (e.getSource() == miMaxmise) {
				SwingUtilities.invokeLater(() -> { frame.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()); frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);});
			}
			if (e.getSource() == miTutorial) listener.produce(new Event(Event.Type.TUTORIAL));
			if (e.getSource() == miDocumentation) listener.produce(new Event(Event.Type.DOCUMENTATION));
		}
	}
	
	
	// Handle window closing button
	private class WindowEvenHandler implements WindowListener {
		@Override
		public void windowClosing(WindowEvent e) {
			listener.produce(new Event(Event.Type.EXIT, 0, null));
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}
	}
}
