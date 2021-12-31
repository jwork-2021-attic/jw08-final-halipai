package com.anic.calabashbros;

import java.awt.Color;

public class Calabash extends Creature{

    public boolean ownKey, ownBoat, ownCane,success;
    public boolean exist;
    public int lifeValue;
    public boolean rightHouse,rightWater, rightCarpet;
    public Color color;

    public Calabash(Color color, World world) {
        super(color, (char) 12, world);
        this.color = color;
        this.ownKey = false;
        this.ownBoat = false;
        this.ownCane = false;
        this.success = false;
        this.exist = true;
        this.rightHouse = true;
        this.rightWater = false;
        this.rightCarpet = false;
        this.lifeValue = 3;
    }

    public Calabash(Color color, World world, int detail, int value){
        super(color, (char) 12, world);
        this.color = color;
        this.ownKey = (detail & 1<<0) == 1? true : false;
        this.ownBoat = (detail & 1<<1) == 1<<1? true : false;
        this.ownCane = (detail & 1<<2) == 1<<2? true : false;
        this.success = (detail & 1<<3) == 1<<3? true : false;
        this.exist = (detail & 1<<4) == 1<<4? true : false;
        this.rightHouse = (detail & 1<<5) == 1<<5? true : false;
        this.rightWater = (detail & 1<<6) == 1<<6? true : false;
        this.rightCarpet = (detail & 1<<7) == 1<<7? true : false;
        this.lifeValue = value;
    }

    public int getDetail(){
        int detail = 0;
        if(ownKey == true)  detail |= 1<<0;
        if(ownBoat == true) detail |= 1<<1;
        if(ownCane == true) detail |= 1<<2;
        if(success == true) detail |= 1<<3;
        if(exist == true)   detail |= 1<<4;
        if(rightHouse == true)  detail |= 1<<5;
        if(rightWater == true)  detail |= 1<<6;
        if(rightCarpet == true) detail |= 1<<7;

        return detail;
    }
    public void audit(int de){
        int detail0 = getDetail();
        detail0 |= de;
        this.ownKey = (detail0 & 1<<0) == 1? true : false;
        this.ownBoat = (detail0 & 1<<1) == 1<<1? true : false;
        this.ownCane = (detail0 & 1<<2) == 1<<2? true : false;
    }

    public int getLiveValue(){
        return lifeValue;
    }

    @Override
    public void getAttacked(){
        this.lifeValue -= 1;
        if(this.lifeValue == 0){
            this.exist = false;

            Floor floor = new Floor(world);
            world.put(floor, this.getX(), this.getY());
        }
    }

    public void recovery(){
        this.ownKey = false;
        this.ownBoat = false;
        this.ownCane = false;
    }
    public void getMoreLife(){
        this.lifeValue += 1;
    }

    @Override
    public boolean isSuccess(){
        return this.success;
    }

    public void getBoat(){
        this.ownBoat = true;
    }

    public void getCane(){
        this.ownCane = true;
    }

    public void getKey(){
        this.ownKey = true;
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
            if(p instanceof Flint)
                return;
            if(p instanceof Water){
                if(this.ownBoat){
                    this.moveTo(x+dx, y-dy);
                    if(rightWater) this.world.put(new Water(this.world), x, y);
                    else this.world.put(new Floor(this.world), x, y);
                    rightWater = true;
                }
                return;
            }
            if(p instanceof Portal){
                if(this.ownKey){
                    this.moveTo(x+dx, y-dy);
                    this.world.put(new Floor(this.world), x, y);
                    this.success = true;
                }
                return;
            }

            if(p instanceof Carpet){
                if(this.ownCane){
                    this.moveTo(x+dx, y-dy);
                    this.world.put(new Floor(this.world), x, y);
                    rightCarpet = true;
                }
                return;
            }
            if(p instanceof Goblin){
                this.getAttacked();
                return;
            }
            if(p instanceof Boat)   this.getBoat();
            if(p instanceof EnergyFood) this.getMoreLife();
            if(p instanceof Mine) this.getAttacked();
            if(p instanceof Key)    this.getKey();
            if(p instanceof Cane)   this.getCane();

            this.moveTo(x+dx, y-dy);

            if(rightHouse){
                this.world.put(new House(this.world), x, y);
                if(!(p instanceof House))   rightHouse = false;
                return;
            }
            if(rightWater){
                this.world.put(new Water(this.world), x, y);
                rightWater = false;
                return;
            }
            if(rightCarpet){
                this.world.put(new Carpet(this.world), x, y);
                if(!(p instanceof Carpet))  rightCarpet = false;
                return;
            }
            if(p instanceof House){
                rightHouse = true;
            }

            this.world.put(new Floor(this.world), x, y);
        }
        return;
    }
}
