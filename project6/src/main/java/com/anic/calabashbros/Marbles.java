package com.anic.calabashbros;

import java.awt.Color;

public class Marbles extends Thing {

    public boolean exist;
    public String direction;
    public Creature master;

    public Marbles(World world, Color color, Creature master) {
        super(color, (char) 7, world);
        this.exist = true;
        this.direction = "";
        this.master = master;
    }

    public Marbles(World world, Color color, int direct) {
        super(color, (char) 7, world);
        this.exist = true;
        this.direction = "";
        if(direct == 1) direction = "attackleft";
        if(direct == 2) direction = "attackup";
        if(direct == 3) direction = "attackright";
        if(direct == 4) direction = "attackdown";
    }

    public void setDirection(String direction){
        this.direction = direction;
    }

    public int getDirection(){
        if(direction == "attackleft")       return 1;
        else if(direction == "attackup")    return 2;
        else if(direction == "attackright") return 3;
        else if(direction == "attackdown")  return 4;
        return 0;
    }

    public void launch(){
        switch(this.direction){
            case "attackleft":
                go(-1,0);
                break;
            case "attackup":
                go(0,1);
                break;
            case "attackright":
                go(1,0);
                break;
            case "attackdown":
                go(0,-1);
                break;
        }
    }

    public void go(int dx, int dy){
        int x = this.getX();
        int y = this.getY();
        if(this.world.inTheWorld(x+dx, y-dy)){
            Thing p = this.world.get(x+dx, y-dy);
            if(p instanceof Floor){
                this.moveTo(x+dx, y-dy);
                this.world.put(new Floor(this.world), x, y);
                return;
            }
            else if(p instanceof Goblin){
                Goblin g = (Goblin) p;
                this.world.put(new Floor(this.world), x, y);
                this.world.put(new Floor(this.world), x+dx, y-dy);
                g.getAttacked();
                if(master instanceof Calabash){
                    Calabash m = (Calabash) master;
                    m.getMoreLife();
                }
            }
            else if(p instanceof BigGoblin){
                BigGoblin g = (BigGoblin) p;
                this.world.put(new Floor(this.world), x, y);
                g.getAttacked();
                if(!g.isExistent() && master instanceof Calabash){
                    Calabash m = (Calabash) master;
                    m.getMoreLife();
                }
            }
            else if(p instanceof Calabash){
                Calabash g = (Calabash) p;
                this.world.put(new Floor(this.world), x, y);
                g.getAttacked();
                if(!g.isExistent() && master instanceof Calabash){
                    Calabash m = (Calabash) master;
                    m.audit(g.getDetail());
                    g.recovery();
                }
            }
        }
        this.exist = false;
    }

    @Override
    public boolean isExistent(){
        return this.exist;
    }
}