import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.Color;
import javax.swing.JFrame;

import com.anic.calabashbros.World;
import com.anic.screen.ClientScreen;
import com.anic.screen.Screen;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

public class Client0 extends JFrame implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;
    private static final long serialVersionUID = 1L;
    private Lock lock = new ReentrantLock();
    int clientID = 0;
    SocketChannel socketChannel;
    Selector selector = null;
    boolean getCala = false;
    private static int BLOCK = 4096*32;
    private static ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK), sendbuffer2 = ByteBuffer.allocate(BLOCK);
    private static ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
    int width = 21, height = 23;
    boolean press = false;
    public Client0(){
        super();
        terminal = new AsciiPanel(World.OUTWIDTH, World.OUTHEIGHT, AsciiFont.CP437_10x10);
        add(terminal);
        pack();
        screen = new ClientScreen();
        addKeyListener(this);
        repaint();
        ExecutorService exec = Executors.newCachedThreadPool();
        String hostName = "DESKTOP-T1FCIAB";
        int port = 1920;
        InetSocketAddress address = new InetSocketAddress(hostName, port);
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exec.execute(new repaintTimer());
    }

    @Override
    public void repaint() {
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(this.clientID==-1){
            return;
        }
        int actionNum = e.getKeyCode();
        lock.lock();
        byte[] out = new byte[3];
        out[0] = 102;
        out[1] = (byte)clientID;
        out[2] = (byte)(0xff & actionNum);
        sendbuffer2.clear();
        sendbuffer2.put(out);
        sendbuffer2.flip();
        // client.write(sendbuffer);
        // ByteBuffer buffer = ByteBuffer.allocate(12);
        // buffer.putInt(102);// 2代表进行操作
        // buffer.putInt(clientID);
        // buffer.putInt(actionNum);
        // buffer.flip();
        try {
			socketChannel.write(sendbuffer2);
            //System.out.println("surprise");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        lock.unlock();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public class repaintTimer implements Runnable {
        public void run() {
            try {
                Set<SelectionKey> selectionKeys;
                Iterator<SelectionKey> iterator;
                SelectionKey selectionKey;
                SocketChannel client;
                int count=0;
                while (true) {
                    selector.select();
                    selectionKeys = selector.selectedKeys();
                    iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        selectionKey = iterator.next();
                        if (selectionKey.isConnectable()) {
                            System.out.println("client connect");
                            client = (SocketChannel) selectionKey.channel();
                            if (client.isConnectionPending()) {
                                client.finishConnect();
                                System.out.println("完成连接!");
                                sendbuffer.clear();
                                sendbuffer.put("a is coming".getBytes());
                                sendbuffer.flip();
                                client.write(sendbuffer);
                            }
                            client.register(selector, SelectionKey.OP_READ);
                        } else if (selectionKey.isReadable()) {
                            client = (SocketChannel) selectionKey.channel();
                            receivebuffer.clear();
                            count=client.read(receivebuffer);
                            byte[] in = receivebuffer.array();
                            //System.out.println("count= "+count);
                            //System.out.println(in.length);
                            int num = 0;
                            if(in[num++] == 101){
                                ClientScreen clientScreen = (ClientScreen) screen;
                                for (int i = 0; i < World.WIDTH * World.HEIGHT; i++) {
                                    clientScreen.worldGlyph[i % World.WIDTH][i / World.WIDTH] = (char) (0xff &in[num++]);
                                    int r = (int)(0xff &in[num++]), g = (int)(0xff &in[num++]), b = (int)(0xff &in[num++]);
                                    clientScreen.worldColor[i % World.WIDTH][i / World.WIDTH] = new Color(r,g,b);
                                }

                                for(int i = 0; i < 3*20; i++){
                                    clientScreen.glyph[i/20][i%20] = (char) (0xff &in[num++]);
                                    int r = (int)(0xff &in[num++]), g = (int)(0xff &in[num++]), b = (int)(0xff &in[num++]);
                                    clientScreen.color[i/20][i%20] = new Color(r,g,b);
                                }
                                repaint();
                            }
                            if(count>0){
                                client.register(selector, SelectionKey.OP_WRITE);
                            }

                        } else if (selectionKey.isWritable()) {
                            sendbuffer.clear();
                            client = (SocketChannel) selectionKey.channel();
                            sendbuffer.put("a".getBytes());
                            sendbuffer.flip();
                            client.write(sendbuffer);
                            client.register(selector, SelectionKey.OP_READ);
                        }
                    }
                    selectionKeys.clear();
                    TimeUnit.MILLISECONDS.sleep(20);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client0 client = new Client0();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.setVisible(true);
    }
}