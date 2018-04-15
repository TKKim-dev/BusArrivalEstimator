# @requester.py
import requests

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
        response = requests.get("http://openapi.gbis.go.kr/ws/rest/buslocationservice", params=params)
        return response.text