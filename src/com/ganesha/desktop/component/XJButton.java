package com.ganesha.desktop.component;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.slf4j.LoggerFactory;

import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.desktop.component.xcomponentutils.XComponentUtils;

public class XJButton extends JButton implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJButton() {
		this(null);
	}

	public XJButton(String text) {
		super(text);
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
		setBackground(BTN_BG);
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parent = XComponentUtils
						.getParentWindowOf(XJButton.this);
				String parentName = parent.getClass().getName();
				LoggerFactory.getLogger(Loggers.BUTTON).trace(
						"Button " + parentName + "_" + getText()
								+ " is clicked");
			}
		});
	}

	@Override
	protected void fireActionPerformed(ActionEvent event) {
		/*
		 * Nyontek dari AbstractButton.fireActionPerformed(); Ubah order
		 * loopingnya supaya execute listener yang pertama didaftaring. Sama
		 * buang skip listener di-index 0 karena ga tau dari mana datengnya.
		 */
		Object[] listeners = listenerList.getListenerList();
		ActionEvent e = null;
		for (int i = 2; i < listeners.length; i += 2) {
			if (listeners[i] == ActionListener.class) {
				if (e == null) {
					String actionCommand = event.getActionCommand();
					if (actionCommand == null) {
						actionCommand = getActionCommand();
					}
					e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
							actionCommand, event.getWhen(),
							event.getModifiers());
				}
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}
}
