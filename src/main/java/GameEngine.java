public class GameEngine implements Runnable {

    private final Thread gameLoopThread;

    private Window window;
    private GameLogic gameLogic;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, GameLogic gameLogic) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync);
        this.gameLogic = gameLogic;
    }

    public void start() {
        gameLoopThread.start();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            gameLogic.cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        gameLogic.init();
    }

    protected void gameLoop() {
        boolean running = true;
        while (running && !window.winodowShouldClose()) {
            input();
            update(1);
            render();
        }
    }

    protected void input() {
        gameLogic.input(window);
    }

    protected void update(float interval) {
        gameLogic.update(interval);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }
}
