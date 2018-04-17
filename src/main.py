# @main.py
import datetime, threading
from requester import *
from parse import *
from compare import *

current, is_seoul_bus, route_name, routeID = {}, True, '5513', 100100251
def repeat(second = 1.0):
    global current, is_seoul_bus, route_name, routeID
    response = parse(is_seoul_bus, get_response(is_seoul_bus, routeID))
    if current == {}:
        compare(route_name, response, current)
        current = response
    else:
        compare(route_name, response, current)
        current = response
    #print('Repeating...')
    threading.Timer(second, repeat, [second]).start()
    
repeat(3.0)