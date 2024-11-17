package io.github.natank25.epitechutils.module;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LibMyDocumentActionListener implements DocumentListener {
	private final Runnable checkValid;
	
	public LibMyDocumentActionListener(Runnable checkValid) {
		this.checkValid = checkValid;
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		checkValid.run();
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		checkValid.run();
	}
	
	@Override
	public void removeUpdate(DocumentEvent e) {
		checkValid.run();
	}
}
