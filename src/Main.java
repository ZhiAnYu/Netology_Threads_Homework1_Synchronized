import java.util.*;
import java.util.concurrent.Callable;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final int amountThread = 1000;

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < amountThread; i++) {
            Thread thread = new Thread(() -> {
                String myRoute = generateRoute("RLRFR", 100);
                //  System.out.println(myRoute);

                int counterR = 0;
                for (int j = 0; j < myRoute.length(); j++) {
                    if (myRoute.charAt(j) == 'R') {
                        counterR++;
                    }
                }
                //               System.out.println(" R: " + counterR);
                synchronized (sizeToFreq) {
                    calculateFreqRoute(counterR);
                    sizeToFreq.notify();
                }
            });
            threads.add(thread);
            thread.start();
        }


        Runnable logic = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    Map.Entry<Integer, Integer> maxEntry = sizeToFreq.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .orElse(null);

                    System.out.printf("Самое частое количество повторений: %d (встретилось %d раз) \n", maxEntry.getKey(), maxEntry.getValue());
                }
            }
        };
        Thread printThread = new Thread(logic);
        printThread.start();

        for(Thread thread:threads){
         thread.join();
        } //ждем завершения всех потоков

        printThread.interrupt();   //Прерываем поток поиска максимума
        if (!sizeToFreq.isEmpty()) {
            for (Integer key : sizeToFreq.keySet()) {
                int value = sizeToFreq.get(key);
                System.out.println(key + " - " + value + " раз");
            }
        }
    }

    private static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }


    //метод вычисляет количество частот R  в одной строке/маршруте
    public static Map<Integer, Integer> calculateFreq(String route) {

        int counter = 0;
        for (int i = 0; i < route.length(); i++) {
            if (route.charAt(i) == 'R') {
                counter++;
            } else {
                if (counter > 0) {
                    sizeToFreq.put(counter, sizeToFreq.getOrDefault(counter, 0) + 1);
                    counter = 0;
                }
            }

        }
        if (counter > 0) {
            sizeToFreq.put(counter, sizeToFreq.getOrDefault(counter, 0) + 1);
        }
        return sizeToFreq;

    }

    public static Map<Integer, Integer> calculateFreqRoute(int counter) {
        if (counter > 0) {
            sizeToFreq.put(counter, sizeToFreq.getOrDefault(counter, 0) + 1);
            counter = 0;
        }

        return sizeToFreq;
    }


}
