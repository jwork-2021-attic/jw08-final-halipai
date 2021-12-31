package com.anic.screen;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public interface Screen {

    public int displayOutput(AsciiPanel terminal);

    public Screen respondToUserInput(KeyEvent key);

    public void displayBegin(AsciiPanel terminal, int k);

    public void displayOver(AsciiPanel terminal);

    public void displayFail(AsciiPanel terminal);

	public boolean over();

    public boolean fail();

    public void getScreen(byte[] out, int num);
}
