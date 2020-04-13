package com.`is`.shadowingplayer

class SubtData(time:Long,text:String,useSmi:Boolean) {
    var time=time
    var text=if(useSmi){
        convertString(text)
    }else{
        text
    }
    var isUse=false
    private fun convertString(str:String):String
    {
        var temp:String=str.replace("<br>","\n")

        var i=0;var j=0
        try
        {
            while((temp.indexOf('<').also{i=it})!=-1)
            {
                i=temp.indexOf('<')
                j=str.indexOf('>')
                temp=temp.removeRange(i,j-i+1)
            }
            return temp;
        }catch(e:Exception ){
            return temp
        }

    }
}