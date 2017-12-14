package busArrivalEst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HTTPRequester {
	static URL url = null;
	static URLConnection uc = null;
	final static String sUrl = "http://openapi.gbis.go.kr/ws/rest/buslocationservice"; // URL 주소
    final static String serviceKey = "1234567890"; // 서비스 인증키. 1234567890 은 테스트 값
    static String routeID; // 사용자가 입력하는 노선 ID
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
    Date currentTime = new Date();
    
    // 경기도 gbis api에 HTTP get 신호를 보내고 그에 대한 결과를 문자열로 리턴하는 메서드.
    public static String getResponse(){
        StringBuffer sb = new StringBuffer();
        String result;
    	try {
    		url = new URL(sUrl + "?" + "serviceKey" + "=" + serviceKey
        								+ "&" + "routeId" + "=" + routeID);
    		uc = url.openConnection();
    		BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
    		String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
    		result = sb.toString();
            if(result.contains("<resultCode>0")) {
            	System.out.println("\n\n" + simpleDateFormat.format(new Date(System.currentTimeMillis())) 
            			+ "초의 시각에 성공적으로 데이터 수신.");
                return result;
            } else {
            	BusArrivalEstimator.toolkit.beep();
            	System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis()))
            			+ "초의 시각에 오류 발생. 서버로부터 온 메시지: "
            			+ result.split("<resultMessage>")[1].split("</resultMessage>")[0]);
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return "ERROR";
	}
    //http://dimdol.blogspot.kr/2009/09/%EC%9E%90%EB%B0%94-http-post-%EC%9A%94%EC%B2%AD.html
    public static void postResponse(String userRouteID, String userRouteName) {
    	try {
    		URL url = new URL("http://m.bus.go.kr/mBus/bus/getRouteAndPos.bms");
    		URLConnection uc = url.openConnection();
    		uc.setDoOutput(true);
    		String parameter = URLEncoder.encode("busRouteId", "UTF-8") + "=" + URLEncoder.encode(userRouteID, "UTF-8");
    		OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
    		wr.write(parameter);
    		wr.flush();
    		BufferedReader rd = null;
    		rd = new BufferedReader(new InputStreamReader(uc.getInputStream(), "EUC-KR"));
    		String line = null;
    		File file = new File("./SeoulRouteInfo.txt");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
    		while ((line = rd.readLine()) != null) {
    		  System.out.println(line + userRouteID);
    		  bw.write(line);
    		  break;
    		}
    		bw.close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    // 서버로부터 온 응답을 해석하고 파싱하는 메서드. 현재 운행중인 버스에 대한 정보를 담은 busID, busSeq 어레이리스트에 저장.
    public static void parseResponse(String response, ArrayList<String> busID, ArrayList<String> busSeq) {
    	if(response.equals("ERROR")) return;
    	ArrayList<String> responseID = new ArrayList<String>();
    	ArrayList<String> responseSeq = new ArrayList<String>();
    	ArrayList<Integer> del_List = new ArrayList<Integer>(); // 운행을 마쳐서 제거할 index에 대한 List
    	String[] str;
    	String file_path;
    	File file;
    	FileWriter fw;
    	int i = 0;
    	if(response.contains("<plateNo>")) str = new String(response).split("<plateNo>");
    	else if(response.contains("plainNo\":\"")) str = new String(response).split("\\[\\{\"busType\":\"");
    	else {
    		System.out.println("오류! 조회된 데이터가 없습니다. 버스가 더이상 운행되지 않거나 서버 오류일 수 있습니다.");
    		return;
    	}
    	// responseID, Seq 로 파싱하는 부분.
    	for(String s:str) {
    		// 경기도 버스의 경우 아래와 같은 문자열을 포함한다.
    		if(s.contains("</plateNo>") && s.contains("</stationSeq>")) { // 해당 String에 이 문자열들이 존재한다면(유효한 데이터라면)
    			responseID.add(new String(s.split("<")[0])); // 우선 버스의 식별 번호(예: 경기79바8143)와 정류소 번호(seq)를 temp 에 저장하고
    			responseSeq.add(new String(s.split("<stationSeq>")[1].split("</stationSeq>")[0]));
    		} else if(s.contains("isFullFlag")) { // 서울 버스의 경우 버스가 특정 위치에 있을 때 isFullFlag라는 문자열을 가진다.
    			// EXC1 해결 위해서 처리한 부분. 아래와 같이 수정함.
    			for(String temp : s.split("\\}\\]")[0].split("plain")) {
    				if(temp.contains("No")) {
    					responseID.add(new String(temp.split("No\":\"")[1].split("\"")[0])); // 우선 버스의 식별 번호(예: 경기79바8143)와 정류소 번호(seq)를 temp 에 저장하고
    	    			responseSeq.add(new String(s.split("q\":\"")[1].split("\"")[0]));
    				}
    			}
    		}
    	}

		// 배열 검사 부분. 메모리(bus 어레이리스트)에 이미 해당 버스가 로드되어있는지를 우선 검사.
    	try {
    		for(String s:responseID) { // 서버로부터 받은 문자열 s_res 와 메모리의 busInfo 배열 검사.
    			if(busID.contains(s)) {
    				// 만약 메모리에 올라가있는 버스의 정류소 위치와 서버로부터 받은 정류소 위치가 다르다면(정류소 이동한 경우) 리스트에 새롭게 할당.
    				if(!busSeq.get(busID.indexOf(s)).equals(responseSeq.get(responseID.indexOf(s)))) {
    					busSeq.set(busID.indexOf(s), responseSeq.get(responseID.indexOf(s)));
    					// 그리고 해당 일련번호의 마지막 txt 파일에 해당 정보와 날짜를 기록. 
    					for(i = 1;true;i++) {
    						// i에 1을 더한 파일이 존재하지 않을 경우! 즉 i번째가 마지막 파일인 경우, 파일을 열고 기록함.
    						if(!(file = new File("./Result//" + BusArrivalEstimator.userRouteName + "//" + s + "_" + i+1)).exists()) {
    							file = new File("./Result//" + BusArrivalEstimator.userRouteName + "//" + s + "_" + i);
    							fw = new FileWriter(file, true);
    							fw.write(busSeq.get(busID.indexOf(s)) + "|" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\n");
    							fw.close();
    							break;
    						}
        				}
    				}
    			} else { // responseID를 busID에서 찾아봐도 나오지 않는 경우. 즉 새롭게 실행했거나 버스가 기점에서 출발한경우 새롭게
    				// 파일을 새롭게 생성.
    				i = 1;
    				file_path = new String("./Result//" + BusArrivalEstimator.userRouteName + "//" + s + "_" + i);
    				file = new File(file_path);
    				while(file.exists()) { // 파일이 이미 존재한다면 i를 늘려서 다시 시도.
    					i++;
    					file_path = new String("./Result//" + BusArrivalEstimator.userRouteName + "//" + s + "_" + i);
        				file = new File(file_path);
    				}
    				file.createNewFile();
    				// 파일이 생성되었다면 busID와 busSeq에 해당 response 값을 각각 기록하고, 파일에 첫번째 위치 기록.
    				busID.add(s);
    				busSeq.add(responseSeq.get(responseID.indexOf(s)));
    				fw = new FileWriter(file);
					fw.write(busSeq.get(busID.indexOf(s)) + "|" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\n");
					fw.close();
    			}
    		}
			for(String s:busID) {// 이번엔 반대로 busID로 responseID를 검사. busID에는 있는데 responseID에는 포함되지 않은 경우? 이건 운행되던 버스가 종점에 들어갔을 때!
	    		// 그렇다면 busID 와 busSeq의 리스트를 제거하고, 파일에 "<end of file> 날짜" 표시.
				if(!responseID.contains(s)) {
					for(i = 1;true;) {
						// i번째인 마지막 파일을 찾고, 연 뒤에 기록함.
						if(!(file = new File("./Result//" + BusArrivalEstimator.userRouteName + "//" + s + "_" + ++i)).exists()) {
							file = new File("./Result//" + BusArrivalEstimator.userRouteName + "//" + s + "_" + --i);
							fw = new FileWriter(file, true);
							fw.write("\n<End Of FILE> at " + simpleDateFormat.format(new Date(System.currentTimeMillis())));
							fw.close();
							System.out.println("※※※ " + s + "버스가 운행을 마쳤습니다. ※※※");
							del_List.add(busID.indexOf(s));
							break;
						}
    				}
	    		}
			}
			// 저장된 제거 목록을 차례대로 제거하고 다시 del_List를 초기화. ConcurrentModificationException의 방지를 위해 번거롭지만 이렇게 설정함.
			for(int temp:del_List) {
				busID.remove(temp);
				busSeq.remove(temp);
			}
			del_List.clear();
			responseID.clear();
			responseSeq.clear();
		} catch(Exception e) {
			e.printStackTrace();
		}
   	for(String s:busID)
   		if(s != null) System.out.println("현재 조회된 버스의 위치: " + s + "의 버스가 " 
   												+ busSeq.get(busID.indexOf(s)) + "번째 정류장을 지나는 중.");
   	}   
}