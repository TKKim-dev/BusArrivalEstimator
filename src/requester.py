# @requester.py
import requests, time

def get_response(is_seoul_bus, routeId):
    if is_seoul_bus:
        params = {
            'busRouteId': routeId
        }
        response = requests.get("http://m.bus.go.kr/mBus/bus/getRouteAndPos.bms", params=params)
        return response.text
    else:
        params = {
            'serviceKey': '1234567890',
            'routeId': routeId
        }
		try:
			response = requests.get("http://openapi.gbis.go.kr/ws/rest/buslocationservice", params=params)
		except:
			print(time.strftime("%Y-%m-%d %H:%M:%S]", "Connection Time Out. Sleeping for 30 minutes"))
			time.sleep(1800)
			return ''
        return response.text