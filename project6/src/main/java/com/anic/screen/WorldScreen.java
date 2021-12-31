package com.anic.screen;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;
import com.anic.calabashbros.*;

import com.anic.maze_generator.MazeGenerator;

import asciiPanel.AsciiPanel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorldScreen implements Screen {

    public World world;
    private Calabash bro;
    public AsciiPanel terminal;
    MazeGenerator mazeGenerator;
    private int score;
    public ArrayList<Creature> creatures;
    public ExecutorService exec;
    public Lock lock = new ReentrantLock();
    public String action = "1", attackaction = "1", action0 = "1", attackaction0 = "1", action1 = "1", attackaction1 = "1";
    public static int over;
    public boolean all_offline;
    Calabash cala0, cala1;
    boolean instance0 = false, instance1 = false;
    public Byte[][] store = new Byte[3][20*4];

    public WorldScreen() {
        init();
    }
    public void setTerminal(AsciiPanel terminal){
        this.terminal = terminal;
    }
    public void getNewCala(int i){
        if(i >= 2)  return;
        if(i == 0){
            cala0 = new Calabash(new Color(0, 197,205), this.world);
            world.put(cala0, 1, 12);
            creatures.add(cala0);
            world.addCala(cala0);
            exec.execute(new creatureThread(cala0));
            instance0 = true;
        }
        else if(i == 1){
            cala1 = new Calabash(new Color(165, 42, 42), this.world);
            world.put(cala1, 0, 11);
            creatures.add(cala1);
            world.addCala(cala1);
            exec.execute(new creatureThread(cala1));
            instance1 = true;
        }
    }
    public void getMove(int i, int keyCode){
        System.out.println(i+" "+keyCode);
        if(i == 0){
            if(keyCode == 0x25){//left
                action0 = "left";
            }else if(keyCode == 0x26){//up
                action0 = "up";
            }else if(keyCode == 0x27){//right
                action0 = "right";
            }else if(keyCode == 0x28){//down
                action0 = "down";
            }else if(keyCode == 0x53){//s
                attackaction0 = "attackleft";
            }else if(keyCode == 0x45){//e
                attackaction0 = "attackup";
            }else if(keyCode == 0x46){//f
                attackaction0 = "attackright";
            }else if(keyCode == 0x44){//d
                attackaction0 = "attackdown";
            }
        }
        else if(i == 1){
            if(keyCode == 0x25){//left
                action1 = "left";
            }else if(keyCode == 0x26){//up
                action1 = "up";
            }else if(keyCode == 0x27){//right
                action1 = "right";
            }else if(keyCode == 0x28){//down
                action1 = "down";
            }else if(keyCode == 0x53){//s
                attackaction1 = "attackleft";
            }else if(keyCode == 0x45){//e
                attackaction1 = "attackup";
            }else if(keyCode == 0x46){//f
                attackaction1 = "attackright";
            }else if(keyCode == 0x44){//d
                attackaction1 = "attackdown";
            }
        }
    }
    public void init(){
        all_offline = false;
        world = new World();
        mazeGenerator = new MazeGenerator(World.WIDTH, World.HEIGHT, world);

        exec = Executors.newCachedThreadPool();
        creatures = mazeGenerator.CreateCreature(exec);
        for (int i = 0; i < creatures.size(); i++) {
            exec.execute(new creatureThread(creatures.get(i)));
        }
        over = 0;
    }

    public void reInit(){
        all_offline = true;
        exec.shutdown();
        lock.lock();
        while (!exec.isTerminated()) {
            lock.unlock();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
        }
        lock.unlock();
        all_offline = false;
        world = new World();
        exec = Executors.newCachedThreadPool();
        loadWorld();
        loadLives();
        over = 0;
    }

    public void reInit(int i){
        all_offline = true;
        exec.shutdown();
        lock.lock();
        while (!exec.isTerminated()) {
            lock.unlock();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
        }
        lock.unlock();
        all_offline = false;
        world = new World();
        init();
        over = 0;
    }

    public boolean running(){
        return !all_offline;
    }

    public class marblesThread implements Runnable{
        public Marbles marbles;

        public marblesThread(Marbles marbles){
            this.marbles = marbles;
        }

        @Override
        public void run(){
            while (marbles.isExistent() && !all_offline){
                try {
                    lock.lock();
                    marbles.launch();
                } finally {
                    lock.unlock();
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                lock.lock();
                world.put(new Floor(world), marbles.getX(), marbles.getY());
                marbles = null;
                System.gc();
            } finally {
                lock.unlock();
            }
        }

    }

    public class creatureThread implements Runnable{
        public Creature creature;

        public creatureThread(Creature creature){
            this.creature = creature;
        }

        @Override
        public void run(){
            if(this.creature instanceof Goblin){
                this.creature = (Goblin)this.creature;
                try{
                    while(this.creature.isExistent() && !all_offline){
                        try{
                            lock.lock();
                            boolean direc[] ={false, false, false, false};
                            int x = this.creature.getX(), y = this.creature.getY();
                            if(x-1 >= 0 && (world.tiles[x-1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash))
                                direc[0] = true;
                            if(y-1 >= 15 && (world.tiles[x][y-1].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash))
                                direc[1] = true;
                            if(world.tiles[x+1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash)
                                direc[2] = true;
                            if(y+1 < World.HEIGHT && (world.tiles[x][y+1].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash))
                                direc[3] = true;
                            if(direc[0] == false && direc[1] == false && direc[2] == false && direc[3] == false){
                                this.creature.getAttacked();
                            }
                            else{
                                while(true){
                                    Random ran = new Random();
                                    int i = ran.nextInt(4);
                                    if(direc[i]){
                                        if(i == 0)  this.creature.go(-1, 0);
                                        else if(i == 1)  this.creature.go(0, 1);
                                        else if(i == 2)  this.creature.go(1, 0);
                                        else if(i == 3)  this.creature.go(0, -1);
                                        break;
                                    }
                                }
                            }
                        }finally{
                            lock.unlock();
                            try {
                                Random ran = new Random();
                                TimeUnit.MILLISECONDS.sleep(ran.nextInt(500));
                            } catch (InterruptedException e) {
                                System.out.println("get exception!");
                                e.printStackTrace();
                            }
                        }
                    }
                }finally{
                    try {
                        lock.lock();
                        world.put(new Floor(world), creature.getX(), creature.getY());
                        creature = null;
                        System.gc();
                    } finally {
                        lock.unlock();
                    }
                }
            }
            else if(this.creature instanceof Calabash){
                while(this.creature.isExistent() && !all_offline && this.creature.getColor().getRed() == 218){
                    try{
                        lock.lock();
                        if(this.creature.isSuccess()) break;
                        switch(action){
                            case "left":
                                this.creature.go(-1,0);
                                break;
                            case "up":
                                this.creature.go(0,1);
                                break;
                            case "right":
                                this.creature.go(1,0);
                                break;
                            case "down":
                                this.creature.go(0,-1);
                                break;

                        }
                        if(!attackaction.equals("1")){
                            Marbles m = new Marbles(world, this.creature.getColor(), (Calabash)this.creature);
                            world.addMarbles(m);
                            m.setDirection(attackaction);
                            int x = creature.getX(), y = creature.getY();
                            switch(attackaction){
                                case "attackleft":
                                    if(world.tiles[x-1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Goblin)
                                        world.put(m, x-1, y);
                                    break;
                                case "attackup":
                                    if(world.tiles[x][y-1].getThing() instanceof Floor|| world.tiles[x][y-1].getThing() instanceof Goblin)
                                        world.put(m, x, y-1);
                                    break;
                                case "attackright":
                                    if(world.tiles[x+1][y].getThing() instanceof Floor|| world.tiles[x+1][y].getThing() instanceof Goblin)
                                        world.put(m, x+1, y);
                                    break;
                                case "attackdown":
                                    if(world.tiles[x][y+1].getThing() instanceof Floor|| world.tiles[x][y+1].getThing() instanceof Goblin)
                                        world.put(m, x, y+1);
                                    break;
                                default: break;
                            }
                            exec.execute(new marblesThread(m));
                        }

                        action = "";
                        attackaction = "1";
                    }finally{
                        lock.unlock();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                while(this.creature.isExistent() && !all_offline && this.creature.getColor().getRed() == 0){
                    try{
                        lock.lock();
                        if(this.creature.isSuccess()) break;
                        switch(action0){
                            case "left":
                                this.creature.go(-1,0);
                                break;
                            case "up":
                                this.creature.go(0,1);
                                break;
                            case "right":
                                this.creature.go(1,0);
                                break;
                            case "down":
                                this.creature.go(0,-1);
                                break;

                        }
                        if(!attackaction0.equals("1")){
                            Marbles m = new Marbles(world, this.creature.getColor(), (Calabash)this.creature);
                            world.addMarbles(m);
                            m.setDirection(attackaction0);
                            int x = creature.getX(), y = creature.getY();
                            switch(attackaction0){
                                case "attackleft":
                                    if(world.tiles[x-1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Goblin)
                                        world.put(m, x-1, y);
                                    break;
                                case "attackup":
                                    if(world.tiles[x][y-1].getThing() instanceof Floor|| world.tiles[x][y-1].getThing() instanceof Goblin)
                                        world.put(m, x, y-1);
                                    break;
                                case "attackright":
                                    if(world.tiles[x+1][y].getThing() instanceof Floor|| world.tiles[x+1][y].getThing() instanceof Goblin)
                                        world.put(m, x+1, y);
                                    break;
                                case "attackdown":
                                    if(world.tiles[x][y+1].getThing() instanceof Floor|| world.tiles[x][y+1].getThing() instanceof Goblin)
                                        world.put(m, x, y+1);
                                    break;
                                default: break;
                            }
                            exec.execute(new marblesThread(m));
                        }

                        action0 = "";
                        attackaction0 = "1";
                    }finally{
                        lock.unlock();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                while(this.creature.isExistent() && !all_offline && this.creature.getColor().getRed() == 165){
                    try{
                        lock.lock();
                        if(this.creature.isSuccess()) break;
                        switch(action1){
                            case "left":
                                this.creature.go(-1,0);
                                break;
                            case "up":
                                this.creature.go(0,1);
                                break;
                            case "right":
                                this.creature.go(1,0);
                                break;
                            case "down":
                                this.creature.go(0,-1);
                                break;

                        }
                        if(!attackaction1.equals("1")){
                            Marbles m = new Marbles(world, this.creature.getColor(), (Calabash)this.creature);
                            world.addMarbles(m);
                            m.setDirection(attackaction1);
                            int x = creature.getX(), y = creature.getY();
                            switch(attackaction1){
                                case "attackleft":
                                    if(world.tiles[x-1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Goblin)
                                        world.put(m, x-1, y);
                                    break;
                                case "attackup":
                                    if(world.tiles[x][y-1].getThing() instanceof Floor|| world.tiles[x][y-1].getThing() instanceof Goblin)
                                        world.put(m, x, y-1);
                                    break;
                                case "attackright":
                                    if(world.tiles[x+1][y].getThing() instanceof Floor|| world.tiles[x+1][y].getThing() instanceof Goblin)
                                        world.put(m, x+1, y);
                                    break;
                                case "attackdown":
                                    if(world.tiles[x][y+1].getThing() instanceof Floor|| world.tiles[x][y+1].getThing() instanceof Goblin)
                                        world.put(m, x, y+1);
                                    break;
                                default: break;
                            }
                            exec.execute(new marblesThread(m));
                        }

                        action1 = "";
                        attackaction1 = "1";
                    }finally{
                        lock.unlock();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(all_offline){
                    creature = null;
                    System.gc();
                }
                else if(this.creature.isExistent()){
                    exec.shutdownNow();
                    over = 1;
                }
                else{
                    int calanumber = 0;
                    boolean goon = false;
                    for(int i = 0; i < creatures.size(); i++){
                        if(creatures.get(i) instanceof Calabash){
                            calanumber += 1;
                            if(creatures.get(i).isExistent()) goon= true;
                            break;
                        }
                    }
                    if(! goon){
                        displayFail(terminal);
                    }
                    over = -1;
                }
            }
            else if(this.creature instanceof BigGoblin){
                try{
                    while(this.creature.isExistent() && !all_offline){
                        try{
                            lock.lock();
                            boolean direc[] ={false, false, false, false};
                            int x = this.creature.getX(), y = this.creature.getY();
                            if(x-1 >= 0 && (world.tiles[x-1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash))
                                direc[0] = true;
                            if(y-1 >= 0 && (world.tiles[x][y-1].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash))
                                direc[1] = true;
                            if(world.tiles[x+1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash)
                                direc[2] = true;
                            if(y+1 < World.HEIGHT && (world.tiles[x][y+1].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Calabash))
                                direc[3] = true;
                            if(direc[0] == false && direc[1] == false && direc[2] == false && direc[3] == false){
                                this.creature.getAttacked();
                            }
                            else{
                                while(true){
                                    Random ran = new Random();
                                    int i = ran.nextInt(4);
                                    if(direc[i]){
                                        if(i == 0)  this.creature.go(-1, 0);
                                        else if(i == 1)  this.creature.go(0, 1);
                                        else if(i == 2)  this.creature.go(1, 0);
                                        else if(i == 3)  this.creature.go(0, -1);
                                        break;
                                    }
                                }
                            }
                            Marbles m = new Marbles(world, this.creature.getColor(), (BigGoblin)this.creature);
                            m.setDirection(attackaction);
                            x = creature.getX();
                            y = creature.getY();
                            int nowx = creatures.get(0).getX(), nowy = creatures.get(0).getY();
                            if(nowy > y-5 && nowy < y+5 && nowx < x && nowx > x-20){
                                m.setDirection("attackleft");
                                if(world.tiles[x-1][y].getThing() instanceof Floor|| world.tiles[x-1][y].getThing() instanceof Goblin)
                                    world.put(m, x-1, y);
                                world.addMarbles(m);
                                exec.execute(new marblesThread(m));
                            }
                            else if(nowy < y && nowx > x-10){
                                m.setDirection("attackup");
                                if(world.tiles[x][y-1].getThing() instanceof Floor|| world.tiles[x][y-1].getThing() instanceof Goblin)
                                    world.put(m, x, y-1);
                                world.addMarbles(m);
                                exec.execute(new marblesThread(m));
                            }
                            else if(nowy > y-5 && nowy < y+5 && nowx > x){
                                m.setDirection("attackright");
                                if(world.tiles[x+1][y].getThing() instanceof Floor|| world.tiles[x+1][y].getThing() instanceof Goblin)
                                    world.put(m, x+1, y);
                                world.addMarbles(m);
                                exec.execute(new marblesThread(m));
                            }
                            else if(nowy < y+5 && nowy > y && nowx > x-10){
                                m.setDirection("attackdown");
                                if(world.tiles[x][y+1].getThing() instanceof Floor|| world.tiles[x][y+1].getThing() instanceof Goblin)
                                    world.put(m, x, y+1);
                                world.addMarbles(m);
                                exec.execute(new marblesThread(m));
                            }
                        }finally{
                            lock.unlock();
                            try {
                                TimeUnit.MILLISECONDS.sleep(800);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }finally{
                    try {
                        lock.lock();
                        world.put(new Floor(world), creature.getX(), creature.getY());
                        creature = null;
                        System.gc();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    void saveWorld() {
        try {
            FileOutputStream tar = new FileOutputStream("map/world.txt");
            BufferedOutputStream stream = new BufferedOutputStream(tar);
            for (int i = 0; i < World.WIDTH; i++) {
                for (int j = 0; j < World.HEIGHT; j++) {
                    if(world.tiles[i][j].getThing() instanceof Boat){
                        stream.write(1);
                    }else if(world.tiles[i][j].getThing() instanceof Cane){
                        stream.write(2);
                    }else if(world.tiles[i][j].getThing() instanceof Carpet){
                        stream.write(3);
                    }else if(world.tiles[i][j].getThing() instanceof EnergyFood){
                        stream.write(4);
                    }else if(world.tiles[i][j].getThing() instanceof Floor){
                        stream.write(5);
                    }else if(world.tiles[i][j].getThing() instanceof Grass){
                        stream.write(6);
                    }else if(world.tiles[i][j].getThing() instanceof House){
                        stream.write(7);
                    }else if(world.tiles[i][j].getThing() instanceof Key){
                        stream.write(8);
                    }else if(world.tiles[i][j].getThing() instanceof Mine){
                        stream.write(9);
                    }else if(world.tiles[i][j].getThing() instanceof Portal){
                        stream.write(10);
                    }else if(world.tiles[i][j].getThing() instanceof Tree){
                        stream.write(11);
                    }else if(world.tiles[i][j].getThing() instanceof Wall){
                        stream.write(12);
                    }else if(world.tiles[i][j].getThing() instanceof Water){
                        stream.write(13);
                    }else{
                        stream.write(0);
                    }
                }
                stream.write('\n');
            }
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadWorld(){
        try {
            FileInputStream tar = new FileInputStream("map/world.txt");
            BufferedInputStream stream = new BufferedInputStream(tar);
            for (int i = 0; i < World.WIDTH; i++) {
                for (int j = 0; j < World.HEIGHT; j++) {
                    int info = stream.read();
                    if(info == 1){
                        world.put(new Boat(world), i, j);
                    }else if(info == 2){
                        world.put(new Cane(world), i, j);
                    }else if(info == 3){
                        world.put(new Carpet(world), i, j);
                    }else if(info == 4){
                        world.put(new EnergyFood(world), i, j);
                    }else if(info == 5){
                        world.put(new Floor(world), i, j);
                    }else if(info == 6){
                        world.put(new Grass(world), i, j);
                    }else if(info == 7){
                        world.put(new House(world), i, j);
                    }else if(info == 8){
                        world.put(new Key(world), i, j);
                    }else if(info == 9){
                        world.put(new Mine(world), i, j);
                    }else if(info == 10){
                        world.put(new Portal(world), i, j);
                    }else if(info == 11){
                        world.put(new Tree(world), i, j);
                    }else if(info == 12){
                        world.put(new Wall(world), i, j);
                    }else if(info == 13){
                        world.put(new Water(world), i, j);
                    }
                }
                stream.read();
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveLives(){
        try {
            FileOutputStream tar = new FileOutputStream("map/lives.txt");
            BufferedOutputStream stream = new BufferedOutputStream(tar);
            ArrayList<Calabash> calas = world.getCalabash();
            for (int i = 0; i < calas.size(); i++) {
                Calabash calabash = calas.get(i);
                if (!calabash.isExistent()) {
                    continue;
                }
                stream.write(0);
                stream.write(calabash.getColor().getRed());
                stream.write(calabash.getColor().getGreen());
                stream.write(calabash.getColor().getBlue());
                stream.write(calabash.getDetail());
                stream.write(calabash.getLiveValue());
                stream.write(calabash.getX());
                stream.write(calabash.getY());
                stream.write('\n');
            }

            ArrayList<Goblin> gobs = world.getGoblin();
            for (int i = 0; i < gobs.size(); i++) {
                Goblin gob = gobs.get(i);
                if (!gob.isExistent()) {
                    continue;
                }
                stream.write(1);
                stream.write(gob.getColor().getRed());
                stream.write(gob.getColor().getGreen());
                stream.write(gob.getColor().getBlue());
                stream.write(gob.getX());
                stream.write(gob.getY());
                stream.write('\n');
            }

            ArrayList<BigGoblin> bigGobs = world.getBigGoblin();
            for (int i = 0; i < bigGobs.size(); i++) {
                BigGoblin bigGob = bigGobs.get(i);
                if (!bigGob.isExistent()) {
                    continue;
                }
                stream.write(2);
                stream.write(bigGob.getLiveValue());
                stream.write(bigGob.getX());
                stream.write(bigGob.getY());
                stream.write('\n');
            }
            ArrayList<Marbles> marbs = world.getMarbles();
            for (int i = 0; i < marbs.size(); i++) {
                Marbles marb = marbs.get(i);
                if (!marb.isExistent()) {
                    continue;
                }
                stream.write(3);
                stream.write(marb.getColor().getRed());
                stream.write(marb.getColor().getGreen());
                stream.write(marb.getColor().getBlue());
                stream.write(marb.getDirection());
                stream.write(marb.getX());
                stream.write(marb.getY());
                stream.write('\n');
            }

            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadLives(){
        try {
            FileInputStream tar = new FileInputStream("map/lives.txt");
            BufferedInputStream stream = new BufferedInputStream(tar);
            creatures = new ArrayList<>();
            ArrayList<Marbles> marbs = new ArrayList<>();
            int num = stream.read();
            while (num != -1){
                if (num == 0){
                    Calabash cala = new Calabash(new Color(stream.read(), stream.read(), stream.read()),world, stream.read(), stream.read());
                    creatures.add(cala);
                    world.put(cala, stream.read(), stream.read());
                    world.addCala(cala);
                }else if (num == 1){
                    Goblin gob = new Goblin(world, new Color(stream.read(), stream.read(), stream.read()));
                    creatures.add(gob);
                    world.put(gob, stream.read(), stream.read());
                    world.addGoblin(gob);
                }else if (num == 2){
                    BigGoblin gob = new BigGoblin(world, stream.read());
                    creatures.add(gob);
                    world.put(gob, stream.read(), stream.read());
                    world.addBigGoblin(gob);
                }else if (num == 3){
                    Marbles marb = new Marbles(world, new Color(stream.read(), stream.read(), stream.read()), stream.read());
                    marbs.add(marb);
                    world.put(marb, stream.read(), stream.read());
                    world.addMarbles(marb);
                }

                stream.read();
                num = stream.read();
            }
            stream.close();
            for (int i = 0; i < creatures.size(); i++) {
                exec.execute(new creatureThread(creatures.get(i)));
            }
            for (int i = 0; i < marbs.size(); i++) {
                exec.execute(new marblesThread(marbs.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        if(key.getKeyCode() == 0x25){//left
            action = "left";
        }else if(key.getKeyCode() == 0x26){//up
            action = "up";
        }else if(key.getKeyCode() == 0x27){//right
            action = "right";
        }else if(key.getKeyCode() == 0x28){//down
            action = "down";
        }else if(key.getKeyCode() == 0x53){//s
            attackaction = "attackleft";
        }else if(key.getKeyCode() == 0x45){//e
            attackaction = "attackup";
        }else if(key.getKeyCode() == 0x46){//f
            attackaction = "attackright";
        }else if(key.getKeyCode() == 0x44){//d
            attackaction = "attackdown";
        }else if(key.getKeyCode() == 0x41){//a
            saveWorld();
            saveLives();
            System.out.println("has saved");
        }else if(key.getKeyCode() == 0x42){//b
            reInit();
            System.out.println("load the map");
        }
        return this;
    }
    @Override
    public void getScreen(byte[] out, int num){
        for (int y = 0; y < 21; y++) {
            for (int x = 0; x < 21; x++) {
                if(creatures.get(0).getX()+x-10 < 0 ||creatures.get(0).getX()+x-10 >= World.WIDTH){
                    Wall w = new Wall(world);
                    out[num++] = (byte)w.getGlyph();
                    out[num++] = (byte)w.getColor().getRed();
                    out[num++] = (byte)w.getColor().getGreen();
                    out[num++] = (byte)w.getColor().getBlue();
                    continue;
                }
                else if(creatures.get(0).getY()+y-10 < 0 ||creatures.get(0).getY()+y-10 >= World.HEIGHT){
                    Wall w = new Wall(world);
                    out[num++] = (byte)w.getGlyph();
                    out[num++] = (byte)w.getColor().getRed();
                    out[num++] = (byte)w.getColor().getGreen();
                    out[num++] = (byte)w.getColor().getBlue();
                    continue;
                }else{
                    out[num++] = (byte)world.get(creatures.get(0).getX()+x-10, creatures.get(0).getY()+y-10).getGlyph();
                    out[num++] = (byte)world.get(creatures.get(0).getX()+x-10, creatures.get(0).getY()+y-10).getColor().getRed();
                    out[num++] = (byte)world.get(creatures.get(0).getX()+x-10, creatures.get(0).getY()+y-10).getColor().getGreen();
                    out[num++] = (byte)world.get(creatures.get(0).getX()+x-10, creatures.get(0).getY()+y-10).getColor().getBlue();
                }
            }
        }
        Calabash cal = (Calabash)creatures.get(0);
        int lifenum = cal.lifeValue;
        EnergyFood en = new EnergyFood(world);
        Color black = new Color(0, 0, 0);
        for(int a = 0; a < 21; a++){
            out[num++] = (byte)205;
            out[num++] = (byte)0;
            out[num++] = (byte)0;
            out[num++] = (byte)0;
        }
        Floor f = new Floor(world);
        out[num++] = (byte)f.getGlyph();
        out[num++] = (byte)f.getColor().getRed();
        out[num++] = (byte)f.getColor().getGreen();
        out[num++] = (byte)f.getColor().getBlue();
        out[num++] = (byte)f.getGlyph();
        out[num++] = (byte)f.getColor().getRed();
        out[num++] = (byte)f.getColor().getGreen();
        out[num++] = (byte)f.getColor().getBlue();
        //terminal.write((char)12, 2, 22, new Color(218, 112, 214));
        out[num++] = (byte)12;
        out[num++] = (byte)218;
        out[num++] = (byte)112;
        out[num++] = (byte)214;
        //terminal.write(0, 3, 22, cal.getColor());
        out[num++] = (byte)0;
        out[num++] = (byte)cal.getColor().getRed();
        out[num++] = (byte)cal.getColor().getGreen();
        out[num++] = (byte)cal.getColor().getBlue();
        int a = 4;
        for(; a-4 < lifenum; a++){
            out[num++] = (byte)en.getGlyph();
            out[num++] = (byte)en.getColor().getRed();
            out[num++] = (byte)en.getColor().getGreen();
            out[num++] = (byte)en.getColor().getBlue();
        }
            //terminal.write(en.getGlyph(), 4+a, 22, en.getColor());
        if(cal.ownBoat == true){
            Boat bo = new Boat(world);
            out[num++] = (byte)bo.getGlyph();
            out[num++] = (byte)bo.getColor().getRed();
            out[num++] = (byte)bo.getColor().getGreen();
            out[num++] = (byte)bo.getColor().getBlue();
            //terminal.write(bo.getGlyph(), 4+a, 22, bo.getColor());
            a += 1;
        }
        if(cal.ownCane == true){
            Cane cane = new Cane(world);
            out[num++] = (byte)cane.getGlyph();
            out[num++] = (byte)cane.getColor().getRed();
            out[num++] = (byte)cane.getColor().getGreen();
            out[num++] = (byte)cane.getColor().getBlue();
            //terminal.write(cane.getGlyph(), 4+a, 22, cane.getColor());
            a += 1;
        }
        if(cal.ownKey == true){
            Key k = new Key(world);
            out[num++] = (byte)k.getGlyph();
            out[num++] = (byte)k.getColor().getRed();
            out[num++] = (byte)k.getColor().getGreen();
            out[num++] = (byte)k.getColor().getBlue();
            //terminal.write(k.getGlyph(), 4+a, 22, k.getColor());
            a += 1;
        }
        for(;a < 21; a++){
            out[num++] = (byte)f.getGlyph();
            out[num++] = (byte)f.getColor().getRed();
            out[num++] = (byte)f.getColor().getGreen();
            out[num++] = (byte)f.getColor().getBlue();
        }
    }
    @Override
    public int displayOutput(AsciiPanel terminal) {
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {

                terminal.write(world.get(x, y).getGlyph(), x+1, y+7, world.get(x, y).getColor());

            }
        }
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 20*4; j++){
                store[i][j] = 0;
            }
        }
        Calabash cal = (Calabash)creatures.get(0);
        int lifenum = cal.lifeValue;
        EnergyFood en = new EnergyFood(world);
        terminal.write((char)12, 2, 2, cal.getColor());
        terminal.write((char)254, 3, 2, cal.getColor());
        int num0 = 0;
        store[0][num0++] = (byte)12;
        store[0][num0++] = (byte)cal.getColor().getRed();
        store[0][num0++] = (byte)cal.getColor().getGreen();
        store[0][num0++] = (byte)cal.getColor().getBlue();
        store[0][num0++] = (byte)254;
        store[0][num0++] = (byte)cal.getColor().getRed();
        store[0][num0++] = (byte)cal.getColor().getGreen();
        store[0][num0++] = (byte)cal.getColor().getBlue();
        int a = 0;
        for(; a < lifenum; a++){
            terminal.write(en.getGlyph(), 4+a, 2, en.getColor());
            store[0][num0++] = (byte)en.getGlyph();
            store[0][num0++] = (byte)en.getColor().getRed();
            store[0][num0++] = (byte)en.getColor().getGreen();
            store[0][num0++] = (byte)en.getColor().getBlue();
        }

        if(cal.ownBoat == true){
            Boat bo = new Boat(world);
            terminal.write(bo.getGlyph(), 4+a, 2, bo.getColor());
            store[0][num0++] = (byte)bo.getGlyph();
            store[0][num0++] = (byte)bo.getColor().getRed();
            store[0][num0++] = (byte)bo.getColor().getGreen();
            store[0][num0++] = (byte)bo.getColor().getBlue();
            a += 1;
        }
        if(cal.ownCane == true){
            Cane cane = new Cane(world);
            terminal.write(cane.getGlyph(), 4+a, 2, cane.getColor());
            store[0][num0++] = (byte)cane.getGlyph();
            store[0][num0++] = (byte)cane.getColor().getRed();
            store[0][num0++] = (byte)cane.getColor().getGreen();
            store[0][num0++] = (byte)cane.getColor().getBlue();
            a += 1;
        }
        if(cal.ownKey == true){
            Key k = new Key(world);
            terminal.write(k.getGlyph(), 4+a, 2, k.getColor());
            store[0][num0++] = (byte)k.getGlyph();
            store[0][num0++] = (byte)k.getColor().getRed();
            store[0][num0++] = (byte)k.getColor().getGreen();
            store[0][num0++] = (byte)k.getColor().getBlue();
            a += 1;
        }

        if(instance0){
            int lifenum0 = cala0.lifeValue;
            terminal.write((char)12, 2, 3, cala0.getColor());
            terminal.write((char)254, 3, 3, cala0.getColor());
            store[0][num0++] = (byte)12;
            store[0][num0++] = (byte)cala0.getColor().getRed();
            store[0][num0++] = (byte)cala0.getColor().getGreen();
            store[0][num0++] = (byte)cala0.getColor().getBlue();
            store[0][num0++] = (byte)254;
            store[0][num0++] = (byte)cala0.getColor().getRed();
            store[0][num0++] = (byte)cala0.getColor().getGreen();
            store[0][num0++] = (byte)cala0.getColor().getBlue();
            a = 0;
            for(; a < lifenum0; a++){
                terminal.write(en.getGlyph(), 4+a, 3, en.getColor());
                store[0][num0++] = (byte)en.getGlyph();
                store[0][num0++] = (byte)en.getColor().getRed();
                store[0][num0++] = (byte)en.getColor().getGreen();
                store[0][num0++] = (byte)en.getColor().getBlue();

            }
            if(cala0.ownBoat == true){
                Boat bo = new Boat(world);
                terminal.write(bo.getGlyph(), 4+a, 3, bo.getColor());
                store[1][num0++] = (byte)bo.getGlyph();
                store[1][num0++] = (byte)bo.getColor().getRed();
                store[1][num0++] = (byte)bo.getColor().getGreen();
                store[1][num0++] = (byte)bo.getColor().getBlue();
                a += 1;
            }
            if(cala0.ownCane == true){
                Cane cane = new Cane(world);
                terminal.write(cane.getGlyph(), 4+a, 3, cane.getColor());
                store[1][num0++] = (byte)cane.getGlyph();
                store[1][num0++] = (byte)cane.getColor().getRed();
                store[1][num0++] = (byte)cane.getColor().getGreen();
                store[1][num0++] = (byte)cane.getColor().getBlue();
                a += 1;
            }
            if(cala0.ownKey == true){
                Key k = new Key(world);
                terminal.write(k.getGlyph(), 4+a, 3, k.getColor());
                store[1][num0++] = (byte)k.getGlyph();
                store[1][num0++] = (byte)k.getColor().getRed();
                store[1][num0++] = (byte)k.getColor().getGreen();
                store[1][num0++] = (byte)k.getColor().getBlue();
                a += 1;
            }
        }

        if(instance1){
            int lifenum1 = cala1.lifeValue;
            terminal.write((char)12, 2, 4, cala1.getColor());
            terminal.write((char)254, 3, 4, cala1.getColor());
            store[0][num0++] = (byte)12;
            store[0][num0++] = (byte)cala1.getColor().getRed();
            store[0][num0++] = (byte)cala1.getColor().getGreen();
            store[0][num0++] = (byte)cala1.getColor().getBlue();
            store[0][num0++] = (byte)254;
            store[0][num0++] = (byte)cala1.getColor().getRed();
            store[0][num0++] = (byte)cala1.getColor().getGreen();
            store[0][num0++] = (byte)cala1.getColor().getBlue();
            a = 0;
            for(; a < lifenum1; a++){
                terminal.write(en.getGlyph(), 4+a, 4, en.getColor());
                store[0][num0++] = (byte)en.getGlyph();
                store[0][num0++] = (byte)en.getColor().getRed();
                store[0][num0++] = (byte)en.getColor().getGreen();
                store[0][num0++] = (byte)en.getColor().getBlue();
            }
            if(cala1.ownBoat == true){
                Boat bo = new Boat(world);
                terminal.write(bo.getGlyph(), 4+a, 4, bo.getColor());
                store[2][num0++] = (byte)bo.getGlyph();
                store[2][num0++] = (byte)bo.getColor().getRed();
                store[2][num0++] = (byte)bo.getColor().getGreen();
                store[2][num0++] = (byte)bo.getColor().getBlue();
                a += 1;
            }
            if(cala1.ownCane == true){
                Cane cane = new Cane(world);
                terminal.write(cane.getGlyph(), 4+a, 4, cane.getColor());
                store[2][num0++] = (byte)cane.getGlyph();
                store[2][num0++] = (byte)cane.getColor().getRed();
                store[2][num0++] = (byte)cane.getColor().getGreen();
                store[2][num0++] = (byte)cane.getColor().getBlue();
                a += 1;
            }
            if(cala1.ownKey == true){
                Key k = new Key(world);
                terminal.write(k.getGlyph(), 4+a, 4, k.getColor());
                store[2][num0++] = (byte)k.getGlyph();
                store[2][num0++] = (byte)k.getColor().getRed();
                store[2][num0++] = (byte)k.getColor().getGreen();
                store[2][num0++] = (byte)k.getColor().getBlue();
                a += 1;
            }
        }
        // for (int x = 0; x < 21; x++) {
        //     for (int y = 0; y < 21; y++) {
        //         if(creatures.get(0).getX()+x-10 < 0 ||creatures.get(0).getX()+x-10 >= World.WIDTH){
        //             Wall w = new Wall(world);
        //             terminal.write(w.getGlyph(), x, y, w.getColor());
        //             continue;
        //         }
        //         if(creatures.get(0).getY()+y-10 < 0 ||creatures.get(0).getY()+y-10 >= World.HEIGHT){
        //             Wall w = new Wall(world);
        //             terminal.write(w.getGlyph(), x, y, w.getColor());
        //             continue;
        //         }
        //         terminal.write(world.get(creatures.get(0).getX()+x-10, creatures.get(0).getY()+y-10).getGlyph(), x, y, world.get(creatures.get(0).getX()+x-10, creatures.get(0).getY()+y-10).getColor());

        //     }
        // }
        // Calabash cal = (Calabash)creatures.get(0);
        // int lifenum = cal.lifeValue;
        // EnergyFood en = new EnergyFood(world);
        // Color black = new Color(0, 0, 0);
        // for(int a = 0; a < 21; a++)
        //     terminal.write((char)205, a, 21, black);
        // terminal.write((char)12, 2, 22, new Color(218, 112, 214));
        // terminal.write(":", 3, 22, cal.getColor());
        // int a = 0;
        // for(; a < lifenum; a++)
        //     terminal.write(en.getGlyph(), 4+a, 22, en.getColor());
        // if(cal.ownBoat == true){
        //     Boat bo = new Boat(world);
        //     terminal.write(bo.getGlyph(), 4+a, 22, bo.getColor());
        //     a += 1;
        // }
        // if(cal.ownCane == true){
        //     Cane cane = new Cane(world);
        //     terminal.write(cane.getGlyph(), 4+a, 22, cane.getColor());
        //     a += 1;
        // }
        // if(cal.ownKey == true){
        //     Key k = new Key(world);
        //     terminal.write(k.getGlyph(), 4+a, 22, k.getColor());
        //     a += 1;
        // }
        return over;
    }

    @Override
    public boolean over(){
        if(bro.isOver())    return true;
        return false;
    }

    int i = 0;



    @Override
    public void displayOver(AsciiPanel terminal) {
        for (int x = 0; x < 21; x++) {
            for (int y = 0; y < 21; y++) {

                terminal.write((char) 255, x, y, Color.gray);

            }
        }

        terminal.writeCenter("CONGRATULATION!", 8, new Color(173, 127, 168));
        terminal.writeCenter("YOU WIN!", 15, new Color(173, 127, 168));

    }
    @Override
    public void displayBegin(AsciiPanel terminal, int k) {
        for (int x = 0; x < World.OUTWIDTH; x++) {
            for (int y = 0; y < World.OUTHEIGHT; y++) {

                terminal.write((char) 255, x, y, Color.gray);

            }
        }

        terminal.writeCenter("ROUND "+ k, 10, new Color(173, 127, 168));
        terminal.writeCenter("WELCOME TO MY WORLD", 15, new Color(173, 127, 168));
        terminal.writeCenter("I AM A MAZE GAME", 17, new Color(173, 127, 168));
        terminal.writeCenter("PRESS ENTER TO BEGIN ME NOW!", 19, new Color(173, 127, 168));
        terminal.writeCenter("----------------------", 24, new Color(173, 127, 168));
        terminal.writeCenter("|want to play easier?|", 25, new Color(173, 127, 168));
        terminal.writeCenter("| try to press <A> to use ASTAR  |", 27, new Color(173, 127, 168));
        terminal.writeCenter("----------------------", 28, new Color(173, 127, 168));
        terminal.write((char) 2, 17, 32, new Color(204, 0, 0));
        terminal.write((char) 2, 21, 32, new Color(255, 165, 0));
        terminal.write((char) 2, 25, 32, new Color(252, 233, 79));
        terminal.write((char) 2, 29, 32, new Color(78, 154, 6));
        terminal.write((char) 2, 33, 32, new Color(50, 175, 255));
        terminal.write((char) 2, 37, 32, new Color(114, 159, 207));
        terminal.write((char) 2, 41, 32, new Color(173, 127, 168));
    }

    @Override
    public boolean fail() {
        if(score < 0){
            return true;
        }
        return false;
    }

    @Override
    public void displayFail(AsciiPanel terminal) {
        for (int x = 0; x < World.OUTWIDTH; x++) {
            for (int y = 0; y < World.OUTHEIGHT; y++) {
                terminal.write((char) 255, x, y, Color.gray);
            }
        }

        terminal.writeCenter("--GAME OVER--", 8, new Color(173, 127, 168));
        terminal.writeCenter("YOU LOST!", 15, new Color(173, 127, 168));
    }






}