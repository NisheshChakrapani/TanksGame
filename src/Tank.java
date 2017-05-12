import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by nishu on 5/9/2017.
 */
public class Tank {
    private boolean fired = false;
    private int prevTurretPt = 1;
    private final double G = -98.1;
    private Graphics g;
    private Graphics2D g2;
    private DrawingPanel panel;
    private int xPos;
    private int yPos;
    private int wind;
    private Rectangle windBox;
    private Color sky = new Color(135, 206 ,250);
    private BufferedImage bomb = ImageIO.read(new File("bomb.png"));
    private Point[] turretPts = {new Point(163, 475), new Point(158, 465), new Point(162, 465), new Point(160, 451),
    new Point(158, 447), new Point(158, 447), new Point(156, 436), new Point(154, 431), new Point(150, 428),
    new Point(145, 424), new Point(143, 419), new Point(138, 415), new Point(133, 412)};
    public Tank(int xPos, int yPos) throws IOException {
        this.panel = new DrawingPanel(2500, 800);
        panel.setBackground(sky);
        this.g = panel.getGraphics();
        g2 = (Graphics2D)g;
        this.xPos = xPos;
        this.yPos = yPos;
        g.drawImage(ImageIO.read(new File("tanksprite1.png")), xPos, yPos, null);
        Color brown = new Color(139, 69, 19);
        g.setColor(brown);
        g.fillRect(0, 780, 2500, 20);
        g.drawImage(ImageIO.read(new File("Powerbar\\Powerbar1.png")), turretPts[0].x, turretPts[0].y, null);
        wind = getRandomWind();
        if (wind > 0) {
            g.drawImage(ImageIO.read(new File("WindLeft.png")), 1200, 50, null);
        } else {
            g.drawImage(ImageIO.read(new File("WindRight.png")), 1200, 50, null);
        }
        g.setFont(new Font("Large", Font.BOLD, 30));
        g.setColor(Color.black);
        g.drawString(Integer.toString(Math.abs(wind)), 1230, 43);
        windBox = new Rectangle(1200, 10, 100, 80);
        //fire(350, 200);
        panel.addMouseListener(new MouseInputAdapter() {
            public void mouseClicked(MouseEvent e) {
                fired = true;
                Point p1 = new Point(xPos+144, yPos);
                Point p2 = new Point(e.getX(), e.getY());
                double vX = Math.abs(p1.x-p2.x);
                double vY = Math.abs(p1.y-p2.y);
                if (vX > 350) {
                    vX = 350;
                }
                if (vY > 350) {
                    vY = 350;
                }

                try {
                    fire(vX, vY);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                panel.sleep(3000);
                fired = false;
            }

            public void mouseMoved(MouseEvent e) {
                if (!fired) {
                    Point p1 = new Point(e.getX(), e.getY());
                    Point p2 = new Point(155, 670);
                    double xDist = (p1.x - p2.x);
                    double yDist = (p2.y - p1.y);

                    double theta = Math.toDegrees(Math.atan2(yDist, xDist));
                    theta = 5 * (Math.round(theta / 5));

                    double turretAngle = theta;
                    if (turretAngle > 60) {
                        turretAngle = 60;
                    } else if (turretAngle < 0) {
                        turretAngle = 0;
                    }

                    int imgIndex = ((int) turretAngle / 5) + 1;
                    g.setColor(sky);
                    g.fillRect(xPos, yPos, 170, 70);
                    try {
                        g.drawImage(ImageIO.read(new File("TankSprite" + imgIndex + ".png")), xPos, yPos, null);
                    } catch (IOException ex) {
                        System.out.println("TankSprite" + imgIndex);
                    }
                    Point turretPt = turretPts[imgIndex - 1];
                    p1 = new Point(e.getX(), e.getY());
                    double power = new Point(155, 670).distance(p1);

                    if (power > 350) {
                        power = 350;
                    }
                    power = 25 * Math.round(power / 25);
                    int pwrIndex = ((int) power / 25) + 1;
                    g2.fillRect(turretPts[prevTurretPt].x - 10, turretPts[prevTurretPt].y - 55, 370, 340);

                    try {
                        BufferedImage bar = ImageIO.read(new File("Powerbar\\Powerbar" + pwrIndex + ".png"));
                        double deg = Math.toRadians((10 - imgIndex) * (5));
                        AffineTransform at = g2.getTransform();
                        at.translate(turretPts[imgIndex - 1].x, turretPts[imgIndex - 1].y);
                        at.rotate(deg, 0, 262);
                        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                        BufferedImage rotated = ato.createCompatibleDestImage(bar, bar.getColorModel());
                        g2.drawImage(bar, at, null);
                    } catch (IOException ex) {
                        System.out.println("Powerbar" + pwrIndex);
                    }
                    prevTurretPt = (imgIndex - 1);
                }
            }
        });
    }

    private void fire(double vX, double vY) throws IOException {
        g.setColor(sky);
        g.fillRect(133, 412, 300, 350);
        double deltaT = 0.01;
        double x = xPos+144;
        double y = yPos;
        double oldX = x;
        double oldY = y;
        Rectangle ground = new Rectangle(0, 780, 10000, 20);
        Rectangle ball = new Rectangle((int)xPos, (int)yPos, 32, 32);
        while (!ball.intersects(ground)) {
            g.drawImage(bomb, (int)x, (int)y, null);
            ball = new Rectangle((int)x, (int)y, 32, 32);
            if (ball.intersects(ground)) {
                panel.sleep(1000);
                g.fillRect((int)x, (int)y, 32, 30);
                break;
            }
            if (ball.intersects(windBox)) {
                if (wind > 0) {
                    g.drawImage(ImageIO.read(new File("WindLeft.png")), 1200, 50, null);
                } else {
                    g.drawImage(ImageIO.read(new File("WindRight.png")), 1200, 50, null);
                }
                g.setFont(new Font("Large", Font.BOLD, 30));
                g.setColor(Color.black);
                g.drawString(Integer.toString(Math.abs(wind)), 1230, 43);
            }
            vX -= ((wind*1.1)*deltaT);
            vY += ((G-(wind*1.1))*deltaT);
            x += (vX*deltaT);
            y -= (vY*deltaT);
            panel.sleep(10);
            g.setColor(sky);
            g.fillRect((int)oldX, (int)oldY, 32, 32);
            oldX = x;
            oldY = y;
        }
    }

    private int getRandomWind() {
        Random rand = new Random();
        int wind = rand.nextInt(25)+1;
        int direction = rand.nextInt(2);
        if (direction == 0) {
            return wind;
        } else {
            return (wind*-1);
        }
    }
}
