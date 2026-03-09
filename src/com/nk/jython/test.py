#unicode=UTF-8

#通过java package导入java类
from com.nk.jython import SayHello

execpy = SayHello()

#将python属性传入后续调用的java实例
execpy.setUserName(userName)

def say():
    execpy.say(14)
    return

say()