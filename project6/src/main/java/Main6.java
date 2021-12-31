import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.anic.calabashbros.World;
import com.anic.screen.Screen;
import com.anic.screen.WorldScreen;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

public class Main6 extends JFrame implements KeyListener{

    private static final long serialVersionUID = 1L;
    private AsciiPanel terminal;
    private WorldScreen screen;
    public static int over;
    ServerSocketChannel ssChannel;
    Selector selector = null;
    int BLOCK = 4096*32;
    ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
    ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);
    Boolean sendMap = false;
    public Main6() {
        super();
        terminal = new AsciiPanel(World.OUTWIDTH, World.OUTHEIGHT, AsciiFont.CP437_10x10);
        //terminal = new AsciiPanel(21, 23, AsciiFont.CP437_16x16);
        add(terminal);
        pack();
        screen = new WorldScreen();
        screen.setTerminal(terminal);
        addKeyListener(this);
        repaint();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new repaintCounter());
        exec.execute(new playerServer());
        over = 0;
    }

    public class playerServer implements Runnable{

        public void run() {
            WorldScreen worldScreen = (WorldScreen)screen;
            try {
                int port = 1920;
                ssChannel = ServerSocketChannel.open();
                ssChannel.configureBlocking(false);
                ServerSocket serverSocket = ssChannel.socket();
                serverSocket.bind(new InetSocketAddress(port));
                selector = Selector.open();
                ssChannel.register(selector, SelectionKey.OP_ACCEPT);
                while (true) {
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        ServerSocketChannel server = null;
                        SocketChannel client = null;
                        String sendText;
                        String receiveText = "";
                        if (selectionKey.isAcceptable()) {
                            server = (ServerSocketChannel) selectionKey.channel();
                            client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        }
                        if (selectionKey.isReadable()) {
                            //System.out.println("why");
                            client = (SocketChannel) selectionKey.channel();
                            // Read byte coming from the client
                            receivebuffer.clear();
                            try {
                                int len = client.read(receivebuffer);
                                if(len > 0){
                                    receiveText = new String( receivebuffer.array(),0,len);
                                    if(receiveText.equals("a is coming")){
                                        screen.getNewCala(0);
                                    }
                                    else if(receiveText.equals("b is coming")){
                                        screen.getNewCala(1);
                                    }
                                    else if(receiveText.equals("a") || receiveText.equals("b")) sendMap = true;
                                    //System.out.println("服务器端接受客户端数据--:"+receiveText);
                                    byte[] in = receivebuffer.array();
                                    if(in[0] == 102){
                                        screen.getMove(in[1], 0xff&in[2]);
                                    }
                                    client.register(selector, SelectionKey.OP_WRITE);
                                }

                                receivebuffer.flip();
                                //System.out.println(sendMap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectionKey.isWritable()) {
                            sendbuffer.clear();
                            // 返回为之创建此键的通道。
                            client = (SocketChannel) selectionKey.channel();
                            byte[] out;
                            if (sendMap) {
                                //int[] out1 = new int[World.WIDTH * World.HEIGHT * 4 + 1];
                                out = new byte[World.HEIGHT*World.WIDTH * 4 + 1+3*World.WIDTH];
                                if (worldScreen.running()){
                                    //System.out.println("running");
                                    int num = 0;
                                    out[num++] = 101;
                                    //screen.getScreen(out, num);
                                    //System.out.println(out[0]);
                                    for (int j = 0; j < World.HEIGHT; j++) {
                                        for (int i = 0; i < World.WIDTH; i++) {
                                            out[num++] = (byte)worldScreen.world.get(i, j).getGlyph();
                                            out[num++] = (byte)worldScreen.world.get(i, j).getColor().getRed();
                                            out[num++] = (byte)worldScreen.world.get(i, j).getColor().getGreen();
                                            out[num++] = (byte)worldScreen.world.get(i, j).getColor().getBlue();
                                        }
                                    }
                                    Byte[][] store = screen.store;
                                    for(int i = 0; i < 3; i++){
                                        for(int j = 0; j < 20*4; j++){
                                            out[num++] = store[i][j];
                                        }
                                    }
                                    //System.out.println(res.getInt());
                                }
                                else {
                                    out = new byte[1];
                                    out[0] = 101;
                                }
                                sendbuffer.put(out);
                                sendbuffer.flip();
                                client.write(sendbuffer);
                                // System.out.println(out);
                                // System.out.println("服务器端向客户端发送数据--：");
                            }
                            // sendText="message from server--";
                            // sendbuffer.put(sendText.getBytes());
                            // sendbuffer.flip();
                            client.write(sendbuffer);
                            //System.out.println("服务器端向客户端发送数据out：");
                            client.register(selector, SelectionKey.OP_READ);
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class repaintCounter implements Runnable{
        public void run(){
            while (over == 0){
                try {
                    repaint();
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if(over == 1){
                repaintOver();
            }
            if(over == -1){
                repaintFail();
            }
        }
    }

    public void repaintOver(){
        terminal.clear();
        screen.displayOver(terminal);
        super.repaint();
    }

    int k = 1;

    public void repaintBegin(){
        terminal.clear();
        screen.displayBegin(terminal, k);
        super.repaint();
        k++;
    }

    public void repaintFail(){
        terminal.clear();
        screen.displayFail(terminal);
        super.repaint();;
    }
    @Override
    public void repaint() {
        terminal.clear();
        this.over = screen.displayOutput(terminal);
        super.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        screen = (WorldScreen) screen.respondToUserInput(e);
        repaint();
        // if(gameBegin){
        //     if(e.getKeyCode() == 0x0A){//enter
        //         gameBegin = false;
        //         screen = new WorldScreen();
        //         repaint();
        //     }
        //     else if(e.getKeyCode() == 0x41){//a
        //         gameBegin = false;
        //         screen = new WorldScreen();
        //         repaint();
        //     }
        // }
        // else if(gameOver){
        //     if(e.getKeyCode() == 0x0A){
        //         repaintBegin();
        //         gameOver = false;
        //         gameBegin = true;
        //         return;
        //     }
        // }
        // else{
        //     screen = screen.respondToUserInput(e);
        //     repaint();
        //     if(screen.over()){
        //         repaintOver();
        //     }
        //     else if(screen.fail()){
        //         repaintFail();
        //     }
        // }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        Main6 app = new Main6();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
        //app.repaintBegin();
    }

}
