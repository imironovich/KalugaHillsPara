package ru.afpf.kalugahillspara;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

import static java.lang.Float.intBitsToFloat;
import static java.lang.Float.parseFloat;
import static javax.xml.bind.DatatypeConverter.parseInt;

//грабим в 8:00 результат начиная с текущего дня с 6:00, далее 9:00, 12:00 и тд

//Driver driver = (Driver) Class.forName("mysql.jdbc").newInstance();



public class WindguruParser extends TimerTask {
    ArrayList<Integer> windSpeedInt = new ArrayList<>();
    final String sqlpasswd;
    final String sqlurl;
    final String sqluser;

    public WindguruParser(String DB_PASSWD, String DB_USER, String SQLURL){
        sqlpasswd = DB_PASSWD;
        sqluser = DB_USER;
        sqlurl = SQLURL;
    }


    public ArrayList<Integer> getWindDirInt() {
        return windDirInt;
    }

    public ArrayList<Integer> getWeekdayInt() {
        return weekdayInt;
    }

    public ArrayList<Integer> getHourseInt() {
        return hourseInt;
    }

    public ArrayList<Integer> getSmernInt() {
        return windBoostInt;
    }

    public ArrayList<Integer> getRainInt() {
        return rainInt;
    }

    public ArrayList<Integer> getWindSpeedInt() {
        return windSpeedInt;
    }

    ArrayList<Integer> windDirInt = new ArrayList<>();
    ArrayList<Integer> weekdayInt = new ArrayList<>();
    ArrayList<Integer> hourseInt = new ArrayList<>();
    ArrayList<Integer> windBoostInt = new ArrayList<>();
    ArrayList<Integer> rainInt = new ArrayList<>();

    @Override
    public void run() {
//        this.properties =  new Properties();
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;


        Calendar calendar = new GregorianCalendar();
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        try {
            url = new URL("https://old.windguru.cz/ru/index.php?sc=743917&sty=m_spot");
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            String allLine = null;
            while ((line = br.readLine()) != null) {
                allLine += line;
            }
            int tableStart = allLine.indexOf("var wg_fcst_tab_data_1");
            if (tableStart >0) {
                String resultStr1 = allLine.substring(tableStart + 1, allLine.indexOf("}}}}", tableStart));
       //         Wind speed in knot ~ m/s*2
                String winspd = resultStr1.substring(resultStr1.indexOf("WINDSPD\":[") + 10, resultStr1.indexOf("]", resultStr1.indexOf("WINDSPD\":[")));
                List<String> windSpeed = Arrays.asList(winspd.split(","));

                for (int i = 0; i < windSpeed.size(); i++) {
                    windSpeedInt.add(((int)(parseFloat(windSpeed.get(i))+0.5))/2);
                }

       //         wind direction in degrees
                String windir = resultStr1.substring(resultStr1.indexOf("WINDDIR\":[") + 10, resultStr1.indexOf("]", resultStr1.indexOf("WINDDIR\":[")));
                List<String> windDir = Arrays.asList(windir.split(","));

                for (int i = 0; i < windDir.size(); i++) {
                    windDirInt.add(parseInt(windDir.get(i)));
                }

       //         day of week - integer
                String weekday = resultStr1.substring(resultStr1.indexOf("hr_weekday\":[") + 13, resultStr1.indexOf("]", resultStr1.indexOf("hr_weekday\":[")));
                List<String> hrWeekday = Arrays.asList(weekday.split(","));

                for (int i = 0; i < hrWeekday.size(); i++) {
                    weekdayInt.add(parseInt(hrWeekday.get(i)));
                }

       //
                String hours = resultStr1.substring(resultStr1.indexOf("hours\":[") + 8, resultStr1.indexOf("]", resultStr1.indexOf("hours\":[")));
                List<String> hoursS = Arrays.asList(hours.split(","));

                for (int i = 0; i < hoursS.size(); i++) {
                    hourseInt.add(parseInt(hoursS.get(i)));
                }

                String gust = resultStr1.substring(resultStr1.indexOf("GUST\":[") + 7, resultStr1.indexOf("]", resultStr1.indexOf("GUST\":[")));
                List<String> gustS = Arrays.asList(gust.split(","));


        //        wind boost in knot ~m/s*2
                for (int i = 0; i < gustS.size(); i++) {
                //
                     windBoostInt.add(((int)(parseFloat(gustS.get(i))+0.5))/2);
                }


       //         rain or snow or ...
                String rain = resultStr1.substring(resultStr1.indexOf("APCP\":[") + 7, resultStr1.indexOf("]", resultStr1.indexOf("APCP\":[")));
                List<String> rainn = Arrays.asList(rain.split(","));

                for (int i = 0; i < rainn.size(); i++) {
                    String s = rainn.get(i);
                    s = s.replace("null","0");
                    rainInt.add((int)(parseFloat(s)*10));

                }
            }

            else System.out.println("Не найдена таблица");
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {

            }
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to load class.");
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(sqlurl, sqluser, sqlpasswd)){
            String sql = "REPLACE INTO windGuru (date, hour, windSpeedInt, windDirInt, windBoostInt, rainInt, weekdayInt) Values (?,?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement  = connection.prepareStatement(sql)){

                LocalDate datenow = LocalDate.now();

                int k = weekdayInt.indexOf(todayOfWeek);
                for (int i=1; i<9; i++){
                    LocalDate thisDay = datenow.plusDays(i);
                    for (int j=0; j<8; j++){
                       int hours3 = (j*3);
                       preparedStatement.setObject(1, thisDay);
                       preparedStatement.setInt(2, hours3);
                       preparedStatement.setInt(3, windSpeedInt.get(k));
                       preparedStatement.setInt(4, windDirInt.get(k));
                       preparedStatement.setInt(5 , windBoostInt.get(k));
                       preparedStatement.setInt(6, rainInt.get(k));
                       preparedStatement.setInt(7, weekdayInt.get(k));
                       preparedStatement.addBatch();
                       k++;
                    }
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        windSpeedInt.clear();
        windDirInt.clear();
        windBoostInt.clear();
        rainInt.clear();


    }
}
