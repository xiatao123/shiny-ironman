package kamcord;

import kamcord.stats.BaseStats;
import kamcord.stats.HighLevelStats;
import kamcord.stats.Stats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by taoxia on 3/3/15.
 */
public class Model {
    Map<String, Stats> allStats;

    final static String FILE_NAME = "input.csv";
    final static String SEPARATOR = ",";
    final static String EVENT = "UI_OPEN_COUNT";
    final static int TOTAL_FIELDS = 7;
    final static int LOOK_BACK_DAYS = -7;

    //Need two date formatter
    //2014-09-23 19:00:00+00:00
    //9/29/2014  1:10:16 AM
    SimpleDateFormat format1 = new SimpleDateFormat("M/d/yyyy h:mm:ss a");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat queryDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Model(){
        allStats = new HashMap<>();
    }

    public void processingFile() {

        try {
            String line;
            ClassLoader classLoader = getClass().getClassLoader();

            BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource(FILE_NAME).getFile()));
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                if(fields.length>=TOTAL_FIELDS){
                    //look for UI_OPEN_COUNT only
                    if(fields[2].equals(EVENT)){
                        processEvent(fields[3],fields[4],fields[5],fields[6]);
                    }
                }else{
                    System.out.println("Invalid input: "+line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void processEvent(String eventTime, String os, String sdk, String id) {
        try{
            Calendar cal = parseEventTime(eventTime);
            String initialDate = getDateString(cal);

            //the current kamcord.stats is used as reopen kamcord.stats 7 days ago
            cal.add(Calendar.DAY_OF_MONTH, LOOK_BACK_DAYS);
            String reopenDate = getDateString(cal);

            Key initialKey = new Key(initialDate,os,sdk);
            Key reopenKey = new Key(reopenDate,os,sdk);

            if(allStats.containsKey(initialKey.getKey())){
                ((BaseStats)allStats.get(initialKey.getKey())).insertInitialId(id);
            }else{
                //create a new kamcord.stats
                BaseStats stats = new BaseStats(initialKey);
                stats.insertInitialId(id);
                addNewBaseStatsWithInitial(stats, reopenKey);
            }
        } catch (ParseException e) {
            System.out.println("failed to parse event time: " + eventTime);
        }
    }

    private Calendar parseEventTime(String eventTime) throws ParseException {
        //parsing date using local timezone
        Date date;
        try{
            date = format1.parse(eventTime);
        } catch (ParseException e) {
            try {
                date = format2.parse(eventTime);
            } catch (ParseException e1) {
                throw e1;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private String getDateString(Calendar cal){
        return (cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+"-"+cal.get(Calendar.YEAR);
    }

    private void addNewBaseStatsWithInitial(BaseStats stats, Key reopenKey){
        //add new states to allStats
        addNewBaseStats(stats);

        //check if the corresponding reopen entry exist already
        //set the current kamcord.stats as reopen kamcord.stats 7 days ago
        if(allStats.containsKey(reopenKey.getKey())){
            ((BaseStats)allStats.get(reopenKey.getKey())).setReopenIds(stats.getInitialSet());
        }else{
            BaseStats reopenStats = new BaseStats(reopenKey);
            reopenStats.setReopenIds(stats.getInitialSet());
            //add new states to allStats
            addNewBaseStats(reopenStats);
        }
    }

    private void addNewBaseStats(Stats stats) {
        allStats.put(stats.getKey().getKey(), stats);
        updateHighLevelKeys(stats.getKey().getParentKey(), stats);
    }

    private void updateHighLevelKeys(Key key, Stats stats){
        //check if os key exist
        if(allStats.containsKey(key.getKey())){
            ((HighLevelStats)allStats.get(key.getKey())).addStats(stats);
        }else{
            HighLevelStats newStats = new HighLevelStats(key);
            newStats.addStats(stats);
            allStats.put(key.getKey(), newStats);

            Key parenKey = key.getParentKey();
            if(parenKey!=null){
                updateHighLevelKeys(parenKey, newStats);
            }
        }
    }

    private void queryDay7Retention(String startDate, int numDays, String os, String sdk) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(queryDateFormat.parse(startDate));
        double initialCount = 0;
        double reopenCount = 0;
        for(int i=0;i<numDays;i++){
            String date = getDateString(cal);
            Key key = new Key(date, os, sdk);
            if(allStats.containsKey(key.getKey())) {
                Stats stats = allStats.get(key.getKey());
                initialCount += stats.getInitialCount();
                reopenCount += stats.getReopenCount();
            }
            cal.add(Calendar.DAY_OF_MONTH,1);
        }
        if(initialCount==0){
            System.out.println("No result for query");
        }else {
            System.out.println("Day 7 retention " + numDays + " days from " + startDate + " for " + os + sdk + " is: " + reopenCount / initialCount);
        }
    }

    public static void main(String[] args) throws Exception {
        Model model = new Model();
        model.processingFile();
        model.queryDay7Retention("2014-09-08", 14, "", "");
        model.queryDay7Retention("2014-09-09", 14, "ios", "");
        model.queryDay7Retention("2014-09-10", 14, "android", "");
        model.queryDay7Retention("2014-09-11", 14, "ios", "1.7.5");
        model.queryDay7Retention("2014-09-12", 14, "android", "1.4.4");
        model.queryDay7Retention("2014-09-12", 1, "android", "1.4.4");
        model.queryDay7Retention("2014-09-12", 2, "android", "1.4.4");
        model.queryDay7Retention("2014-09-12", 0, "android", "1.4.4");
        model.queryDay7Retention("2014-09-13", 14, "xxx", "");


        //a. What was the overall Day­7 UI Retention over the month of September?
        model.queryDay7Retention("2014-09-01", 30, "", "");

        //b. What was the Day­7 UI Retention from September 8 through September 10 for the Android SDK?
        model.queryDay7Retention("2014-09-08", 3, "android", "");

        //c. What was the Day­7 UI Retention over the month of September for version 1.7.5 of the iOS SDK?
        model.queryDay7Retention("2014-09-01", 30, "ios", "1.7.5");

    }
}
