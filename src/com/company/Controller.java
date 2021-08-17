package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Time;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Controller {
    private View view;
    private static final int SQUARE_SIZE = 80;
    private static final int FIELD_WIDTH = 9;
    private static final int FIELD_HEIGHT = 9;
    private static final int SCREEN_WIDTH = SQUARE_SIZE * FIELD_WIDTH;
    private static final int SCREEN_HEIGHT = SQUARE_SIZE * FIELD_HEIGHT;
    private Graphics graphics;
    private Color[][] field = new Color[FIELD_WIDTH][FIELD_HEIGHT];
    private Color[] colors = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.PINK,
            Color.MAGENTA,
            Color.YELLOW,
    };

    private static final double CIRCLE_RATIO = 0.5;

    private Point selectedPoint;
    private static final int WIN_COUNT = 5;
    private Set<Point> winPoints = new HashSet<>();
    private BufferedImage image;


    public void start() {
        view.create(SCREEN_WIDTH, SCREEN_HEIGHT);
        nextRandomCircles();
        generateImage();
    }

    public void setView(View view) {
        this.view = view;
    }

    private void generateImage() {
        image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawSquares();
        drawCircles();
        if (selectedPoint != null) {
            //draw(createSelection(), selectedPoint.x, selectedPoint.y);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createSelection();
                }
            }).start();

        }
        view.setImage(image);
    }

    private void drawCircles() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                if (field[i][j] != null) {
                    //draw(createCircle(field[i][j]), i, j);
                    draw(createCircle2(field[i][j]), i, j);
                }
            }
        }
    }

    private BufferedImage createCircle(Color color) {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        //graphics.setColor(color);
        int circleSize = (int) (SQUARE_SIZE * CIRCLE_RATIO);
        int startPoint = (SQUARE_SIZE - circleSize) / 2;
        graphics.fillOval(startPoint, startPoint, circleSize, circleSize);
        for (int i = startPoint; i < startPoint + circleSize; i++) {
            for (int j = startPoint; j < startPoint + circleSize; j++) {
                if (image.getRGB(i, j) == Color.WHITE.getRGB()) {
                    double k = (i - startPoint + j - startPoint) / 2.0 / circleSize;
                    k = 1 - k;
                    image.setRGB(i, j, new Color((int) (color.getRed() * k), (int) (color.getGreen() * k), (int) (color.getBlue() * k)).getRGB());
                }
            }

        }
        return image;
    }

    private BufferedImage createCircle2(Color color) {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        int circleSize = (int) (SQUARE_SIZE * CIRCLE_RATIO);
        int startPoint = (SQUARE_SIZE - circleSize) / 2;
        int halfCircle = (circleSize + 1) / 2;
        for (int i = 0; i < halfCircle; i++) {
            double k = (i + halfCircle * 0.5) / ((double) halfCircle * 1.5);
            int size = circleSize - i * 2;
            graphics.setColor(new Color((int) (k * color.getRed()), (int) (k * color.getGreen()), (int) (k * color.getBlue())));
            if (i == 0) {
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
                graphics.fillOval(startPoint + i, startPoint + i, size, size);

            } else {
                graphics.drawOval(startPoint + i, startPoint + i, size, size);
            }
        }
        return image;
    }

    private BufferedImage createSquare() {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(0x545454));
        graphics.fillRect(1, 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
        return image;
    }

    private BufferedImage createSelection() {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        int circleSize = (int) (SQUARE_SIZE * CIRCLE_RATIO);
        graphics.setColor(field[selectedPoint.x][selectedPoint.y]);
        for (int i = 0; i < (SQUARE_SIZE - circleSize) / 2; i++) {
            graphics.drawOval(i, i, SQUARE_SIZE - i * 2, SQUARE_SIZE - i * 2);
            draw(image, selectedPoint.x, selectedPoint.y);
            view.setImage(this.image);
            sleep(25);
            //System.out.println(i + " ");
        }
        return image;

    }

    private void drawSquares() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                draw(createSquare(), i, j);
            }
        }
    }

    private void draw(BufferedImage image, int x, int y) {
        graphics.drawImage(image, x * SQUARE_SIZE, y * SQUARE_SIZE, null);
    }

    public void handleMousePress(int mouseX, int mouseY) {
        int x = mouseX / SQUARE_SIZE;
        int y = mouseY / SQUARE_SIZE;
        if (field[x][y] == null) {
            if (selectedPoint != null) {
                field[x][y] = field[selectedPoint.x][selectedPoint.y];
                field[selectedPoint.x][selectedPoint.y] = null;
                selectedPoint = null;
                checkField();
                if (winPoints.size() == 0) {
                    nextRandomCircles();
                    checkField();
                }
            }
        } else {
            selectedPoint = new Point(x, y);
        }
        generateImage();
    }

    private int random(int max) {
        return (int) (Math.random() * max);
    }

    private void createRandomCircle() {
        int x;
        int y;
        do {
            x = random(FIELD_WIDTH);
            y = random(FIELD_HEIGHT);
        } while (field[x][y] != null);
        field[x][y] = colors[random(colors.length)];
    }

    private void nextRandomCircles() {
        for (int i = 0; i < 3; i++) {
            if (isFieldFull()) {
                return;
            }
            createRandomCircle();
        }
    }

    private void similarColors(int x, int y, int dX, int dY) {
        Color color = field[x][y];
        if (color == null) {
            return;
        }

        int curX = x;
        int curY = y;
        for (int i = 0; i < WIN_COUNT - 1; i++) {
            curX += dX;
            curY += dY;
            if (field[curX][curY] != color) {
                return;
            }
        }

        curX = x;
        curY = y;
        for (int i = 0; i < WIN_COUNT; i++) {
            winPoints.add(new Point(curX, curY));
            curX += dX;
            curY += dY;
        }
    }

    private void checkField() {
        winPoints.clear();

        for (int i = 0; i <= FIELD_WIDTH - WIN_COUNT; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                similarColors(i, j, 1, 0);
            }
        }
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j <= FIELD_HEIGHT - WIN_COUNT; j++) {
                similarColors(i, j, 0, 1);
            }
        }
        for (int i = 0; i <= FIELD_WIDTH - WIN_COUNT; i++) {
            for (int j = 0; j <= FIELD_HEIGHT - WIN_COUNT; j++) {
                similarColors(i, j, 1, 1);
            }
        }
        for (int i = WIN_COUNT - 1; i < FIELD_WIDTH; i++) {
            for (int j = 0; j <= FIELD_HEIGHT - WIN_COUNT; j++) {
                similarColors(i, j, -1, 1);
            }
        }

        removeCircles();
    }

    private void removeCircles() {
        for (Point point : winPoints) {
            field[point.x][point.y] = null;
        }
    }

    private boolean isFieldFull() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                if (field[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void sleep(int value) {
        try {
            TimeUnit.MILLISECONDS.sleep(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
