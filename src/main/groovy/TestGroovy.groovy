import org.apache.commons.lang.StringUtils

def str="1.1.1"
def str2="张进松/果建鑫"
def split = str2.split("/");
def of = str.lastIndexOf(".")
println(split)

def pad = StringUtils.leftPad("1", 2, "0")
println(pad)