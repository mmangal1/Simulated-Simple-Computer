package project;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class ControlPanel implements Observer {
	private ViewsOrganizer view = new ViewsOrganizer();
	private JMenuItem stepButton = new JMenuItem("Step");
	private JMenuItem clearButton = new JMenuItem("Clear");
	private JMenuItem runButton = new JMenuItem("Run/Pause");
	private JMenuItem reloadButton = new JMenuItem("Reload");
	
	public ControlPanel(ViewsOrganizer gui) {
		view = gui;
		gui.addObserver(this);
	}
	
	public JComponent createControlDisplay(){
		//System.out.println("hello");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		stepButton.addActionListener(arg -> view.step());
		clearButton.addActionListener(arg -> view.clearJob());
		runButton.addActionListener(arg -> view.toggleAutoStep());
		reloadButton.addActionListener(arg -> view.reload());
		panel.add(stepButton);
		panel.add(clearButton);
		panel.add(runButton);
		panel.add(reloadButton);
		JSlider slider = new JSlider(5, 1000);
		slider.addChangeListener(e -> view.setPeriod(slider.getValue()));
		panel.add(slider);
		return panel;
	}

	@Override
	public void update(Observable o, Object arg){
		stepButton.setEnabled(view.getCurrentState().getStepActive());
		clearButton.setEnabled(view.getCurrentState().getClearActive());
		runButton.setEnabled(view.getCurrentState().getRunPauseActive());
		reloadButton.setEnabled(view.getCurrentState().getReloadActive());		
	}
}
