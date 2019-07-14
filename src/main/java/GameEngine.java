public class GameEngine implements Runnable {

    private final Thread gameLoopThread;

    private Window window;
    private GameLogic gameLogic;
    private final MouseInput mouseInput = new MouseInput();

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
        mouseInput.init(window);
        gameLogic.init(window);
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
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }
}
