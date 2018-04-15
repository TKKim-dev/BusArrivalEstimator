# @compare.py
import datetime
import os

def compare(route_name ,response, current):
    now = datetime.datetime.now()
    for response_key in response: # response 에는 있는데 current 에 없는 경우 : 기점에서 운행 시작
        if not response_key in current:
            file = route_name + '/' + response_key + '.txt'
            if os.path.exists(file):
                f = open(file, 'a+')
            else:
                if not os.path.exists(route_name):
                    os.mkdir(route_name)
                f = open(file, 'w+')
            f.write(now.strftime('%Y-%m-%d %H:%M:%S') + '|'+ response[response_key] + '\n')
            f.close()
    for current_key in current: # current 에는 있는데 response 에 없는 경우 : 종점에서 운행 종료
        if not current_key in response:
            file = route_name + '/' + current_key + '.txt'
            if os.path.exists(file):
                f = open(file, 'a+')
            else:
                if not os.path.exists(route_name):
                    os.mkdir(route_name)
                f = open(file, 'w+')
            f = open(file, 'a+')
            f.write(now.strftime('%Y-%m-%d %H:%M:%S') + '|' + 'EOF\n')
            f.close()
    for current_key in current:
        if not current_key in response:
            continue
        if current[current_key] != response[current_key]:
            file = route_name + '/' + current_key + '.txt'
            if os.path.exists(file):
                f = open(file, 'a+')
            else:
                if not os.path.exists(route_name):
                    os.mkdir(route_name)
                f = open(file, 'w+')
            f = open(file, 'a+')
            f.write(now.strftime('%Y-%m-%d %H:%M:%S') + '|'+ response[current_key] + '\n')
            f.close()