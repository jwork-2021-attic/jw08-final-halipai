package com.anic.calabashbros;

import java.awt.Color;

public class Goblin extends Creature{

    public boolean exist;

    public Goblin(World world, Color color) {
        super(color, (char) 2, world);
        this.exist = true;
    }

    public Goblin(World world) {
        super(new Color(105, 105, 105), (char) 2, world);
        this.exist = true;
    }

    @Override
    public void getAttacked(){
        this.exist = false;
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