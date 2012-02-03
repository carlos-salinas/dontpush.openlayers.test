package com.thingtrack.dontpush.openlayers.test;

import java.util.Date;
import java.util.HashSet;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;


public class TestWindow extends Window {
	public static HashSet<TestWindow> openWindows = new HashSet<TestWindow>();

	private CssLayout messages;

	private Label label;

	public TestWindow() {

		label = new Label("Hello Vaadin user. This is a demo app for Atmosphere powered DontPush implementation.");
		addComponent(label);
        final ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setValue(0);

        /*
         * Don't poll, don't push.
         */
        progressIndicator.setPollingInterval(9990000);

        addComponent(progressIndicator);

        Button b = new Button("a Button");
        addComponent(b);
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                event.getButton().getWindow().showNotification("clicked");
            }
        });

        Thread thread = new Thread() {
            float f = 0;

            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    f += 0.05;
                    synchronized (getApplication()) {
                        progressIndicator.setValue(new Float(f));
                    }
                    if (f > 1) {
                        break;
                    }
                    // Don't push, the magic will just happen.
                }
            }
        };

        thread.start();
        
        messages = new CssLayout();
        messages.setWidth("600px");
        
        final TextField textField = new TextField("Post message");
        
        Button button = new Button("Post to all users");
        
        button.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				String msg = (String) textField.getValue();
				broadcast(msg);
			}
		});
        
        addComponent(textField);
        addComponent(button);
        addComponent(messages);
        
		
	}
	
	@Override
	public void attach() {
		super.attach();
		label.setValue(label.getValue() + " Window name/id: " + getName());
        register(this);
	}
	
	@Override
	public void detach() {
		super.detach();
		unregister(this);
	}
	
	static void register(TestWindow app) {
		synchronized (openWindows) {
			openWindows.add(app);
			System.err.println("Registered " + app.getName());
		}
	}
	
	static void unregister(TestWindow app) {
		synchronized (openWindows) {
			openWindows.remove(app);
			System.err.println("Unregistered " + app.getName());
		}
	}
	
	static void broadcast(String msg) {
		TestWindow[] windows;
		synchronized (openWindows) {
			windows = new TestWindow[openWindows.size()];
			openWindows.toArray(windows);
		}
		for (int i = 0; i < windows.length; i++) {
			TestWindow w = windows[i];
			System.err.println("Sending message " + msg + " to " + w.getName());
			synchronized (w) {
				w.messages.requestRepaintRequests();
				w.addMessage(msg);
			}
		}
	}


	public void addMessage(String msg) {
		messages.addComponent(new Label(new Date() + " New message:" + msg));
		if(messages.getComponentCount() > 4) {
			messages.removeComponent(messages.getComponentIterator().next());
		}
	}


}
