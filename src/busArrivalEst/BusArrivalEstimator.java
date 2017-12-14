package busArrivalEst;
import java.awt.Toolkit;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.ArrayUtils;

public class BusArrivalEstimator {
	static String[][] routeInfo = new String[3130][2]; // 총 2155개의 노선 수. 노선 이름과 노선 ID를 저장.
	static String[][] routeStationInfo = new String[147967][3]; // 노선 번호, 노선 정류장 번호, 정류장 이름을 저장.
	static ArrayList<String> busID; // 버스 정보(버스 식별자)를 저장하는 어레이리스트.
	static ArrayList<String> busSeq; // 버스 정보(버스 위치)를 저장하는 어레이리스트.
	static String userRouteName; // 사용자가 입력한 노선 이름
    static String userRouteID = "-1"; // 사용자의 노선 번호. -1의 값이면 아직 설정이 안된 상태.
    static ArrayList<String> userRouteStationArray = new ArrayList<String>(); // 사용자가 입력한 노선에 대한 정류소 순서
    static boolean shouldShowDefaultMenu = true; // 기본 메뉴(showMenu(0))를 표시할지의 여부.
    static Timer requestTimer;
    static TimerTask task;
    static int selection;
    static ArrayList<Double> resultArrayList;
    static HashMap<String, String> userRouteStationMap = new HashMap<String, String>();
	static boolean isSeoulBus;
    static Toolkit toolkit = Toolkit.getDefaultToolkit();
    
	public static void main(String[] args) {
    	while(true) {
    		if(shouldShowDefaultMenu) showMenu(0);
    		try {
    			selection = Integer.parseInt(inputByUser());
    		} catch(NumberFormatException nfe) {
    			toolkit.beep();
    			System.out.println("");
    			System.out.println("다시 입력해주세요:");
    			continue;
    		}
    		switch(selection) {
////////////////////////// 첫번째 메뉴 표시: 사용자가 직접 노선 이름 입력하고 메모리에 로드 ////////////////////////
    			case 1 : 
    				clearScreen();
    				showMenu(1);
    				userRouteName = inputByUser(); // 사용자로부터 입력 받는 메서드.
    				// 만약 사용자가 -1을 입력했다면 다시 처음으로.
    				if(userRouteName.equals("-1")) {
    					shouldShowDefaultMenu = true;
    					break;
    				} else {
    					// 우선 노선 이름과 노선 ID가 저장된 배열을 메모리에 로드하고
    					RouteInfo.loadGBISRouteIDNMInfo(routeInfo);
    					// 사용자가 입력한 노선 이름으로 ID를 검색해주는 메서드 이용. -1이 반환되면 다시 처음으로.
    					if((userRouteID = RouteInfo.searchRouteID(userRouteName)).equals("-1")) {
    						System.out.println("노선 이름이 존재하지 않습니다. 엔터를 누르면 처음으로 돌아갑니다.");
    						pause();
    						shouldShowDefaultMenu = true;
    						break;
    					} else { // 여기까지 문제없이 진행되었다면 우선 routeStation(날짜).txt 파일에서 필요한 정보를 파싱하고 
    						RouteInfo.parseStationInfo("./data//routestation20171209.txt");
    						// 그 중 해당 노선에 해당하는 정류소 이름만 따로 ArrayList에 저장.
    						userRouteStationArray = RouteInfo.loadStationInfo(userRouteID);
    						// 그리고 상대 경로에 해당 노선 번호로 디렉토리를 만듦. 기록 시 결과 파일을 저장할 분류 폴더임.
    						File fileDir = new File("./Result//" + userRouteName);
    						if(!fileDir.exists()) fileDir.mkdirs();
    						// 그리고 요청을 보낼 HTTPRequester 의 노선 번호 초기화.
    						HTTPRequester.routeID = userRouteID;
    						
    						// 이건 서울 버스일 때!
    						userRouteStationMap = RouteInfo.loadTOPISRouteIDNMInfo(userRouteID, userRouteName);
    					}
    					int i = 0;
    					if(!userRouteStationArray.isEmpty()) {
    						isSeoulBus = false;
    						for(String str : userRouteStationArray) 
    							System.out.println(++i + "번째 정류소 이름 : " + str);
    					} else { // 서울 버스일 때.
    						isSeoulBus = true;
    						for(; i < userRouteStationMap.size();) 
    							System.out.println(++i + "번째 정류소 이름 : " + userRouteStationMap.get("" + i));
    					}
    					System.out.println("----------------------------------------------");
    					System.out.println("성공적으로 로드 되었습니다. 해당 노선의 정류소 정보는 위와 같습니다.");
    					System.out.println("");
    					System.out.println("엔터를 입력하면 처음으로 돌아갑니다.");
    					pause();
    					shouldShowDefaultMenu = true;
    					break;
    				}
    				
////////////////// 두 번째 기능, 사용자가 설정해놓은 노선 번호로 서버에 요청 & 기다리기 ////////////////////////
    			case 2 :
    				clearScreen();
    				if(userRouteID.equals("-1")) {
    					toolkit.beep();
    					System.out.println("※※※현재 입력된 노선 번호가 존재하지 않습니다. 다시 메뉴로 돌아가 노선 번호를 입력해주세요!※※※");
    					System.out.println("");
    					System.out.println("엔터를 입력하면 처음으로 돌아갑니다.");
    					pause();
    					shouldShowDefaultMenu = true;
    					break;
    				}
					System.out.println("현재 입력된 노선 이름은 " + userRouteName + "번 입니다.");
					System.out.println("");
					System.out.println("해당 노선으로 기록 모드를 실행할까요? 기록 모드는 30초의 간격으로 버스 위치 정보를 조회하고 Result 폴더에 그 결과를 저장합니다.");
					System.out.println("");
					System.out.println("y를 입력하여 계속. 그 이외의 키를 입력하면 다시 처음으로 돌아갑니다 :");
					if(!inputByUser().equalsIgnoreCase("y")) {
    					shouldShowDefaultMenu = true;
    					break;
					}
					System.out.println("기록 모드를 실행합니다! stop 을 입력하면 이전 메뉴로 돌아갑니다 :");
					repeat();
					requestTimer.schedule(task, 0, 3000);
					while(true) {
    					if(inputByUser().equalsIgnoreCase("stop")) {
    						requestTimer.cancel();
    						shouldShowDefaultMenu = true;
    						break;
    					}
    					System.out.println("");
    					System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
					}
    				break;
//////////////////////// 조회 모드. 숫자 2개를 입력해서.. ////////////////////////////////////////////////    				
    			case 3 :
    				clearScreen();
					showMenu(3);
					int station1Num, station2Num;
    				if(!inputByUser().equalsIgnoreCase("y")) {
    					shouldShowDefaultMenu = true;
    					break;
					}
    				clearScreen();
    				
    				resultArrayList = new ArrayList<Double>();
    				int i = 0;
					if(isSeoulBus) 
						for(; i < userRouteStationMap.size();) 
							System.out.println(++i + "번째 정류소 이름 : " + userRouteStationMap.get("" + i));
					else
						for(String str : userRouteStationArray) 
							System.out.println(++i + ":  " + str);
    				System.out.println("");
					System.out.println("위의 노선에 해당하는 정류소 번호 2개를 띄어쓰기로 구분하여 입력해주세요:");
					System.out.println("			(예: 1 15)");
    				try {
    					Scanner scan = new Scanner(System.in);
    					String s1 = scan.next();
    					String s2 = scan.next();
    					station1Num = Integer.parseInt(s1);
    					station2Num = Integer.parseInt(s2);
    					scan.close();
    				} catch(Exception e) {
    					toolkit.beep();
    					System.out.println("잘못된 입력입니다. 다시 처음으로 돌아갑니다.");
    					shouldShowDefaultMenu = true;
    					break;
    				}
    				// 성공적으로 두 변수가 설정되었다면 파일들 불러오기.
    				try {
    					String line;
    					StringTokenizer st;
    					File file = new File("./Result//" + userRouteName);
    					File[] files = file.listFiles();
    					FileReader fr;
    					BufferedReader br;
    					SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    					for(File temp:files) {
    						Date d1 = new Date();
    						Date d2 = new Date();
    						fr = new FileReader(temp);
    						br = new BufferedReader(fr);
    						boolean isStation1Found = false;
    						while ((line = br.readLine()) != null) {
    							st = new StringTokenizer(line,"|");
    							if(st.hasMoreTokens()) {
    								String temp1 = st.nextToken();
    								String temp2 = st.nextToken();
    								// 만약 저장되어있는 파일에 station1Num 이 있다면 날짜 Date 객체에 저장.
    								if(!isStation1Found & Integer.parseInt(temp1) == station1Num) {
    									d1 = stringToDate.parse(temp2);
    									isStation1Found = true;
    									continue;
    								} else if(isStation1Found & Integer.parseInt(temp1) == station2Num) {
    									d2 = stringToDate.parse(temp2);
    									resultArrayList.add((double)(d2.getTime() - d1.getTime()));
    									System.out.println("소요된 시간: " + ((d2.getTime() - d1.getTime())) + " ms");
    									System.out.println("조회된 파일: " + temp.getName());									
    									break;
    								}
    							} else {
    								break;
    							}
    						}
    					}
    		        } catch(Exception e) {
    					e.printStackTrace();
    				}
    				// ArrayList -> Double -> double 형변환.
    				double[] result = ArrayUtils.toPrimitive(resultArrayList.toArray(new Double[resultArrayList.size()]));
    				if(result.length == 0) {
        				toolkit.beep();
        				System.out.println("오류! 조회된 데이터가 없습니다. 엔터를 눌러 다시 돌아갑니다.");
        				pause();
        				shouldShowDefaultMenu = true;
        				break;
    				}
    				System.out.println("평균값: " + ArraySummary.mean(result) + " ms");
    				System.out.println("");
    				System.out.println("표준편차: " + ArraySummary.stdDev(ArraySummary.variance(result))+ " ms");
    				System.out.println("");
    				System.out.println("최소값: " + ArraySummary.min(result)+ " ms");
    				System.out.println("");
    				System.out.println("최대값: " + ArraySummary.max(result)+ " ms");
    				System.out.println("");
    				System.out.println("메인으로 돌아가시려면 엔터를 눌러주세요.");
   					pause();
					shouldShowDefaultMenu = true;
					break;
    			default:
    				toolkit.beep();
    				System.out.println("다시 입력해주세요.");
    				shouldShowDefaultMenu = true;
    		}
    	}
    }
	
	static void showMenu(int n) {
		switch(n) {
		case 0:
			System.out.println("\n################################################");
			System.out.println("");
			System.out.println("		버스 소요시간 예측 프로그램");
			System.out.println("");
			System.out.println("################################################");
			System.out.println("");
			System.out.println("1: 노선 번호 직접 입력하여 설정");
			System.out.println("");
			System.out.println("2: 버스 위치 정보 기록");
			System.out.println("");
			System.out.println("3: 기록된 정보 조회");
			System.out.println("");
			System.out.println("원하시는 기능에 해당하는 번호를 입력해주세요 :");
			shouldShowDefaultMenu = false;
			break;
		case 1:
			System.out.println("사용자의 버스 노선을 직접 입력합니다.");
			System.out.println("");
			System.out.println("원하는 노선 번호를 입력해주세요. -1을 입력하면 이전 메뉴로 돌아갑니다.");
			System.out.println("						  (예시: 1000, M7731, -1)");
			System.out.println("입력 값 : ");
			break;
		case 3:
			if(userRouteID.equals("-1")) {
				System.out.println("아직 노선 번호 입력이 안된 상태입니다. 메인으로 돌아갑니다.");
				shouldShowDefaultMenu = true;
				break;
			}
			System.out.println("사용자가 입력한 버스 노선에 쌓인 데이터를 읽고 통계를 작성합니다.");
			System.out.println("");
			System.out.println("현재 등록된 노선은 " + userRouteName + "번 입니다.");
			System.out.println("");
			System.out.println("그대로 진행하시려면 y를 눌러주세요.");
		}
	}
	// 사용자에게 문자열 입력을 받는 함수
	static String inputByUser() {
		Scanner scn = new Scanner(System.in);
		return scn.nextLine();
	}
	public static void clearScreen() {
		for (int i = 0; i < 80; i++)
			System.out.println("");
	}
	// 잠시 프로그램을 멈추게 하는 메서드.
	public static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {}
	}
	// 프로그램을 반복적으로 돌아가게 하는 메서드
    static void repeat() {
    	busID = new ArrayList<String>(); // 초기화
    	busSeq = new ArrayList<String>();
    	requestTimer = new Timer();
    	task = new TimerTask() {
        	@Override
        	public void run() {
        		if(isSeoulBus) HTTPRequester.parseResponse(TOPISRequester.sendBusRouteAndPosRequest(userRouteID, userRouteName), busID, busSeq);
        		else HTTPRequester.parseResponse(HTTPRequester.getResponse(), busID, busSeq);
        	}
        };
    }
}