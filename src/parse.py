# @parse.py
from requester import get_response

def parse(is_seoul_bus, string):
    List = []
    result = {}
    if is_seoul_bus: # 서울 버스인 경우의 파싱 구현
        temp = string.split('plainNo')[1:]
        for i in range(len(temp)):
            if i%2 == 1:
                List.append(temp[i].split('sectSpdCol')[0])
        for s in List:
            t = s.split(''':"''')
            result[t[1].split('''"''')[0]] = t[2].split('''"''')[0]
        return result
    else: # 경기도 버스인 경우의 파싱 구현
        temp = string.split('<plateNo>')[1:]
        for i in range(len(temp)):
            List.append(temp[i].split('</stationSeq>')[0])
        for s in List:
            result[s.split('<')[0]] = s.split('Seq>')[1]
        return result