package com.anic.calabashbros;

import java.awt.Color;

public class BigGoblin extends Creature{

    public int lifeValue;
    public boolean exist;

    public BigGoblin(World world) {
        super(new Color(255, 165, 0), (char) 64, world);
        this.exist = true;
        this.lifeValue = 3;
    }

    public BigGoblin(World world, int value) {
        super(new Color(255, 165, 0), (char) 64, world);
        this.exist = true;
        this.lifeValue = value;
    }

    public int getLiveValue(){
        return lifeValue;
    }

    @Override
    public void getAttacked(){
        this.lifeValue -= 1;
        if(this.lifeValue == 0) this.exist = false;
    }

    @Override
    public boolean isExistent(){
        return this.exist;
    }


    @Override
    public void go(int dx, int dy){
        int x = this.getX();
        int y = this.getY();
        if(this.world.inTheWorld(x+dx, y-dy)){
            Thing p = this.world.get(x+dx, y-dy);
            if(p instanceof Calabash){
                Calabash pp = (Calabash) p;
                pp.getAttacked();
                return;
            }
            this.moveTo(x+dx, y-dy);
            this.world.put(new Floor(this.world), x, y);
            return;
        }
        return;
    }
}