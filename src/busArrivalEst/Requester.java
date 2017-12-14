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
	static String sUrl; // URL �ּ�
    static int routeID; // ����ڰ� �Է��ϴ� �뼱 ID
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
	
	// ����ڰ� �Է��� �뼱ID�� �뼱 �̸��� String ���� �޾Ƽ� ������ Request�� ����.
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
            			+ "���� �ð��� ���������� ������ ����.");
                return result;
            } else {
            	System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis()))
            			+ "���� �ð��� ���� �߻�. �����κ��� �� �޽���: "
            			+ result);
            }
    		return sb.toString();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
}

//Type1�� REST ������� ������ ��. ���߿� ��¼�� �� ��ũ���� ������� �ϰ� �� ���� �ְڴ�.
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
            
            /* ���ڰ� ������ �κ��� �ڵ�
             * url = new URL(sUrl + "?" + "serviceKey" + "=" + serviceKey
        			+ "&" + "routeId" + "=" + routeID);
    		uc = url.openConnection();
            while((len = uc.getInputStream().read(buf, 0, buf.length)) != -1) {
                sb.append(new String(buf, 0, len));
            }*/
            
    		result = sb.toString();
            if(result.contains("<resultCode>0")) {
            	System.out.println("\n\n" + simpleDateFormat.format(new Date(System.currentTimeMillis())) 
            			+ "���� �ð��� ���������� ������ ����.");
            	System.out.println(result);
                return result;
            } else {
            	System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis()))
            			+ "���� �ð��� ���� �߻�. �����κ��� �� �޽���: "
            			+ result.split("<resultMessage>")[1].split("</resultMessage>")[0]);
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return "ERROR";
	}
}
