package kyh.labs.lab4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class TollFeeCalculator {

    public TollFeeCalculator(String inputFile) {
        //// Skapar den högre upp för att få tillgång till dates?
        LocalDateTime[] dates = new LocalDateTime[0];
        try {
            Scanner sc = new Scanner(new File(inputFile));
            String[] dateStrings = sc.nextLine().split(", ");
            //#Bug# (length ej -1)
            dates = new LocalDateTime[dateStrings.length];
            for(int i = 0; i < dates.length; i++) {
                dates[i] = LocalDateTime.parse(dateStrings[i], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            System.out.println("The total fee for the inputfile is: " + getTotalFeeCost(dates));
        } catch(DateTimeParseException e) {
            //#Bug# (catch , throw fångade inte upp)
            System.err.println("Could not read file " + dates);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally
        //ToDo (1)
        {
        }
    }

    private int getTotalFeeCost(LocalDateTime[] dates) {
        int totalFee = 0;
        LocalDateTime intervalStart = dates[0];
        int maxFeesUnder60min = 0;
        for(LocalDateTime date: dates) {
            if(date == null) continue;          //ToDo (2)
            long diffInMinutes = intervalStart.until(date, ChronoUnit.MINUTES);
            int fee = 0;
            if(diffInMinutes > 60) {
                fee = getTollFeePerPassing(date) + maxFeesUnder60min;
                maxFeesUnder60min = 0;
                intervalStart = date;
                totalFee += getTollFeePerPassing(date);
            } else {
                maxFeesUnder60min = Math.max(getTollFeePerPassing(date), maxFeesUnder60min);
            }
            totalFee += fee;
            System.out.println(date.toString() +" \n" + "Fee: " + getTollFeePerPassing(date));
        }
        //#Bug# (Math.min från Math.max.)
        return Math.min(totalFee + maxFeesUnder60min, 60);
    }

    //#"Bug"# (Förenklat koderna, då minute är falskt om ovan är sant...boolean)
    private int getTollFeePerPassing(LocalDateTime date) {
        if (isTollFreeDate(date)) return 0;
        int hour = date.getHour();
        int minute = date.getMinute();
        if (hour == 6 && minute <= 29) return 8;
        else if (hour == 6) return 13;
        else if (hour == 7) return 18;
        else if (hour == 8 && minute<= 29) return 13;
        else if (hour >= 8 && hour <15) return 8;
        else if (hour == 15 && minute <= 29) return 13;
        else if (hour == 15 || hour == 16) return 18;
        else if (hour == 17) return 13;
        else if (hour == 18 && minute <= 29) return 8;
        else return 0;
    }

    private boolean isTollFreeDate(LocalDateTime date) {
        return date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7 || date.getMonth().getValue() == 7;
    }

    public static void main(String[] args) {
        new TollFeeCalculator("testData/Lab4.txt");
    }
}
