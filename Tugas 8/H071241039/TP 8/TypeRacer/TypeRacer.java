package TypeRacer;

import java.util.ArrayList;
import java.util.Random;

public class TypeRacer {
    private String wordsToType;
    private ArrayList<Typer> rareContestant = new ArrayList<>();
    private ArrayList<Result> rareStanding = new ArrayList<>();

    public String getWordsToType() {
        return wordsToType;
    }

    public ArrayList<Typer> getRareContestant() {
        return rareContestant;
    }

    private String[] wordsToTypeList = {
            "Di Bikini Bottom ada Spongebob Squarepants, dia memang keren suka main drumband",
            "Dia jadi koki masaknya krabby patty, menjalani hari hidup bersama Garry",
            "Ayo sama-sama sebutkan nama-nama makhluk dalam sana di Bikini Bottom jaya",
            "Namun ada juga namanya Patrick Star, walau dia cetar tapi hidupnya liar",
            "Tinggal dalam batu tapi suka membantu, sayang hanya satu otaknya itu buntu"
    };

    public void setNewWordsToType() {
        Random random = new Random();
        int angkaRandom = random.nextInt(wordsToTypeList.length);
        wordsToType = wordsToTypeList[angkaRandom];
    }

 
    public synchronized void addResult(String name, int finishTime) {
        rareStanding.add(new Result(name, finishTime));
    }


    private void printRaceStanding() {
        System.out.println("\nKlasemen Akhir Type Racer");
        System.out.println("=========================\n");

        rareStanding.sort((r1, r2) -> Integer.compare(r1.getFinishTime(), r2.getFinishTime()));

        int posisi = 1;
        for (Result r : rareStanding) {
            System.out.println(posisi + ". " + r.getName() + " = " + r.getFinishTime() + " detik");
            posisi++;
        }
    }

 
    public void startRace() {
        for (Typer typer : rareContestant) {
            typer.start();
        }
    }

    public void displayRaceStandingPeriodically() throws InterruptedException {
        boolean allFinished = false;

        while (!allFinished) {
            System.out.println("Typing Progress...");
            System.out.println("===================");

            allFinished = true; 

            for (Typer typer : rareContestant) {
                String wordsTyped = typer.getWordsTyped();
                System.out.println("-" + typer.getBotName() + " => " + wordsTyped.trim());

                if (!typer.isFinished()) {
                    allFinished = false; 
                }
            }
            System.out.println();

            if (!allFinished) {
                Thread.sleep(2000);
            }
        }

        printRaceStanding();
    }
}
