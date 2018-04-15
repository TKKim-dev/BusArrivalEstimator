<<<<<<< HEAD
package busArrivalEst;
import com.opencsv.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

// 해당 노선에 대한 정보를 작성하는 클래스. 기반 정보 txt파일에서 노선 ID, 노선 번호로 이루어진 CSV 로 출력.
public class RouteInfo {

	// 기반 정보를 제공하는 route(날짜).txt 파일을 노선ID와 노선 번호로만 이루어진 CSV 파일로 파싱하는 메서드
	public static void parseBaseInfoToRouteInfo(String file_path) {
        try {
            String line;
            StringTokenizer st;
            
            // 소스 파일읽기;
            BufferedReader br = new BufferedReader(new FileReader(new File(file_path + ".txt")));
            // 결과 출력파일
            File file = new File(file_path + "_Result" + ".csv");
 
            // 파일생성
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            // 한줄씩 읽기
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line,"stStationNm");
                if(st.hasMoreTokens()) st.nextToken();
                String s1, s2;
                int i = 0;
            	while (st.hasMoreTokens()) {
            			i++;
            			bw.write(st.nextToken());
            			if(i==2) break;
            			bw.write(",");
            			// 한줄내려쓰기
            	}
    			bw.write("\n");
    			bw.flush();
            }
            bw.close();
            fw.close();
            br.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// 서버에서 받은 routeStation(날짜).txt 파일에서 필요한 정보를 파싱하는 메서드. routeStation(날짜).txt 파일 -> routeStationInfo[][] 배열에 모두 저장.
	public static void parseStationInfo(String file_path) {
        try {
            String str;
            StringTokenizer st_row, st_column;
            
            // 소스 파일읽기;
            FileInputStream fis = new FileInputStream(file_path);
            InputStreamReader isr = new InputStreamReader(fis, "EUC-KR");
            BufferedReader br = new BufferedReader(isr);
 
            // 한줄씩 읽기
            while ((str = br.readLine()) != null) {
                st_row = new StringTokenizer(str,"^");
                st_row.nextToken(); // 항목에 대한 정보가 담긴 첫번째 행은 넘기기.
                int i = 0; // 147967개의 문자열에 해당 정보 모두 저장.
            	while (st_row.hasMoreTokens()) {
            		st_column = new StringTokenizer(st_row.nextToken(), "|");
            		int j = 0;
            		int k = 0;
            		while(st_column.hasMoreTokens()) {    
            			// 해당 파일에서 노선ID, 정류소 번호, 정류소 이름만 추출
            			if(j == 1 || j == 2 || j == 4) {
            				j++;
            				// 원치 않는 값을 가진 열(column)인 경우 nextToken 으로 넘기기.
            				st_column.nextToken();
            				continue;
            			} else {
                			BusArrivalEstimator.routeStationInfo[i][k] = st_column.nextToken();
                			j++;
                			k++;
            			}
            		}
            		i++;
            	}
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// CSV 파일을 읽어서 routeInfo 배열에 저장하는 메서드. 메모리 로드
	public static void loadGBISRouteIDNMInfo(String[][] routeInfo) {
		try {
			String fileName = "./data//routeID-routeNM.csv";
			CSVReader reader = new CSVReader(new FileReader(fileName));
			String[] nextLine;
			for(int i = 0; (nextLine = reader.readNext()) != null && i != 3130; i++) {
				routeInfo[i][0] = nextLine[0];
				routeInfo[i][1] = nextLine[1];
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static HashMap<String, String> loadTOPISRouteIDNMInfo(String userRouteID, String userRouteName) {
		String temp = TOPISRequester.sendBusRouteAndPosRequest(userRouteID, userRouteName);
    	if(!temp.contains("stationNm")) return null;
    	HashMap<String, String> tempMap = new HashMap<String, String>();
    	String[] str = new String(temp).split("stationNm\":\"");
        for(String s:str) 
        	if(s.contains("seq")) 
        		tempMap.put(s.split("seq\":\"")[1].split("\"")[0], s.split("\"")[0]);
		return tempMap;
	}
	
	// 노선 이름을 검색하여 노선 ID를 리턴. 찾는 값이 없으면 -1 리턴.
	public static String searchRouteID(String routeName) {
		for(int i = 0; i < 3130; i++) 
			if(BusArrivalEstimator.routeInfo[i][1].equalsIgnoreCase(routeName)) 
				return BusArrivalEstimator.routeInfo[i][0];
		return "-1";
	}
	// 노선 번호를 매개변수로 입력하면 해당 노선에 해당하는 정류소 번호 & 정류소 이름만 배열에 저장.
	public static ArrayList<String> loadStationInfo(String userRouteID) {
		ArrayList<String> stationInfo = new ArrayList<String>();
		for(int i = 0; i < 147967; i++) 
			if(BusArrivalEstimator.routeStationInfo[i][0].equals(userRouteID)) 
				stationInfo.add(BusArrivalEstimator.routeStationInfo[i][2]);
		return stationInfo;
	}
}
=======
package busArrivalEst;
import com.opencsv.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

// 해당 노선에 대한 정보를 작성하는 클래스. 기반 정보 txt파일에서 노선 ID, 노선 번호로 이루어진 CSV 로 출력.
public class RouteInfo {

	// 기반 정보를 제공하는 route(날짜).txt 파일을 노선ID와 노선 번호로만 이루어진 CSV 파일로 파싱하는 메서드
	public static void parseBaseInfoToRouteInfo(String file_path) {
        try {
            String line;
            StringTokenizer st;
            
            // 소스 파일읽기;
            BufferedReader br = new BufferedReader(new FileReader(new File(file_path + ".txt")));
            // 결과 출력파일
            File file = new File(file_path + "_Result" + ".csv");
 
            // 파일생성
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            // 한줄씩 읽기
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line,"stStationNm");
                if(st.hasMoreTokens()) st.nextToken();
                String s1, s2;
                int i = 0;
            	while (st.hasMoreTokens()) {
            			i++;
            			bw.write(st.nextToken());
            			if(i==2) break;
            			bw.write(",");
            			// 한줄내려쓰기
            	}
    			bw.write("\n");
    			bw.flush();
            }
            bw.close();
            fw.close();
            br.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// 서버에서 받은 routeStation(날짜).txt 파일에서 필요한 정보를 파싱하는 메서드. routeStation(날짜).txt 파일 -> routeStationInfo[][] 배열에 모두 저장.
	public static void parseStationInfo(String file_path) {
        try {
            String str;
            StringTokenizer st_row, st_column;
            
            // 소스 파일읽기;
            FileInputStream fis = new FileInputStream(file_path);
            InputStreamReader isr = new InputStreamReader(fis, "EUC-KR");
            BufferedReader br = new BufferedReader(isr);
 
            // 한줄씩 읽기
            while ((str = br.readLine()) != null) {
                st_row = new StringTokenizer(str,"^");
                st_row.nextToken(); // 항목에 대한 정보가 담긴 첫번째 행은 넘기기.
                int i = 0; // 147967개의 문자열에 해당 정보 모두 저장.
            	while (st_row.hasMoreTokens()) {
            		st_column = new StringTokenizer(st_row.nextToken(), "|");
            		int j = 0;
            		int k = 0;
            		while(st_column.hasMoreTokens()) {    
            			// 해당 파일에서 노선ID, 정류소 번호, 정류소 이름만 추출
            			if(j == 1 || j == 2 || j == 4) {
            				j++;
            				// 원치 않는 값을 가진 열(column)인 경우 nextToken 으로 넘기기.
            				st_column.nextToken();
            				continue;
            			} else {
                			BusArrivalEstimator.routeStationInfo[i][k] = st_column.nextToken();
                			j++;
                			k++;
            			}
            		}
            		i++;
            	}
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// CSV 파일을 읽어서 routeInfo 배열에 저장하는 메서드. 메모리 로드
	public static void loadGBISRouteIDNMInfo(String[][] routeInfo) {
		try {
			String fileName = "./data//routeID-routeNM.csv";
			CSVReader reader = new CSVReader(new FileReader(fileName));
			String[] nextLine;
			for(int i = 0; (nextLine = reader.readNext()) != null && i != 3130; i++) {
				routeInfo[i][0] = nextLine[0];
				routeInfo[i][1] = nextLine[1];
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static HashMap<String, String> loadTOPISRouteIDNMInfo(String userRouteID, String userRouteName) {
		String temp = TOPISRequester.sendBusRouteAndPosRequest(userRouteID, userRouteName);
    	if(!temp.contains("stationNm")) return null;
    	HashMap<String, String> tempMap = new HashMap<String, String>();
    	String[] str = new String(temp).split("stationNm\":\"");
        for(String s:str) 
        	if(s.contains("seq")) 
        		tempMap.put(s.split("seq\":\"")[1].split("\"")[0], s.split("\"")[0]);
		return tempMap;
	}
	
	// 노선 이름을 검색하여 노선 ID를 리턴. 찾는 값이 없으면 -1 리턴.
	public static String searchRouteID(String routeName) {
		for(int i = 0; i < 3130; i++) 
			if(BusArrivalEstimator.routeInfo[i][1].equalsIgnoreCase(routeName)) 
				return BusArrivalEstimator.routeInfo[i][0];
		return "-1";
	}
	// 노선 번호를 매개변수로 입력하면 해당 노선에 해당하는 정류소 번호 & 정류소 이름만 배열에 저장.
	public static ArrayList<String> loadStationInfo(String userRouteID) {
		ArrayList<String> stationInfo = new ArrayList<String>();
		for(int i = 0; i < 147967; i++) 
			if(BusArrivalEstimator.routeStationInfo[i][0].equals(userRouteID)) 
				stationInfo.add(BusArrivalEstimator.routeStationInfo[i][2]);
		return stationInfo;
	}
}
>>>>>>> 2def7f9fa2af651098b955e1a57abb0f4072516f
