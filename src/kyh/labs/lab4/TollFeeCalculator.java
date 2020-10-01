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
            //ToDo #Bug# (length ej -1)
            dates = new LocalDateTime[dateStrings.length];
            for(int i = 0; i < dates.length; i++) {
                dates[i] = LocalDateTime.parse(dateStrings[i], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            System.out.println("The total fee for the inputfile is: " + getTotalFeeCost(dates));
        } catch(DateTimeParseException e) {
            //ToDo #Bug# (catch , throw fångade inte upp)
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
        //ToDo #Bug# (Lagt till max 1 taxa per passering under 60min)
        int maxFeesUnder60min = 0;
        for(LocalDateTime date: dates) {
            long diffInMinutes = intervalStart.until(date, ChronoUnit.MINUTES);
            int fee = 0;
            if(diffInMinutes >= 60) {
                fee = getTollFeePerPassing(date) + maxFeesUnder60min;
                maxFeesUnder60min = 0;
                intervalStart = date;
                totalFee += getTollFeePerPassing(date);
            } else {
                maxFeesUnder60min = Math.max(getTollFeePerPassing(date), maxFeesUnder60min);
            }
            totalFee += fee;
            System.out.println(date.toString() +"\n" + "Fee: " + getTollFeePerPassing(date)+ "\n" + "---------" );
        }
        //ToDo #Bug# (Math.min från Math.max.)
        return Math.min(totalFee + maxFeesUnder60min, 60);
    }

    //ToDo #"Bug"# (Förenklat koderna, då minute är falskt om ovan är sant...boolean + stod hour iso minu på en rad)
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


// ToDo (1) = Få till en sc.close(); för att stänga scannern
