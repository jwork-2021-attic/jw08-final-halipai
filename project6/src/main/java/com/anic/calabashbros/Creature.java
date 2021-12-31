package com.anic.calabashbros;

import java.awt.Color;

public class Creature extends Thing {

    Creature(Color color, char glyph, World world) {
        super(color, glyph, world);
    }

    public void go(int x, int y){
        return;
    }

    public boolean isSuccess(){
        return false;
    }

    public void getAttacked(){

    }
}
