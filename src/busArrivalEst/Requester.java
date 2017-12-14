package busArrivalEst;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class Requester {
	static URL url;
	static URLConnection uc;
	static String sUrl; // URL 주소
    static int routeID; // 사용자가 입력하는 노선 ID
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
    void setURL(String sUrl) throws Exception{
    	this.sUrl = sUrl;
    	url = new URL(sUrl);
		uc = url.openConnection();
		uc.setDoOutput(true);
    }
    static void  setParams() {}

    public static void postResponse() {}
}

class TOPISRequester extends Requester {
	
	// 사용자가 입력한 노선ID와 노선 이름을 String 으로 받아서 서버에 Request를 날림.
    static String sendBusRouteAndPosRequest(String userRouteID, String userRouteName) {
    	try {
    		URL url = new URL("http://m.bus.go.kr/mBus/bus/getRouteAndPos.bms");
    		URLConnection uc = url.openConnection();
    		uc.setDoOutput(true);
    		String parameter = URLEncoder.encode("busRouteId", "UTF-8") + "=" + URLEncoder.encode(userRouteID, "UTF-8");
            StringBuffer sb = new StringBuffer();
    		String result;
            OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
    		wr.write(parameter);
    		wr.flush();
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), "EUC-KR"));
    		String line = null;
    		while((line = br.readLine()) != null) {
    			sb.append(line);
    		}
    		br.close();
    		result = sb.toString();
            if(result.contains("resultList")) {
            	System.out.println("\n\n" + simpleDateFormat.format(new Date(System.currentTimeMillis())) 
            			+ "초의 시각에 성공적으로 데이터 수신.");
                return result;
            } else {
            	System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis()))
            			+ "초의 시각에 오류 발생. 서버로부터 온 메시지: "
            			+ result);
            }
    		return sb.toString();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
}

//Type1은 REST 방식으로 보내는 것. 나중에 어쩌면 웹 스크래핑 방식으로 하게 될 수도 있겠다.
class GBISRequesterType1 extends Requester {
    static String serviceKey;
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
    
    static void setParams() {
    	
    }
    
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
            
            /* 글자가 깨지던 부분의 코드
             * url = new URL(sUrl + "?" + "serviceKey" + "=" + serviceKey
        			+ "&" + "routeId" + "=" + routeID);
    		uc = url.openConnection();
            while((len = uc.getInputStream().read(buf, 0, buf.length)) != -1) {
                sb.append(new String(buf, 0, len));
            }*/
            
    		result = sb.toString();
            if(result.contains("<resultCode>0")) {
            	System.out.println("\n\n" + simpleDateFormat.format(new Date(System.currentTimeMillis())) 
            			+ "초의 시각에 성공적으로 데이터 수신.");
            	System.out.println(result);
                return result;
            } else {
            	System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis()))
            			+ "초의 시각에 오류 발생. 서버로부터 온 메시지: "
            			+ result.split("<resultMessage>")[1].split("</resultMessage>")[0]);
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return "ERROR";
	}
}
