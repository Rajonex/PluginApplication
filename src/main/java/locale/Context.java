package locale;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;


public class Context {

    private final ArrayList<ContextChangeListener> listeners
        = new ArrayList<ContextChangeListener>();

	private Locale locale;
	private ResourceBundle bundle;
	private String baseName;

	public Context(String baseName) {
		this.baseName = baseName;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}


	public Locale getLocale() {
		return locale;
	}


	public void setLocale(Locale locale) {
		if (locale.equals(this.locale)) {
			return;
		}

		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, this.getClass().getClassLoader());
		if (bundle == null) {
			throw new IllegalArgumentException("No resource bundle for: "
						+ locale.getLanguage());
		}
		this.locale = locale;
		this.bundle = bundle;

		fireContextChangedEvent();
	}


    public synchronized void addContextChangeListener(ContextChangeListener listener) {
        listeners.add(listener);
    }


	private void fireContextChangedEvent() {
        final Runnable dispatcher = new Runnable() {
            public void run() {
                synchronized (Context.this) {
                    for (Iterator<ContextChangeListener> i = listeners.iterator();i.hasNext();) {
                        i.next().contextChanged();
                    }
                }
            }
        };

		Platform.runLater(dispatcher);
	}
}
