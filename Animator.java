package project;
import javax.swing.Timer;

public class Animator {

	private static final int TICK = 500;
	private boolean autoStepOn = false;
	private Timer timer;
	private ViewsOrganizer view;
	
	public Animator(ViewsOrganizer view) {
		super();
		this.view = view;
	}

	public boolean isAutoStepOn() {
		return autoStepOn;
	}

	public void setAutoStepOn(boolean autoStepOn) {
		this.autoStepOn = autoStepOn;
	}
	
	void toggleAutoStep(){
		if(autoStepOn)
			autoStepOn = false;
		else
			autoStepOn = true;
	}
	
	void setPeriod(int period){
		timer.setDelay(period);
	}
	
	public void start() {
		timer = new Timer(TICK, e -> {if(autoStepOn) view.step();});
		timer.start();
	}
	

}
