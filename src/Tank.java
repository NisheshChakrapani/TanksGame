import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by nishu on 5/9/2017.
 */
public class Tank {
    private final double G = -9.81;
    private Graphics g;
    private DrawingPanel panel;
    private int xPos;
    private int yPos;
    private BufferedImage bomb = ImageIO.read(new File("bomb.png"));
    private Point[] turretPts = {new Point(153, 739), new Point(148, 734), new Point(152, 729), new Point(150, 721),
    new Point(148, 716), new Point(148, 711), new Point(146, 704), new Point(144, 699), new Point(140, 696),
    new Point(135, 692), new Point(133, 687), new Point(128, 683), new Point(123, 680)};
    public Tank(int xPos, int yPos) throws IOException {
        this.panel = new DrawingPanel(1000, 800);
        this.g = panel.getGraphics();
        this.xPos = xPos;
        this.yPos = yPos;
        g.drawImage(ImageIO.read(new File("tanksprite1.png")), xPos, yPos, null);
        Color brown = new Color(139, 69, 19);
        g.setColor(brown);
        g.fillRect(0, 780, 1000, 20);
        g.drawImage(ImageIO.read(new File("Powerbar\\Powerbar1.png")), 10, 70, null);
        g.setColor(Color.BLUE);
        g.setFont(new Font("Large", Font.BOLD, 50));
        g.drawString("POWER:", 10, 55);
        //animate();
        panel.addMouseListener(new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                /*try {
                    animate();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Point p1 = new Point(xPos+144, yPos);
                Point p2 = new Point(e.getX(), e.getY());
                double vX = Math.abs(p1.x-p2.x);
                double vY = Math.abs(p1.y-p2.y);
                double deltaT = 0.01;
                double x = xPos+144;
                double y = yPos;
                double oldX = x;
                double oldY = y;
                boolean done = false;
                Rectangle ground = new Rectangle(0, 780, 1000, 20);
                Rectangle ball = new Rectangle((int)xPos, (int)yPos, 32, 32);
                while (!ball.intersects(ground)) {
                    g.drawImage(bomb, (int)x, (int)y, null);
                    ball = new Rectangle((int)xPos, (int)yPos, 32, 32);
                    if (ball.intersects(ground)) {
                        done = true;
                    }
                    vY += (G*deltaT);
                    x += (vX*deltaT);
                    y -= (vY*deltaT);
                    panel.sleep(50);
                    g.setColor(Color.white);
                    g.fillRect((int)oldX, (int)oldY, 32, 32);
                    oldX = x;
                    oldY = y;
                }*/
            }

            public void mouseMoved(MouseEvent e) {
                Point p1 = new Point(e.getX(), e.getY());
                Point p2 = new Point((int)(xPos+144), (int)(yPos+20));
                double xDist = (p1.x-p2.x);
                double yDist = (p2.y-p1.y);

                double theta = Math.toDegrees(Math.atan2(yDist, xDist));
                theta = 5*(Math.round(theta/5));

                double turretAngle = theta;
                if (turretAngle > 60) {
                    turretAngle = 60;
                } else if (turretAngle < 0) {
                    turretAngle = 0;
                }

                int imgIndex = ((int)turretAngle/5)+1;
                g.setColor(Color.white);
                g.fillRect(xPos, yPos, 170, 70);
                try {
                    g.drawImage(ImageIO.read(new File("TankSprite" + imgIndex + ".png")), xPos, yPos, null);
                } catch (IOException ex) {
                    System.out.println("TankSprite"+imgIndex);
                }

                Point turretPt = turretPts[imgIndex-1];
                double power = turretPt.distance(p1);
                if (power > 350) {
                    power = 350;
                }
                int pwrIndex = ((int)power/10)+1;
                g.fillRect(10, 70, 350, 30);
                try {
                    g.drawImage(ImageIO.read(new File("Powerbar\\Powerbar" + pwrIndex + ".png")), 10, 70, null);
                } catch (IOException ex) {
                    System.out.println("Powerbar" + pwrIndex);
                }
            }
        });
    }

    private void fire(double vX, double vY) throws IOException {

    }

    public void animate() throws IOException {
        forward();
        backward();
        forward();
        backward();
    }
    private void forward() throws IOException {
        for (int i = 1; i <= 12; i++) {
            g.setColor(Color.white);
            g.fillRect(xPos, yPos, 170, 70);
            try {
                g.drawImage(ImageIO.read(new File("TankSprite" + i + ".png")), xPos, yPos, null);
            } catch (IIOException e) {
                System.out.println("TankSprite"+i);
            }
            panel.sleep(100);
        }
    }
    private void backward() throws IOException {
        for (int i = 12; i >= 1; i--) {
            g.setColor(Color.white);
            g.fillRect(xPos, yPos, 170, 70);
            try {
                g.drawImage(ImageIO.read(new File("TankSprite" + i + ".png")), xPos, yPos, null);
            } catch (IIOException e) {
                System.out.println("TankSprite"+i);
            }
            panel.sleep(100);
        }
    }
}
