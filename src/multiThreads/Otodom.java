package multiThreads;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Otodom {


    private int count;
    final static String URLAdress = "https://www.otodom.pl/sprzedaz/mieszkanie/sopot/";
    private String city;
    private String voivodeship;
    private int price;

    public Otodom() throws IOException {
        long timeBeforeExecution = System.currentTimeMillis();
        getContent();
        long timeAfterExecution = System.currentTimeMillis();
        System.out.println("\t\tIle trwalo wykonanie programu? : " + (timeAfterExecution - timeBeforeExecution));
    }

    public void getContent() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(30);// każdemu wątkowi executorService przydziela dokładnie jedno zadanie readWebsite()

        URL otoDom = new URL(URLAdress);
        BufferedReader in = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            in = new BufferedReader(
                    new InputStreamReader(otoDom.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                stringBuilder.append(inputLine);
                stringBuilder.append(System.lineSeparator());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            in.close();
        }
        String content = stringBuilder.toString();
        Set<String> setStrings = new TreeSet<>();
        for(int i = 0; i < content.length(); i++){
            i = content.indexOf("https://www.otodom.pl/pl/oferta/", i);
            if(i == -1){
                break;
            }
            String link = content.substring(i).split(".html")[0];
            setStrings.add(link);
        }
        count = 0;
        setStrings.forEach(element -> {
                executorService.submit(() -> {
                try {
                    readWebsite(element, count + ".html");
                }catch (IOException e) {
                        e.printStackTrace();
                    }});
                count++;
        });
        executorService.shutdown();
        setStrings.forEach(System.out::println);
    }

    public void readWebsite(String link, String fileName) throws IOException {
        File f = new File(fileName);
        if(f.exists() && !f.isDirectory()) {
            System.out.println("\t\tPlik o nazwie " + fileName + " już istnieje!");
//            if(f.delete()){
//                System.out.println("\t\tPlik o nazwie " + fileName + " został zaktualizowany istnieje!");
//            }else{
//                System.out.println("Failed!!");
//            }
            System.exit(-1);
        }
        URL url = new URL(link);
        BufferedReader in = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                stringBuilder.append(inputLine);
                stringBuilder.append(System.lineSeparator());
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            in.close();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));
        bw.write(stringBuilder.toString());
        bw.close();
    }
}
