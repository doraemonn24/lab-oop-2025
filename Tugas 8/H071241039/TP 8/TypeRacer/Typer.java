package TypeRacer;

public class Typer extends Thread {
    private String botName, wordsTyped;
    private double wpm; 
    private TypeRacer typeRacer;
    private volatile boolean finished = false; 
    private int finishTime = 0; 

    public Typer(String botName, double wpm, TypeRacer typeRacer) {
        this.botName = botName;
        this.wpm = wpm;
        this.wordsTyped = "";
        this.typeRacer = typeRacer;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setWpm(double wpm) {
        this.wpm = wpm;
    }

    public synchronized void addWordsTyped(String newWordsTyped) {
        this.wordsTyped += newWordsTyped + " ";
    }

    public synchronized String getWordsTyped() {
        return wordsTyped;
    }

    public String getBotName() {
        return botName;
    }

    public double getWpm() {
        return wpm;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        String[] wordsToType = typeRacer.getWordsToType().split(" ");

        int totalWords = wordsToType.length;
        int wordsTypedCount = 0;

        double secondsPerWord = 60.0 / wpm;

        long startTime = System.currentTimeMillis();

        while (wordsTypedCount < totalWords) {
            try {
                Thread.sleep((long)(secondsPerWord * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            addWordsTyped(wordsToType[wordsTypedCount]);
            wordsTypedCount++;
        }

        addWordsTyped("(Selesai)");

        long endTime = System.currentTimeMillis();
        finishTime = (int)((endTime - startTime) / 1000); 

        finished = true;

        typeRacer.addResult(botName, finishTime);
    }
}
