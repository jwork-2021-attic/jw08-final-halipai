package com.anic.screen;

import com.anic.calabashbros.World;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;


public class ClientScreen implements Screen {

    public char[][] worldGlyph=new char[World.WIDTH][World.HEIGHT];
    public Color[][] worldColor=new Color[World.WIDTH][World.HEIGHT];

    public char[][] glyph = new char[3][20];
    public Color[][] color = new Color[3][20];
    public ClientScreen(){

    }

    @Override
    public int displayOutput(AsciiPanel terminal) {
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {

                //terminal.write(worldGlyph[x][y], x, y, worldColor[x][y]);
                terminal.write(worldGlyph[x][y], x+1, y+7, worldColor[x][y]);
            }
        }
        for(int y = 2; y < 5; y++){
            for(int x = 2; x < 2+20; x++){
                terminal.write(glyph[y-2][x-2], x, y, color[y-2][x-2]);
            }
        }
        return 0;
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }

    @Override
    public void displayBegin(AsciiPanel terminal, int k) {
    }

    @Override
    public void displayOver(AsciiPanel terminal) {
    }

    @Override
    public void displayFail(AsciiPanel terminal) {
    }

    @Override
    public boolean over() {
        return false;
    }

    @Override
    public boolean fail() {
        return false;
    }

    @Override
    public void getScreen(byte[] out, int num) {
    }

}