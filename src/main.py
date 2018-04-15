# @main.py
import datetime, threading
from requester import *
from parse import *
from compare import *

current, is_seoul_bus, route_name, routeID = {}, False, '88A', 218000018
def repeat(second = 1.0):
    global current, is_seoul_bus, route_name, routeID
    if datetime.datetime.now().hour == 3: # 새벽 3시가 되면 쓰레드를 종료
        return
    response = parse(is_seoul_bus, get_response(is_seoul_bus, routeID))
    if current == {}:
        compare(route_name, response, current)
        current = response
    else:
        compare(route_name, response, current)
        current = response
    print('Repeating...')
    threading.Timer(second, repeat, [second]).start()
    
repeat(3.0)